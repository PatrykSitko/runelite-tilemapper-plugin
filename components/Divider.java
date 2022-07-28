package net.runelite.client.plugins.tileMapper.components;

import java.awt.image.BufferedImage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.plugins.tileMapper.helpers.ImageLoader;

public interface Divider {

    public static class Horizontal {

        @AllArgsConstructor
        @Getter
        public static enum Type {
            DARK(ImageLoader.loadImage("../dividers/divider-horizontal-dark.png"));

            private BufferedImage divider;
        }

        private Divider.Horizontal.Type dividerType;

        public Horizontal(int x, int y, int width, Divider.Horizontal.Type dividerType) {
            this.dividerType = dividerType;
        }
    }
}
