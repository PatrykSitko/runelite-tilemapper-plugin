package net.runelite.client.plugins.tileMapper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.CanvasSizeChanged;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.events.ViewportChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class SaveTileDataToPathOverlay
  extends Overlay
  implements MouseListener, net.runelite.client.input.KeyListener {

  // private Viewport viewport;
  private final SaveDataButtonOverlay SAVE_DATA_BUTTON_OVERLAY;
  private final TileMapperPlugin plugin;
  private final Background background = new Background(
    -1,
    -1,
    488,
    300,
    Background.Type.DARK
  );

  @Inject
  public SaveTileDataToPathOverlay(TileMapperPlugin tileMapperPlugin) {
    this.plugin = tileMapperPlugin;
    this.SAVE_DATA_BUTTON_OVERLAY = tileMapperPlugin.getSaveDataButtonOverlay();
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_WIDGETS);
    setPriority(OverlayPriority.MED);
  }

  private void updateBackgroundLocation() {
    final Viewport viewportInUse = plugin.getCurrentViewportType();
    final Client client = plugin.getClient();
    final int xNegativeOffset_case_defaut = 108;
    final int yNegativeOffset_case_default = 79;
    if (viewportInUse == null) {
      return;
    }
    switch (viewportInUse) {
      default:
        background.setLocation(
          client.getCanvasWidth() /
          2 -
          background.getBounds().width /
          2 -
          xNegativeOffset_case_defaut,
          client.getCanvasHeight() /
          2 -
          background.getBounds().height /
          2 -
          yNegativeOffset_case_default
        );
        break;
      case FIXED_CLASSIC_LAYOUT:
        background.setLocation(16, 24);
        break;
    }
  }

  public void onCanvasSizeChanged(CanvasSizeChanged event) {
    updateBackgroundLocation();
  }

  public void onViewportChanged(ViewportChanged event) {
    updateBackgroundLocation();
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    if (SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay()) {
      background.render(graphics);
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

  @Override
  public void keyTyped(KeyEvent e) {
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
