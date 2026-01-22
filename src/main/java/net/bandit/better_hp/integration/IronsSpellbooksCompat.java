package net.bandit.better_hp.integration;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

import java.lang.reflect.Method;

public final class IronsSpellbooksCompat {

    public record ManaInfo(int mana, int maxMana) {}

    private static final String MODID = "irons_spellbooks";

    private static boolean lookedUp = false;

    // Client mana accessor (the one their HUD uses)
    private static Method CLIENT_MAGICDATA_GET_PLAYER_MANA;

    public static boolean isLoaded() {
        return ModList.get().isLoaded(MODID);
    }

    public static ManaInfo getMana(Player player) {
        if (player == null || !isLoaded()) return null;

        try {
            ensureReflection();

            // 1) max mana via attribute (synced)
            Attribute maxManaAttr = ForgeRegistries.ATTRIBUTES.getValue(new ResourceLocation(MODID, "max_mana"));
            if (maxManaAttr == null) return null;
            int maxMana = (int) Math.floor(player.getAttributeValue(maxManaAttr));

            // 2) current mana via ClientMagicData (synced by their packets)
            int mana = ((Number) CLIENT_MAGICDATA_GET_PLAYER_MANA.invoke(null)).intValue();

            // clamp just in case
            if (maxMana < 0) maxMana = 0;
            if (mana < 0) mana = 0;
            if (mana > maxMana) mana = maxMana;

            return new ManaInfo(mana, maxMana);
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    private static void ensureReflection() throws Exception {
        if (lookedUp) return;
        lookedUp = true;

        Class<?> clientMagicDataClz = Class.forName("io.redspace.ironsspellbooks.player.ClientMagicData");
        CLIENT_MAGICDATA_GET_PLAYER_MANA = clientMagicDataClz.getMethod("getPlayerMana");
    }

    private IronsSpellbooksCompat() {}
}
