package net.runelite.client.plugins.tileMapper.helpers;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import lombok.Getter;
import net.runelite.client.ui.overlay.RenderableEntity;

public class PositionedImage implements RenderableEntity {

    private final BufferedImage image;
    @Getter
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

    @Override
    public Dimension render(Graphics2D graphics) {
        graphics.drawImage(image, x, y, width, height, null);
        return null;
    }
}
