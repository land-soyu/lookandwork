package com.soyu.qr_read;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class MainActivity extends AppCompatActivity {
    private MainActivity context;

    public static int REQUEST_QR_READ_DATA = 49374;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        Button bt_qrread = (Button)findViewById(R.id.bt_qrread);
        bt_qrread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentIntegrator intentIntegrator = new IntentIntegrator(context);
                intentIntegrator.setBeepEnabled(false);//바코드 인식시 소리
                intentIntegrator.setCaptureActivity(ScanActivity.class);
                intentIntegrator.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.e("!!!", "requestCode :: "+requestCode );
        Log.e("!!!", "resultCode :: "+resultCode );
    if (requestCode == REQUEST_QR_READ_DATA) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null && result.getContents() != null) {
            Log.e("!!!", "REQUEST_QR_READ_DATA :: "+result.getContents());
        }
    }
    }
}
