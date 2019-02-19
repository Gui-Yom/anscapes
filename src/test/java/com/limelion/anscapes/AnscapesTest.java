package com.limelion.anscapes;

import org.junit.Test;

public class AnscapesTest {

    @Test
    public void test() {

        System.out.print(Anscapes.CLEAR);
        System.out.println(Anscapes.Colors.FG_GREEN_BRIGHT + "" + Anscapes.Colors.BG_BLUE + "Some bright green text !" + Anscapes.RESET);
        System.out.println();
    }

}
