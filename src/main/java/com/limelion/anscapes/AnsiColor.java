package com.limelion.anscapes;

import java.awt.Color;

/**
 * Represent a terminal color.
 */
public interface AnsiColor {

    Color color();

    String fg();

    String bg();
}
