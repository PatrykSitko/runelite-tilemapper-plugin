package net.runelite.client.plugins.tileMapper;

// import javax.inject.Inject;

import com.google.inject.Inject;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import net.runelite.api.events.GameStateChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class SaveTileDataToPathOverlay
  extends Overlay
  implements MouseListener, net.runelite.client.input.KeyListener {

  // private Viewport viewport;
  private ArrayList<Point> mouseClicks;
  private ArrayList<Integer> keyPresses;
  private boolean enabledListeners = false;
  private final SaveDataButtonOverlay SAVE_DATA_BUTTON_OVERLAY;

  @Inject
  public SaveTileDataToPathOverlay(
    SaveDataButtonOverlay saveDataButtonOverlay
  ) {
    this.SAVE_DATA_BUTTON_OVERLAY = saveDataButtonOverlay;
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_WIDGETS);
    setPriority(OverlayPriority.MED);
  }

  @Subscribe
  public void onGameStateChanged(GameStateChanged event) {
    switch (event.getGameState()) {
      default:
        enabledListeners = false;
        break;
      case LOGGED_IN:
        enabledListeners = true;
        break;
    }
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay()) {
      System.out.println("save data overlay");
    }
    return null;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (enabledListeners) {
      mouseClicks.add(mouseEvent.getPoint());
    }
    mouseEvent.consume();
    return mouseEvent;
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    mouseEvent.consume();
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    mouseEvent.consume();
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

  @Override
  public void keyTyped(KeyEvent e) {
    if (enabledListeners) {
      keyPresses.add(e.getKeyCode());
    }
    e.consume();
  }

  @Override
  public void keyPressed(KeyEvent e) {
    e.consume();
  }

  @Override
  public void keyReleased(KeyEvent e) {
    e.consume();
  }
}
