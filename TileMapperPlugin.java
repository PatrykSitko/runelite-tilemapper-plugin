package net.runelite.client.plugins.tileMapper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import javax.inject.Inject;

import com.google.inject.Provides;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.events.CanvasSizeChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.tileMapper.events.ViewportChanged;
import net.runelite.client.plugins.tileMapper.helpers.Viewport;
import net.runelite.client.ui.overlay.OverlayManager;

@PluginDescriptor(name = "Tile Mapper", description = "Maps tiles the player walked on and saved the values to a file while displaying marked tiles.", tags = {
    "tiles" }, enabledByDefault = false)
public class TileMapperPlugin extends Plugin implements Runnable {

  public static final String CONFIG_GROUP = "tilemapper";
  private volatile boolean collectTileLocations = false;

  @Getter
  private volatile HashMap<Integer, ArrayList<Integer>> collectedTileLocations = new HashMap<>();

  private volatile Thread tileLocationsCollectionThread;

  @Inject
  private MouseManager mouseManager;

  @Inject
  private KeyManager keyManager;

  @Getter
  @Inject
  private volatile Client client;

  @Inject
  private OverlayManager overlayManager;

  @Inject
  private TileMapperOverlay tileMapperOverlay;

  @Inject
  @Getter
  private SaveDataButtonOverlay saveDataButtonOverlay;

  @Inject
  private SaveTileDataToPathOverlay saveTileDataToPathOverlay;

  @Inject
  private volatile EventBus eventBus;

  private volatile Viewport previousViewport = null;

  public Viewport getCurrentViewportType() {
    return Viewport.getCurrent(client);
  }

  public boolean collectedTileLocationsDataExists() {
    return collectedTileLocations.size() > 0;
  }

  public void clearCollectedTileLocationsData() {
    collectedTileLocations = new HashMap<>();
  }

  private void notifyEventBusAboutEventualViewportChange() {
    Viewport currentViewport = Viewport.getCurrent(client);
    if (previousViewport != currentViewport) {
      previousViewport = currentViewport;
      ViewportChanged viewportChanged = new ViewportChanged();
      viewportChanged.setViewport(currentViewport);
      eventBus.post(viewportChanged);
    }
  }

  @Override
  protected void startUp() throws Exception {
    overlayManager.add(tileMapperOverlay);
    overlayManager.add(saveDataButtonOverlay);
    overlayManager.add(saveTileDataToPathOverlay);
    mouseManager.registerMouseListener(saveDataButtonOverlay);
    mouseManager.registerMouseListener(saveTileDataToPathOverlay);
    keyManager.registerKeyListener(saveTileDataToPathOverlay);
  }

  @Override
  protected void shutDown() throws Exception {
    overlayManager.remove(tileMapperOverlay);
    overlayManager.remove(saveDataButtonOverlay);
    overlayManager.remove(saveTileDataToPathOverlay);
    mouseManager.unregisterMouseListener(saveDataButtonOverlay);
    mouseManager.unregisterMouseListener(saveTileDataToPathOverlay);
    keyManager.unregisterKeyListener(saveTileDataToPathOverlay);
  }

  @Provides
  TileMapperConfig provideConfig(ConfigManager configManager) {
    return configManager.getConfig(TileMapperConfig.class);
  }

  @Subscribe
  public void onViewportChanged(ViewportChanged event) {
    saveTileDataToPathOverlay.onViewportChanged(event);
  }

  @Subscribe
  public void onGameTick(GameTick event) {
    notifyEventBusAboutEventualViewportChange();
    saveTileDataToPathOverlay.onGameTick(event);
  }

  @Subscribe
  public void onCanvasSizeChanged(CanvasSizeChanged event) {
    saveTileDataToPathOverlay.onCanvasSizeChanged(event);
  }

  @Subscribe
  public void onGameStateChanged(GameStateChanged event) {
    switch (event.getGameState()) {
      default:
      case LOADING:
      case HOPPING:
      case LOGIN_SCREEN:
        collectTileLocations = false;
        if (tileLocationsCollectionThread != null &&
            (!tileLocationsCollectionThread.isInterrupted() ||
                tileLocationsCollectionThread.isAlive())) {
          tileLocationsCollectionThread.interrupt();
        }
        break;
      case LOGGED_IN:
        collectTileLocations = true;
        tileLocationsCollectionThread = new Thread(this);
        tileLocationsCollectionThread.setName("tileLocationsCollectionThread");
        tileLocationsCollectionThread.setDaemon(true);
        tileLocationsCollectionThread.start();
        break;
    }
  }

  @Override
  public void run() {
    do {
      if (!collectTileLocations) {
        continue;
      }
      LocalPoint localPlayerLocation = LocalPoint.fromWorld(
          client,
          client.getLocalPlayer().getWorldLocation());
      if (collectedTileLocations.get(localPlayerLocation.getX()) == null) {
        collectedTileLocations.put(
            localPlayerLocation.getX(),
            new ArrayList<>(Arrays.asList(localPlayerLocation.getY())));
      } else {
        ArrayList<Integer> Ylocations = collectedTileLocations.get(
            localPlayerLocation.getX());
        if (!Ylocations.contains(localPlayerLocation.getY())) {
          Ylocations.add(localPlayerLocation.getY());
          collectedTileLocations.put(localPlayerLocation.getX(), Ylocations);
        }
      }
    } while (!tileLocationsCollectionThread.isInterrupted());
  }
}
