package com.example.tetrisdemo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.softwarecountry.movesensegamelib.Api;
import com.softwarecountry.movesensegamelib.listeners.ChairListener;
import com.softwarecountry.movesensegamelib.listeners.ClockListener;
import com.softwarecountry.movesensegamelib.listeners.ConnectedListener;
import com.softwarecountry.movesensegamelib.listeners.Coordinates;
import com.softwarecountry.movesensegamelib.listeners.DataListener;
import com.softwarecountry.movesensegamelib.listeners.DirectionListener;
import com.softwarecountry.movesensegamelib.listeners.HrListener;
import com.softwarecountry.movesensegamelib.listeners.RawListener;
import com.softwarecountry.movesensegamelib.listeners.RotateListener;
import com.softwarecountry.movesensegamelib.listeners.RotateProgressListener;
import com.softwarecountry.movesensegamelib.listeners.RotateStickyListener;
import com.softwarecountry.movesensegamelib.listeners.SideListener;
import com.softwarecountry.movesensegamelib.listeners.SideProgressListener;

import org.jetbrains.annotations.Nullable;

public class MainActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private Api api;
    private ProgressBar progressBarScan;
    private TextView text;
    private Button buttonStartNewGame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        progressBarScan = findViewById(R.id.progressBarScan);

        View.OnClickListener onClickListenerStart = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        };

        buttonStartNewGame = findViewById(R.id.buttonStartNewGame);
        buttonStartNewGame.setOnClickListener(onClickListenerStart);
        text = findViewById(R.id.textView1);

    }

    private final Handler mainHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message message) {
            if (message.what == Constants.CONNECTION_ON) {
                progressBarScan.setVisibility(ProgressBar.INVISIBLE);
                buttonStartNewGame.setEnabled(true);
                text.setText("Connection established!");
            }
        }
    };

    public void onResume() {
        super.onResume();
        api = Api.getApi(this, new DataListener() {
            //Получение экзмепляра класса Api с переопределение его методов для получения слушателей
            @Nullable
            @Override
            public ConnectedListener getConnectedListener() {
                ConnectedListener connectedListener = new ConnectedListener() {
                    @Override
                    public void onConnected(boolean b) {
                        new Thread(() -> {
                            MainActivity.this.mainHandler.sendEmptyMessage(Constants.CONNECTION_ON);
                        }).start();
                    }
                };
                return connectedListener;
            }

            @Nullable
            @Override
            public HrListener getHrListener() {
                return null;
            }

            @Nullable
            @Override
            public RawListener getRawListener() {
                return null;
            }


            @Override
            public RotateListener getRotateListener() {
                RotateListener rotateListener = new RotateListener() {
                    @Override
                    public void onRotateRight() {
                        Intent intent = new Intent(MainActivity.this, GameActivity.class);
                        startActivity(intent);

                    }

                    @Override
                    public void onRotateLeft() {

                    }

                    @Override
                    public void onRotateDown() {

                    }

                    @Override
                    public void onRotateUp() {

                    }
                };
                return rotateListener;
            }

            @Nullable
            @Override
            public RotateProgressListener getRotateProgressListener() {
                return null;
            }

            @Nullable
            @Override
            public RotateStickyListener getRotateStickyListener() {
                return null;
            }

            @Nullable
            @Override
            public SideListener getSideListener() {
                return null;
            }

            @Nullable
            @Override
            public SideProgressListener getSideProgressListener() {
                return null;
            }

            @Nullable
            @Override
            public DirectionListener getDirectionListener() {
                return null;
            }

            @Nullable
            @Override
            public ClockListener getClockListener() {
                return null;
            }

            @Nullable
            @Override
            public ChairListener getChairListener() {
                return null;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        api.onRequestPermissionsResult(requestCode, grantResults);
    }

    @Override
    public void onPause() {
        super.onPause();
        api.onPause();
    }
}