package com.limelion.anscapes;

import org.junit.jupiter.api.Test;

public class AnscapesTest {

    @Test
    public void test() {

        System.out.print(Anscapes.CLEAR);
        System.out.println(AnsiColors.ColorFG.FG_GREEN_BRIGHT + "" + AnsiColors.ColorBG.BG_BLUE + "Some bright green text !" + Anscapes.RESET);
        System.out.println();
    }

}
