package com.elisoft.radiomovilclasico.Scanear;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;

import com.elisoft.radiomovilclasico.R;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.*;

public class QR_vehiculo extends AppCompatActivity implements me.dm7.barcodescanner.zxing.ZXingScannerView.ResultHandler {
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

        String placa=rawResult.getText().toString();
        SharedPreferences pedido = getSharedPreferences("ultimo_pedido", MODE_PRIVATE);

        String code=pedido.getString("placa","")+"clasico";
        code=Base64.encodeToString(code.getBytes(),  Base64.DEFAULT);
        code=code.replace("\n","");

        if(code.equals(placa.toString()))
        {
            String nombre=pedido.getString("marca","");
            String ci=pedido.getString("placa","");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("VEHICULO SEGURO.\n\nMARCA: "+nombre+".\nPLACA: "+ci);
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
            String ci=pedido.getString("placa","");
            AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
            dialogo1.setTitle(getString(R.string.app_name));
            dialogo1.setMessage("VEHICULO INSEGURO.\n\nPLACA: "+ci);
            dialogo1.setCancelable(false);
            dialogo1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogo1, int id) {
                    finish();
                }
            });

            dialogo1.show();
        }
    }

    public void toggleFlash(View v) {
        mFlash = !mFlash;
        mScannerView.setFlash(mFlash);
    }
}