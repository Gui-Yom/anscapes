package com.limelion.anscapes;

import java.awt.Color;

public interface AnsiColors {

    static String rgbFG(int r, int g, int b) {

        return Anscapes.CSI + "38;2;" + r + ';' + g + ';' + b + 'm';
    }

    static String rgbBG(int r, int g, int b) {

        return Anscapes.CSI + "48;2;" + r + ';' + g + ';' + b + 'm';
    }

    int value();

    Color color();

    String code();

    /**
     * Chain a foreground and a background.
     *
     * @param other
     *
     * @return
     */
    String chain(AnsiColors other);

    /**
     * All 16 ANSI colors (foreground version)
     */
    enum ColorFG implements AnsiColors {

        FG_BLACK(30, new Color(0, 0, 0)),
        FG_RED(31, new Color(178, 0, 0)),
        FG_GREEN(32, new Color(50, 184, 26)),
        FG_YELLOW(33, new Color(185, 183, 26)),
        FG_BLUE(34, new Color(0, 21, 182)),
        FG_MAGENTA(35, new Color(177, 0, 182)),
        FG_CYAN(36, new Color(47, 186, 184)),
        FG_WHITE(37, new Color(184, 184, 184)),

        FG_BLACK_BRIGHT(90, new Color(58, 58, 58)),
        FG_RED_BRIGHT(91, new Color(247, 48, 58)),
        FG_GREEN_BRIGHT(92, new Color(89, 255, 68)),
        FG_YELLOW_BRIGHT(93, new Color(255, 255, 67)),
        FG_BLUE_BRIGHT(94, new Color(85, 91, 253)),
        FG_MAGENTA_BRIGHT(95, new Color(246, 55, 253)),
        FG_CYAN_BRIGHT(96, new Color(86, 255, 255)),
        FG_WHITE_BRIGHT(97, new Color(255, 255, 255));

        private final int value;
        private final Color c;

        ColorFG(int value, Color c) {

            this.value = value;
            this.c = c;
        }

        @Override
        public int value() {

            return value;
        }

        @Override
        public Color color() {

            return c;
        }

        @Override
        public String code() {

            return Anscapes.CSI + value + 'm';
        }

        @Override
        public String chain(AnsiColors other) {

            return Anscapes.CSI + value + ';' + other.value() + 'm';
        }

        @Override
        public String toString() {

            return code();
        }
    }

    /**
     * All ANSI 16 colors (background version)
     */
    enum ColorBG implements AnsiColors {

        BG_BLACK(40, ColorFG.FG_BLACK.c),
        BG_RED(41, ColorFG.FG_RED.c),
        BG_GREEN(42, ColorFG.FG_GREEN.c),
        BG_YELLOW(43, ColorFG.FG_YELLOW.c),
        BG_BLUE(44, ColorFG.FG_BLUE.c),
        BG_MAGENTA(45, ColorFG.FG_MAGENTA.c),
        BG_CYAN(46, ColorFG.FG_CYAN.c),
        BG_WHITE(47, ColorFG.FG_WHITE.c),

        BG_BLACK_BRIGHT(100, ColorFG.FG_BLACK_BRIGHT.c),
        BG_RED_BRIGHT(101, ColorFG.FG_RED_BRIGHT.c),
        BG_GREEN_BRIGHT(102, ColorFG.FG_GREEN_BRIGHT.c),
        BG_YELLOW_BRIGHT(103, ColorFG.FG_YELLOW_BRIGHT.c),
        BG_BLUE_BRIGHT(104, ColorFG.FG_BLUE_BRIGHT.c),
        BG_MAGENTA_BRIGHT(105, ColorFG.FG_MAGENTA_BRIGHT.c),
        BG_CYAN_BRIGHT(106, ColorFG.FG_CYAN_BRIGHT.c),
        BG_WHITE_BRIGHT(107, ColorFG.FG_WHITE_BRIGHT.c);

        private final int value;
        private final Color c;

        ColorBG(int value, Color c) {

            this.value = value;
            this.c = c;
        }

        @Override
        public int value() {

            return value;
        }

        @Override
        public Color color() {

            return c;
        }

        @Override
        public String code() {

            return Anscapes.CSI + value + 'm';
        }

        @Override
        public String chain(AnsiColors other) {

            return Anscapes.CSI + value + ';' + other.value() + 'm';
        }

        @Override
        public String toString() {

            return code();
        }
    }
}
