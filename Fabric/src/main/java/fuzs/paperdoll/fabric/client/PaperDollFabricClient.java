package fuzs.paperdoll.fabric.client;

import fuzs.paperdoll.common.PaperDoll;
import fuzs.paperdoll.common.client.PaperDollClient;
import fuzs.puzzleslib.common.api.client.core.v1.ClientModConstructor;
import net.fabricmc.api.ClientModInitializer;

public class PaperDollFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ClientModConstructor.construct(PaperDoll.MOD_ID, PaperDollClient::new);
    }
}
