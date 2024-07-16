package com.abish.thesuitcase;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class GettingStartActivity extends AppCompatActivity implements View.OnClickListener{
    private Button startButton;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.getting_start);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(this);

        ConstraintLayout constraintLayout = findViewById(R.id.home_layout);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(10);
        animationDrawable.setExitFadeDuration(5000);
        animationDrawable.start();
    }
    @Override
    public void onClick(View view){
        if(view == startButton){
            Intent intent = new Intent(GettingStartActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
