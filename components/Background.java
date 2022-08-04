package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.helpers.PiecesTool;
import net.runelite.client.plugins.tileMapper.helpers.PositionedImage;
import net.runelite.client.plugins.tileMapper.helpers.loaders.ImageLoader;
import net.runelite.client.ui.overlay.RenderableEntity;

public class Background implements RenderableEntity, MouseListener {

  @AllArgsConstructor
  @Getter
  public static enum Type {
    LIGHT(
        // background
        ImageLoader.loadImage("backgrounds/background-light.png"),
        // corners
        ImageLoader.loadImage("borders/corners/left-top-corner-light.png"),
        ImageLoader.loadImage("borders/corners/right-top-corner-light.png"),
        ImageLoader.loadImage("borders/corners/left-bottom-corner-light.png"),
        ImageLoader.loadImage("borders/corners/right-bottom-corner-light.png"),
        // borders
        ImageLoader.loadImage("borders/left-border-light.png"),
        ImageLoader.loadImage("borders/top-border-light.png"),
        ImageLoader.loadImage("borders/right-border-light.png"),
        ImageLoader.loadImage("borders/bottom-border-light.png")),
    DARK(
        // background
        ImageLoader.loadImage("backgrounds/background-dark.png"),
        // corners
        ImageLoader.loadImage("borders/corners/left-top-corner-dark.png"),
        ImageLoader.loadImage("borders/corners/right-top-corner-dark.png"),
        ImageLoader.loadImage("borders/corners/left-bottom-corner-dark.png"),
        ImageLoader.loadImage("borders/corners/right-bottom-corner-dark.png"),
        // borders
        ImageLoader.loadImage("borders/left-border-dark.png"),
        ImageLoader.loadImage("borders/top-border-dark.png"),
        ImageLoader.loadImage("borders/right-border-dark.png"),
        ImageLoader.loadImage("borders/bottom-border-dark.png"));

    // background
    private final BufferedImage background;
    // corners
    private final BufferedImage topLeftCorner;
    private final BufferedImage topRightCorner;
    private final BufferedImage bottomLeftCorner;
    private final BufferedImage bottomRightCorner;
    // borders
    private final BufferedImage leftBorder;
    private final BufferedImage topBorder;
    private final BufferedImage rightBorder;
    private final BufferedImage bottomBorder;
  }

  public static interface OnOutOfBoundsClickAction {
    public void perform();
  }

  @Getter
  private final Rectangle bounds = new Rectangle();

  private Rectangle previousBounds;
  private final Type backgroundType;

  private volatile ArrayList<PositionedImage> background = new ArrayList<>();
  private volatile ArrayList<PositionedImage> border = new ArrayList<>();

  @Getter
  @Setter
  private boolean visible;

  private boolean isMouseHovering;

  @Setter
  private Background.OnOutOfBoundsClickAction onOutOfBoundsClickAction;

  public Background(int x, int y, int width, int height, Type backgroundType) {
    bounds.setBounds(x, y, width, height);
    this.backgroundType = backgroundType;
  }

  public void setLocation(int x, int y) {
    this.bounds.setLocation(x, y);
  }

  public Point getLocation() {
    return this.bounds.getLocation();
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!visible) {
      return null;
    }
    drawBackgroundWidthBorder(graphics);
    return null;
  }

  private void drawBackgroundWidthBorder(Graphics2D graphics) {
    if (!bounds.equals(previousBounds)) {
      previousBounds = new Rectangle(bounds);
      background = new ArrayList<>();
      border = new ArrayList<>();
      PiecesTool.Populator.populateBackground(background,
          backgroundType.getBackground(), bounds);
      PiecesTool.Populator.populateBorder(border,
          new BufferedImage[] { backgroundType.getTopLeftCorner(),
              backgroundType.getTopRightCorner(),
              backgroundType.getBottomLeftCorner(), backgroundType.getBottomRightCorner()
          },
          new BufferedImage[] { backgroundType.getTopBorder(),
              backgroundType.getRightBorder(),
              backgroundType.getBottomBorder(), backgroundType.getLeftBorder() },
          bounds);
    }
    background.forEach(entry -> entry.render(graphics));
    border.forEach(entry -> entry.render(graphics));
  }

  private boolean mouseInBounds(MouseEvent mouseEvent) {
    final Point mouseLocation = mouseEvent.getPoint();
    final boolean inBounds_horizontalLocation = mouseLocation.x >= bounds.x
        && mouseLocation.x <= bounds.x + bounds.width;
    final boolean inBounds_verticalLocation = mouseLocation.y >= bounds.y &&
        mouseLocation.y <= bounds.y + bounds.height;
    return inBounds_horizontalLocation && inBounds_verticalLocation;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    } else if (visible && !isMouseHovering && onOutOfBoundsClickAction != null) {
      onOutOfBoundsClickAction.perform();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    }
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
    if (isMouseHovering && visible) {
      mouseEvent.consume();
    } else if (visible && !isMouseHovering && onOutOfBoundsClickAction != null) {
      onOutOfBoundsClickAction.perform();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    isMouseHovering = mouseInBounds(mouseEvent);

    final MouseEvent substituteMouseEvent = new MouseEvent(
        mouseEvent.getComponent(),
        MouseEvent.MOUSE_MOVED,
        mouseEvent.getWhen(),
        mouseEvent.getModifiersEx(),
        0,
        0,
        mouseEvent.getClickCount(),
        mouseEvent.isPopupTrigger(),
        mouseEvent.getButton());
    return !isMouseHovering | !visible ? mouseEvent : substituteMouseEvent;
  }
}
