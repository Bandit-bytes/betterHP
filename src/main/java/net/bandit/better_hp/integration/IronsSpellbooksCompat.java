package net.bandit.better_hp.integration;

import net.bandit.better_hp.BetterhpMod;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.neoforged.fml.ModList;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class IronsSpellbooksCompat {
    public static final String IRONS_MODID = "irons_spellbooks";

    private static boolean initialized = false;
    private static boolean warnedOnce = false;
    private static boolean debugLoggedOnce = false;
    private static Holder<?> maxManaHolder;
    private static Method mGetPlayerMagicData;
    private static Method mMagicGetMana;
    private static Method mClientGetMana_Living;
    private static Method mClientGetMana_NoArgs;
    private static Method mClientGetMana_Player;

    private IronsSpellbooksCompat() {}

    public static boolean isLoaded() {
        return ModList.get().isLoaded(IRONS_MODID);
    }

    public static void initClient() {
        if (initialized) return;
        initialized = true;

        if (!isLoaded()) return;

        try {
            ensureReflectionReady();
            BetterhpMod.getLogger().info("Better HP: Iron's mana compat ready.");
        } catch (Throwable t) {
            BetterhpMod.getLogger().warn("Better HP: Iron's detected but mana API could not be resolved. Mana HUD will be hidden.", t);
        }
    }

    @Nullable
    public static ManaSnapshot getMana(@Nullable Player player) {
        if (player == null || !isLoaded()) return null;

        try {
            ensureReflectionReady();

            float maxMana = (float) player.getAttributeValue((Holder) maxManaHolder);
            if (maxMana <= 0.0f) return null;

            float mana = readClientManaPreferred(player);
            if (mana < 0) return null;

            if (mana > maxMana) mana = maxMana;

            return new ManaSnapshot(Math.round(mana), Math.round(maxMana));
        } catch (Throwable t) {
            if (!warnedOnce) {
                warnedOnce = true;
                BetterhpMod.getLogger().warn("Better HP: Failed to read Iron's mana. Mana HUD will be hidden.", t);
            }
            return null;
        }
    }

    /**
     * Attempts to read mana using the same client-synced source Iron's HUD typically uses.
     * Falls back to MagicData attachment if no client accessor exists.
     */
    private static float readClientManaPreferred(Player player) throws Exception {
        if (mClientGetMana_NoArgs != null) {
            Object v = mClientGetMana_NoArgs.invoke(null);
            float mana = asFloat(v);
            if (logDebugOnce("client no-args", mana)) {}
            if (mana >= 0) return mana;
        }

        if (mClientGetMana_Living != null) {
            Object v = mClientGetMana_Living.invoke(null, (LivingEntity) player);
            float mana = asFloat(v);
            if (logDebugOnce("client living", mana)) {}
            if (mana >= 0) return mana;
        }

        if (mClientGetMana_Player != null) {
            Object v = mClientGetMana_Player.invoke(null, player);
            float mana = asFloat(v);
            if (logDebugOnce("client player", mana)) {}
            if (mana >= 0) return mana;
        }

        Object magicData = mGetPlayerMagicData.invoke(null, (LivingEntity) player);
        if (magicData == null) return -1;

        Object v = mMagicGetMana.invoke(magicData);
        float mana = asFloat(v);
        if (logDebugOnce("magic attachment", mana)) {}
        return mana;
    }

    private static float asFloat(Object v) {
        if (v == null) return -1;
        if (v instanceof Number n) return n.floatValue();
        return -1;
    }

    private static boolean logDebugOnce(String source, float mana) {
        if (debugLoggedOnce) return false;
        debugLoggedOnce = true;
        BetterhpMod.getLogger().info("Better HP: Iron's mana source='{}' value={}", source, mana);
        return true;
    }

    private static void ensureReflectionReady() throws Exception {
        if (mGetPlayerMagicData != null && mMagicGetMana != null && maxManaHolder != null) return;

        Class<?> cAttrReg = Class.forName("io.redspace.ironsspellbooks.api.registry.AttributeRegistry");
        Field fMaxMana = cAttrReg.getField("MAX_MANA");
        Object holderObj = fMaxMana.get(null);
        if (!(holderObj instanceof Holder<?> h)) {
            throw new IllegalStateException("AttributeRegistry.MAX_MANA was not a Holder. Got: " +
                    (holderObj == null ? "null" : holderObj.getClass().getName()));
        }
        maxManaHolder = h;

        Class<?> cMagicData = Class.forName("io.redspace.ironsspellbooks.api.magic.MagicData");
        mGetPlayerMagicData = cMagicData.getMethod("getPlayerMagicData", LivingEntity.class);
        mMagicGetMana = cMagicData.getMethod("getMana");

        resolveClientManaAccessors();
    }

    private static void resolveClientManaAccessors() {
        String[] candidates = new String[] {
                "io.redspace.ironsspellbooks.player.ClientMagicData",
                "io.redspace.ironsspellbooks.client.ClientMagicData",
                "io.redspace.ironsspellbooks.client.ClientManaData",
                "io.redspace.ironsspellbooks.api.magic.ClientMagicData"
        };

        for (String cn : candidates) {
            try {
                Class<?> c = Class.forName(cn);

                mClientGetMana_NoArgs = tryMethod(c, "getMana");
                if (mClientGetMana_NoArgs == null) mClientGetMana_NoArgs = tryMethod(c, "getPlayerMana");

                mClientGetMana_Living = tryMethod(c, "getMana", LivingEntity.class);
                if (mClientGetMana_Living == null) mClientGetMana_Living = tryMethod(c, "getPlayerMana", LivingEntity.class);

                mClientGetMana_Player = tryMethod(c, "getMana", Player.class);
                if (mClientGetMana_Player == null) mClientGetMana_Player = tryMethod(c, "getPlayerMana", Player.class);

                if (mClientGetMana_NoArgs != null || mClientGetMana_Living != null || mClientGetMana_Player != null) {
                    BetterhpMod.getLogger().info("Better HP: Found Iron's client mana accessor in {}", cn);
                    return;
                }
            } catch (Throwable ignored) {
            }
        }
        BetterhpMod.getLogger().info("Better HP: No Iron's client mana accessor found; using MagicData attachment (may be 0 client-side).");
    }

    @Nullable
    private static Method tryMethod(Class<?> c, String name, Class<?>... args) {
        try {
            Method m = c.getMethod(name, args);
            Class<?> rt = m.getReturnType();
            if (Number.class.isAssignableFrom(rt) || rt.isPrimitive()) return m;
        } catch (Throwable ignored) {}
        return null;
    }

    public record ManaSnapshot(int mana, int maxMana) {}
}
