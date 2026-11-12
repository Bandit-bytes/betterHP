package net.bandit.better_hp.client;

import net.bandit.better_hp.BetterhpMod;
import net.bandit.better_hp.integration.AppleSkinCompat;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;

/** Client-only entry point. Never class-loaded on a dedicated server. */
@Mod(value = BetterhpMod.MOD_ID, dist = Dist.CLIENT)
public final class BetterhpClient {

    public static final ResourceLocation HUD_LAYER = ResourceLocation.fromNamespaceAndPath(BetterhpMod.MOD_ID, "hud");

    public BetterhpClient(IEventBus modEventBus, ModContainer modContainer) {
        // In-game config editor: Mods -> Better HP -> Config
        modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);

        modEventBus.addListener(BetterhpClient::registerLayers);
        modEventBus.addListener(BetterhpClient::clientSetup);
        NeoForge.EVENT_BUS.addListener(LayerVisibilityHandler::onLayerPre);
    }

    private static void registerLayers(RegisterGuiLayersEvent event) {
        // Drawn as a real HUD layer (not RenderGuiEvent.Post) so chat, subtitles, the
        // debug screen etc. correctly render on top of it, and F1 hides it.
        event.registerAbove(VanillaGuiLayers.AIR_LEVEL, HUD_LAYER, new HudLayer());
    }

    private static void clientSetup(FMLClientSetupEvent event) {
        logCompat("AppleSkin", AppleSkinCompat.isLoaded());
    }

    private static void logCompat(String name, boolean loaded) {
        if (loaded) {
            BetterhpMod.getLogger().info("{} detected - compat enabled.", name);
        }
    }
}
