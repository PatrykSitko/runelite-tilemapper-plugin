package net.runelite.client.plugins.tileMapper.SaveTileDataToPathOverlayComponents;

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

public class Background implements RenderableEntity {

  public static class PositionedImage {

    private final BufferedImage image;
    private final int x, y, width, height;

    public PositionedImage(
      BufferedImage image,
      int x,
      int y,
      int width,
      int height
    ) {
      this.image = image.getSubimage(0, 0, width, height);
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
    }

    public void draw(Graphics2D graphics) {
      graphics.drawImage(image, x, y, width, height, null);
    }
  }

  @AllArgsConstructor
  @Getter
  public static enum Type {
    LIGHT(
      // background
      loadImage("../backgrounds/background-light.png"),
      // corners
      loadImage("../borders/corners/left-top-corner-light.png"),
      loadImage("../borders/corners/right-top-corner-light.png"),
      loadImage("../borders/corners/left-bottom-corner-light.png"),
      loadImage("../borders/corners/right-bottom-corner-light.png"),
      // borders
      loadImage("../borders/left-border-light.png"),
      loadImage("../borders/top-border-light.png"),
      loadImage("../borders/right-border-light.png"),
      loadImage("../borders/bottom-border-light.png")
    ),
    DARK(
      // background
      loadImage("../backgrounds/background-dark.png"),
      // corners
      loadImage("../borders/corners/left-top-corner-dark.png"),
      loadImage("../borders/corners/right-top-corner-dark.png"),
      loadImage("../borders/corners/left-bottom-corner-dark.png"),
      loadImage("../borders/corners/right-bottom-corner-dark.png"),
      // borders
      loadImage("../borders/left-border-dark.png"),
      loadImage("../borders/top-border-dark.png"),
      loadImage("../borders/right-border-dark.png"),
      loadImage("../borders/bottom-border-dark.png")
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

  private volatile ArrayList<PositionedImage> background = new ArrayList<>();
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

  private float calculateAmmountOfHorizontalBackgroundPieces() {
    final int backgroundWidth = backgroundType.getBackground().getWidth();
    final int containerWidth = this.bounds.width;
    final double ammountOfFittingPieces =
      ((double) containerWidth) / ((double) backgroundWidth);
    return Float.parseFloat(DECIMAL_FORMAT.format(ammountOfFittingPieces));
  }

  private float calculateAmmountOfVerticalBackgroundPieces() {
    final int backgroundHeight = backgroundType.getBackground().getHeight();
    final int containerHeight = this.bounds.height;
    final double ammountOfFittingPieces =
      ((double) containerHeight) / ((double) backgroundHeight);
    return Float.parseFloat(DECIMAL_FORMAT.format(ammountOfFittingPieces));
  }

  private float calculateAmmountOfHorizontalBorderPieces() {
    final int cornerPieceWidth = backgroundType.getLeftTopCorner().getWidth();
    final int borderPieceWidth = backgroundType.getTopBorder().getWidth();
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
    final int cornerPieceHeight = backgroundType.getLeftTopCorner().getHeight();
    final int borderPieceHeight = backgroundType.getLeftBorder().getHeight();
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
      background = new ArrayList<>();
      border = new ArrayList<>();
      populateBackgroundArray(background);
      populateBorderArrayWithCornerEntries(border);
      populateBorderArrayWithHorizontalEntries(border);
      populateBorderArrayWithVerticalEntries(border);
    }
    background.forEach(entry -> entry.draw(graphics));
    border.forEach(entry -> entry.draw(graphics));
  }

  private void populateBackgroundArray(ArrayList<PositionedImage> background) {
    final BufferedImage backgroundImage = backgroundType.getBackground();
    final int startingXposition = this.bounds.x;
    final int startingYposition = this.bounds.y;
    final float ammountOfColumnsFloat = calculateAmmountOfHorizontalBackgroundPieces();
    final float ammountOfRowsFloat = calculateAmmountOfVerticalBackgroundPieces();
    final int pieceWidth = backgroundImage.getWidth();
    final int pieceHeight = backgroundImage.getHeight();
    final int lastPieceWidth = (int) (
      backgroundImage.getWidth() *
      (ammountOfColumnsFloat - (int) ammountOfColumnsFloat)
    );
    final int lastPieceHeight = (int) (
      backgroundImage.getHeight() *
      (ammountOfRowsFloat - (int) ammountOfRowsFloat)
    );
    final int ammountOfColumns = (int) ammountOfColumnsFloat +
    (lastPieceWidth != 0 ? 1 : 0);
    final int ammountOfRows = (int) ammountOfRowsFloat +
    (lastPieceHeight != 0 ? 1 : 0);
    ArrayList<Thread> backgroundRowThreads = new ArrayList<>();
    for (int column = 0; column <= ammountOfColumns - 1; column++) {
      final int finalColumn = column;
      final Thread rowThread = new Thread(
        () -> {
          for (int row = 0; row <= ammountOfRows - 1; row++) {
            final boolean isLastColumn = finalColumn == ammountOfColumns - 1;
            final boolean isLastRow = row == ammountOfRows - 1;
            final int x = startingXposition + finalColumn * pieceWidth;
            final int y = startingYposition + row * pieceHeight;
            final int width = isLastColumn && lastPieceWidth > 0
              ? lastPieceWidth
              : pieceWidth;
            final int height = isLastRow && lastPieceHeight > 0
              ? lastPieceHeight
              : pieceHeight;
            synchronized (Background.class) {
              background.add(
                new PositionedImage(backgroundImage, x, y, width, height)
              );
            }
          }
        }
      );
      backgroundRowThreads.add(rowThread);
    }
    backgroundRowThreads.forEach(
      rowThread -> {
        rowThread.setDaemon(true);
        rowThread.start();
      }
    );
  }

  private void populateBorderArrayWithCornerEntries(
    ArrayList<PositionedImage> border
  ) {
    final BufferedImage leftTop = backgroundType.getLeftTopCorner();
    border.add(
      new PositionedImage(
        leftTop,
        bounds.x,
        bounds.y,
        leftTop.getWidth(),
        leftTop.getHeight()
      )
    );

    final BufferedImage rightTop = backgroundType.getRightTopCorner();
    border.add(
      new PositionedImage(
        rightTop,
        bounds.x + bounds.width - rightTop.getWidth(),
        bounds.y,
        rightTop.getWidth(),
        rightTop.getHeight()
      )
    );

    final BufferedImage leftBottom = backgroundType.getLeftBottomCorner();
    border.add(
      new PositionedImage(
        leftBottom,
        bounds.x,
        bounds.y + bounds.height - leftBottom.getHeight(),
        leftBottom.getWidth(),
        leftBottom.getHeight()
      )
    );

    final BufferedImage rightBottom = backgroundType.getRightBottomCorner();
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
    final BufferedImage topBorderPiece = backgroundType.getTopBorder();
    final BufferedImage bottomBorderPiece = backgroundType.getBottomBorder();
    final float ammountOfPieces = calculateAmmountOfHorizontalBorderPieces();
    final int lastPieceWidth = (int) (
      topBorderPiece.getWidth() * (ammountOfPieces - (int) ammountOfPieces)
    );
    final int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
    (lastPieceWidth != 0 ? 1 : 0);
    final int borderPieceWidth = topBorderPiece.getWidth();
    int xPos = bounds.x + backgroundType.getLeftTopCorner().getWidth();
    final int topYpos = bounds.y;
    final int bottomYpos =
      bounds.y + bounds.height - backgroundType.getBottomBorder().getHeight();
    for (
      int currentPiece = 0;
      currentPiece <= totalAmmountOfPiecesToBeDrawn - 1;
      currentPiece++
    ) {
      final boolean useLastPieceWidth =
        currentPiece == totalAmmountOfPiecesToBeDrawn - 1 && lastPieceWidth > 0;
      border.add(
        new PositionedImage(
          topBorderPiece,
          xPos,
          topYpos,
          useLastPieceWidth ? lastPieceWidth : topBorderPiece.getWidth(),
          topBorderPiece.getHeight()
        )
      );
      border.add(
        new PositionedImage(
          bottomBorderPiece,
          xPos,
          bottomYpos,
          useLastPieceWidth ? lastPieceWidth : bottomBorderPiece.getWidth(),
          bottomBorderPiece.getHeight()
        )
      );
      xPos += borderPieceWidth;
    }
  }

  private void populateBorderArrayWithVerticalEntries(
    ArrayList<PositionedImage> border
  ) {
    final BufferedImage leftBorderPiece = backgroundType.getLeftBorder();
    final BufferedImage rightBorderPiece = backgroundType.getRightBorder();
    final float ammountOfPieces = calculateAmmountOfVerticalBorderPieces();
    final int lastPieceHeigh = (int) (
      leftBorderPiece.getHeight() * (ammountOfPieces - (int) ammountOfPieces)
    );
    final int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
    (lastPieceHeigh != 0 ? 1 : 0);
    final int borderPieceHeight = leftBorderPiece.getHeight();
    int yPos = bounds.y + backgroundType.getLeftTopCorner().getHeight();
    final int leftXpos = bounds.x;
    final int rightXpos = bounds.x + bounds.width - leftBorderPiece.getWidth();
    for (
      int currentPiece = 0;
      currentPiece <= totalAmmountOfPiecesToBeDrawn - 1;
      currentPiece++
    ) {
      final boolean useLastPieceHeight =
        currentPiece == totalAmmountOfPiecesToBeDrawn - 1 && lastPieceHeigh > 0;
      border.add(
        new PositionedImage(
          leftBorderPiece,
          leftXpos,
          yPos,
          leftBorderPiece.getWidth(),
          useLastPieceHeight ? lastPieceHeigh : leftBorderPiece.getHeight()
        )
      );
      border.add(
        new PositionedImage(
          rightBorderPiece,
          rightXpos,
          yPos,
          rightBorderPiece.getWidth(),
          useLastPieceHeight ? lastPieceHeigh : rightBorderPiece.getHeight()
        )
      );
      yPos += borderPieceHeight;
    }
  }
}
