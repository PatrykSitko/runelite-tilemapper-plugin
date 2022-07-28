package net.runelite.client.plugins.tileMapper.helpers;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public interface ImageLoader {
    static BufferedImage loadImage(String path) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(ImageLoader.class.getResource(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
