package com.limelion.anscape;

public class Anscape {

    private static final String PREFIX = "\u001b[";
    public static final String RESET = PREFIX + "0m";
    public static final String CLEAR = PREFIX + "H" + PREFIX + "2J";

    public enum Colors {

        RED("31;1m"),
        GREEN("32;1m"),
        BLUE("34;1m");

        public final String value;

        Colors(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return PREFIX + value;
        }
    }

}
