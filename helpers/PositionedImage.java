package net.runelite.client.plugins.tileMapper.helpers;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class PositionedImage {

    private final BufferedImage image;
    private final int x, y, width, height;

    public PositionedImage(
            BufferedImage image,
            int x,
            int y,
            int width,
            int height) {
        this.image = image.getSubimage(0, 0, width, height);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void draw(Graphics2D graphics) {
        graphics.drawImage(image, x, y, width, height, null);
    }
}
