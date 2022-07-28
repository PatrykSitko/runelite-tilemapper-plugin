package net.runelite.client.plugins.tileMapper.Overlays;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.inject.Inject;

import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.plugins.tileMapper.TileMapperConfig;
import net.runelite.client.plugins.tileMapper.TileMapperPlugin;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TileMapperOverlay extends Overlay {

  private final TileMapperConfig config;
  private final TileMapperPlugin plugin;

  @Inject
  private TileMapperOverlay(TileMapperConfig config, TileMapperPlugin plugin) {
    this.config = config;
    this.plugin = plugin;
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_SCENE);
    setPriority(OverlayPriority.MED);
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    renderCollectedTileLocations(graphics);
    return null;
  }

  public void renderCollectedTileLocations(Graphics2D graphics) {
    for (Integer x : plugin.getCollectedTileLocations().keySet()) {
      for (Integer y : plugin.getCollectedTileLocations().get(x)) {
        final Polygon poly = Perspective.getCanvasTilePoly(
            plugin.getClient(),
            new LocalPoint(x, y));

        if (poly == null) {
          continue;
        }

        OverlayUtil.renderPolygon(
            graphics,
            poly,
            config.mappedTileColor(),
            config.mappedTileColor(),
            new BasicStroke((float) 2));
      }
    }
  }
}
