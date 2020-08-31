package tech.guiyom.anscapes;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

public final class Utils {

    private static BufferedImage sampleImage = null;

    static {
        try {
            sampleImage = ImageIO.read(Utils.class.getResourceAsStream("/shield.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static BufferedImage getSampleImage() {
        return sampleImage;
    }
}
