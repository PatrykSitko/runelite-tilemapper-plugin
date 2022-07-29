package net.runelite.client.plugins.tileMapper.components;

import java.awt.image.BufferedImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.tileMapper.helpers.ImageLoader;

public interface Divider {

    @Getter
    @AllArgsConstructor
    public static class Horizontal {

        @AllArgsConstructor
        @Getter
        public static enum Type {
            DARK(ImageLoader.loadImage("../dividers/divider-horizontal-dark.png"));

            private final BufferedImage divider;
        }

        private int x;
        private int y;
        @Setter
        private int width;
        private Divider.Horizontal.Type dividerType;

        public void setPosition(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
