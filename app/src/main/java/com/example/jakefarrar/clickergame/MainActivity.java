package com.example.jakefarrar.clickergame;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.view.animation.Animation;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    TextView timerTextView;
    private JSONObject clickerSetup;
    long startTime = 0, gameTime, secondUpdate;
    TextView scoreText;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;

            timerTextView.setText(String.format("%d:%02d", minutes, seconds));
            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String clickerGameState = loadJSON();
        try {
            clickerSetup = new JSONObject(clickerGameState);
            gameTime = clickerSetup.getInt("time");
            secondUpdate = clickerSetup.getInt("plus10") * 10;
            secondUpdate += clickerSetup.getInt("plus20") * 20;
            secondUpdate += clickerSetup.getInt("plus50") * 50;
        }
        catch (JSONException e) {
            Log.d("JSON", "Failed to load json file");
            Log.d("JSON", e.getMessage());
        }


        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.scale);
        scoreText = findViewById(R.id.textView_score);
        try {
            scoreText.setText(String.format(Locale.US, "%d", clickerSetup.getInt("score")));
        }
        catch (JSONException e) {
            Log.d("JSON", "Unable to get score");
        }

        timerTextView = findViewById(R.id.timerTextView);
        startTime = System.currentTimeMillis() - gameTime;
        timerHandler.postDelayed(timerRunnable, 0);


        ImageButton targetButton = findViewById(R.id.imageButton4);
        ImageButton shopButton = findViewById(R.id.imageButton2);

        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                timerHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        String currentScoreString = scoreText.getText().toString();
                        int currentScoreInt = Integer.parseInt(currentScoreString);
                        currentScoreInt += secondUpdate;
                        scoreText.setText(Integer.toString(currentScoreInt));
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(timerTask, 1000, 1000);

        targetButton.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                v.startAnimation(animScale);
                String currentScoreString = scoreText.getText().toString();
                int currentScoreInt = Integer.parseInt(currentScoreString);
                currentScoreInt += 1;
                scoreText.setText(Integer.toString(currentScoreInt));
            }
        });

        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toShop();
            }
        });
    }

    private void toShop() {
        Intent intent = new Intent(this, Store.class);
        intent.putExtra("score", scoreText.getText());
        writeState(clickerSetup);
        startActivity(intent);
        this.finish();
    }

    protected void writeState(JSONObject clickerSetup) {
        String time = (String) timerTextView.getText();
        String minutes = time.substring(0, 1);
        String seconds = time.substring(2);

        long totalTime = ((Integer.parseInt(minutes) * 60) + (Integer.parseInt(seconds))) * 1000;

        JSONObject newObject = new JSONObject();
        try {
            newObject.put("score", scoreText.getText());
            newObject.put("time", totalTime);
            newObject.put("plus10", clickerSetup.getInt("plus10"));
            newObject.put("plus20", clickerSetup.getInt("plus20"));
            newObject.put("plus50", clickerSetup.getInt("plus50"));
        } catch(JSONException e) {
            Log.d("ERROR", e.getMessage());
        }
        writeJSON(newObject);

    }

    private void writeJSON(JSONObject json) {
        try {
            FileWriter file = new FileWriter(getFilesDir() + "/clickerGame.json");
            Log.d("FILEPATH", getFilesDir() + "");
            Log.d("WRITING", json.toString());
            file.write(json.toString());
            file.close();
        } catch (Exception e) {
            Log.d("ERROR", e.getLocalizedMessage());
        }
    }

    public String loadJSON() {
        String json = null;
        InputStream is = null;
        Log.d("FILE LOCAL", getFilesDir().toString());

        try {
            File file = new File(getFilesDir() + "/clickerGame.json");
            is = new FileInputStream(file);
            Log.d("READ", "Reading from local");
        } catch (IOException e1) {
            try {
                is = getAssets().open("clickerGame.json");
                Log.d("READ", "Reading from assets");
            } catch (IOException e2) {
                e2.printStackTrace();
                return null;
            }
        }

        try {
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException e3) {
            Log.d("ERROR", e3.getMessage());
        }
        return json;
    }
}
