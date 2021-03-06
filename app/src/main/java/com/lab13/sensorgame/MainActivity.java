package com.lab13.sensorgame;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    public static final long REFRESH_DELAY = 50;

    private SensorManager sensorManager;
    private Sensor sensor;
    private SensorEventListener sensorListener;


    private int xMin, xMax;
    private int yMin, yMax;

    private int ballWidth, ballHeight;
    private float ballX, ballY;

    private double forceX, forceY;
    private double speedVector = 1.5;
    private boolean gameStarted = false;


    private Handler handler;
    private Runnable gameTick;

    private View ball;
    private Button btStart;
    private ViewGroup ltGame;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        ball = findViewById(R.id.ball);
        btStart = findViewById(R.id.btStart);
        ltGame = findViewById(R.id.ltGame);

        handler = new Handler();
        btStart.setOnClickListener(view -> startGame());
        gameTick = () -> {
            if (!gameStarted) return;
            ballX += forceX * speedVector;
            ballY += forceY * speedVector;
            ball.setX(ballX);
            ball.setY(ballY);
            if (isBallOutOfBounds()) {
                finishGame();
            } else {
                handler.postDelayed(gameTick, REFRESH_DELAY);
            }
        };

        initSensor();
    }

    void initSensor() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                forceX = -sensorEvent.values[0];
                forceY = sensorEvent.values[1];
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    private void startGame() {
        ballX = 300;
        ballY = 300;
        xMin = 0;
        yMin = 0;
        xMax = ltGame.getWidth();
        yMax = ltGame.getHeight();
        ballWidth = ball.getWidth();
        ballHeight = ball.getHeight();
        btStart.setVisibility(View.INVISIBLE);
        sensorManager.registerListener(sensorListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        handler.postDelayed(gameTick, REFRESH_DELAY);
        gameStarted = true;
    }

    private void finishGame() {
        btStart.setVisibility(View.VISIBLE);
        sensorManager.unregisterListener(sensorListener);
        handler.removeCallbacks(gameTick);
        gameStarted = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        finishGame();
    }

    private boolean isBallOutOfBounds() {
        return ballX < xMin || ballX + ballWidth > xMax
                || ballY < yMin || ballY + ballHeight > yMax;
    }
}
