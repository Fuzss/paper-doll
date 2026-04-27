package fuzs.paperdoll.common.client;

import fuzs.paperdoll.common.PaperDoll;
import fuzs.paperdoll.common.client.handler.PaperDollHandler;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import fuzs.puzzleslib.common.api.client.core.v1.context.GuiLayersContext;
import fuzs.puzzleslib.common.api.client.event.v1.ClientTickEvents;

public class PaperDollClient implements ClientModConstructor {

    @Override
    public void onConstructMod() {
        registerEventHandlers();
    }

    private static void registerEventHandlers() {
        ClientTickEvents.END.register(PaperDollHandler::onEndClientTick);
    }

    @Override
    public void onRegisterGuiLayers(GuiLayersContext context) {
        context.registerGuiLayer(PaperDoll.id("paper_doll"),
                GuiLayersContext.SLEEP_OVERLAY,
                PaperDollHandler::renderPaperDoll);
    }
}
