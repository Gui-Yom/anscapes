package tech.guiyom.anscapes;

import java.awt.Color;

/**
 * Represent a terminal color.
 */
public interface AnsiColor {

    /**
     * @return the associated java.awt.Color
     */
    Color color();

    /**
     * @return the foreground version of this color
     */
    String fg();

    /**
     * @return the background version of this color
     */
    String bg();
}
