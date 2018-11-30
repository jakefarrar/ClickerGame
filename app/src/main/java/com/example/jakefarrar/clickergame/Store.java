package com.example.jakefarrar.clickergame;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class Store extends AppCompatActivity {

    protected Intent gameIntent;
    protected TextView pointsView;
    protected long time, points;
    protected JSONObject clickerSetup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);

        gameIntent = getIntent();
        String clickerGameState = loadJSON();
        try {
            clickerSetup = new JSONObject(clickerGameState);
        }
        catch (JSONException e) {
            Log.d("JSON", e.getMessage());
        }

        pointsView = findViewById(R.id.textView7);
        points = Integer.parseInt(gameIntent.getStringExtra("score"));
        time = gameIntent.getLongExtra("time", 0);
        String pointsStr = "Points: " + gameIntent.getStringExtra("score");
        pointsView.setText(pointsStr);

        Button toMain = findViewById(R.id.button3);

        toMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                backToGame();
            }
        });

        ImageButton plus10Up, plus20Up, plus50Up, plus10Down, plus20Down, plus50Down;
        final TextView plus10Count, plus20Count, plus50Count;
        plus10Count = findViewById(R.id.plus10Count);
        plus20Count = findViewById(R.id.plus20Count);
        plus50Count = findViewById(R.id.plus50Count);

        plus10Up = findViewById(R.id.plus10Up);
        plus10Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus10Count.getText().toString());
                count++;
                plus10Count.setText(String.format(Locale.US, "%d", count));
                updateTotal();
            }
        });

        plus10Down = findViewById(R.id.plus10Down);
        plus10Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus10Count.getText().toString());
                if (count > 0){
                    count--;
                    plus10Count.setText(String.format(Locale.US, "%d", count));
                    updateTotal();
                }
            }
        });

        plus20Up = findViewById(R.id.plus20Up);
        plus20Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus20Count.getText().toString());
                count++;
                plus20Count.setText(String.format(Locale.US, "%d", count));
                updateTotal();
            }
        });

        plus20Down = findViewById(R.id.plus20Down);
        plus20Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus20Count.getText().toString());
                if (count > 0){
                    count--;
                    plus20Count.setText(String.format(Locale.US, "%d", count));
                    updateTotal();
                }
            }
        });

        plus50Up = findViewById(R.id.plus50Up);
        plus50Up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus50Count.getText().toString());
                count++;
                plus50Count.setText(String.format(Locale.US, "%d", count));
                updateTotal();
            }
        });

        plus50Down = findViewById(R.id.plus50Down);
        plus50Down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int count = Integer.parseInt(plus50Count.getText().toString());
                if (count > 0){
                    count--;
                    plus50Count.setText(String.format(Locale.US, "%d", count));
                }
                updateTotal();
            }
        });

        Button buyButton = findViewById(R.id.buyButton);
        buyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                TextView totalCost = findViewById(R.id.totalCost);
                String costStr = (String) totalCost.getText();
                int cost = Integer.parseInt(costStr.substring(6));

                if (cost <= points) {
                    points -= cost;
                    int currentPlus10, currentPlus20, currentPlus50;

                    TextView plus10Count, plus20Count, plus50Count;
                    plus10Count = findViewById(R.id.plus10Count);
                    plus20Count = findViewById(R.id.plus20Count);
                    plus50Count = findViewById(R.id.plus50Count);

                    int plus10CountInt, plus20CountInt, plus50CountInt, total;
                    plus10CountInt = Integer.parseInt((String) plus10Count.getText());
                    plus20CountInt = Integer.parseInt((String) plus20Count.getText());
                    plus50CountInt = Integer.parseInt((String) plus50Count.getText());

                    try {
                        currentPlus10 = clickerSetup.getInt("plus10");
                        currentPlus20 = clickerSetup.getInt("plus20");
                        currentPlus50 = clickerSetup.getInt("plus50");

                        clickerSetup.put("plus10", currentPlus10 + plus10CountInt);
                        clickerSetup.put("plus20", currentPlus20 + plus20CountInt);
                        clickerSetup.put("plus50", currentPlus50 + plus50CountInt);
                    }
                    catch (JSONException e) {
                        Log.d("JSON", e.getMessage());
                    }
                    writeState(clickerSetup);
                    Toast.makeText(view.getContext(), "Purchase successful!",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(view.getContext(), "Unable to purchase the selected items!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void updateTotal() {
        TextView totalCost = findViewById(R.id.totalCost);

        TextView plus10Count, plus20Count, plus50Count;
        plus10Count = findViewById(R.id.plus10Count);
        plus20Count = findViewById(R.id.plus20Count);
        plus50Count = findViewById(R.id.plus50Count);

        int plus10CountInt, plus20CountInt, plus50CountInt, total;
        plus10CountInt = Integer.parseInt((String) plus10Count.getText());
        plus20CountInt = Integer.parseInt((String) plus20Count.getText());
        plus50CountInt = Integer.parseInt((String) plus50Count.getText());

        total = (plus10CountInt * 1000) + (plus20CountInt * 2000) + (plus50CountInt * 10000);

        totalCost.setText(String.format(Locale.US, "Cost: %d", total));
    }

    private void backToGame() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    protected void writeState(JSONObject clickerSetup) {
        JSONObject newObject = new JSONObject();
        try {
            newObject.put("score", points);
            newObject.put("time", clickerSetup.getLong("time"));
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
