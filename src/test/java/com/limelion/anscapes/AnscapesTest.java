package com.limelion.anscapes;

import org.junit.jupiter.api.Test;

public class AnscapesTest {

    @Test
    public void test() {

        System.out.print(Anscapes.CLEAR);
        System.out.println(Anscapes.Colors.GREEN_BRIGHT.fg() + Anscapes.Colors.BLUE.bg() + "Some bright green text !" + Anscapes.RESET);
        System.out.println();
    }

}
