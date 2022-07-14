package net.runelite.client.plugins.tileMapper;

import java.awt.Color;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;

@ConfigGroup(TileMapperPlugin.CONFIG_GROUP)
public interface TileMapperConfig extends Config{
    @Alpha
	@ConfigItem(
		keyName = "mappedTileColor",
		name = "mapped tile  color",
		description = "Configures the color of the mapped tiles",
		position = 0
	)
	default Color mappedTileColor()
	{
		return new Color(0, 125, 0, 50);
	}
}
