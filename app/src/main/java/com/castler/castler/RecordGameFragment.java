package com.castler.castler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordGameFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordGameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordGameFragment extends Fragment implements View.OnClickListener, WebAPIManager.OnWebAPIManagerListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Player whitePlayer = null;
    private Player blackPlayer = null;

    private OnFragmentInteractionListener mListener;
    private View thisView = null;

    ToggleButton btnWhiteWin = null;
    ToggleButton btnWhiteLose = null;
    ToggleButton btnWhiteDraw = null;

    ToggleButton btnBlackWin = null;
    ToggleButton btnBlackLose = null;
    ToggleButton btnBlackDraw = null;

    Button btnScanCodeWhite = null;
    Button btnChooseWhitePlayer = null;

    Button btnScanCodeBlack = null;
    Button btnChooseBlackPlayer = null;

    Button btnSubmit = null;

    TextView txtWhiteName = null;
    TextView txtBlackName = null;

    QRCodeScanner codeScanner;
    boolean whiteScanCodeWasClicked = true;

    String strWhiteName = null;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecordGameFragment.
     */
    public static RecordGameFragment newInstance(String param1, String param2) {
        RecordGameFragment fragment = new RecordGameFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RecordGameFragment() {
        codeScanner = new QRCodeScanner();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (thisView == null) {
            // Inflate the layout for this fragment
            thisView = inflater.inflate(R.layout.fragment_record_game, container, false);

            btnWhiteWin = (ToggleButton) thisView.findViewById(R.id.btn_white_win);
            btnWhiteWin.setOnClickListener(this);
            btnWhiteLose = (ToggleButton) thisView.findViewById(R.id.btn_white_lose);
            btnWhiteLose.setOnClickListener(this);
            btnWhiteDraw = (ToggleButton) thisView.findViewById(R.id.btn_white_draw);
            btnWhiteDraw.setOnClickListener(this);
            btnBlackWin = (ToggleButton) thisView.findViewById(R.id.btn_black_win);
            btnBlackWin.setOnClickListener(this);
            btnBlackLose = (ToggleButton) thisView.findViewById(R.id.btn_black_lose);
            btnBlackLose.setOnClickListener(this);
            btnBlackDraw = (ToggleButton) thisView.findViewById(R.id.btn_black_draw);
            btnBlackDraw.setOnClickListener(this);

            btnScanCodeWhite = (Button) thisView.findViewById(R.id.btn_scan_code_white);
            btnScanCodeWhite.setOnClickListener(this);

            btnScanCodeBlack = (Button) thisView.findViewById(R.id.btn_scan_code_black);
            btnScanCodeBlack.setOnClickListener(this);

            btnChooseWhitePlayer = (Button) thisView.findViewById(R.id.btn_choose_white_player);
            btnChooseWhitePlayer.setOnClickListener(this);

            btnChooseBlackPlayer = (Button) thisView.findViewById(R.id.btn_choose_black_player);
            btnChooseBlackPlayer.setOnClickListener(this);

            btnSubmit = (Button) thisView.findViewById(R.id.btn_submit);
            btnSubmit.setOnClickListener(this);

            txtWhiteName = (TextView) thisView.findViewById(R.id.white_name);
            txtBlackName = (TextView) thisView.findViewById(R.id.black_name);
        }

        updatePlayers();

        return thisView;
    }

    public void onToggleClicked(View v) {
        boolean[] checkedState = new boolean[6];
        for (int i = 0; i < 6; i++) {
            checkedState[i] = false;
        }

        if (v.getId() == R.id.btn_white_win) {
            checkedState[0] = true;
            checkedState[4] = true;
        } else if (v.getId() == R.id.btn_white_lose) {
            checkedState[1] = true;
            checkedState[3] = true;
        } else if (v.getId() == R.id.btn_white_draw) {
            checkedState[2] = true;
            checkedState[5] = true;
        } else if (v.getId() == R.id.btn_black_win) {
            checkedState[3] = true;
            checkedState[1] = true;
        } else if (v.getId() == R.id.btn_black_lose) {
            checkedState[4] = true;
            checkedState[0] = true;
        } else if (v.getId() == R.id.btn_black_draw) {
            checkedState[5] = true;
            checkedState[2] = true;
        }

        btnWhiteWin.setChecked(checkedState[0]);
        btnWhiteLose.setChecked(checkedState[1]);
        btnWhiteDraw.setChecked(checkedState[2]);
        btnBlackWin.setChecked(checkedState[3]);
        btnBlackLose.setChecked(checkedState[4]);
        btnBlackDraw.setChecked(checkedState[5]);

        updatePlayers();
    }

    public void onWhiteScanCodeClicked() {
        whiteScanCodeWasClicked = true;
        codeScanner.scanQRCode(this);
    }

    public void onBlackScanCodeClicked() {
        whiteScanCodeWasClicked = false;
        codeScanner.scanQRCode(this);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (codeScanner.parseQRCode(requestCode, resultCode, intent)) {
            WebAPIManager webAPIManager = new WebAPIManager(this);
            webAPIManager.requestPlayerData(codeScanner.contents, this.getActivity());
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        if (v == btnWhiteWin ||
                v == btnWhiteLose ||
                v == btnWhiteDraw ||
                v == btnBlackWin ||
                v == btnBlackLose ||
                v == btnBlackDraw) {
            onToggleClicked(v);
        }
        else if (v == btnScanCodeWhite) {
           onWhiteScanCodeClicked();
        }
        else if (v == btnChooseWhitePlayer) {
            onChooseWhitePlayerButtonClicked();
        }
        else if (v == btnScanCodeBlack) {
            onBlackScanCodeClicked();
        }
        else if (v == btnChooseWhitePlayer) {
            onChooseBlackPlayerButtonClicked();
        }
        else if (v == btnSubmit) {
            onSubmitButtonClicked();
        }

    }

    private void onChooseWhitePlayerButtonClicked() {

    }

    private void onChooseBlackPlayerButtonClicked() {

    }

    private void onSubmitButtonClicked() {
        Game gameResult = new Game();
        gameResult.setWhitePlayer(whitePlayer);
        gameResult.setBlackPlayer(blackPlayer);
        if (btnWhiteWin.isChecked())
            gameResult.setResult(Game.GAME_RESULT_WHITE_WIN);
        else if (btnBlackWin.isChecked())
            gameResult.setResult(Game.GAME_RESULT_BLACK_WIN);
        else
            gameResult.setResult(Game.GAME_RESULT_DRAW);

        WebAPIManager webAPIManager = new WebAPIManager(this);
        webAPIManager.sendGameResult(gameResult, this.getActivity());
    }

    @Override
    public void onPlayerInfoReturned(Player player) {
        if (whiteScanCodeWasClicked) {
            // Check to see if it's the same player as the black player
            if (blackPlayer != null && player.getPlayerID().equals(blackPlayer.getPlayerID()))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("The white player cannot be the same as the black player. Please choose a different player.");
                builder.setNeutralButton("Same Player", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });

                builder.show();
            }

            this.whitePlayer = player;
        }
        else
        {
            if (whitePlayer != null && player.getPlayerID().equals(whitePlayer.getPlayerID()))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Same Player");
                builder.setMessage("The black player cannot be the same as the white player. Please choose a different player.");
                builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Do nothing
                    }
                });

                builder.show();
            }

            this.blackPlayer = player;
        }

        updatePlayers();

        return;
    }

    @Override
    public void onSendGameResultFinished(Game gameResult, boolean success) {
        if (success)
        {
            mListener.onGameSent(gameResult);
        }
    }

    private void updatePlayers()
    {
        if (whitePlayer != null)
            txtWhiteName.setText(whitePlayer.getFirstName() + " " + whitePlayer.getLastName() + "\nGrade: " + whitePlayer.getGrade() + "\nRating: " + whitePlayer.getRating());
        else
            txtWhiteName.setText("Select a player");

        if (blackPlayer != null)
            txtBlackName.setText(blackPlayer.getFirstName() + " " + blackPlayer.getLastName() + "\nGrade: " + blackPlayer.getGrade() + "\nRating: " + blackPlayer.getRating());
        else
            txtBlackName.setText("Select a player");

        boolean areAnyChecked = false;

        if (btnWhiteWin.isChecked())
            areAnyChecked = true;
        if (btnWhiteLose.isChecked())
            areAnyChecked = true;
        if (btnWhiteDraw.isChecked())
            areAnyChecked = true;

        if (whitePlayer != null
                && blackPlayer != null
                && !whitePlayer.getPlayerID().equals(blackPlayer.getPlayerID())
                && areAnyChecked) {
            btnSubmit.setEnabled(true);
        }
        else
            btnSubmit.setEnabled(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        public void onCancelRecordGame();
        public void onGameSent(Game gameResult);
    }

}
