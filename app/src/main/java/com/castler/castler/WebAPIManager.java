package com.castler.castler;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Paul on 6/20/2015.
 */
public class WebAPIManager {
    private final String DOMAIN = "http://69.67.26.169:181/api/";

    public final int ERROR_NOT_CONNECTED = 0;
    public final int ERROR_MALFORMED_URL = 1;

    private final int API_NONE = 0;
    private final int API_PLAYER_INFO = 1;
    private final int API_SEND_GAME_RESULT = 2;

    private int apiCallType = API_NONE;

    String playerID = "";
    Game gameResult = null;

    private OnWebAPIManagerListener mListener;

    private int errorCode = 0;
    ProgressDialog progressDialog = null;

    public interface OnWebAPIManagerListener {
        public void onPlayerInfoReturned(Player player);
        public void onSendGameResultFinished(Game gameResult, boolean success);
    }

    public WebAPIManager (OnWebAPIManagerListener listener)
    {
        mListener = listener;
    }

    public int getErrorCode() {
        return errorCode;
    }

    private void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public boolean requestPlayerData(String playerID, Context context) {

        this.playerID = playerID;

        // Indicate that the player info was requested
        apiCallType = API_PLAYER_INFO;

        // Check if the device is connected to the network
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            errorCode = ERROR_NOT_CONNECTED;
            return false;
        }

        // Create the web URL
        URL url;
        try {
            url = new URL(DOMAIN + "player/" + playerID);
        }
        catch (MalformedURLException e) {
            errorCode = ERROR_MALFORMED_URL;
            return false;
        }

        // Create an AsyncTask
        // Task tries to get the data from the web service
        new DownloadWebpageTask().execute(url);

        // UI shows the Progress Dialog
        progressDialog = ProgressDialog.show(context, "Retrieving player", "Please wait...", true);

        // UI waits for the task to complete
        return true;
    }

    public boolean sendGameResult(Game gameResult, Context context) {

        this.gameResult = gameResult;

        // Indicate that the player info was requested
        apiCallType = API_SEND_GAME_RESULT;

        // Check if the device is connected to the network
        ConnectivityManager connMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected()) {
            errorCode = ERROR_NOT_CONNECTED;
            return false;
        }

        // Create the web URL
        String strResult = "";
        if (gameResult.getResult() == Game.GAME_RESULT_WHITE_WIN)
            strResult = "whitewin";
        else if (gameResult.getResult() == Game.GAME_RESULT_BLACK_WIN)
            strResult = "blackwin";
        else if (gameResult.getResult() == Game.GAME_RESULT_DRAW)
            strResult = "draw";

        //TODO: enter the name of the person recording the game here
        String nameOfRecorder = "paulb";

        URL url;
        try {
            url = new URL(DOMAIN + "game/enter?b=" + gameResult.getBlackPlayer().getPlayerID() + "&w=" + gameResult.getWhitePlayer().getPlayerID() + "&r=" + strResult + "&recordedby=" + nameOfRecorder);
        }
        catch (MalformedURLException e) {
            errorCode = ERROR_MALFORMED_URL;
            return false;
        }

        // Create an AsyncTask
        // Task tries to get the data from the web service
        new DownloadWebpageTask().execute(url);

        // UI shows the Progress Dialog
        progressDialog = ProgressDialog.show(context, "Sending result", "Please wait...", true);

        // UI waits for the task to complete
        return true;
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class DownloadWebpageTask extends AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {

            progressDialog.hide();

            // Parse the JSON string
            JSONObject jsonObject;

            try {
                jsonObject = new JSONObject(result);
            } catch (JSONException e) {
                // TODO: What to do if the JSON parsing fails?
                if (apiCallType == API_SEND_GAME_RESULT) {
                    // The game was probably sent successfully, we just didn't get a proper response from the site.
                    mListener.onSendGameResultFinished(WebAPIManager.this.gameResult, true);
                }
                return;
            }

            if (apiCallType == API_PLAYER_INFO) {
                // Get the first name
                try {
                    Player player = new Player();
                    player.setFirstName(jsonObject.getString("FirstName"));
                    player.setLastName(jsonObject.getString("LastName"));
                    player.setGrade(jsonObject.getInt("Grade"));
                    player.setRating(jsonObject.getInt("Score"));
                    player.setPlayerID(playerID);

                    mListener.onPlayerInfoReturned(player);

                } catch (JSONException e) {

                }
            }
            else if (apiCallType == API_SEND_GAME_RESULT)
            {
                // TODO: Normally the new player ratings would come back in this JSON string
                // The game was sent successfully
                mListener.onSendGameResultFinished(WebAPIManager.this.gameResult, true);
            }
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(URL myurl) throws IOException {
        InputStream is = null;
        // Only display the first 1000000 characters of the retrieved
        // web page content.
        int len = 1000000;

        try {
            HttpURLConnection conn = (HttpURLConnection) myurl.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            is = conn.getInputStream();

            // Convert the InputStream into a string
            String contentAsString = readIt(is, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream stream, int len) throws IOException, UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

}
