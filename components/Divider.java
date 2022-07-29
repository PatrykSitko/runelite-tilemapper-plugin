package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.openhft.chronicle.core.annotation.Positive;
import net.runelite.client.plugins.tileMapper.helpers.ImageLoader;
import net.runelite.client.plugins.tileMapper.helpers.PiecesTool;
import net.runelite.client.plugins.tileMapper.helpers.PositionedImage;
import net.runelite.client.ui.overlay.RenderableEntity;

public interface Divider {

    public static class Horizontal implements RenderableEntity {

        @AllArgsConstructor
        @Getter
        public static enum Type {
            DARK(ImageLoader.loadImage("../dividers/divider-horizontal-dark.png"));

            private final BufferedImage divider;
        }

        private final ArrayList<PositionedImage> divider = new ArrayList<>();
        private Point previousPosition = new Point();
        private final Point position = new Point();
        @Setter
        @Getter
        private int width;
        private Divider.Horizontal.Type dividerType;

        public Horizontal(@Positive int x, @Positive int y, @Positive int width, @Nonnull Type dividerType) {
            this.position.setLocation(x, y);
            this.width = width;
            this.dividerType = dividerType;
        }

        public Point getPosition() {
            return new Point(position);
        }

        public void setPosition(@Positive int x, @Positive int y) {
            position.setLocation(x, y);
        }

        @Override
        public Dimension render(Graphics2D graphics) {
            if (!position.equals(previousPosition)) {
                previousPosition = new Point(position);
                PiecesTool.Populator.populateHorizontalLine(divider, dividerType.getDivider(), position.x, position.y,
                        width);
            }
            divider.forEach(entry -> entry.draw(graphics));
            return null;
        }
    }
}
