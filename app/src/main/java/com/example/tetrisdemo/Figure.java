package com.example.tetrisdemo;

import java.util.ArrayList;
import java.util.List;

public class Figure {
    public static List<Coordinate> getFigure(int type, int rotateTimes) {
        List<Coordinate> figureCoordinates = new ArrayList<>();

        switch (type) {
            case 0:
                figureCoordinates.add(new Coordinate(0, 0));
                figureCoordinates.add(new Coordinate(0, 1));
                figureCoordinates.add(new Coordinate(1, 0));
                figureCoordinates.add(new Coordinate(1, 1));

                break;

            case 1:
                switch (rotateTimes) {
                    case 0:
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(1, 2));

                        break;

                    case 1:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 1));

                        break;

                    case 2:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(1, 0));

                        break;

                    case 3:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(2, 0));
                        figureCoordinates.add(new Coordinate(2, 1));

                        break;

                    default:
                        break;
                }

                break;

            case 2:
                switch (rotateTimes) {
                    case 0:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(1, 2));

                        break;

                    case 1:
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 0));
                        figureCoordinates.add(new Coordinate(2, 1));

                        break;

                    case 2:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(1, 2));

                        break;

                    case 3:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(2, 0));

                        break;

                    default:
                        break;
                }

                break;

            case 3:
                switch (rotateTimes) {
                    case 0:case 2:
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));

                        break;

                    case 1:case 3:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 1));

                        break;

                    default:
                        break;
                }

                break;

            case 4:
                switch (rotateTimes) {
                    case 0:case 2:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(1, 2));

                        break;

                    case 1:case 3:
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 0));

                        break;

                    default:
                        break;
                }

                break;

            case 5:
                switch (rotateTimes) {
                    case 0:case 2:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(0, 3));

                        break;

                    case 1:case 3:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(2, 0));
                        figureCoordinates.add(new Coordinate(3, 0));

                        break;

                    default:
                        break;
                }

                break;

            case 6:
                switch (rotateTimes) {
                    case 0:
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(1, 2));

                        break;

                    case 1:
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 1));

                        break;

                    case 2:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(0, 1));
                        figureCoordinates.add(new Coordinate(0, 2));
                        figureCoordinates.add(new Coordinate(1, 1));

                        break;

                    case 3:
                        figureCoordinates.add(new Coordinate(0, 0));
                        figureCoordinates.add(new Coordinate(1, 0));
                        figureCoordinates.add(new Coordinate(1, 1));
                        figureCoordinates.add(new Coordinate(2, 0));

                        break;

                    default:
                        break;
                }

                break;

            default:
                break;
        }

        return figureCoordinates;
    }

}
