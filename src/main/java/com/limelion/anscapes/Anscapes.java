package com.limelion.anscapes;

import java.awt.Color;
import java.io.IOException;

public class Anscapes {

    public static final String CSI = "\033[",
        RESET = CSI + "m",
        CLEAR = CSI + "2J",
        CLEAR_BUFFER = CSI + "3J",
        RESET_CURSOR = CSI + "H",
        CLEAR_LINE = CSI + "2K",
        MOVE_UP = CSI + "A",
        MOVE_DOWN = CSI + "B",
        MOVE_RIGHT = CSI + "C",
        MOVE_LEFT = CSI + "D",
        MOVE_LINEUP = CSI + "E",
        MOVE_LINEDOWN = CSI + "F";

    public static String moveUp(int n) {

        return CSI + n + "A";
    }

    public static String moveDown(int n) {

        return CSI + n + "B";
    }

    public static String moveRight(int n) {

        return CSI + n + "C";
    }

    public static String moveLeft(int n) {

        return CSI + n + "D";
    }

    public static String moveNextLine(int n) {

        return CSI + n + "E";
    }

    public static String movePreviousLine(int n) {

        return CSI + n + "F";
    }

    public static String cursorPos(int n, int m) {

        return CSI + n + ";" + m + "H";
    }

    public static CursorPos cursorPos() {

        System.out.print(CSI + "6n");
        try {
            System.in.read();
            System.in.read();
            int read = -1;
            StringBuilder row = new StringBuilder();
            while ((read = System.in.read()) != ';') {
                row.append((char) read);
            }
            StringBuilder col = new StringBuilder();
            while ((read = System.in.read()) != 'R') {
                col.append((char) read);
            }
            return new CursorPos(row.length() > 0 ? Integer.parseInt(row.toString()) : 1,
                                 col.length() > 0 ? Integer.parseInt(col.toString()) : 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static class CursorPos {

        public int row;
        public int col;

        public CursorPos(int row, int col) {

            this.row = row;
            this.col = col;
        }
    }
}
