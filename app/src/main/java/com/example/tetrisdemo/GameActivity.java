package com.example.tetrisdemo;

import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends AppCompatActivity {

    Api api;
    private DrawView drawView;
    private NewBlockView nextBlockView;
    private Timer timer;
    private int currentScore = 0;
    private TextView curr_score_view;
    private ProgressBar progressBarSensors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        progressBarSensors = findViewById(R.id.progressBarSensors);
        progressBarSensors.setVisibility(ProgressBar.VISIBLE);

        this.drawView = findViewById(R.id.game_activity);
        this.nextBlockView = findViewById(R.id.next_block);
        this.curr_score_view = findViewById(R.id.curr_score_view);

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
        new Thread(new Runnable() {
            @Override
            public void run() {
                GameActivity.this.mHandler.sendEmptyMessage(Constants.FAST_DOWN);
            }
        }).start();
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
                        int count = GameActivity.this.drawView.checkRowsCollision();
                        GameActivity.this.currentScore += count * 100;
                        GameActivity.this.curr_score_view.setText(Integer.toString(GameActivity.this.currentScore));

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
                        GameActivity.this.drawView.checkRowsCollision();
                    }
                    break;

                case Constants.RESET:
                    GameActivity.this.timer.cancel();
                    GameActivity.this.timer.purge();
                    GameActivity.this.gameOver();
                    GameActivity.this.currentScore = 0;
                    GameActivity.this.curr_score_view.setText("0");
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
        }, 1000, 400);
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
        }, 0, 200);
    }

    static final float ALPHA = 0.2f;

    int count = 0;
    double[] vel_input_x = new double[1_000_000];
    double[] vel_out_x = new double[1_000_000];
    double[] vel_input_z = new double[1_000_000];
    double[] vel_out_z = new double[1_000_000];

    protected double[] lowPass(double[] input, double[] output) {
        if (output == null) return input;

        for (int i = 0; i < input.length; i++) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
            //output.add(i, (output.get(i) + ALPHA * (input.get(i) - output.get(i))));
        }
        return output;
    }

    protected ArrayList<Double> lowPass2(ArrayList<Double> input, ArrayList<Double> output) {
        if (output == null) return input;

        for (int i = 0; i < input.size(); i++) {
            //output[i] = output[i] + ALPHA * (input[i] - output[i]);
            output.add(i, (output.get(i) + ALPHA * (input.get(i) - output.get(i))));
        }
        return output;
    }

    double bias;
    boolean wasRotated = false;
    boolean wasMoved = false;

    double initial_speed = 0;
    double new_speed = 0;
    ArrayList<Long> arr_time = new ArrayList<>();
    long time;
    int count_speed = 0;
    double[] acc_x = new double[1_000_000];
    double[] acc_x_with_Kalman = new double[1_000_000];
    double acc;
    //double[] initial_x = new double[1_000_000];
    //double[] new_x = new double[1_000_000];
    double initial_x = 0;
    double new_x = 0;
    //ArrayList<Double> initial_x = new ArrayList<>();
    //ArrayList<Double> new_x = new ArrayList<>();
    double dx;
    long dt = 0;

    ArrayList<Double> mass_x = new ArrayList<>();
    ArrayList<Double> mass_x_with_lowFilter = new ArrayList<>();
    boolean sensorsReady = false;

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
                        time = 0;
                        if (coordinates.getX() != 0) {
                            sensorsReady = true;
                        }

                        arr_time.add(System.currentTimeMillis());
                        if (count_speed == 0) mass_x_with_lowFilter.add(0.0);

                        //if (dt != 0) {
                        initial_speed = new_speed;
                        //acc_x.add(coordinates.getX());
                        acc_x[count_speed] = coordinates.getX();
                        acc_x_with_Kalman = lowPass(acc_x, acc_x_with_Kalman);
                        System.out.println("acc_x_with_Kalman : " + acc_x_with_Kalman[count_speed]);
                        System.out.println("acc_x" + acc_x[count_speed]);
                        new_speed = initial_speed + acc_x_with_Kalman[count_speed] * dt;
                        //new_speed = initial_speed + coordinates.getX() * dt;

                        System.out.println("initial speed: " + initial_speed);
                        System.out.println("new speed: " + new_speed);
                        System.out.println("time mass: " + arr_time);
                        System.out.println("dt :" + dt);

                        initial_x = new_x;
                        dx = (initial_speed + new_speed) / 2 * dt;
                        new_x = (initial_x + dx) / 1000;
                        //new_x = (initial_x + dx);
                        mass_x.add(new_x);

                        //mass_x_with_lowFilter = lowPass2(mass_x, mass_x_with_lowFilter);
                        System.out.println("X: " + mass_x);
                        //System.out.println("x_filter: " + mass_x_with_lowFilter);
//                        if (mass_x.get(count_speed) > 30) {
//                            moveRight();
//                        }
//                        if (mass_x.get(count_speed) < -30) {
//                            moveLeft();
//                        }
                        //}


                        if (count_speed != 0) {
                            dt = arr_time.get(count_speed) - arr_time.get(count_speed - 1);
                        }

                        count_speed++;


                        bias = Math.atan((coordinates.getZ() * (-1)) / (Math.sqrt(Math.pow(coordinates.getX(), 2) + Math.pow(coordinates.getY(), 2)))) * (180 / Math.PI);
                        if (bias > 30 && !wasRotated) {
                            rotate();
                            wasRotated = true;
                            return;
                        }

                        wasRotated = false;
                    }

                    @Override
                    public void onGetVel(@NotNull Coordinates coordinates) {

                        vel_input_x[count] = coordinates.getX();
                        vel_out_x = lowPass(vel_input_x, vel_out_x);

                        if (vel_out_x[count] > 2 && !wasMoved) {
                            moveLeft();
                            wasMoved = true;
                            return;
                        }

                        if (vel_out_x[count] < -2 && !wasMoved) {
                            moveRight();
                            wasMoved = true;
                            return;
                        }

                        wasMoved = false;

                        if (vel_out_z[count] < -10) {
                            rotate();
                        }

//                        if (count != 0) {
//                            time_1 = (vel_input_z[count] - vel_input_z[count - 1]) / acc_storage_X.get(count);
//                            S = vel_input_z[count] * time_1 + 0.5 * acc_storage_X.get(count) * time_1 * time_1;
//                            arr_S.add(S);
//                        }

                        count++;


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
