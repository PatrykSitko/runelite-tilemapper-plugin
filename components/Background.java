package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.helpers.ImageLoader;
import net.runelite.client.plugins.tileMapper.helpers.PiecesTool;
import net.runelite.client.plugins.tileMapper.helpers.PositionedImage;
import net.runelite.client.ui.overlay.RenderableEntity;

public class Background implements RenderableEntity, MouseListener {

  @AllArgsConstructor
  @Getter
  public static enum Type {
    LIGHT(
        // background
        ImageLoader.loadImage("../backgrounds/background-light.png"),
        // corners
        ImageLoader.loadImage("../borders/corners/left-top-corner-light.png"),
        ImageLoader.loadImage("../borders/corners/right-top-corner-light.png"),
        ImageLoader.loadImage("../borders/corners/left-bottom-corner-light.png"),
        ImageLoader.loadImage("../borders/corners/right-bottom-corner-light.png"),
        // borders
        ImageLoader.loadImage("../borders/left-border-light.png"),
        ImageLoader.loadImage("../borders/top-border-light.png"),
        ImageLoader.loadImage("../borders/right-border-light.png"),
        ImageLoader.loadImage("../borders/bottom-border-light.png")),
    DARK(
        // background
        ImageLoader.loadImage("../backgrounds/background-dark.png"),
        // corners
        ImageLoader.loadImage("../borders/corners/left-top-corner-dark.png"),
        ImageLoader.loadImage("../borders/corners/right-top-corner-dark.png"),
        ImageLoader.loadImage("../borders/corners/left-bottom-corner-dark.png"),
        ImageLoader.loadImage("../borders/corners/right-bottom-corner-dark.png"),
        // borders
        ImageLoader.loadImage("../borders/left-border-dark.png"),
        ImageLoader.loadImage("../borders/top-border-dark.png"),
        ImageLoader.loadImage("../borders/right-border-dark.png"),
        ImageLoader.loadImage("../borders/bottom-border-dark.png"));

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
  }

  public static interface OnOutOfBoundsClickAction {
    public void perform();
  }

  @Getter
  private final Rectangle bounds = new Rectangle();

  private Rectangle previousBounds;
  private final Type backgroundType;

  private volatile ArrayList<PositionedImage> background = new ArrayList<>();
  private ArrayList<PositionedImage> border = new ArrayList<>();

  @Getter
  @Setter
  private boolean visible;

  private boolean isMouseHovering;

  @Setter
  private Background.OnOutOfBoundsClickAction onOutOfBoundsClickAction;

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

  @Override
  public Dimension render(Graphics2D graphics) {
    if (!visible) {
      return null;
    }
    drawBackgroundWidthBorder(graphics);
    return null;
  }

  private void drawBackgroundWidthBorder(Graphics2D graphics) {
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
    final float ammountOfColumnsFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
        PiecesTool.Calculator.Orientation.HORIZONTAL.setPieceSize(backgroundType.getBackground().getWidth()),
        this.bounds.width);
    final float ammountOfRowsFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
        PiecesTool.Calculator.Orientation.VERTICAL.setPieceSize(backgroundType.getBackground().getHeight()),
        this.bounds.height);
    final int pieceWidth = backgroundImage.getWidth();
    final int pieceHeight = backgroundImage.getHeight();
    final int lastPieceWidth = (int) (backgroundImage.getWidth() *
        (ammountOfColumnsFloat - (int) ammountOfColumnsFloat));
    final int lastPieceHeight = (int) (backgroundImage.getHeight() *
        (ammountOfRowsFloat - (int) ammountOfRowsFloat));
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
                    new PositionedImage(backgroundImage, x, y, width, height));
              }
            }
          });
      backgroundRowThreads.add(rowThread);
    }
    backgroundRowThreads.forEach(
        rowThread -> {
          rowThread.setDaemon(true);
          rowThread.start();
        });
  }

  private void populateBorderArrayWithCornerEntries(
      ArrayList<PositionedImage> border) {
    final BufferedImage leftTop = backgroundType.getLeftTopCorner();
    border.add(
        new PositionedImage(
            leftTop,
            bounds.x,
            bounds.y,
            leftTop.getWidth(),
            leftTop.getHeight()));

    final BufferedImage rightTop = backgroundType.getRightTopCorner();
    border.add(
        new PositionedImage(
            rightTop,
            bounds.x + bounds.width - rightTop.getWidth(),
            bounds.y,
            rightTop.getWidth(),
            rightTop.getHeight()));

    final BufferedImage leftBottom = backgroundType.getLeftBottomCorner();
    border.add(
        new PositionedImage(
            leftBottom,
            bounds.x,
            bounds.y + bounds.height - leftBottom.getHeight(),
            leftBottom.getWidth(),
            leftBottom.getHeight()));

    final BufferedImage rightBottom = backgroundType.getRightBottomCorner();
    border.add(
        new PositionedImage(
            rightBottom,
            bounds.x + bounds.width - rightBottom.getWidth(),
            bounds.y + bounds.height - rightBottom.getHeight(),
            rightBottom.getWidth(),
            rightBottom.getHeight()));
  }

  private void populateBorderArrayWithHorizontalEntries(
      ArrayList<PositionedImage> border) {
    final BufferedImage topBorderPiece = backgroundType.getTopBorder();
    final BufferedImage bottomBorderPiece = backgroundType.getBottomBorder();
    final int borderPieceWidth = topBorderPiece.getWidth();
    final int availableSpace = this.bounds.width - backgroundType.getLeftTopCorner().getWidth() * 2;
    final float ammountOfPieces = PiecesTool.Calculator.calculateAmmountOfPieces(
        PiecesTool.Calculator.Orientation.HORIZONTAL.setPieceSize(borderPieceWidth),
        availableSpace);
    final int lastPieceWidth = (int) (topBorderPiece.getWidth() * (ammountOfPieces - (int) ammountOfPieces));
    final int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
        (lastPieceWidth != 0 ? 1 : 0);
    int xPos = bounds.x + backgroundType.getLeftTopCorner().getWidth();
    final int topYpos = bounds.y;
    final int bottomYpos = bounds.y + bounds.height - backgroundType.getBottomBorder().getHeight();
    for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawn - 1; currentPiece++) {
      final boolean useLastPieceWidth = currentPiece == totalAmmountOfPiecesToBeDrawn - 1 && lastPieceWidth > 0;
      border.add(
          new PositionedImage(
              topBorderPiece,
              xPos,
              topYpos,
              useLastPieceWidth ? lastPieceWidth : topBorderPiece.getWidth(),
              topBorderPiece.getHeight()));
      border.add(
          new PositionedImage(
              bottomBorderPiece,
              xPos,
              bottomYpos,
              useLastPieceWidth ? lastPieceWidth : bottomBorderPiece.getWidth(),
              bottomBorderPiece.getHeight()));
      xPos += borderPieceWidth;
    }
  }

  private void populateBorderArrayWithVerticalEntries(
      ArrayList<PositionedImage> border) {
    final BufferedImage leftBorderPiece = backgroundType.getLeftBorder();
    final BufferedImage rightBorderPiece = backgroundType.getRightBorder();
    final int borderPieceHeight = leftBorderPiece.getHeight();
    final int availableSpace = this.bounds.height - backgroundType.getLeftTopCorner().getHeight() * 2;
    final float ammountOfPieces = PiecesTool.Calculator.calculateAmmountOfPieces(
        PiecesTool.Calculator.Orientation.VERTICAL.setPieceSize(borderPieceHeight), availableSpace);
    final int lastPieceHeigh = (int) (leftBorderPiece.getHeight() * (ammountOfPieces - (int) ammountOfPieces));
    final int totalAmmountOfPiecesToBeDrawn = (int) ammountOfPieces +
        (lastPieceHeigh != 0 ? 1 : 0);
    int yPos = bounds.y + backgroundType.getLeftTopCorner().getHeight();
    final int leftXpos = bounds.x;
    final int rightXpos = bounds.x + bounds.width - leftBorderPiece.getWidth();
    for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawn - 1; currentPiece++) {
      final boolean useLastPieceHeight = currentPiece == totalAmmountOfPiecesToBeDrawn - 1 && lastPieceHeigh > 0;
      border.add(
          new PositionedImage(
              leftBorderPiece,
              leftXpos,
              yPos,
              leftBorderPiece.getWidth(),
              useLastPieceHeight ? lastPieceHeigh : leftBorderPiece.getHeight()));
      border.add(
          new PositionedImage(
              rightBorderPiece,
              rightXpos,
              yPos,
              rightBorderPiece.getWidth(),
              useLastPieceHeight ? lastPieceHeigh : rightBorderPiece.getHeight()));
      yPos += borderPieceHeight;
    }
  }

  private boolean mouseInBounds(MouseEvent mouseEvent) {
    final Point mouseLocation = mouseEvent.getPoint();
    final boolean inBounds_horizontalLocation = mouseLocation.x >= bounds.x
        && mouseLocation.x <= bounds.x + bounds.width;
    final boolean inBounds_verticalLocation = mouseLocation.y >= bounds.y &&
        mouseLocation.y <= bounds.y + bounds.height;
    return inBounds_horizontalLocation && inBounds_verticalLocation;
  }

  @Override
  public MouseEvent mouseClicked(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    } else if (visible && !isMouseHovering) {
      if (onOutOfBoundsClickAction != null) {
        onOutOfBoundsClickAction.perform();
      }
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mousePressed(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseReleased(MouseEvent mouseEvent) {
    if (visible && isMouseHovering) {
      mouseEvent.consume();
    }
    return mouseEvent;
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
    if (isMouseHovering && visible) {
      mouseEvent.consume();
    }
    return mouseEvent;
  }

  @Override
  public MouseEvent mouseMoved(MouseEvent mouseEvent) {
    isMouseHovering = mouseInBounds(mouseEvent);

    final MouseEvent substituteMouseEvent = new MouseEvent(
        mouseEvent.getComponent(),
        MouseEvent.MOUSE_MOVED,
        mouseEvent.getWhen(),
        mouseEvent.getModifiersEx(),
        0,
        0,
        mouseEvent.getClickCount(),
        mouseEvent.isPopupTrigger(),
        mouseEvent.getButton());
    return !isMouseHovering | !visible ? mouseEvent : substituteMouseEvent;
  }
}
