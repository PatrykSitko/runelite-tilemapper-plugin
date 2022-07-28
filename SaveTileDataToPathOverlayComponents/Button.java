package net.runelite.client.plugins.tileMapper.SaveTileDataToPathOverlayComponents;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import lombok.Getter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.RenderableEntity;

public class Button implements RenderableEntity, MouseListener {

  public static interface Action {
    public void onClick();
  }

  @Getter
  private Point location;

  @Getter
  private Dimension dimension;

  private BufferedImage normal;
  private BufferedImage hover;
  private Button.Action action;
  private boolean isMouseHovering;

  public Button(
    int x,
    int y,
    int width,
    int height,
    BufferedImage normal,
    BufferedImage hover,
    Button.Action action
  ) {
    this.location = new Point(x, y);
    this.dimension = new Dimension(width, height);
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
    location.setLocation(x, y);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    graphics.drawImage(
      isMouseHovering ? hover : normal,
      location.x,
      location.y,
      dimension.width,
      dimension.height,
      null
    );
    return null;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (isMouseHovering && mouseEvent.getButton() == MouseEvent.BUTTON1) {
      this.action.onClick();
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    if (isMouseHovering) {
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    if (isMouseHovering) {
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
      mouseLocation.x >= location.x &&
      mouseLocation.x <= location.x + dimension.width;
    final boolean inBounds_verticalLocation =
      mouseLocation.y >= location.y &&
      mouseLocation.y <= location.y + dimension.height;
    isMouseHovering = inBounds_horizontalLocation && inBounds_verticalLocation;
    return mouseEvent;
  }
}
