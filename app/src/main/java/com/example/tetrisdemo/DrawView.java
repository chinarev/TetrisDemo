package com.example.tetrisdemo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import java.util.ArrayList;
import java.util.List;

class DrawView extends View {
    private final Paint paint = new Paint();
    Board board = new Board();
    private float curr_width;
    private float curr_height;
    private final boolean[][] field = new boolean[board.rows][board.cols];

    private int currentX;
    private int currentY;
    private List<Coordinate> blockCoordinates = new ArrayList<>();

    public DrawView(Context context) {
        super(context);
    }

    public DrawView(Context context, AttributeSet a) {
        super(context, a);
    }

    public DrawView(Context context, AttributeSet a, int b) {
        super(context, a, b);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(getResources().getColor(R.color.board_background));

        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 10; j++) {
                if (field[i][j]) {
                    this.paint.setColor(Color.RED);
                } else {
                    this.paint.setColor(this.getResources().getColor(R.color.cell));
                }

                this.drawRectAtPosition(i, j, canvas, paint);
            }
        }
    }

    @Override
    public void onSizeChanged(int width, int height, int old_width, int old_height) {
        super.onSizeChanged(width, height, old_width, old_height);
        this.curr_width = this.getMeasuredWidth();
        this.curr_height = this.getMeasuredHeight();
    }

    private void drawRectAtPosition(int i, int j, Canvas canvas, Paint paint) {
        float cell;
        float margin;
        cell = this.curr_height / 20;
        margin = this.curr_height / 200;

        float left = cell * j;
        float top = cell * i;
        float right = cell * (j + 1);
        float bottom = cell * (i + 1);
        RectF rectF = new RectF(left + margin, top + margin, right - margin, bottom - margin);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rectF, paint);
    }

    public void fallDown() {
        this.currentX += 1;
        for (Coordinate coordinate : this.blockCoordinates) {
            this.field[coordinate.getX()][coordinate.getY()] = false;
        }
        for (Coordinate coordinate : this.blockCoordinates) {
            coordinate.setX(coordinate.getX() + 1);
            this.field[coordinate.getX()][coordinate.getY()] = true;
        }

        this.invalidate();
    }

    public void moveLeft() {
        this.currentY -= 1;
        for (Coordinate coordinate : this.blockCoordinates) {
            this.field[coordinate.getX()][coordinate.getY()] = false;
        }
        for (Coordinate coordinate : this.blockCoordinates) {
            coordinate.setY(coordinate.getY() - 1);
            this.field[coordinate.getX()][coordinate.getY()] = true;
        }

        this.invalidate();
    }

    public void moveRight() {
        this.currentY += 1;
        for (Coordinate coordinate : this.blockCoordinates) {
            this.field[coordinate.getX()][coordinate.getY()] = false;
        }
        for (Coordinate coordinate : this.blockCoordinates) {
            coordinate.setY(coordinate.getY() + 1);
            this.field[coordinate.getX()][coordinate.getY()] = true;
        }

        this.invalidate();
    }

    private boolean checkCurrentCoordinates(Coordinate coordinate) {
        for (Coordinate new_coordinate: this.blockCoordinates) {
            if (coordinate.getX() == new_coordinate.getX() && coordinate.getY() == new_coordinate.getY()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkDownCollision() {
        for (Coordinate coordinate : this.blockCoordinates) {
            if (checkCurrentCoordinates(new Coordinate(coordinate.getX() + 1, coordinate.getY()))) {
                continue;
            } else if (coordinate.getX() == 19) {
                return false;
            } else if (this.field[coordinate.getX() + 1][coordinate.getY()]) {
                return false;
            }
        }

        return true;
    }

    public boolean checkLeftCollision() {
        for (Coordinate coordinate : this.blockCoordinates) {
            if (checkCurrentCoordinates(new Coordinate(coordinate.getX(), coordinate.getY() - 1))) {
                continue;
            } else if (coordinate.getY() == 0) {
                return false;
            } else if (this.field[coordinate.getX()][coordinate.getY() - 1]) {
                return false;
            }
        }

        return true;
    }

    public boolean checkRightCollision() {
        for (Coordinate coordinate : this.blockCoordinates) {
            if (checkCurrentCoordinates(new Coordinate(coordinate.getX(), coordinate.getY() + 1))) {
                continue;
            } else if (coordinate.getY() == 9) {
                return false;
            } else if (this.field[coordinate.getX()][coordinate.getY() + 1]) {
                return false;
            }
        }

        return true;
    }

    public boolean checkGameOver(int type) {
        for (Coordinate coordinate : Figure.getFigure(type, 0)) {
            if (this.field[coordinate.getX()][coordinate.getY() + 3]) {
                return true;
            }
        }

        return false;
    }

    public void reset() {
        for (int row = 0; row < 20; row++) {
            for (int column = 0; column < 10; column++) {
                this.field[row][column] = false;
            }
        }

        this.currentX = 0;
        this.currentY = 3;
        this.blockCoordinates.clear();
        this.invalidate();
    }

    public void pushBlock(int type) {
        this.currentX = 0;
        this.currentY = 3;
        this.blockCoordinates.clear();
        for (Coordinate coordinate : Figure.getFigure(type, 0)) {
            this.field[this.currentX + coordinate.getX()][this.currentY + coordinate.getY()] = true;
            this.blockCoordinates.add(new Coordinate(this.currentX + coordinate.getX(), this.currentY + coordinate.getY()));
        }
    }
}