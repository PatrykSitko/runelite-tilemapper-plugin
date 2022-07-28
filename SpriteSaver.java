package net.runelite.client.plugins.tileMapper;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import net.runelite.client.game.SpriteManager;

public interface SpriteSaver {
  static void saveSprite(
    SpriteManager spriteManager,
    int spriteId,
    String saveLocation
  )
    throws IOException {
    ImageIO.write(
      spriteManager.getSprite(spriteId, 0),
      "png",
      new File(saveLocation)
    );
  }
}
