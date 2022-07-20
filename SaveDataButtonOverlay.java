package net.runelite.client.plugins.tileMapper;

import java.awt.Point;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.Color;

import javax.inject.Inject;


import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.input.MouseListener;
import net.runelite.client.input.MouseManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.TooltipComponent;

public class SaveDataButtonOverlay extends Overlay implements MouseListener{
    private final TileMapperPlugin plugin;
    private final Client client;
    private final Color BUTTON_BACKGROUND_COLOR_NORMAL = new Color(108,97,83);
    private final Color BUTTON_BACKGROUND_COLOR_HOVER = new Color(50,97,83);
    private final Image saveButtonImage = Toolkit.getDefaultToolkit().getImage(this.getClass().getResource("save.png")); 
    private Point location = new Point(0,0);
    private Dimension dimension = new Dimension(30, 30);
    private boolean mouseIsHovering = false;
    @Getter
    private boolean displayPathPickerOverlay = false;
    private final TooltipComponent hoverInfo;

    @Inject
    public SaveDataButtonOverlay(TileMapperPlugin plugin,MouseManager mouseManager){
        this.plugin = plugin;
        this.client = plugin.getClient();
        mouseManager.registerMouseListener(this);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(OverlayPriority.MED);
        hoverInfo = new TooltipComponent();
        hoverInfo.setText("Save Collected Tile Data");
    }

    public void cleardisplayPathPickerOverlay(){
        displayPathPickerOverlay = false;
    }
    
    private void setLocation(int x,int y){
        location.x = x;
        location.y = y;
    }

    private void setDimension(int width,int height){
        dimension.width = width;
        dimension.height = height;
    }

    private boolean mouseInBounds(MouseEvent mouseEvent){
        final Point mouseLocation = mouseEvent.getPoint();
        final int radius = dimension.width / 2;
        final int x = mouseLocation.x;
        final int y = mouseLocation.y;
        final int circleX = location.x + dimension.width /2;
        final int circleY = location.y + dimension.height /2;
        
        return (x - circleX) * (x - circleX) +
        (y - circleY) * (y - circleY) <= radius * radius;
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        if(mouseIsHovering){
            hoverInfo.render(graphics);
        }
        
        final Widget worldmapOrb = client.getWidget(WidgetInfo.MINIMAP_WORLDMAP_ORB);
        if(worldmapOrb == null){
            return null;
        }
        switch(plugin.getCurrentViewportType()){
            case FIXED_CLASSIC_LAYOUT:
                this.setLocation(client.getCanvasWidth() - dimension.width - 4,4);
                this.setDimension(30, 30);
            break;
            case RESIZABLE_CLASSIC_LAYOUT:
                this.setLocation(worldmapOrb.getBounds().x + 5, worldmapOrb.getBounds().y - 124);
                this.setDimension(26, 26);
            break;
            case RESIZABLE_MODERN_LAYOUT:
                this.setLocation(worldmapOrb.getBounds().x - 55, worldmapOrb.getBounds().y + 33);
                this.setDimension(30, 30);
            break;
        }
        drawButtonBackground(graphics);
        graphics.drawImage(saveButtonImage, location.x,location.y,dimension.width,dimension.height,null);
        return null;
    }

    private void drawButtonBackground(Graphics2D graphics){
        graphics.setColor(mouseIsHovering?BUTTON_BACKGROUND_COLOR_HOVER:BUTTON_BACKGROUND_COLOR_NORMAL);
        graphics.fillOval(location.x,location.y,dimension.width,dimension.height);
    }

    @Override
    public MouseEvent mouseClicked(MouseEvent mouseEvent) {
        if(mouseIsHovering){
            mouseEvent.consume();
            displayPathPickerOverlay = true;
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mousePressed(MouseEvent mouseEvent) {
        if(mouseIsHovering){
            mouseEvent.consume();
        }
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseReleased(MouseEvent mouseEvent) {
        if(mouseIsHovering){
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
        return mouseEvent;
    }

    @Override
    public MouseEvent mouseMoved(MouseEvent mouseEvent) {
        mouseIsHovering = mouseInBounds(mouseEvent);
        int hoverPositionX = mouseEvent.getX();
        int hoverPositionY = mouseEvent.getY();
        // if(hoverPositionX + hoverInfo.getBounds().width  > client.getCanvasWidth()){
        //     hoverPositionX = client.getCanvasWidth() - hoverInfo.getBounds().width;
        // }
        hoverInfo.setPosition(new Point(hoverPositionX,hoverPositionY + 25));
        return mouseEvent;
    }
    
}
