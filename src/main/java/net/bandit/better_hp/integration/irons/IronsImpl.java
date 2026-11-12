package net.bandit.better_hp.integration.irons;

import io.redspace.ironsspellbooks.api.registry.AttributeRegistry;
import io.redspace.ironsspellbooks.player.ClientMagicData;
import net.bandit.better_hp.integration.IronsSpellbooksCompat.ManaSnapshot;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

/** Only class-loaded when Iron's Spells 'n Spellbooks is installed. */
public final class IronsImpl {
    private IronsImpl() {}

    @Nullable
    public static ManaSnapshot getMana(Player player) {
        int maxMana = (int) Math.round(player.getAttributeValue(AttributeRegistry.MAX_MANA));
        if (maxMana <= 0) return null;
        // Same client-synced value Iron's own mana bar uses.
        int mana = Math.max(0, Math.min(ClientMagicData.getPlayerMana(), maxMana));
        return new ManaSnapshot(mana, maxMana);
    }
}
