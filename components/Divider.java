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
import net.runelite.client.plugins.tileMapper.helpers.PiecesTool;
import net.runelite.client.plugins.tileMapper.helpers.PositionedImage;
import net.runelite.client.plugins.tileMapper.helpers.loaders.ImageLoader;
import net.runelite.client.ui.overlay.RenderableEntity;

public interface Divider {

    public static class Horizontal implements RenderableEntity {

        @AllArgsConstructor
        @Getter
        public static enum Type {
            DARK(ImageLoader.loadImage("dividers/divider-horizontal-dark.png"));

            private final BufferedImage divider;
        }

        private ArrayList<PositionedImage> divider = new ArrayList<>();
        private Point previousLocation = new Point();
        private final Point location = new Point();
        @Setter
        @Getter
        private int width;
        private Divider.Horizontal.Type dividerType;

        @Getter
        @Setter
        private boolean visible;

        public Horizontal(@Positive int x, @Positive int y, @Positive int width, @Nonnull Type dividerType) {
            this.location.setLocation(x, y);
            this.width = width;
            this.dividerType = dividerType;
        }

        public Point getLocation() {
            return new Point(location);
        }

        public void setLocation(@Positive int x, @Positive int y) {
            location.setLocation(x, y);
        }

        @Override
        public Dimension render(Graphics2D graphics) {
            if (!visible) {
                return null;
            }
            if (!location.equals(previousLocation)) {
                previousLocation = new Point(location);
                divider = new ArrayList<>();
                PiecesTool.Populator.populateHorizontalLine(divider, dividerType.getDivider(), location.x, location.y,
                        width);
            }
            divider.forEach(entry -> entry.render(graphics));
            return null;
        }
    }
}
