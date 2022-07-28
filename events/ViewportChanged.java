package net.runelite.client.plugins.tileMapper.events;

import lombok.Data;
import net.runelite.client.plugins.tileMapper.helpers.Viewport;

@Data
public class ViewportChanged {

  // new viewport
  private Viewport viewport;
}
