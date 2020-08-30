package tech.guiyom.anscapes;

import tech.guiyom.anscapes.renderer.ImageRenderer;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        ImageRenderer converter = ImageRenderer.createRenderer(ColorMode.valueOf(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        System.out.println(Anscapes.escape(converter.renderString(ImageIO.read(new File(args[3])))));
    }
}
