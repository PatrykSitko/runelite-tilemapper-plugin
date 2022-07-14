package net.runelite.client.plugins.tileMapper;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;

import javax.inject.Inject;

import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.OverlayUtil;

public class TileMapperOverlay extends Overlay{
	private final TileMapperConfig config;
	private final TileMapperPlugin plugin;
    
	@Inject
	private TileMapperOverlay(TileMapperConfig config, TileMapperPlugin plugin)
	{
		this.config = config;
		this.plugin = plugin;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		setPriority(OverlayPriority.MED);
	}

    @Override
    public Dimension render(Graphics2D graphics) {
        for(Integer x:plugin.getCollectedTileLocations().keySet()){
            for(Integer y:plugin.getCollectedTileLocations().get(x)){
            final Polygon poly = Perspective.getCanvasTilePoly(plugin.getClient(), new LocalPoint(x,y));
            
            if (poly == null)
            {
                continue;
            }

            OverlayUtil.renderPolygon(graphics, poly, config.mappedTileFillColor(), config.mappedTileFillColor(), new BasicStroke((float) 2));}}
        graphics.setColor(new Color(191,255,0));
        graphics.fillRect(800, 0, 100, 100);
        return null;
    }
    
}
