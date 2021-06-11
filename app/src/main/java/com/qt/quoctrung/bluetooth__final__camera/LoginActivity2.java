package com.qt.quoctrung.bluetooth__final__camera;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity2 extends AppCompatActivity {
    View view;
    TextView txtSignUp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        view = findViewById(R.id.myProgress);
        txtSignUp = findViewById(R.id.txtSignUp);

        txtSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogCustomLogin(Gravity.CENTER);
            }
        });
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProgressButton progressButton = new ProgressButton(LoginActivity2.this, view);
                progressButton.buttonActivated();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressButton.buttonFinished();
                        Handler handler1 = new Handler();
                        handler1.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                                startActivity(intent);
                                progressButton.buttonNormal();
                            }
                        },350);
                    }
                },600);
            }
        });

    }

    private void dialogCustomLogin(int gravity){
        final Dialog dialog = new Dialog(LoginActivity2.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_create_account);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if(Gravity.CENTER == gravity){
            dialog.setCancelable(false);

        }else{
            dialog.setCancelable(true);
        }

        EditText edtEmail;
        Button btnExit;

        btnExit = dialog.findViewById(R.id.btnExit);
        btnExit.setOnClickListener(view -> {
            dialog.cancel();

        });
        edtEmail = dialog.findViewById(R.id.edtEmail_dialog);
        dialog.show();
    }
}