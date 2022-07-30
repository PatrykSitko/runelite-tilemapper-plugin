package net.runelite.client.plugins.tileMapper.components;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;

import javax.annotation.Nonnull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.openhft.chronicle.core.annotation.Positive;
import net.runelite.client.plugins.tileMapper.helpers.loaders.FontLoader;
import net.runelite.client.ui.overlay.RenderableEntity;

@NoArgsConstructor
public class Text implements RenderableEntity {

    public static interface Fonts {
        static final Font BOLD_12 = FontLoader.loadFont("fonts/RuneScape-Bold-12.ttf");
    }

    @Setter
    private boolean visible = false;
    @Getter
    private Point location = new Point();
    @Getter
    @Setter
    private String text = "";
    private final Color RUNESCAPE_ORANGE = new Color(255, 152, 31);

    private FontMetrics fontMetrics;
    @Getter
    @Setter
    private boolean outline;

    @Getter
    @Setter
    private Font font = Text.Fonts.BOLD_12;

    public Text(@Nonnull String text) {
        this.text = text;
    }

    @Positive
    public int getWidth() {
        return fontMetrics != null ? fontMetrics.stringWidth(text) : 0;
    }

    public void setLocation(@Positive int x, @Positive int y) {
        this.location.setLocation(x, y);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!visible) {
            return null;
        }

        fontMetrics = graphics.getFontMetrics();
        final Font defaultFont = graphics.getFont();
        graphics.setFont(font);
        graphics.setColor(Color.BLACK);
        if (outline)
            if (outline) {
                graphics.drawString(text, location.x, location.y + 1);
                graphics.drawString(text, location.x, location.y - 1);
                graphics.drawString(text, location.x + 1, location.y);
                graphics.drawString(text, location.x - 1, location.y);
            } else {
                // shadow
                graphics.drawString(text, location.x + 1, location.y + 1);
            }
        graphics.setColor(RUNESCAPE_ORANGE);
        graphics.drawString(text, location.x, location.y);

        graphics.setFont(defaultFont);
        return null;
    }
}
