package net.runelite.client.plugins.tileMapper.helpers;

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.annotation.Nonnull;

import lombok.Getter;
import net.openhft.chronicle.values.Array;
import net.runelite.client.plugins.tileMapper.components.Background;

public interface PiecesTool {

    public static interface Calculator {
        static final String DEFAULT_DECIMAL_FORMAT_PATTERN = "0.00";
        static final DecimalFormat DECIMAL_FORMATTER = new DecimalFormat(DEFAULT_DECIMAL_FORMAT_PATTERN);

        public static enum Orientation {
            HORIZONTAL, VERTICAL;

            @Getter
            private int pieceSize;

            public Orientation setPieceSize(int pieceSize) {
                this.pieceSize = pieceSize;
                return this;
            }
        }

        public static float calculateAmmountOfPieces(Orientation pieceSize, int availableSpace) {
            final double ammountOfFittingPieces = ((double) availableSpace) / ((double) pieceSize.getPieceSize());
            return Float.parseFloat(DECIMAL_FORMATTER.format(ammountOfFittingPieces));
        }
    }

    public static interface Populator {

        public static void populateBackground(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
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
                                    positionedImages.add(
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
            new Thread(() -> {
                do {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } while (backgroundRowThreads.stream().filter(rowThread -> rowThread.isAlive()).count() > 0);
                arrayToPopulate.addAll(positionedImages);
            });
        }

        @Array(length = 4)
        public static void populateBorders(@Nonnull ArrayList<PositionedImage> arrayToPopulate,
                @Nonnull final BufferedImage[] borderPieces,
                @Nonnull final Rectangle availableSpace,
                @Nonnull final BufferedImage[] cornerPieces) {
            final ArrayList<PositionedImage> positionedImages = new ArrayList<>();

            int iteration = 0;
            final int TOP_LEFT = 0, TOP = 0;
            final int TOP_RIGHT = 1, RIGHT = 1;
            final int BOTTOM_LEFT = 2, BOTTOM = 2;
            final int BOTTOM_RIGHT = 3, LEFT = 3;
            for (BufferedImage cornerPiece : cornerPieces) {
                switch (iteration++) {
                    case TOP_LEFT:
                        positionedImages.add(new PositionedImage(cornerPiece, availableSpace.x,
                                availableSpace.y, availableSpace.width, availableSpace.height));
                        break;
                    case TOP_RIGHT:
                        positionedImages
                                .add(new PositionedImage(cornerPiece,
                                        availableSpace.x + availableSpace.width - cornerPiece.getWidth(),
                                        availableSpace.y,
                                        availableSpace.width, availableSpace.height));
                        break;
                    case BOTTOM_LEFT:
                        positionedImages.add(new PositionedImage(cornerPiece, availableSpace.x,
                                availableSpace.y + availableSpace.height - cornerPiece.getHeight(),
                                availableSpace.width, availableSpace.height));
                        break;
                    case BOTTOM_RIGHT:
                        positionedImages
                                .add(new PositionedImage(cornerPiece,
                                        availableSpace.x + availableSpace.width - cornerPiece.getWidth(),
                                        availableSpace.y + availableSpace.height - cornerPiece.getHeight(),
                                        availableSpace.width, availableSpace.height));
                        break;
                }
            }
            iteration = 0;
            for (BufferedImage borderPiece : borderPieces) {
                switch (iteration) {
                    case TOP:
                        break;
                }
            }
            arrayToPopulate.addAll(positionedImages);
        }
    }
}
