package com.castler.castler;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.Menu;
import android.view.MenuItem;

//import com.google.zxing.integration.android.IntentIntegrator;
//import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends AppCompatActivity implements ScanSessionCodeFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener, RecordGameFragment.OnFragmentInteractionListener{

    public ChessSession chessSession = null;
    HomeFragment homeFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Add the first fragment into the layout
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        ScanSessionCodeFragment fragment = new ScanSessionCodeFragment();
        fragmentTransaction.add(R.id.main_layout, fragment);
        fragmentTransaction.commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSessionScanned(String clubName, String sessionID) {
        chessSession = new ChessSession();
        chessSession.setClubName(clubName);
        chessSession.setSessionID(sessionID);

        // Switch to the home fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);

        homeFragment = new HomeFragment();
        fragmentTransaction.replace(R.id.main_layout, homeFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onRecordNewGameClicked() {
        // Switch to the record new game fragment
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left, R.animator.slide_in_left, R.animator.slide_out_right);

        RecordGameFragment fragment = new RecordGameFragment();
        fragmentTransaction.replace(R.id.main_layout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onCancelSession() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onCancelRecordGame() {
        // Switch to the home fragment
        /*FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.animator.slide_in_left, R.animator.slide_out_right);

        fragmentManager.popBackStack();
        //fragmentTransaction.replace(R.id.main_layout, homeFragment);
        fragmentTransaction.commit();*/
    }

    @Override
    public void onGameSent(Game gameResult) {
        // Game was successfully sent. Go back to the previous fragment.
        getFragmentManager().popBackStack();

        homeFragment.addGameResult(gameResult);
    }

    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            this.finish();
        }
    }
}
