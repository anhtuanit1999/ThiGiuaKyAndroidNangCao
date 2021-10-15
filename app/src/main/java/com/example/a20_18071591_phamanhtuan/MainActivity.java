package com.example.a20_18071591_phamanhtuan;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {
    private ImageView imgLoa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imgLoa = findViewById(R.id.imgLoa);
        Animation animation =
                AnimationUtils.loadAnimation(getApplicationContext(),
                        R.anim.aniblink);

        imgLoa.startAnimation(animation);
    }
}