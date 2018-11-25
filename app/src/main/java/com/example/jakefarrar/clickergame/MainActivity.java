package com.example.jakefarrar.clickergame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        final TextView scoreText = findViewById(R.id.textView_score);

        ImageButton targetButton = findViewById(R.id.imageButton4);

        targetButton.setOnClickListener(
                new Button.OnClickListener()
                {
                    public void onClick(View v)
                    {
                        v.startAnimation(animScale);
                        String currentScoreString = scoreText.getText().toString();
                        int currentScoreInt = Integer.parseInt(currentScoreString);
                        currentScoreInt += 1;
                        scoreText.setText(Integer.toString(currentScoreInt));
                    }
                }
        );
    }
}
