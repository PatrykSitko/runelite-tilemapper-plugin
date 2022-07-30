package net.runelite.client.plugins.tileMapper.Overlays;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.stream.Stream;

import javax.inject.Inject;

import net.runelite.api.Client;
import net.runelite.api.events.CanvasSizeChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.TileMapperPlugin;
import net.runelite.client.plugins.tileMapper.components.Background;
import net.runelite.client.plugins.tileMapper.components.Button;
import net.runelite.client.plugins.tileMapper.components.Divider;
import net.runelite.client.plugins.tileMapper.components.Text;
import net.runelite.client.plugins.tileMapper.events.ViewportChanged;
import net.runelite.client.plugins.tileMapper.helpers.Viewport;
import net.runelite.client.plugins.tileMapper.helpers.loaders.ImageLoader;
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
      Background.Type.DARK);
  private final Button exitButton;
  private final Divider.Horizontal divider = new Divider.Horizontal(0, 0, 478, Divider.Horizontal.Type.DARK);
  private final Text title = new Text();

  @Inject
  public SaveTileDataToPathOverlay(TileMapperPlugin tileMapperPlugin) {
    this.tileMapperPlugin = tileMapperPlugin;
    this.SAVE_DATA_BUTTON_OVERLAY = tileMapperPlugin.getSaveDataButtonOverlay();
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_WIDGETS);
    setPriority(OverlayPriority.MED);
    background.setOnOutOfBoundsClickAction(
        () -> {
          SAVE_DATA_BUTTON_OVERLAY.clearDisplayPathPickerOverlayVar();
        });
    exitButton = new Button(
        -1,
        -1,
        ImageLoader.loadImage("buttons/close-menu-button.png"),
        ImageLoader.loadImage("buttons/close-menu-button-hover.png"),
        () -> {
          SAVE_DATA_BUTTON_OVERLAY.clearDisplayPathPickerOverlayVar();
        });
    title.setText(SAVE_DATA_BUTTON_OVERLAY.getHOVERINFO_TEXT());
  }

  private void updateOverlayLocation() {
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
                    2
                -
                xNegativeOffset_case_resizable_modern,
            client.getCanvasHeight() /
                2 -
                background.getBounds().height /
                    2
                -
                yNegativeOffset_case_resizable_modern);
        break;
      case RESIZABLE_CLASSIC_LAYOUT:
        background.setLocation(
            client.getCanvasWidth() /
                2 -
                background.getBounds().width /
                    2
                -
                xNegativeOffset_case_resizable_classic,
            client.getCanvasHeight() /
                2 -
                background.getBounds().height /
                    2
                -
                yNegativeOffset_case_resizable_classic);
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
        background.getBounds().y + 7);
    divider.setLocation(background.getBounds().x + 5, background.getBounds().y + 29);
    title.setLocation(
        background.getBounds().x + background.getBounds().width / 2 - title.getWidth() / 2
            - exitButton.getBounds().width - 4,
        background.getBounds().y + 22);
  }

  public void onCanvasSizeChanged(CanvasSizeChanged event) {
    updateOverlayLocation();
  }

  public void onViewportChanged(ViewportChanged event) {
    updateOverlayLocation();
  }

  private boolean updatedOverlayLocation = false;

  public void onGameTick(GameTick event) {
    if (SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay() && !updatedOverlayLocation) {
      updatedOverlayLocation = true;
      updateOverlayLocation();
    } else if (updatedOverlayLocation == true) {
      updatedOverlayLocation = false;
    }
    background.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
    exitButton.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
    divider.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
    title.setVisible(SAVE_DATA_BUTTON_OVERLAY.displayPathPickerOverlay());
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    background.render(graphics);
    exitButton.render(graphics);
    divider.render(graphics);
    title.render(graphics);
    return null;
  }

  private MouseEvent compareAndReturnDivergent(
      MouseEvent defaultMouseEvent,
      MouseEvent[] potentiallyModifiedMouseEvents) {
    MouseEvent[] modifiedMouseEvents = Stream
        .of(potentiallyModifiedMouseEvents)
        .filter(
            potentiallyModifiedMouseEvent -> !potentiallyModifiedMouseEvent.equals(defaultMouseEvent))
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
        });
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
        mouseEvent,
        new MouseEvent[] {
            exitButton.mousePressed(mouseEvent),
            background.mousePressed(mouseEvent),
        });
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
        mouseEvent,
        new MouseEvent[] {
            exitButton.mouseReleased(mouseEvent),
            background.mouseReleased(mouseEvent),
        });
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
        });
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    return compareAndReturnDivergent(
        mouseEvent,
        new MouseEvent[] {
            exitButton.mouseMoved(mouseEvent),
            background.mouseMoved(mouseEvent),
        });
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
