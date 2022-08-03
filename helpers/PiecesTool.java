package net.runelite.client.plugins.tileMapper.helpers;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import lombok.Getter;
import net.openhft.chronicle.core.annotation.Positive;
import net.openhft.chronicle.values.Array;

public interface PiecesTool {

        static interface Calculator {
                static final String DEFAULT_DECIMAL_FORMAT_PATTERN = "0.00";
                static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat(DEFAULT_DECIMAL_FORMAT_PATTERN);

                static enum Orientation {
                        HORIZONTAL, VERTICAL;

                        @Getter
                        private int pieceSize;

                        public Orientation setPieceSize(@Positive int pieceSize) {
                                this.pieceSize = pieceSize;
                                return this;
                        }
                }

                static float calculateAmmountOfPieces(@Nonnull Orientation pieceSize, @Positive int availableSpace) {
                        final double ammountOfFittingPieces = ((double) availableSpace)
                                        / ((double) pieceSize.getPieceSize());
                        return Float.parseFloat(DECIMAL_FORMATTER.format(ammountOfFittingPieces));
                }
        }

        static interface Populator {

                static interface SynchronizationKeys {

                        static interface BACKGROUND {
                        }

                        static interface BORDER {
                        }

                        static interface HORIZONTAL_LINE {
                        }

                        static interface VERTICAL_LINE {
                        }
                }

                /**
                 * 
                 * @param arrayToPopulate the ArrayList where the positioned images will be put
                 *                        in.
                 * @param image           the image to be used.
                 * @param x               the x location where the vertical line will start.
                 * @param y               the y location where the vertical line will be
                 *                        positioned.
                 * @param height          the requested heigh of the vertical line.
                 */
                static void populateVerticalLine(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
                                @Nonnull final BufferedImage image, @Positive int x, @Positive int y,
                                @Positive int height) {
                        final ArrayList<PositionedImage> positionedImages = new ArrayList<>();
                        final int startingYposition = y;
                        final int xPos = x;
                        final float ammountOfPiecesFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.VERTICAL
                                                        .setPieceSize(image.getHeight()),
                                        height);
                        final int pieceHeight = image.getHeight();
                        final int lastPieceHeight = (int) (image.getHeight() *
                                        (ammountOfPiecesFloat - (int) ammountOfPiecesFloat));
                        final int ammountOfPieces = (int) ammountOfPiecesFloat +
                                        (lastPieceHeight != 0 ? 1 : 0);
                        for (int iterator = 0; iterator <= ammountOfPieces - 1; iterator++) {
                                final boolean isLastPiece = iterator == ammountOfPieces - 1;
                                final int positionedImageHeight = isLastPiece && lastPieceHeight > 0
                                                ? lastPieceHeight
                                                : pieceHeight;
                                final int yPos = startingYposition + iterator * pieceHeight;
                                synchronized (Populator.SynchronizationKeys.VERTICAL_LINE.class) {
                                        positionedImages.add(
                                                        new PositionedImage(image, xPos, yPos, image.getWidth(),
                                                                        positionedImageHeight));
                                }
                        }
                        synchronized (Populator.SynchronizationKeys.VERTICAL_LINE.class) {
                                arrayToPopulate.addAll(positionedImages);
                        }
                }

                /**
                 * 
                 * @param arrayToPopulate the ArrayList where the positioned images will be put
                 *                        in.
                 * @param image           the image to be used.
                 * @param x               the x location where the horizontal line will start.
                 * @param y               the y location where the horizontal line will be
                 *                        positioned.
                 * @param width           the requested width of the horizontal line.
                 */
                static void populateHorizontalLine(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
                                @Nonnull final BufferedImage image, @Positive int x, @Positive int y,
                                @Positive int width) {
                        final ArrayList<PositionedImage> positionedImages = new ArrayList<>();
                        final int startingXposition = x;
                        final int yPos = y;
                        final float ammountOfPiecesFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.HORIZONTAL
                                                        .setPieceSize(image.getWidth()),
                                        width);
                        final int pieceWidth = image.getWidth();
                        final int lastPieceWidth = (int) (image.getWidth() *
                                        (ammountOfPiecesFloat - (int) ammountOfPiecesFloat));
                        final int ammountOfPieces = (int) ammountOfPiecesFloat +
                                        (lastPieceWidth != 0 ? 1 : 0);
                        for (int iterator = 0; iterator <= ammountOfPieces - 1; iterator++) {
                                final boolean isLastPiece = iterator == ammountOfPieces - 1;
                                final int positionedImageWidth = isLastPiece && lastPieceWidth > 0
                                                ? lastPieceWidth
                                                : pieceWidth;
                                final int xPos = startingXposition + iterator * pieceWidth;
                                synchronized (Populator.SynchronizationKeys.HORIZONTAL_LINE.class) {
                                        positionedImages.add(
                                                        new PositionedImage(image, xPos, yPos, positionedImageWidth,
                                                                        image.getHeight()));
                                }
                        }
                        synchronized (Populator.SynchronizationKeys.HORIZONTAL_LINE.class) {
                                arrayToPopulate.addAll(positionedImages);
                        }
                }

                /**
                 * 
                 * @param arrayToPopulate the ArrayList where the positioned images will be put
                 *                        in.
                 * @param backgroundImage the image to be used.
                 * @param availableSpace  the bounds of the drawn component.
                 */
                static void populateBackground(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
                                @Nonnull final BufferedImage backgroundImage, @Nonnull final Rectangle availableSpace) {
                        final ArrayList<PositionedImage> positionedImages = new ArrayList<>();
                        final int startingXposition = availableSpace.x;
                        final int startingYposition = availableSpace.y;
                        final float ammountOfColumnsFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.HORIZONTAL
                                                        .setPieceSize(backgroundImage.getWidth()),
                                        availableSpace.width);
                        final float ammountOfRowsFloat = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.VERTICAL
                                                        .setPieceSize(backgroundImage.getHeight()),
                                        availableSpace.height);
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
                                                                final boolean isLastColumn = finalColumn == ammountOfColumns
                                                                                - 1;
                                                                final boolean isLastRow = row == ammountOfRows - 1;
                                                                final int x = startingXposition
                                                                                + finalColumn * pieceWidth;
                                                                final int y = startingYposition + row * pieceHeight;
                                                                final int width = isLastColumn && lastPieceWidth > 0
                                                                                ? lastPieceWidth
                                                                                : pieceWidth;
                                                                final int height = isLastRow && lastPieceHeight > 0
                                                                                ? lastPieceHeight
                                                                                : pieceHeight;
                                                                synchronized (Populator.SynchronizationKeys.BACKGROUND.class) {
                                                                        positionedImages.add(
                                                                                        new PositionedImage(
                                                                                                        backgroundImage,
                                                                                                        x, y, width,
                                                                                                        height));
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
                        Thread populatingArrayThread = new Thread(() -> {
                                while (backgroundRowThreads.stream().filter(rowThread -> rowThread.isAlive())
                                                .count() > 0) {
                                        continue;
                                }
                                synchronized (Populator.SynchronizationKeys.BACKGROUND.class) {
                                        arrayToPopulate.addAll(positionedImages);
                                }
                        });
                        populatingArrayThread.setDaemon(true);
                        populatingArrayThread.start();
                }

                /**
                 * 
                 * @param arrayToPopulate the ArrayList where the positioned images will be put
                 *                        in.
                 * @param cornerPieces    the corner piece images put into a primitive array in
                 *                        the following order: top-left, top-right, bottom-left,
                 *                        bottom-right.
                 * @param borderPieces    the border piece images put into a primitive array in
                 *                        the following order: top, right, bottom, left.
                 * @param availableSpace  the bounds of the drawn component.
                 */
                @Array(length = 4)
                static void populateBorder(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
                                @Nonnull final BufferedImage[] cornerPieces,
                                @Nonnull final BufferedImage[] borderPieces,
                                @Nonnull final Rectangle availableSpace) {
                        final ArrayList<PositionedImage> positionedImages = new ArrayList<>();

                        int iteration = 0;
                        final int TOP_LEFT = 0, TOP = 0;
                        final int TOP_RIGHT = 1, RIGHT = 1;
                        final int BOTTOM_LEFT = 2, BOTTOM = 2;
                        final int BOTTOM_RIGHT = 3, LEFT = 3;
                        for (BufferedImage cornerPiece : cornerPieces) {
                                switch (iteration++) {
                                        case TOP_LEFT:
                                                synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                        positionedImages.add(new PositionedImage(cornerPiece,
                                                                        availableSpace.x,
                                                                        availableSpace.y, cornerPiece.getWidth(),
                                                                        cornerPiece.getHeight()));
                                                }
                                                break;
                                        case TOP_RIGHT:
                                                synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                        positionedImages
                                                                        .add(new PositionedImage(cornerPiece,
                                                                                        availableSpace.x + availableSpace.width
                                                                                                        - cornerPiece.getWidth(),
                                                                                        availableSpace.y,
                                                                                        cornerPiece.getWidth(),
                                                                                        cornerPiece.getHeight()));
                                                }
                                                break;
                                        case BOTTOM_LEFT:
                                                synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                        positionedImages.add(new PositionedImage(cornerPiece,
                                                                        availableSpace.x,
                                                                        availableSpace.y + availableSpace.height
                                                                                        - cornerPiece.getHeight(),
                                                                        cornerPiece.getWidth(),
                                                                        cornerPiece.getHeight()));
                                                }
                                                break;
                                        case BOTTOM_RIGHT:
                                                synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                        positionedImages
                                                                        .add(new PositionedImage(cornerPiece,
                                                                                        availableSpace.x + availableSpace.width
                                                                                                        - cornerPiece.getWidth(),
                                                                                        availableSpace.y + availableSpace.height
                                                                                                        - cornerPiece.getHeight(),
                                                                                        cornerPiece.getWidth(),
                                                                                        cornerPiece.getHeight()));
                                                }
                                                break;
                                }
                        }
                        // HORIZONTAL:
                        final int HORIZONTAL_PIECE = 0;
                        final int VERTICAL_PIECE = 1;
                        final int borderPieceWidth = borderPieces[HORIZONTAL_PIECE].getWidth();
                        final int spaceToPopulateHorizontal = availableSpace.width
                                        - cornerPieces[HORIZONTAL_PIECE].getWidth() * 2;
                        final float ammountOfPiecesHorizontal = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.HORIZONTAL.setPieceSize(borderPieceWidth),
                                        spaceToPopulateHorizontal);
                        final int lastPieceWidth = (int) (borderPieces[HORIZONTAL_PIECE].getWidth()
                                        * (ammountOfPiecesHorizontal - (int) ammountOfPiecesHorizontal));
                        final int totalAmmountOfPiecesToBeDrawnHorizontal = (int) ammountOfPiecesHorizontal +
                                        (lastPieceWidth != 0 ? 1 : 0);
                        int xPos = availableSpace.x + cornerPieces[0].getWidth();
                        final int topYpos = availableSpace.y;
                        final int bottomYpos = availableSpace.y + availableSpace.height
                                        - borderPieces[HORIZONTAL_PIECE].getHeight();
                        // VERTICAL:
                        final int borderPieceHeight = borderPieces[VERTICAL_PIECE].getHeight();
                        final int spaceToPopulateVertical = availableSpace.height
                                        - cornerPieces[0].getHeight() * 2;
                        final float ammountOfPiecesVertical = PiecesTool.Calculator.calculateAmmountOfPieces(
                                        PiecesTool.Calculator.Orientation.VERTICAL.setPieceSize(borderPieceHeight),
                                        spaceToPopulateVertical);
                        final int lastPieceHeigh = (int) (borderPieces[VERTICAL_PIECE].getHeight()
                                        * (ammountOfPiecesVertical - (int) ammountOfPiecesVertical));
                        final int totalAmmountOfPiecesToBeDrawnVertical = (int) ammountOfPiecesVertical +
                                        (lastPieceHeigh != 0 ? 1 : 0);
                        int yPos = availableSpace.y + cornerPieces[0].getHeight();
                        final int leftXpos = availableSpace.x;
                        final int rightXpos = availableSpace.x + availableSpace.width
                                        - borderPieces[VERTICAL_PIECE].getWidth();
                        iteration = 0;
                        for (BufferedImage borderPiece : borderPieces) {
                                switch (iteration++) {
                                        case TOP:
                                                xPos = availableSpace.x + cornerPieces[0].getWidth();
                                                for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawnHorizontal
                                                                - 1; currentPiece++) {
                                                        final boolean useLastPieceWidth = currentPiece == totalAmmountOfPiecesToBeDrawnHorizontal
                                                                        - 1
                                                                        && lastPieceWidth > 0;
                                                        synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                                positionedImages.add(
                                                                                new PositionedImage(
                                                                                                borderPiece,
                                                                                                xPos,
                                                                                                topYpos,
                                                                                                useLastPieceWidth
                                                                                                                ? lastPieceWidth
                                                                                                                : borderPiece.getWidth(),
                                                                                                borderPiece.getHeight()));
                                                        }
                                                        xPos += borderPieceWidth;
                                                }
                                                break;
                                        case RIGHT:
                                                yPos = availableSpace.y + cornerPieces[0].getHeight();
                                                for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawnVertical
                                                                - 1; currentPiece++) {
                                                        final boolean useLastPieceHeight = currentPiece == totalAmmountOfPiecesToBeDrawnVertical
                                                                        - 1
                                                                        && lastPieceHeigh > 0;
                                                        synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                                positionedImages.add(
                                                                                new PositionedImage(
                                                                                                borderPiece,
                                                                                                rightXpos,
                                                                                                yPos,
                                                                                                borderPiece.getWidth(),
                                                                                                useLastPieceHeight
                                                                                                                ? lastPieceHeigh
                                                                                                                : borderPiece.getHeight()));
                                                        }
                                                        yPos += borderPieceHeight;
                                                }
                                                break;
                                        case BOTTOM:
                                                xPos = availableSpace.x + cornerPieces[0].getWidth();
                                                for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawnHorizontal
                                                                - 1; currentPiece++) {
                                                        final boolean useLastPieceWidth = currentPiece == totalAmmountOfPiecesToBeDrawnHorizontal
                                                                        - 1
                                                                        && lastPieceWidth > 0;
                                                        synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                                arrayToPopulate.add(
                                                                                new PositionedImage(
                                                                                                borderPiece,
                                                                                                xPos,
                                                                                                bottomYpos,
                                                                                                useLastPieceWidth
                                                                                                                ? lastPieceWidth
                                                                                                                : borderPiece.getWidth(),
                                                                                                borderPiece.getHeight()));
                                                        }
                                                        xPos += borderPieceWidth;
                                                }
                                                break;
                                        case LEFT:
                                                yPos = availableSpace.y + cornerPieces[0].getHeight();
                                                for (int currentPiece = 0; currentPiece <= totalAmmountOfPiecesToBeDrawnVertical
                                                                - 1; currentPiece++) {
                                                        final boolean useLastPieceHeight = currentPiece == totalAmmountOfPiecesToBeDrawnVertical
                                                                        - 1
                                                                        && lastPieceHeigh > 0;
                                                        synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                                                positionedImages.add(
                                                                                new PositionedImage(
                                                                                                borderPiece,
                                                                                                leftXpos,
                                                                                                yPos,
                                                                                                borderPiece.getWidth(),
                                                                                                useLastPieceHeight
                                                                                                                ? lastPieceHeigh
                                                                                                                : borderPiece.getHeight()));
                                                        }
                                                        yPos += borderPieceHeight;
                                                }
                                                break;
                                }
                        }
                        synchronized (Populator.SynchronizationKeys.BORDER.class) {
                                arrayToPopulate.addAll(positionedImages);
                        }
                }
        }
}
