package com.limelion.anscape;

import org.junit.Test;

public class AnscapeTest {

    @Test
    public void test() {

        System.out.print(Anscape.CLEAR);
        System.out.println(Anscape.Colors.GREEN + "Some bright green text !" + Anscape.RESET);
        System.out.println();
    }

}
