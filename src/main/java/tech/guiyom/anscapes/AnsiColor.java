package tech.guiyom.anscapes;

import java.awt.Color;

/**
 * Represent a terminal color.
 */
public interface AnsiColor {

    /**
     * This only makes sense in the context of a 24 bit terminal, since each terminal have its own color palette.
     *
     * @return the associated java.awt.Color
     */
    Color color();

    /**
     * @return the red component
     */
    int r();

    /**
     * @return the green component
     */
    int g();

    /**
     * @return the blue component
     */
    int b();

    /**
     * @return the foreground version of this color
     */
    String fg();

    /**
     * @return the background version of this color
     */
    String bg();
}
