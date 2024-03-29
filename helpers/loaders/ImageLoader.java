package net.runelite.client.plugins.tileMapper.helpers.loaders;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public interface ImageLoader {
    static BufferedImage loadImage(String resourcePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(ImageLoader.class.getResource("../../" + resourcePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }
}
