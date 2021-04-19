package com.example.tetrisdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    Api api;
    private DrawView drawView;
    private NewBlockView nextBlockView;
    private boolean isGoing = false;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        initViews();

        View.OnClickListener onClickListenerLeft = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveLeft();
            }
        };

        View.OnClickListener onClickListenerRight = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveRight();
            }
        };

        this.findViewById(R.id.buttonLeft).setOnClickListener(onClickListenerLeft);
        this.findViewById(R.id.buttonRight).setOnClickListener(onClickListenerRight);

        runGame();
    }

    public void runGame() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.START)).start();
    }

    public void moveRight() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.RIGHT)).start();
    }

    public void moveLeft() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.LEFT)).start();
    }

    private void reset() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.RESET)).start();
    }

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constants.START:
                    GameActivity.this.isGoing = true;
                    GameActivity.this.generateNextBlock();
                    GameActivity.this.pushNextBlock();
                    GameActivity.this.setTimer();

                    break;

                case Constants.DEFAULT_DOWN:
                    if (GameActivity.this.drawView.checkDownCollision()) {
                        GameActivity.this.drawView.fallDown();
                    } else {
                        GameActivity.this.timer.cancel();
                        GameActivity.this.timer.purge();

                        if (GameActivity.this.drawView.checkGameOver(GameActivity.this.nextBlockView.getType())) {
                            GameActivity.this.reset();
                        } else {
                            GameActivity.this.pushNextBlock();
                            GameActivity.this.setTimer();
                        }
                    }
                    break;

                case Constants.LEFT:
                    if (GameActivity.this.drawView.checkLeftCollision()) {
                        GameActivity.this.drawView.moveLeft();
                    }
                    break;

                case Constants.RIGHT:
                    if (GameActivity.this.drawView.checkRightCollision()) {
                        GameActivity.this.drawView.moveRight();
                    }
                    break;

                case Constants.RESET:
                    GameActivity.this.isGoing = false;
                    GameActivity.this.timer.cancel();
                    GameActivity.this.timer.purge();
                    GameActivity.this.gameOver();

                    break;

                case Constants.RESET_BOARD:
                    GameActivity.this.drawView.reset();

                    break;

                default:
                    break;
            }
        }
    };

    private void initViews() {
        this.drawView = findViewById(R.id.game_activity);
        this.nextBlockView = findViewById(R.id.next_block);
    }

    private void generateNextBlock() {
        Random random = new Random();
        int randomNum = random.nextInt(7);
        this.nextBlockView.drawNextBlock(randomNum);
    }

    private void pushNextBlock() {
        this.drawView.pushBlock(GameActivity.this.nextBlockView.getType());
        this.drawView.invalidate();
        this.generateNextBlock();
    }

    private void setTimer() {
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameActivity.this.mHandler.sendEmptyMessage(Constants.DEFAULT_DOWN);
                System.gc();
            }
        }, 1000, 200);
    }

    private void gameOver() {
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                GameActivity.this.mHandler.sendEmptyMessage(Constants.RESET_BOARD);
                GameActivity.this.timer.cancel();
                GameActivity.this.timer.purge();
            }
        }, 0, 200);
    }

    public void onResume() {
        super.onResume();
        api = Api.getApi(this, new DataListener() {
            @Nullable
            @Override
            public ConnectedListener getConnectedListener() {
                return null;
            }

            @Nullable
            @Override
            public HrListener getHrListener() {
                return null;
            }

            @Nullable
            @Override
            public RawListener getRawListener() {
                RawListener rawListener = new RawListener() {
                    @Override
                    public void onGetAcc(@NotNull Coordinates coordinates) {
                        System.out.println("Coordinates: " + coordinates);
                    }

                    @Override
                    public void onGetVel(@NotNull Coordinates coordinates) {
                    }
                };
                return rawListener;
            }


            @Override
            public RotateListener getRotateListener() {
                RotateListener rotateListener = new RotateListener() {
                    @Override
                    public void onRotateRight() {
                        moveRight();

                    }

                    @Override
                    public void onRotateLeft() {
                        moveLeft();

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
                SideListener sideListener = new SideListener() {
                    @Override
                    public void onGetRight() {
                        moveRight();
                    }

                    @Override
                    public void onGetLeft() {
                        moveLeft();
                    }

                    @Override
                    public void onGetUp() {

                    }

                    @Override
                    public void onGetDown() {

                    }
                };
                return sideListener;
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
