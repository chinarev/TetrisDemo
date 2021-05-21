package com.example.tetrisdemo;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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
    private NextBlockView nextBlockView;
    private Timer timer;
    private int currentScore = 0;
    private TextView currScoreView;
    private ProgressBar progressBarSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBarSensors = findViewById(R.id.progressBarSensors);
        progressBarSensors.setVisibility(ProgressBar.VISIBLE);

        this.drawView = findViewById(R.id.game_activity);
        this.nextBlockView = findViewById(R.id.next_block);
        this.currScoreView = findViewById(R.id.curr_score_view);

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

        View.OnClickListener onClickListenerRotate = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rotate();
            }
        };

        View.OnClickListener onClickListenerDown = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveDown();
            }
        };

        this.findViewById(R.id.buttonLeft).setOnClickListener(onClickListenerLeft);
        this.findViewById(R.id.buttonRight).setOnClickListener(onClickListenerRight);
        this.findViewById(R.id.buttonRotate).setOnClickListener(onClickListenerRotate);
        this.findViewById(R.id.buttonDown).setOnClickListener(onClickListenerDown);

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

    private void moveDown() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.FAST_DOWN)).start();
    }

    private void rotate() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.ROTATE)).start();
    }

    private void reset() {
        new Thread(() -> GameActivity.this.mHandler.sendEmptyMessage(Constants.RESET)).start();

    }

    private final Handler mHandler = new Handler(Looper.myLooper()) {
        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constants.START:
                    GameActivity.this.generateNextBlock();
                    GameActivity.this.pushNextBlock();
                    GameActivity.this.setTimer();
                    break;

                case Constants.DEFAULT_DOWN:
                    if (GameActivity.this.drawView.checkDownCollision()) {
                        GameActivity.this.drawView.fallDown();
                    } else {
                        int count = GameActivity.this.drawView.checkRows();
                        GameActivity.this.currentScore += count * 100;
                        GameActivity.this.currScoreView.setText(Integer.toString(GameActivity.this.currentScore));

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

                case Constants.ROTATE:
                    if (GameActivity.this.drawView.checkRotate()) {
                        GameActivity.this.drawView.rotate();
                    }
                    break;

                case Constants.FAST_DOWN:
                    if (GameActivity.this.drawView.checkDownCollision()) {
                        GameActivity.this.drawView.fallDown();
                    } else {
                        GameActivity.this.drawView.checkRows();
                    }
                    break;

                case Constants.RESET:
                    GameActivity.this.timer.cancel();
                    GameActivity.this.timer.purge();
                    GameActivity.this.gameOver();
                    GameActivity.this.currentScore = 0;
                    GameActivity.this.currScoreView.setText("0");
                    break;

                case Constants.RESET_BOARD:
                    GameActivity.this.drawView.reset();
                    break;

                default:
                    break;
            }
        }
    };

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
        }, 1000, 500);
    }

    private void gameOver() {
        this.timer = new Timer();
        this.timer.scheduleAtFixedRate(new TimerTask() {
            int row = 19;

            @Override
            public void run() {
                if (this.row >= 0) {
                    Message message = new Message();
                    message.what = Constants.DETECT_FULL_ROW;
                    Bundle bundle = new Bundle();
                    bundle.putInt("row", row);
                    message.setData(bundle);
                    GameActivity.this.mHandler.sendMessage(message);

                    this.row--;
                } else {
                    GameActivity.this.mHandler.sendEmptyMessage(Constants.RESET_BOARD);

                    GameActivity.this.timer.cancel();
                    GameActivity.this.timer.purge();
                    runGame();
                }
            }
        }, 0, 100);
    }


    public static final double ALPHA = 0.2;
    double[] velInputX = new double[1_000_000];
    double[] velOutputX = new double[1_000_000];
    double[] velInputZ = new double[1_000_000];
    double[] velOutputZ = new double[1_000_000];

    protected double[] lowPass(double[] input, double[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    double initialSpeedX = 0;
    double newSpeedX = 0;
    double initialSpeedZ = 0;
    double newSpeedZ = 0;
    int countSpeed = 0;

    double newX = 0;
    double newZ = 0;
    double offsetOnX;
    double offsetOnZ;
    double initialOffestCoord;
    double dx;
    long dt = 0;

    double ftd;
    double std;
    long timeFirst;
    long timeRotated = 0;
    long timeMoved = 0;
    boolean sensorsReady = false;

    public double findOffset(double newOffsetCoordinate, double initialSpeed, double[] speedOut, int speedCount) {
        initialOffestCoord = newOffsetCoordinate;
        dx = (initialSpeed + speedOut[speedCount]) / 2 * dt;
        newOffsetCoordinate = (initialOffestCoord + dx) / 1000;
        return newOffsetCoordinate;
    }

    public void onResume() {
        super.onResume();
        api = Api.getApi(this, new DataListener() {
            @Nullable
            @Override
            public ConnectedListener getConnectedListener() {

                ConnectedListener connectedListener = new ConnectedListener() {
                    @Override
                    public void onConnected(boolean b) {
                        if (sensorsReady) {
                            progressBarSensors.setVisibility(ProgressBar.INVISIBLE);
                            runGame();
                        }
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

                RawListener rawListener = new RawListener() {
                    @Override
                    public void onGetAcc(@NotNull Coordinates coordinates) {

                        ftd = Math.atan((coordinates.getZ() * (-1)) / (Math.sqrt(Math.pow(coordinates.getX(), 2) + Math.pow(coordinates.getY(), 2)))) * Constants.rtd;
                        std = Math.atan(coordinates.getX() / coordinates.getY()) * Constants.rtd;

                    }

                    @Override
                    public void onGetVel(@NotNull Coordinates coordinates) {

                        if (coordinates.getX() != 0) {
                            sensorsReady = true;
                        }

                        timeFirst = System.nanoTime();

                        initialSpeedX = newSpeedX;
                        newSpeedX = coordinates.getX();
                        initialSpeedZ = newSpeedZ;
                        newSpeedZ = coordinates.getZ();
                        dt = System.nanoTime() - timeFirst;

                        velInputX[countSpeed] = coordinates.getX();
                        velOutputX = lowPass(velInputX, velOutputX);
                        velInputZ[countSpeed] = coordinates.getZ();
                        velOutputZ = velInputZ;

                        offsetOnX = findOffset(newX, initialSpeedX, velOutputX, countSpeed);
                        offsetOnZ = findOffset(newZ, initialSpeedZ, velOutputZ, countSpeed);

                        if (offsetOnX > 15 && std < -2 && (System.currentTimeMillis() - timeMoved > 200)) {
                            moveLeft();
                            timeMoved = System.currentTimeMillis();
                        }

                        if (offsetOnX < -15 && std > 2 && (System.currentTimeMillis() - timeMoved > 200)) {
                            moveRight();
                            timeMoved = System.currentTimeMillis();
                        }

                        if (offsetOnZ < -10 && ftd < -10 && (System.currentTimeMillis() - timeRotated > 1000)) {
                            rotate();
                            timeRotated = System.currentTimeMillis();
                        }

                        if (offsetOnZ > 15 && ftd > 25) {
                            moveDown();
                        }

                        countSpeed++;


                    }
                };
                return rawListener;
            }


            @Override
            public RotateListener getRotateListener() {
                RotateListener rotateListener = new RotateListener() {
                    @Override
                    public void onRotateRight() {
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
                return null;
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
                    }

                    @Override
                    public void onGetLeft() {
                    }

                    @Override
                    public void onGetUp() {
                    }

                    @Override
                    public void onGetDown() {
                    }
                };
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

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        api.onRequestPermissionsResult(requestCode, grantResults);
//    }

    @Override
    public void onPause() {
        super.onPause();
        api.onPause();
    }

}
