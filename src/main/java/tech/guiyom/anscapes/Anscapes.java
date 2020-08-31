package tech.guiyom.anscapes;

import java.awt.Color;
import java.io.IOException;
import java.util.regex.Matcher;

/**
 * Contains about everything you need to manipulate the terminal using ansi escape codes.
 * Note that terminal won't render them unless you use things like jansi.
 */
public class Anscapes {

    public static final String CSI = "\33[",
            RESET = CSI + 'm',
            CLEAR_DOWN = CSI + "0J",
            CLEAR_UP = CSI + "1J",
            CLEAR = CSI + "2J",
            CLEAR_BUFFER = CSI + "3J",
            RESET_CURSOR = CSI + "H",
            CLEAR_LINE = CSI + "2K",
            MOVE_UP = CSI + 'A',
            MOVE_DOWN = CSI + 'B',
            MOVE_RIGHT = CSI + 'C',
            MOVE_LEFT = CSI + 'D',
            MOVE_LINEUP = CSI + 'E',
            MOVE_LINEDOWN = CSI + 'F',
            BOLD = CSI + "1m",
            FAINT = CSI + "2m",
            ITALIC = CSI + "3m",
            UNDERLINE = CSI + "4m",
            BLINK_SLOW = CSI + "5m",
            BLINK_FAST = CSI + "6m",
            SWAP_COLORS = CSI + "7m",
            CONCEAL = CSI + "8m",
            DEFAULT_FONT = CSI + "10m",
            FRAKTUR = CSI + "20m",
            UNDERLINE_DOUBLE = CSI + "21m",
            NORMAL = CSI + "22m",
            ITALIC_OFF = CSI + "23m",
            UNDERLINE_OFF = CSI + "24m",
            BLINK_OFF = CSI + "25m",
            INVERSE_OFF = CSI + "26m",
            CONCEAL_OFF = CSI + "28m",
            DEFAULT_FOREGROUND = CSI + "39m",
            DEFAULT_BACKGROUND = CSI + "49m",
            FRAMED = CSI + "51m",
            ENCIRCLED = CSI + "52m",
            OVERLINED = CSI + "53m",
            FRAMED_OFF = CSI + "54m",
            OVERLINED_OFF = CSI + "55m",
            CURSOR_HIDE = CSI + "?25h",
            CURSOR_SHOW = CSI + "?25l",
            CURSOR_POS_SAVE = CSI + 's',
            CURSOR_POS_RESTORE = CSI + 'u',
            ALTERNATIVE_SCREEN_BUFFER = CSI + "?1049h",
            ALTERNATIVE_SCREEN_BUFFER_OFF = CSI + "?1049l",
            RESET_TERMINAL = CSI + 'c';

    /**
     * Escape to allow copy paste. Useful for commands like 'echo -e'
     *
     * @param s the string to be escaped
     * @return the escaped code
     */
    public static String escape(String s) {
        return s.replaceAll("\\n", Matcher.quoteReplacement("\\n"))
                       .replaceAll("\\r", Matcher.quoteReplacement("\\r"))
                       .replaceAll("\\033", Matcher.quoteReplacement("\\033"));
    }

    /**
     * Select an alternative font.
     *
     * @param n the font number to use. Between 0 and 9 where 0 is the default font.
     * @return the corresponding ansi escape code.
     */
    public static String alternativeFont(int n) {

        if (n < 0 || n > 9)
            throw new IllegalArgumentException("Font number should be between 0 and 9.");

        return CSI + (n + 10) + 'm';
    }

    /**
     * Select terminal getColorMode.
     *
     * @param mode the getColorMode number, in this interval : [0,7]U[13,19].
     * @return the corresponding ansi escape code.
     */
    public static String setMode(int mode) {

        if (mode < 0 || (mode > 7 && mode < 13) || mode > 19)
            throw new IllegalArgumentException("ColorMode should be in this interval : [0,7]U[13,19].");

        return CSI + '=' + mode + 'h';
    }

    /**
     * @param mode the getColorMode number, in this interval : [0,7]U[13,19].
     * @return the corresponding ansi escape code.
     */
    public static String resetMode(int mode) {

        if (mode < 0 || (mode > 7 && mode < 13) || mode > 19)
            throw new IllegalArgumentException("ColorMode should be in this interval : [0,7]U[13,19].");

        return CSI + '=' + mode + 'l';
    }

    /**
     * Move cursor up n cells.
     *
     * @param n the number of cells
     * @return the corresponding ansi escape code.
     */
    public static String moveUp(int n) {
        return CSI + n + "A";
    }

    /**
     * Move cursor down n cells.
     *
     * @param n the number of cells
     * @return the corresponding ansi escape code.
     */
    public static String moveDown(int n) {
        return CSI + n + "B";
    }

    /**
     * Move cursor right n cells.
     *
     * @param n the number of cells
     * @return the corresponding ansi escape code.
     */
    public static String moveRight(int n) {
        return CSI + n + "C";
    }

    /**
     * Move cursor left n cells.
     *
     * @param n the number of cells
     * @return the corresponding ansi escape code.
     */
    public static String moveLeft(int n) {
        return CSI + n + "D";
    }

    /**
     * Move cursor n lines after.
     *
     * @param n the number of lines
     * @return the corresponding ansi escape code.
     */
    public static String moveNextLine(int n) {
        return CSI + n + "E";
    }

    /**
     * Move cursor n lines before.
     *
     * @param n the number of lines
     * @return the corresponding ansi escape code.
     */
    public static String movePreviousLine(int n) {
        return CSI + n + "F";
    }

    /**
     * Move cursor to the specified cell.
     *
     * @param n the cell number
     * @return the corresponding ansi escape code.
     */
    public static String moveHorizontal(int n) {

        if (n <= 0)
            n = 1;

        return CSI + n + 'G';
    }

    /**
     * Move cursor at given row and col.
     *
     * @param row the row number
     * @param col the column number
     * @return the corresponding ansi escape code.
     */
    public static String cursorPos(int row, int col) {

        if (row <= 0)
            row = 1;
        if (col <= 0)
            col = 1;

        return CSI + row + ";" + col + "H";
    }

    /**
     * Retrieves cursor position (experimental)
     *
     * @return the cursor position.
     */
    public static CursorPos cursorPos() {

        System.out.print(CSI + "6n");
        try {
            System.in.skip(2);
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

    /**
     * Create a new AnsiColor from its rgb code, only for terminals supporting 24bit color.
     *
     * @param r the red component
     * @param g the green component
     * @param b the blue component
     * @return the corresponding ansi escape code
     */
    public static AnsiColor rgb(final int r, final int g, final int b) {
        return new AnsiColor() {

            private final Color c = new Color(r, g, b);

            @Override
            public Color color() {
                return c;
            }

            @Override
            public int r() {
                return r;
            }

            @Override
            public int g() {
                return g;
            }

            @Override
            public int b() {
                return b;
            }

            @Override
            public String fg() {
                return Anscapes.CSI + "38;2;" + r + ';' + g + ';' + b + 'm';
            }

            @Override
            public String bg() {
                return Anscapes.CSI + "48;2;" + r + ';' + g + ';' + b + 'm';
            }

            @Override
            public boolean equals(Object obj) {
                if (obj instanceof AnsiColor) {
                    AnsiColor other = (AnsiColor) obj;
                    return color().equals(other.color());
                }
                return false;
            }
        };
    }

    /**
     * Create a new AnsiColor from an AWT Color, will only work for terminals supporting 24bit color.
     *
     * @param color the AWT Color
     * @return the corresponding ansi escape code
     */
    public static AnsiColor rgb(Color color) {
        return rgb(color.getRed(), color.getGreen(), color.getBlue());
    }

    public static AnsiColor from256code(int code) {
        return new AnsiColor() {
            @Override
            public Color color() {
                return null;
            }

            @Override
            public int r() {
                return 0;
            }

            @Override
            public int g() {
                return 0;
            }

            @Override
            public int b() {
                return 0;
            }

            @Override
            public String fg() {
                return CSI + "38;5;" + code + 'm';
            }

            @Override
            public String bg() {
                return CSI + "48;5;" + code + 'm';
            }
        };
    }

    /**
     * @param c         the color to convert
     * @param threshold distance to evaluate a spot-on
     * @return the nearest ansi color
     */
    public static Colors findNearestColor(Color c, int threshold) {

        // TODO allow user to use its own color palette for ansi colors.

        Colors closest = null;
        float closestDist = Float.MAX_VALUE;

        for (Colors ansic : Colors.values()) {

            float dist = (float) Math.sqrt(Math.pow(ansic.color().getRed() - c.getRed(), 2) +
                                                   Math.pow(ansic.color().getGreen() - c.getGreen(), 2) +
                                                   Math.pow(ansic.color().getBlue() - c.getBlue(), 2));

            // Speedup, if low distance its a spot-on
            if (dist < threshold) {
                return ansic;
            }

            if (dist < closestDist) {
                closestDist = dist;
                closest = ansic;
            }
        }
        return closest;
    }

    /**
     * All 16 ANSI colors. RGB equivalents are taken to optimize approximation and are totally arbitrary.
     */
    public enum Colors implements AnsiColor {

        BLACK(30, new Color(0, 0, 0)),
        RED(31, new Color(178, 0, 0)),
        GREEN(32, new Color(50, 184, 26)),
        YELLOW(33, new Color(185, 183, 26)),
        BLUE(34, new Color(0, 21, 182)),
        MAGENTA(35, new Color(177, 0, 182)),
        CYAN(36, new Color(47, 186, 184)),
        WHITE(37, new Color(184, 184, 184)),

        BLACK_BRIGHT(90, new Color(58, 58, 58)),
        RED_BRIGHT(91, new Color(247, 48, 58)),
        GREEN_BRIGHT(92, new Color(89, 255, 68)),
        YELLOW_BRIGHT(93, new Color(255, 255, 67)),
        BLUE_BRIGHT(94, new Color(85, 91, 253)),
        MAGENTA_BRIGHT(95, new Color(246, 55, 253)),
        CYAN_BRIGHT(96, new Color(86, 255, 255)),
        WHITE_BRIGHT(97, new Color(255, 255, 255));

        private final int value;
        private final Color c;

        Colors(int value, Color c) {
            this.value = value;
            this.c = c;
        }

        @Override
        public Color color() {
            return c;
        }

        @Override
        public int r() {
            return c.getRed();
        }

        @Override
        public int g() {
            return c.getGreen();
        }

        @Override
        public int b() {
            return c.getBlue();
        }

        @Override
        public String fg() {
            return Anscapes.CSI + value + 'm';
        }

        @Override
        public String bg() {
            return Anscapes.CSI + (value + 10) + 'm';
        }
    }

    public static class CursorPos {

        private final int row;
        private final int col;

        public CursorPos(int row, int col) {
            this.row = row;
            this.col = col;
        }

        public int row() {
            return row;
        }

        public int col() {
            return col;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof CursorPos) {
                CursorPos other = (CursorPos) obj;
                return row == other.row && col == other.col;
            }
            return false;
        }
    }
}
