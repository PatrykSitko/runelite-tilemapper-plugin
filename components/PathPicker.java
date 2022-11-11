package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import lombok.Getter;
import lombok.Setter;
import net.openhft.chronicle.core.annotation.Positive;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.components.Background.Type;
import net.runelite.client.ui.overlay.RenderableEntity;

public class PathPicker implements RenderableEntity {

    public static class PathPickerEntry implements RenderableEntity, MouseListener {

        @Getter
        private final Rectangle bounds = new Rectangle();

        @Getter
        @Setter
        private boolean visible = false;

        @Override
        public Dimension render(Graphics2D graphics) {
            if (!visible) {
                return null;
            }
            return null;
        }

        @Override
        public MouseEvent mouseClicked(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseEntered(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseExited(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseDragged(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseMoved(MouseEvent mouseEvent) {
            return mouseEvent;
        }
    }

    @Getter
    private final Rectangle bounds = new Rectangle();
    private final String USER_HOME_DIRECTORY = System.getProperty("user.home");

    private final Background background;
    private final Scrollbar.Vertical scrollbar;

    @Getter
    private boolean visible = false;

    public PathPicker(int x, int y, int width, int height, Type backgroundType) {
        this.background = new Background(x, y, width, height, backgroundType);
        this.scrollbar = new Scrollbar.Vertical(x, y, height);
        this.bounds.setBounds(x, y, width, height);
    }

    public void setVisible(boolean visible) {
        background.setVisible(visible);
        this.visible = visible;
    }

    public void setLocation(@Positive int x, @Positive int y) {
        this.bounds.setLocation(x, y);
    }

    @Override
    public Dimension render(Graphics2D graphics) {
        if (!visible) {
            return null;
        }
        background.getBounds().setBounds(bounds);
        background.render(graphics);
        return null;
    }
}
