package net.runelite.client.plugins.tileMapper.helpers.loaders;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;

import javax.annotation.Nonnull;

public interface FontLoader {

    static Font loadFont(@Nonnull String resourcePath) {
        Font customFont = null;
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, FontLoader.class.getResourceAsStream(resourcePath))
                    .deriveFont(12f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // register the font
            ge.registerFont(customFont);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
        return customFont;
    }
}
