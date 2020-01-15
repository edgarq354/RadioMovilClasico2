package com.elisoft.radiomovilclasico.Scanear;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.elisoft.radiomovilclasico.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.*;

public class QR_conductor extends AppCompatActivity implements me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler {
    private static final String FLASH_STATE = "FLASH_STATE";

    private me.dm7.barcodescanner.zxing.ZXingScannerView mScannerView;
    private boolean mFlash;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_scaling_scanner);

        ViewGroup contentFrame = (ViewGroup) findViewById(R.id.content_frame);
        mScannerView = new me.dm7.barcodescanner.zxing.ZXingScannerView(this);
        contentFrame.addView(mScannerView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return super.onSupportNavigateUp();
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
        mScannerView.setFlash(mFlash);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(FLASH_STATE, mFlash);
    }

    @Override
    public void handleResult(Result rawResult) {
        Log.e("Countents", rawResult.getText());
        Log.e("Format",rawResult.getBarcodeFormat().toString());

        //verificar si es el mismo numero.
        String id_conductor=rawResult.getText().toString();
        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

        String code=pedido.getString("id_taxi","")+"radiomovilclasico";
        code= Base64.encodeToString(code.getBytes(),  Base64.DEFAULT);

        if(code.equals(id_conductor))
        {
            String nombre=pedido.getString("nombre_taxi","");
            String ci=pedido.getString("id_taxi","");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("\nCONDUCTOR ACTIVO.\n\nNOMBRE: "+ Html.fromHtml("<b>"+nombre+".</b>")+"\nCi: "+ Html.fromHtml("<b>"+ci+".</b>"));
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });

            dialogo1.show();
        }
        else
        {
            String ci=pedido.getString("id_taxi","");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("\nCONDUCTOR INACTIVO \n\nPorfavor reporte del conductor.\n\nCi: "+ Html.fromHtml("<b>"+ci+"</b>"));
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });

            dialogo1.show();
        }
        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
      /*  Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(QR_conductor.this);
            }
        }, 2000);
        */
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }
}