package com.qt.quoctrung.bluetooth__final__camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LogInActivity extends AppCompatActivity {
    ImageView img1,img2,img3,img4,img5,img6,img7,img8,img9,img0,img_sao,img_than, imgDelete;
    TextView txtPos1, txtPos2, txtPos3, txtPos4;
    String passwordDefault = "1235";



    String passWord = "";
    String a,b,c,d;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        initView();

        img1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number1 = img1.getTag().toString();
                checkPosition(number1);

            }
        });
        img2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number2 = img2.getTag().toString();
                checkPosition(number2);

            }
        });
        img3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number3 = img3.getTag().toString();
                checkPosition(number3);

            }
        });
        img4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number4 = img4.getTag().toString();
                checkPosition(number4);
            }
        });
        img5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number5 = img5.getTag().toString();
                checkPosition(number5);
            }
        });
        img6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number6 = img6.getTag().toString();
                checkPosition(number6);
            }
        });
        img7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number7 = img7.getTag().toString();
                checkPosition(number7);
            }
        });
        img8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number8 = img8.getTag().toString();
                checkPosition(number8);
            }
        });
        img9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number9 = img9.getTag().toString();
                checkPosition(number9);
            }
        });
        img0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String number0 = img0.getTag().toString();
                checkPosition(number0);
            }
        });
        imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(txtPos1.length() != 0){
                    txtPos1.setText(null);
                }
                else if(txtPos2.length() != 0){
                    txtPos2.setText(null);

                }
                else if(txtPos3.length() != 0){
                    txtPos3.setText(null);

                }
                else if(txtPos4.length() != 0){
                    txtPos4.setText(null);
                    passWord = "";
                }
                count--;
            }
        });


    }

    private void initView(){
        img1 = findViewById(R.id.imageView5);
        img2 = findViewById(R.id.imageView4);
        img3 = findViewById(R.id.imageView6);

        img4 = findViewById(R.id.imageView7);
        img5 = findViewById(R.id.imageView8);
        img6 = findViewById(R.id.imageView9);

        img7 = findViewById(R.id.imageView10);
        img8 = findViewById(R.id.imageView11);
        img9 = findViewById(R.id.imageView12);

        img0 = findViewById(R.id.imageView14);
        txtPos1 = findViewById(R.id.txtPos1);
        txtPos2 = findViewById(R.id.txtPos2);
        txtPos3 = findViewById(R.id.txtPos3);
        txtPos4 = findViewById(R.id.txtPos4);
        imgDelete = findViewById(R.id.imageView20);

    }

    int count = 0;

    private void checkPosition(String number){
        if(txtPos1.getText().length() == 0){
            txtPos1.setText(number);
            a = txtPos1.getText().toString().trim();
            txtPos1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            count++;
        }
        else if(txtPos2.getText().length() == 0){
            txtPos2.setText(number);
            b = txtPos2.getText().toString().trim();
            txtPos2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            count++;
        }
        else if(txtPos3.getText().length() == 0){
            txtPos3.setText(number);
            c = txtPos3.getText().toString().trim();
            txtPos3.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            count++;
        }
        else if(txtPos4.getText().length() == 0){
            txtPos4.setText(number);
            d = txtPos4.getText().toString().trim();
            txtPos4.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            count++;
        }
        if (count == 4) {
            new Handler().postDelayed(() -> getPassWord(), 1000);
        }
    }

    private void getPassWord(){
        passWord = a + b + c + d;
        if (passWord.length() == 4) {
            if (passWord.equals(passwordDefault)) {
                Intent intent = new Intent(LogInActivity.this, MainActivity.class);
                resetPassword();
                startActivity(intent);
            } else {
                Toast.makeText(this, "LogIn error", Toast.LENGTH_SHORT).show();
            }

        }

    }


    private void resetPassword() {
        count = 0;
        passWord = "";
        txtPos1.setText("");
        txtPos2.setText("");
        txtPos3.setText("");
        txtPos4.setText("");
    }

}