package net.runelite.client.plugins.tileMapper;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;

@Getter
@AllArgsConstructor
public enum Viewport {
    FIXED_CLASSIC_LAYOUT(WidgetInfo.FIXED_VIEWPORT),RESIZABLE_CLASSIC_LAYOUT(WidgetInfo.RESIZABLE_VIEWPORT_OLD_SCHOOL_BOX),RESIZABLE_MODERN_LAYOUT(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE);

    WidgetInfo viewport;

    boolean isCurrent(Client client){
        final Widget viewportWidget= client.getWidget(this.viewport);
        return viewportWidget != null && !viewportWidget.isHidden();
    }

    static Viewport getCurrent(Client client){
        if(Viewport.FIXED_CLASSIC_LAYOUT.isCurrent(client)){
            return Viewport.FIXED_CLASSIC_LAYOUT;
        }
        if(Viewport.RESIZABLE_CLASSIC_LAYOUT.isCurrent(client)){
            return Viewport.RESIZABLE_CLASSIC_LAYOUT;
        }
        if(Viewport.RESIZABLE_MODERN_LAYOUT.isCurrent(client)){
            return Viewport.RESIZABLE_MODERN_LAYOUT;
        }
        return null;
    }
}
