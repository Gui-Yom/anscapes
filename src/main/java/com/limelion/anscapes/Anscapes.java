package com.limelion.anscapes;

import java.io.IOException;

public class Anscapes {

    private static final String CSI = "\033[";
    public static final String RESET = CSI + "0m";
    public static final String CLEAR = CSI + "H" + CSI + "2J";

    public static void clear() {

        write(CLEAR);
    }

    public static void reset() {

        write(RESET);
    }

    public static void moveUp(int n) {

        write(Move.UP.n(n));
    }

    public static void moveDown(int n) {

        write(Move.DOWN.n(n));
    }

    public static void moveRight(int n) {

        write(Move.RIGHT.n(n));
    }

    public static void moveLeft(int n) {

        write(Move.LEFT.n(n));
    }

    public static void setCursorPos(int n, int m) {

        write(CSI + n + ";" + m + "H");
    }

    public static void clearLine() {

        write(CSI + "2K");
    }

    private static void write(String s) {

        try {
            System.out.write(s.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            System.out.flush();
        }
    }

    public enum Colors {

        FG_BLACK(30),
        FG_RED(31),
        FG_GREEN(32),
        FG_YELLOW(33),
        FG_BLUE(34),
        FG_MAGENTA(35),
        FG_CYAN(36),
        FG_WHITE(37),

        FG_BLACK_BRIGHT(FG_BLACK, 60),
        FG_RED_BRIGHT(FG_RED, 60),
        FG_GREEN_BRIGHT(FG_GREEN, 60),
        FG_YELLOW_BRIGHT(FG_YELLOW, 60),
        FG_BLUE_BRIGHT(FG_BLUE, 60),
        FG_MAGENTA_BRIGHT(FG_MAGENTA, 60),
        FG_CYAN_BRIGHT(FG_CYAN, 60),
        FG_WHITE_BRIGHT(FG_WHITE, 60),

        BG_BLACK(FG_BLACK, 10),
        BG_RED(FG_RED, 10),
        BG_GREEN(FG_GREEN, 10),
        BG_YELLOW(FG_YELLOW, 10),
        BG_BLUE(FG_BLUE, 10),
        BG_MAGENTA(FG_MAGENTA, 10),
        BG_CYAN(FG_CYAN, 10),
        BG_WHITE(FG_WHITE, 10),

        BG_BLACK_BRIGHT(FG_BLACK_BRIGHT, 10),
        BG_RED_BRIGHT(FG_RED_BRIGHT, 10),
        BG_GREEN_BRIGHT(FG_GREEN_BRIGHT, 10),
        BG_YELLOW_BRIGHT(FG_YELLOW_BRIGHT, 10),
        BG_BLUE_BRIGHT(FG_BLUE_BRIGHT, 10),
        BG_MAGENTA_BRIGHT(FG_MAGENTA_BRIGHT, 10),
        BG_CYAN_BRIGHT(FG_CYAN_BRIGHT, 10),
        BG_WHITE_BRIGHT(FG_WHITE_BRIGHT, 10);

        public final int value;

        Colors(int value) {

            this.value = value;
        }

        Colors(Colors c, int offset) {

            this.value = c.value + offset;
        }

        @Override
        public String toString() {

            return CSI + value + 'm';
        }
    }

    public enum Move {

        UP,
        DOWN,
        RIGHT,
        LEFT;

        public String n(int n) {

            switch (this) {

                case UP:
                    return CSI + n + 'A';
                case DOWN:
                    return CSI + n + 'B';
                case RIGHT:
                    return CSI + n + 'C';
                case LEFT:
                    return CSI + n + 'D';
                default:
                    return null;
            }
        }
    }

}
