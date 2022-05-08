package com.lookandwork.together_v;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginActivity extends AppCompatActivity {
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        context = this;

        Button loginbt = (Button) findViewById(R.id.loginbt);
        loginbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText idet = (EditText)findViewById(R.id.idet);
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra("id", idet.getText().toString());
                startActivity(intent);
                finish();
            }
        });




    }
}
