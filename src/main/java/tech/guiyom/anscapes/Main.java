package tech.guiyom.anscapes;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        AnsiRenderer converter = new AnsiRenderer(ColorMode.valueOf(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        System.out.println(Anscapes.escape(converter.renderToString(ImageIO.read(new File(args[3])))));
    }
}
