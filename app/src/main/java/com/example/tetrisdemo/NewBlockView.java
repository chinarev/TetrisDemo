package com.example.tetrisdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

public class NewBlockView extends View {
    private final Paint paint = new Paint();
    private float curr_width;
    private float curr_height;
    private int type;

    private boolean[][] field = new boolean[2][4];

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            switch (message.what) {
                case Constants.DRAW_BLOCK:
                    NewBlockView.this.invalidate();
                    break;

                default:
                    break;
            }
        }
    };

    public NewBlockView(Context context) {
        super(context);
    }

    public NewBlockView(Context context, AttributeSet a) {
        super(context, a);
    }

    public NewBlockView(Context context, AttributeSet a, int b) {
        super(context, a, b);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.board_background));

        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 4; j++) {
                if (field[i][j]) {
                    this.paint.setColor(Color.RED);
                } else {
                    this.paint.setColor(this.getResources().getColor(R.color.cell));
                }

                this.drawRectAtPosition(i, j, canvas, paint);
            }
        }
    }

    private void drawRectAtPosition(int i, int j, Canvas canvas, Paint paint) {
        float left = this.curr_width * j / 4;
        float top = this.curr_width * i / 4;
        float right = this.curr_width * (j + 1) / 4;
        float bottom = this.curr_width * (i + 1) / 4;
        float margin = this.curr_width / 40;
        RectF rectF = new RectF(left + margin, top + margin, right - margin, bottom - margin);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
    }

    @Override
    public void onSizeChanged(int width, int height, int old_width, int old_height) {
        super.onSizeChanged(width, height, old_width, old_height);
        this.curr_width = this.getMeasuredWidth();
        this.curr_height = this.getMeasuredHeight();
    }

    public void drawNextBlock(int nextBlockType) {
        this.type = nextBlockType;

        switch (nextBlockType) {
            case 0:
                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {true, true, false, false},
                            {true, true, false, false}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 1:
                this.type = 1;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {false, false, true, false},
                            {true, true, true, false}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 2:
                this.type = 2;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {true, false, false, false},
                            {true, true, true, false}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 3:
                this.type = 3;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {false, true, true, false},
                            {true, true, false, false}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 4:
                this.type = 4;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {true, true, false, false},
                            {false, true, true, false}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 5:
                this.type = 5;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {false, false, false, false},
                            {true, true, true, true}
                    };
                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();

                break;

            case 6:
                this.type = 6;

                new Thread(() -> {
                    NewBlockView.this.field = new boolean[][]{
                            {false, true, false, false},
                            {true, true, true, false}
                    };

                    NewBlockView.this.handler.sendEmptyMessage(Constants.DRAW_BLOCK);
                }).start();
                break;

            default:
                break;
        }
    }

    public int getType() {
        return this.type;
    }
}
