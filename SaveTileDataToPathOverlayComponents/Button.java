package net.runelite.client.plugins.tileMapper.SaveTileDataToPathOverlayComponents;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.RenderableEntity;

public class Button implements RenderableEntity, MouseListener {

  public static interface Action {
    public void onClick();
  }

  @Getter
  private final Rectangle bounds = new Rectangle();

  private BufferedImage normal;
  private BufferedImage hover;
  private Button.Action action;
  private boolean isMouseHovering;

  @Getter
  @Setter
  private boolean visible;

  public Button(
    int x,
    int y,
    int width,
    int height,
    BufferedImage normal,
    BufferedImage hover,
    Button.Action action
  ) {
    this.bounds.setBounds(new Rectangle(x, y, width, height));
    this.normal = normal;
    this.hover = hover;
    this.action = action;
  }

  public Button(
    int x,
    int y,
    BufferedImage normal,
    BufferedImage hover,
    Button.Action action
  ) {
    this(x, y, normal.getWidth(), normal.getHeight(), normal, hover, action);
  }

  public void setLocation(int x, int y) {
    bounds.setLocation(x, y);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!visible) {
      return null;
    }
    graphics.drawImage(
      isMouseHovering ? hover : normal,
      bounds.x,
      bounds.y,
      bounds.width,
      bounds.height,
      null
    );
    return null;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (
      visible && isMouseHovering && mouseEvent.getButton() == MouseEvent.BUTTON1
    ) {
      this.action.onClick();
      mouseEvent.consume();
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
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    final Point mouseLocation = mouseEvent.getPoint();
    final boolean inBounds_horizontalLocation =
      mouseLocation.x >= bounds.x && mouseLocation.x <= bounds.x + bounds.width;
    final boolean inBounds_verticalLocation =
      mouseLocation.y >= bounds.y &&
      mouseLocation.y <= bounds.y + bounds.height;
    isMouseHovering = inBounds_horizontalLocation && inBounds_verticalLocation;
    return mouseEvent;
  }
}
