package net.runelite.client.plugins.tileMapper.components;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.stream.Collectors;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import lombok.Getter;
import net.runelite.client.input.MouseListener;
import net.runelite.client.plugins.tileMapper.helpers.PiecesTool;
import net.runelite.client.plugins.tileMapper.helpers.PositionedImage;
import net.runelite.client.plugins.tileMapper.helpers.loaders.ImageLoader;
import net.runelite.client.ui.overlay.RenderableEntity;

public interface Scrollbar {

    public static class Vertical implements MouseListener, RenderableEntity {

        // scrollbar track enclosing buttons
        private final BufferedImage scrollTowardsTopButttonImage = ImageLoader
                .loadImage("buttons/scrollbar-button-scroll-towards-top.png");
        private final BufferedImage scrollTowardsBottomButtonImage = ImageLoader
                .loadImage("buttons/scrollbar-button-scroll-towards-bottom.png");
        // scrollbar track
        private final BufferedImage scrollbarTrackMiddleImage = ImageLoader
                .loadImage("scrollbar/scrollbar-track-middle.png");
        // scrollbar thumb
        private final BufferedImage scrollbarThumbTopImage = ImageLoader
                .loadImage("scrollbar/scrollbar-thumb-top.png");
        private final BufferedImage scrollbarThumbMiddleImage = ImageLoader
                .loadImage("scrollbar/scrollbar-thumb-middle.png");
        private final BufferedImage scrollbarThumbBottomImage = ImageLoader
                .loadImage("scrollbar/scrollbar-thumb-bottom.png");

        private final int MINIMUM_COMPONENT_HEIGHT = 47;// the total of: scrollTowardsTopButttonImage.getHeight() +
                                                        // scrollTowardsBottomButtonImage.getHeight() +
                                                        // scrollbarThumbTopImage.getHeight() +
                                                        // scrollbarThumbMiddleImage.getHeight() +
                                                        // scrollbarThumbBottomImage.getHeight();

        /**
         * Converts a range of numbers to a percentage scale
         * 
         * @param numberToConvert number to convert.
         * @param startingRange   lowest number of the range.
         * @param endingRange     highest number in the range.
         * @param percentageScale percentage scale.
         * @return
         */
        public float toPct(float numberToConvert, float startingRange, float endingRange, float percentageScale) {
            // reversed high and low
            if (startingRange > endingRange) {
                startingRange = startingRange + endingRange;
                endingRange = startingRange - endingRange;
                startingRange = startingRange - endingRange;
            }

            // input validation
            if (numberToConvert < startingRange || numberToConvert > endingRange) {
                throw new Error("numberToConvert does not fall within the supplied range");
            }

            // edge cases
            if (numberToConvert == startingRange)
                return 0;
            if (numberToConvert == endingRange)
                return percentageScale;

            // everything in between
            float range = endingRange - startingRange;
            if (startingRange < 0) {
                numberToConvert += Math.abs(startingRange);
            }
            return (numberToConvert / range) * percentageScale;
        }

        @Getter
        @Positive
        @Min(MINIMUM_COMPONENT_HEIGHT)
        private int availableContainerHeight = MINIMUM_COMPONENT_HEIGHT;

        @Getter
        /**
         * @param int the container height required to display all items without
         *            overflow.
         */
        @Positive
        @Min(MINIMUM_COMPONENT_HEIGHT)
        private int requiredContainerHeight = MINIMUM_COMPONENT_HEIGHT;

        public void setRequiredContainerHeight(int height) {
            requiredContainerHeight = height < availableContainerHeight ? availableContainerHeight : height;
            updateSubcomponentsRequired = true;
        }

        // scrollbar bounds
        private Rectangle previousBounds = new Rectangle();
        private final Rectangle bounds = new Rectangle(0, 0, scrollTowardsTopButttonImage.getWidth(),
                MINIMUM_COMPONENT_HEIGHT);

        // track bounds
        private Rectangle previousTrackBounds;
        private Rectangle trackBounds = new Rectangle(this.bounds.x, this.bounds.y, this.bounds.width,
                this.bounds.height
                        - (scrollTowardsTopButttonImage.getHeight() + scrollTowardsBottomButtonImage.getHeight()));
        // initial track population
        private ArrayList<PositionedImage> track = new ArrayList<>();
        {
            updateTrack();
        }

        private void updateTrack() {
            if (trackBounds.equals(previousTrackBounds)) {
                return;
            }
            track = new ArrayList<>();
            previousTrackBounds = new Rectangle(trackBounds);
            PiecesTool.Populator.populateVerticalLine(track, scrollbarTrackMiddleImage, trackBounds.x,
                    trackBounds.y,
                    trackBounds.height);
        }

        // thumb bounds
        private Rectangle previousThumbBounds;
        private Rectangle thumbBounds = new Rectangle(this.bounds.x,
                this.bounds.y + scrollTowardsTopButttonImage.getHeight(), this.bounds.width,
                this.bounds.height
                        - (scrollTowardsTopButttonImage.getHeight() + scrollTowardsBottomButtonImage.getHeight()));
        // initial thumb population
        private ArrayList<PositionedImage> thumb = new ArrayList<>();
        {
            updateThumb();
        }

        private void updateThumb() {
            if (thumbBounds.equals(previousThumbBounds)) {
                return;
            }
            previousThumbBounds = new Rectangle(thumbBounds);
            thumb = new ArrayList<>();
            thumb.add(new PositionedImage(scrollbarThumbTopImage, thumbBounds.x,
                    thumbBounds.y,
                    scrollbarThumbTopImage.getWidth(), scrollbarThumbTopImage.getHeight()));
            PiecesTool.Populator.populateVerticalLine(track, scrollbarThumbMiddleImage, thumbBounds.x,
                    thumbBounds.y + scrollbarThumbTopImage.getHeight(),
                    thumbBounds.height - scrollbarThumbTopImage.getHeight()
                            - scrollbarThumbBottomImage.getHeight());
            thumb.add(new PositionedImage(scrollbarThumbBottomImage, thumbBounds.x,
                    thumbBounds.y + thumbBounds.height - scrollbarThumbBottomImage.getHeight(),
                    scrollbarThumbBottomImage.getWidth(),
                    scrollbarThumbBottomImage.getHeight()));
        }

        @Getter
        private final int MINIMUM_THUMB_HEIGHT = 15;// calc of: scrollbarThumbTopImage.getHeight() +
        // scrollbarThumbMiddleImage.getHeight() + scrollbarThumbBottomImage.getHeight()
        @Getter
        private int requestedMinimumThumbHeight = MINIMUM_THUMB_HEIGHT;

        public void setRequestedMinimumThumbHeight(@Positive @Min(MINIMUM_THUMB_HEIGHT) int minimumThumbHeight) {
            requestedMinimumThumbHeight = getMaximumThumbHeight() <= minimumThumbHeight ? minimumThumbHeight
                    : getMaximumThumbHeight();
        }

        private int getMaximumThumbHeight() {
            final int reducerStartingValue = 0;
            return track.stream().map(trackPiece -> trackPiece.getHeight()).collect(Collectors.toList()).stream()
                    .reduce(reducerStartingValue,
                            (trackPieceHeight, trackPieceHeight0) -> trackPieceHeight + trackPieceHeight0);
        }

        private int getThumbStartingPosition() {
            return trackBounds.y + thumbBounds.height;
        }

        private int getThumbCurrentPosition() {
            return thumbBounds.y + thumbBounds.height;
        }

        private int getThumbEndingPosition() {
            return trackBounds.y + trackBounds.height;
        }

        private int normalizeThumbPosition(int positionToNormalize) {
            return positionToNormalize - trackBounds.y;
        }

        private int getThumbMovementSpace() {
            return normalizeThumbPosition(getThumbEndingPosition())
                    - normalizeThumbPosition(getThumbStartingPosition());
        }

        public Pair<Integer, Integer> getCurrentPortionToDisplay() {
            if (!visible) {
                return null;
            }
            final int numberToConvert = normalizeThumbPosition(getThumbCurrentPosition());
            final int currentPosition = (int) toPct(numberToConvert,
                    thumbBounds.height,
                    getThumbMovementSpace(),
                    requiredContainerHeight - availableContainerHeight);
            return new ImmutablePair<Integer, Integer>(currentPosition, currentPosition + availableContainerHeight);
        }

        // buttons
        private final Button scrollTowardsTopButton = new Button(0, 0, scrollTowardsTopButttonImage,
                scrollTowardsTopButttonImage);
        private final Button scrollTowardsBottomButton = new Button(0, 0, scrollTowardsBottomButtonImage,
                scrollTowardsBottomButtonImage);

        private boolean updateSubcomponentsRequired = true;

        private boolean visible;

        public void setVisible(boolean visible) {
            this.visible = visible;
            scrollTowardsTopButton.setVisible(visible);
            scrollTowardsBottomButton.setVisible(visible);
        }

        public Vertical(@Positive int x, @Positive int y, @Positive @Min(MINIMUM_COMPONENT_HEIGHT) int height) {
            this.previousBounds = new Rectangle(x, y, scrollTowardsTopButttonImage.getWidth(), height);
            this.setBounds(x, y, height);
        }

        public void setBounds(@Positive int x, @Positive int y, @Positive @Min(MINIMUM_COMPONENT_HEIGHT) int height) {
            this.previousBounds = new Rectangle(this.bounds);
            this.bounds.setBounds(x, y, scrollTowardsTopButttonImage.getWidth(), height);
            this.availableContainerHeight = height;
            this.updateSubcomponentsRequired = true;
        }

        public void setLocation(@Positive int x, @Positive int y) {
            this.bounds.setLocation(x, y);
            this.updateSubcomponentsRequired = true;
        }

        private void updateLocations() {
            scrollTowardsTopButton.setLocation(this.bounds.x, this.bounds.y);
            scrollTowardsBottomButton.setLocation(this.bounds.x,
                    this.bounds.y + this.bounds.height - scrollTowardsBottomButton.getBounds().height);
            trackBounds.setLocation(this.bounds.x, this.bounds.y + scrollTowardsTopButton.getBounds().height);
            thumbBounds.setLocation(this.bounds.x, this.bounds.y + scrollTowardsTopButttonImage.getHeight());
        }

        private void updateDimensions() {
            trackBounds.setSize(this.bounds.width, this.bounds.height
                    - (scrollTowardsTopButttonImage.getHeight() + scrollTowardsBottomButtonImage.getHeight()));
            thumbBounds.setSize(this.bounds.width, calcThumbHeight());
        }

        private void updateSubcomponents() {
            updateLocations();
            updateDimensions();
            updateTrack();
            updateThumb();
        }

        public int calcThumbHeight() {
            final int calculatedThumbHeight = availableContainerHeight
                    - (requiredContainerHeight - availableContainerHeight) - scrollTowardsTopButton.getBounds().height
                    - scrollTowardsBottomButton.getBounds().height;
            if (calculatedThumbHeight > getMaximumThumbHeight()) {
                throw new Error("Calculated Thumb height is greater that available space for thumb.");
            }
            final int actualThumbHeight = calculatedThumbHeight < getRequestedMinimumThumbHeight()
                    ? getRequestedMinimumThumbHeight()
                    : calculatedThumbHeight;
            return actualThumbHeight;
        }

        public Rectangle getBoundsCopy() {
            return new Rectangle(this.bounds);
        }

        @Override
        public Dimension render(Graphics2D graphics) {
            if (!visible) {
                return null;
            }
            if (updateSubcomponentsRequired) {
                updateSubcomponentsRequired = false;
                updateSubcomponents();
            }
            scrollTowardsTopButton.render(graphics);
            track.forEach(piece -> piece.render(graphics));
            thumb.forEach(piece -> piece.render(graphics));
            scrollTowardsBottomButton.render(graphics);
            return null;
        }

        @Override
        public MouseEvent mouseClicked(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mousePressed(MouseEvent mouseEvent) {
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseReleased(MouseEvent mouseEvent) {
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
            return mouseEvent;
        }

        @Override
        public MouseEvent mouseMoved(MouseEvent mouseEvent) {
            return mouseEvent;
        }

    }
}
