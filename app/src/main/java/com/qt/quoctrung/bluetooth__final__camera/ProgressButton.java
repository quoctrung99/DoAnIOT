package com.qt.quoctrung.bluetooth__final__camera;

import android.content.Context;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

public class ProgressButton {
    private CardView cardView;
    private ConstraintLayout layout;
    private ProgressBar progressBar;
    private TextView txtLogin;

    ProgressButton(Context ct, View view){
        cardView = view.findViewById(R.id.cardView2);
        layout   = view.findViewById(R.id.constraint_layout);
        progressBar = view.findViewById(R.id.progressBar);
        txtLogin = view.findViewById(R.id.textView2);

    }

    void buttonActivated(){
        progressBar.setVisibility(View.VISIBLE);
        txtLogin.setText("Please wait...");
    }

    void buttonFinished(){
        layout.setBackgroundColor(cardView.getResources().getColor(R.color.colorPrimary));
        progressBar.setVisibility(View.GONE);
        txtLogin.setText("Done");
    }

    void buttonNormal(){
        progressBar.setVisibility(View.GONE);
        layout.setBackgroundColor(cardView.getResources().getColor(R.color.card_view));
        layout.setBackgroundResource(R.drawable.custom_button);
        txtLogin.setText("Login");
    }


}
