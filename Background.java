package net.runelite.client.plugins.tileMapper;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.client.ui.overlay.RenderableEntity;

// TODO draw background fill.
public class Background implements RenderableEntity {

  @AllArgsConstructor
  public static class PositionedImage {

    private final BufferedImage image;
    private final int x, y, width, height;

    public void draw(Graphics2D graphics) {
      graphics.drawImage(image, x, y, width, height, null);
    }
  }

  @AllArgsConstructor
  @Getter
  public static enum Type {
    LIGHT(
      // background
      loadImage("backgrounds/background-light.png"),
      // corners
      loadImage("borders/corners/left-top-corner-light.png"),
      loadImage("borders/corners/right-top-corner-light.png"),
      loadImage("borders/corners/left-bottom-corner-light.png"),
      loadImage("borders/corners/right-bottom-corner-light.png"),
      // borders
      loadImage("borders/left-border-light.png"),
      loadImage("borders/top-border-light.png"),
      loadImage("borders/right-border-light.png"),
      loadImage("borders/bottom-border-light.png")
    ),
    DARK(
      // background
      loadImage("backgrounds/background-dark.png"),
      // corners
      loadImage("borders/corners/left-top-corner-dark.png"),
      loadImage("borders/corners/right-top-corner-dark.png"),
      loadImage("borders/corners/left-bottom-corner-dark.png"),
      loadImage("borders/corners/right-bottom-corner-dark.png"),
      // borders
      loadImage("borders/left-border-dark.png"),
      loadImage("borders/top-border-dark.png"),
      loadImage("borders/right-border-dark.png"),
      loadImage("borders/bottom-border-dark.png")
    );

    // background
    private BufferedImage background;
    // corners
    private BufferedImage leftTopCorner;
    private BufferedImage rightTopCorner;
    private BufferedImage leftBottomCorner;
    private BufferedImage rightBottomCorner;
    // borders
    private BufferedImage leftBorder;
    private BufferedImage topBorder;
    private BufferedImage rightBorder;
    private BufferedImage bottomBorder;

    private static BufferedImage loadImage(String path) {
      BufferedImage image = null;
      try {
        image = ImageIO.read(Type.class.getResource(path));
      } catch (IOException e) {
        e.printStackTrace();
      }
      return image;
    }
  }

  private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("0.00");

  @Getter
  private final Rectangle bounds = new Rectangle();

  private Rectangle previousBounds;
  private final Type backgroundType;

  private ArrayList<PositionedImage> border = new ArrayList<>();

  public Background(int x, int y, int width, int height, Type backgoundType) {
    bounds.setBounds(x, y, width, height);
    this.backgroundType = backgoundType;
  }

  public void setLocation(int x, int y) {
    this.bounds.setLocation(x, y);
  }

  public Point getLocation() {
    return this.bounds.getLocation();
  }

  private float calculateAmmountOfHorizontalBorderPieces() {
    final int cornerPieceWidth = backgroundType
      .getLeftTopCorner()
      .getWidth(null);
    final int borderPieceWidth = backgroundType.getTopBorder().getWidth(null);
    final int containerWidth = this.bounds.width;
    final int borderWidthWithoutCorners =
      containerWidth - (cornerPieceWidth * 2);
    final double ammountOfBorderPiecesFittingInBorder =
      ((double) borderWidthWithoutCorners) / ((double) borderPieceWidth);
    return Float.parseFloat(
      DECIMAL_FORMAT.format(ammountOfBorderPiecesFittingInBorder)
    );
  }

  private float calculateAmmountOfVerticalBorderPieces() {
    final int cornerPieceHeight = backgroundType
      .getLeftTopCorner()
      .getHeight(null);
    final int borderPieceHeight = backgroundType
      .getLeftBorder()
      .getHeight(null);
    final int containerHeight = this.bounds.height;
    final int borderHeightWithoutCorners =
      containerHeight - (cornerPieceHeight * 2);
    final double ammountOfBorderPiecesFittingInBorder =
      ((double) borderHeightWithoutCorners) / ((double) borderPieceHeight);
    return Float.parseFloat(
      DECIMAL_FORMAT.format(ammountOfBorderPiecesFittingInBorder)
    );
  }

  @Override
  public Dimension render(Graphics2D graphics) {
    drawBackgroundBorder(graphics);
    // TODO Auto-generated method stub
    return null;
  }

  private void drawBackgroundBorder(Graphics2D graphics) {
    if (!bounds.equals(previousBounds)) {
      previousBounds = new Rectangle(bounds);
      border = new ArrayList<>();
      populateBorderArrayWithCornerEntries(border);
      populateBorderArrayWithHorizontalEntries(border);
      populateBorderArrayWithVerticalEntries(border);
    }
    border.forEach(entry -> entry.draw(graphics));
  }

  private void populateBorderArrayWithCornerEntries(
    ArrayList<PositionedImage> border
  ) {
    BufferedImage leftTop = backgroundType.getLeftTopCorner();
    border.add(
      new PositionedImage(
        leftTop,
        bounds.x,
        bounds.y,
        leftTop.getWidth(),
        leftTop.getHeight()
      )
    );

    BufferedImage rightTop = backgroundType.getRightTopCorner();
    border.add(
      new PositionedImage(
        rightTop,
        bounds.x + bounds.width - rightTop.getWidth(),
        bounds.y,
        rightTop.getWidth(),
        rightTop.getHeight()
      )
    );

    BufferedImage leftBottom = backgroundType.getLeftBottomCorner();
    border.add(
      new PositionedImage(
        leftBottom,
        bounds.x,
        bounds.y + bounds.height - leftBottom.getHeight(),
        leftBottom.getWidth(),
        leftBottom.getHeight()
      )
    );

    BufferedImage rightBottom = backgroundType.getRightBottomCorner();
    border.add(
      new PositionedImage(
        rightBottom,
        bounds.x + bounds.width - rightBottom.getWidth(),
        bounds.y + bounds.height - rightBottom.getHeight(),
        rightBottom.getWidth(),
        rightBottom.getHeight()
      )
    );
  }

  private void populateBorderArrayWithHorizontalEntries(
    ArrayList<PositionedImage> border
  ) {
    BufferedImage topBorderPiece = backgroundType.getTopBorder();
    BufferedImage bottomBorderPiece = backgroundType.getBottomBorder();
    float ammountOfPieces = calculateAmmountOfHorizontalBorderPieces();
    int lastPieceWidth = (int) (
      topBorderPiece.getWidth() * (ammountOfPieces - (int) ammountOfPieces)
    );
    int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
    (lastPieceWidth != 0.0f ? 1 : 0);
    int borderPieceWidth = topBorderPiece.getWidth();
    int xPos = bounds.x + backgroundType.getLeftTopCorner().getWidth();
    int topYpos = bounds.y;
    int bottomYpos =
      bounds.y + bounds.height - backgroundType.getBottomBorder().getHeight();
    for (
      int currentPiece = 0;
      currentPiece <= totalAmmountOfPiecesToBeDrawn - 1;
      currentPiece++
    ) {
      if (
        currentPiece == totalAmmountOfPiecesToBeDrawn - 1 &&
        lastPieceWidth > 0.0f
      ) {
        final BufferedImage topPiece = topBorderPiece.getSubimage(
          0,
          0,
          lastPieceWidth,
          topBorderPiece.getHeight()
        );
        final BufferedImage bottomPiece = bottomBorderPiece.getSubimage(
          0,
          0,
          lastPieceWidth,
          bottomBorderPiece.getHeight()
        );
        border.add(
          new PositionedImage(
            topPiece,
            xPos,
            topYpos,
            topPiece.getWidth(),
            topPiece.getHeight()
          )
        );
        border.add(
          new PositionedImage(
            bottomPiece,
            xPos,
            bottomYpos,
            bottomPiece.getWidth(),
            bottomPiece.getHeight()
          )
        );
      } else {
        border.add(
          new PositionedImage(
            topBorderPiece,
            xPos,
            topYpos,
            topBorderPiece.getWidth(),
            topBorderPiece.getHeight()
          )
        );
        border.add(
          new PositionedImage(
            bottomBorderPiece,
            xPos,
            bottomYpos,
            bottomBorderPiece.getWidth(),
            bottomBorderPiece.getHeight()
          )
        );
        xPos += borderPieceWidth;
      }
    }
  }

  private void populateBorderArrayWithVerticalEntries(
    ArrayList<PositionedImage> border
  ) {
    BufferedImage leftBorderPiece = backgroundType.getLeftBorder();
    BufferedImage rightBorderPiece = backgroundType.getRightBorder();
    float ammountOfPieces = calculateAmmountOfVerticalBorderPieces();
    int lastPieceHeigh = (int) (
      leftBorderPiece.getHeight() * (ammountOfPieces - (int) ammountOfPieces)
    );
    int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
    (lastPieceHeigh != 0.0f ? 1 : 0);
    int borderPieceHeight = leftBorderPiece.getHeight();
    int yPos = bounds.y + backgroundType.getLeftTopCorner().getHeight();
    int leftXpos = bounds.x;
    int rightXpos = bounds.x + bounds.width - leftBorderPiece.getWidth();
    for (
      int currentPiece = 0;
      currentPiece <= totalAmmountOfPiecesToBeDrawn - 1;
      currentPiece++
    ) {
      if (
        currentPiece == totalAmmountOfPiecesToBeDrawn - 1 &&
        lastPieceHeigh > 0.0f
      ) {
        final BufferedImage leftPiece = leftBorderPiece.getSubimage(
          0,
          0,
          leftBorderPiece.getWidth(),
          lastPieceHeigh
        );
        final BufferedImage rightPiece = rightBorderPiece.getSubimage(
          0,
          0,
          rightBorderPiece.getWidth(),
          lastPieceHeigh
        );
        border.add(
          new PositionedImage(
            leftPiece,
            leftXpos,
            yPos,
            leftPiece.getWidth(),
            leftPiece.getHeight()
          )
        );
        border.add(
          new PositionedImage(
            rightPiece,
            rightXpos,
            yPos,
            rightPiece.getWidth(),
            rightPiece.getHeight()
          )
        );
      } else {
        border.add(
          new PositionedImage(
            leftBorderPiece,
            leftXpos,
            yPos,
            leftBorderPiece.getWidth(),
            leftBorderPiece.getHeight()
          )
        );
        border.add(
          new PositionedImage(
            rightBorderPiece,
            rightXpos,
            yPos,
            rightBorderPiece.getWidth(),
            rightBorderPiece.getHeight()
          )
        );
        yPos += borderPieceHeight;
      }
    }
  }
}
