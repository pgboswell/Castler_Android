package com.castler.castler;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;

/**
 * Created by Paul on 6/19/2015.
 */
public class QRCodeScanner {

    public static final int REQUEST_CODE = 0x0000c0de; // Only use bottom 16 bits

    String contents = "";
    String formatName = "";
    byte[] rawBytes;
    int intentOrientation = 0;
    Integer orientation = 0;
    String errorCorrectionLevel = "";
    Fragment callingFragment2;

    public QRCodeScanner() {
    }

    public void scanQRCode(Fragment callingFragment)
    {
        callingFragment2 = callingFragment;
        // Start new CaptureActivity for the barcode scanner
        Intent intentScan = new Intent(callingFragment2.getActivity(), CaptureActivity.class);
        // The following makes it return after scanning a QR code
        intentScan.setAction(Intents.Scan.ACTION);
        // Allow only QR codes
        intentScan.putExtra("SCAN_FORMATS", "QR_CODE");

        callingFragment2.startActivityForResult(intentScan, REQUEST_CODE);
        //IntentIntegrator integrator = new IntentIntegrator(this);
        //integrator.setMessage("Castler requires Barcode Scanner. Would you like to install it?");
        //integrator.initiateScan();
    }

    public boolean parseQRCode(int requestCode, int resultCode, Intent intent)
    {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                contents = intent.getStringExtra("SCAN_RESULT");
                formatName = intent.getStringExtra("SCAN_RESULT_FORMAT");
                rawBytes = intent.getByteArrayExtra("SCAN_RESULT_BYTES");
                intentOrientation = intent.getIntExtra("SCAN_RESULT_ORIENTATION", Integer.MIN_VALUE);
                orientation = intentOrientation == Integer.MIN_VALUE ? null : intentOrientation;
                errorCorrectionLevel = intent.getStringExtra("SCAN_RESULT_ERROR_CORRECTION_LEVEL");

                return true;
            }

        }

        return false;
    }
}
