package com.castler.castler;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends ActionBarActivity {

    ToggleButton btnWhiteWin = null;
    ToggleButton btnWhiteLose = null;
    ToggleButton btnWhiteDraw = null;

    ToggleButton btnBlackWin = null;
    ToggleButton btnBlackLose = null;
    ToggleButton btnBlackDraw = null;

    TextView txtWhiteName = null;

    String strWhiteName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWhiteWin = (ToggleButton) findViewById(R.id.btn_white_win);
        btnWhiteLose = (ToggleButton) findViewById(R.id.btn_white_lose);
        btnWhiteDraw = (ToggleButton) findViewById(R.id.btn_white_draw);
        btnBlackWin = (ToggleButton) findViewById(R.id.btn_black_win);
        btnBlackLose = (ToggleButton) findViewById(R.id.btn_black_lose);
        btnBlackDraw = (ToggleButton) findViewById(R.id.btn_black_draw);
        txtWhiteName = (TextView) findViewById(R.id.white_name);
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
    }

    public void onWhiteScanCodeClicked(View v) {

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setMessage("Castler requires Barcode Scanner. Would you like to install it?");
        integrator.initiateScan();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanResult != null) {
            txtWhiteName.setText(scanResult.getContents());

        }
        // else continue with any other code you need in the method
    }
}
