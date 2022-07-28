package net.runelite.client.plugins.tileMapper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.events.CanvasSizeChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.SaveTileDataToPathOverlayComponents.Background;
import net.runelite.client.plugins.tileMapper.SaveTileDataToPathOverlayComponents.Button;
import net.runelite.client.plugins.tileMapper.events.ViewportChanged;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;

public class SaveTileDataToPathOverlay
  extends Overlay
  implements MouseListener, KeyListener {

  // private Viewport viewport;
  private final SaveDataButtonOverlay SAVE_DATA_BUTTON_OVERLAY;
  private final TileMapperPlugin tileMapperPlugin;
  private final Background background = new Background(
    -1,
    -1,
    488,
    300,
    Background.Type.DARK
  );
  private final Button exitButton;

  @Inject
  public SaveTileDataToPathOverlay(TileMapperPlugin tileMapperPlugin) {
    this.tileMapperPlugin = tileMapperPlugin;
    this.SAVE_DATA_BUTTON_OVERLAY = tileMapperPlugin.getSaveDataButtonOverlay();
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_WIDGETS);
    setPriority(OverlayPriority.MED);
    BufferedImage buttonImage = null, buttonImageHover = null;
    try {
      buttonImage =
        ImageIO.read(
          SaveTileDataToPathOverlay.class.getResource(
              "buttons/close-menu-button.png"
            )
        );
      buttonImageHover =
        ImageIO.read(
          SaveTileDataToPathOverlay.class.getResource(
              "buttons/close-menu-button-hover.png"
            )
        );
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      exitButton =
        new Button(
          -1,
          -1,
          buttonImage,
          buttonImageHover,
          () -> {
            SAVE_DATA_BUTTON_OVERLAY.clearDisplayPathPickerOverlayVar();
          }
        );
    }
    background.setOnOutOfBoundsClickAction(
      () -> {
        SAVE_DATA_BUTTON_OVERLAY.clearDisplayPathPickerOverlayVar();
      }
    );
  }

  private void updateComponentLocation() {
    final Viewport viewportInUse = tileMapperPlugin.getCurrentViewportType();
    final Client client = tileMapperPlugin.getClient();
    final int xNegativeOffset_case_resizable_modern = 108;
    final int yNegativeOffset_case_resizable_modern = 79;
    final int xNegativeOffset_case_resizable_classic = 125;
    final int yNegativeOffset_case_resizable_classic = 79;
    if (viewportInUse == null) {
      return;
    }
    switch (viewportInUse) {
      default:
        break;
      case RESIZABLE_MODERN_LAYOUT:
        background.setLocation(
          client.getCanvasWidth() /
          2 -
          background.getBounds().width /
          2 -
          xNegativeOffset_case_resizable_modern,
          client.getCanvasHeight() /
          2 -
          background.getBounds().height /
          2 -
          yNegativeOffset_case_resizable_modern
        );
        break;
      case RESIZABLE_CLASSIC_LAYOUT:
        background.setLocation(
          client.getCanvasWidth() /
          2 -
          background.getBounds().width /
          2 -
          xNegativeOffset_case_resizable_classic,
          client.getCanvasHeight() /
          2 -
          background.getBounds().height /
          2 -
          yNegativeOffset_case_resizable_classic
        );
        break;
      case FIXED_CLASSIC_LAYOUT:
        background.setLocation(16, 24);
        break;
    }
    exitButton.setLocation(
      background.getBounds().x +
      background.getBounds().width -
      7 -
      exitButton.getBounds().width,
      background.getBounds().y + 7
    );
  }

  public void onCanvasSizeChanged(CanvasSizeChanged event) {
    updateComponentLocation();
  }

  public void onViewportChanged(ViewportChanged event) {
    updateComponentLocation();
  }

  public void onGameTick(GameTick event) {
    background.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
    exitButton.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    background.render(graphics);
    exitButton.render(graphics);
    return null;
  }

  private MouseEvent compareAndReturnDivergent(
    MouseEvent defaultMouseEvent,
    MouseEvent[] potentiallyModifiedMouseEvents
  ) {
    MouseEvent[] modifiedMouseEvents = Stream
      .of(potentiallyModifiedMouseEvents)
      .filter(
        potentiallyModifiedMouseEvent ->
          !potentiallyModifiedMouseEvent.equals(defaultMouseEvent)
      )
      .toArray(MouseEvent[]::new);
    return modifiedMouseEvents.length > 0
      ? modifiedMouseEvents[modifiedMouseEvents.length - 1]
      : defaultMouseEvent;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
      mouseEvent,
      new MouseEvent[] {
        exitButton.mouseClicked(mouseEvent),
        background.mouseClicked(mouseEvent),
      }
    );
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
      mouseEvent,
      new MouseEvent[] {
        exitButton.mousePressed(mouseEvent),
        background.mousePressed(mouseEvent),
      }
    );
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
      mouseEvent,
      new MouseEvent[] {
        exitButton.mouseReleased(mouseEvent),
        background.mouseReleased(mouseEvent),
      }
    );
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
    return compareAndReturnDivergent(
      mouseEvent,
      new MouseEvent[] {
        exitButton.mouseDragged(mouseEvent),
        background.mouseDragged(mouseEvent),
      }
    );
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
      mouseEvent,
      new MouseEvent[] {
        exitButton.mouseMoved(mouseEvent),
        background.mouseMoved(mouseEvent),
      }
    );
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
