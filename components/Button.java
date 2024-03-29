package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.helpers.Action;
import net.runelite.client.ui.overlay.RenderableEntity;

public class Button implements RenderableEntity, MouseListener {

  @Getter
  private final Rectangle bounds = new Rectangle();

  private volatile boolean isMouseInBounds;

  @Getter
  @Setter
  private volatile boolean visible;

  private BufferedImage normal;
  private BufferedImage hover;
  @Setter
  private volatile Action onClickLeftButtonAction;
  @Setter
  private volatile Action onClickMiddleButtonAction;
  @Setter
  private volatile Action onClickRightButtonAction;
  @Setter
  private volatile Action onHoldLeftButtonAction;
  @Setter
  private volatile Action onHoldMiddleButtonAction;
  @Setter
  private volatile Action onHoldRightButtonAction;
  @Setter
  private volatile boolean ignoreHoldingingButton = false;
  private volatile int repeatHoldingButtonActionEveryMillis = 0;
  private volatile int repeatHoldingButtonActionEveryNanos = 100;
  @Setter
  private volatile int triggerHoldingButtonAfterAmmountOfMillis = 250;
  private volatile boolean mouseReleased = true;
  private volatile Thread actionThread;
  private volatile MouseEvent currentMousePressedEvent;
  private Runnable actionThreadRunnable = () -> {
    final long startTime = System.currentTimeMillis();
    boolean isHoldingMouseButton = false;
    if (ignoreHoldingingButton) {
      actionThread.interrupt();
    }
    while (!mouseReleased) {
      if (!isMouseInBounds) {
        try {
          Thread.sleep(0, 1);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
        continue;
      }
      if ((isHoldingMouseButton = startTime + triggerHoldingButtonAfterAmmountOfMillis <= System
          .currentTimeMillis())) {
        switch (currentMousePressedEvent.getButton()) {
          case MouseEvent.BUTTON1:
            if (onHoldLeftButtonAction != null) {
              onHoldLeftButtonAction.perform();
            }
            break;
          case MouseEvent.BUTTON2:
            if (onHoldMiddleButtonAction != null) {
              onHoldMiddleButtonAction.perform();
            }
            break;
          case MouseEvent.BUTTON3:
            if (onHoldRightButtonAction != null) {
              onHoldRightButtonAction.perform();
            }
            break;
        }
        try {
          Thread.sleep(repeatHoldingButtonActionEveryMillis, repeatHoldingButtonActionEveryNanos);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
    if ((!isHoldingMouseButton || ignoreHoldingingButton) && visible && isMouseInBounds) {
      switch (currentMousePressedEvent.getButton()) {
        case MouseEvent.BUTTON1:
          if (onClickLeftButtonAction != null) {
            onClickLeftButtonAction.perform();
          }
          break;
        case MouseEvent.BUTTON2:
          if (onClickMiddleButtonAction != null) {
            onClickMiddleButtonAction.perform();
          }
          break;
        case MouseEvent.BUTTON3:
          if (onClickRightButtonAction != null) {
            onClickRightButtonAction.perform();
          }
          break;
      }
    }
  };

  public Button(
      int x,
      int y,
      int width,
      int height,
      BufferedImage normal,
      BufferedImage hover) {
    this.bounds.setBounds(new Rectangle(x, y, width, height));
    this.normal = normal;
    this.hover = hover;
  }

  public Button(
      int x,
      int y,
      BufferedImage normal,
      BufferedImage hover) {
    this(x, y, normal.getWidth(), normal.getHeight(), normal, hover);
  }

  public void repeatHoldingButtonAction(int millis, int nanos) {
    this.repeatHoldingButtonActionEveryMillis = millis;
    this.repeatHoldingButtonActionEveryNanos = nanos;
  }

  public void setLocation(int x, int y) {
    bounds.setLocation(x, y);
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
  public Dimension render(Graphics2D graphics) {
    if (!visible) {
      return null;
    }
    graphics.drawImage(
        isMouseInBounds ? hover : normal,
        bounds.x,
        bounds.y,
        bounds.width,
        bounds.height,
        null);
    return null;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (visible && isMouseInBounds) {
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    mouseReleased = false;
    if (visible && isMouseInBounds) {
      currentMousePressedEvent = mouseEvent;
      actionThread = new Thread(actionThreadRunnable);
      actionThread.setDaemon(true);
      actionThread.start();
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    mouseReleased = true;
    if (actionThread != null && !actionThread.isInterrupted()) {
      actionThread.interrupt();
    }
    if (visible && isMouseInBounds) {
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
    isMouseInBounds = mouseInBounds(mouseEvent);
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    isMouseInBounds = mouseInBounds(mouseEvent);
    return mouseEvent;
  }
}
