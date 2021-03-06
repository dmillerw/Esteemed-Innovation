package eiteam.esteemedinnovation.api.tool;

import net.minecraft.util.ResourceLocation;

import java.util.*;

public class ToolUpgradeRegistry {
    /**
     * Map of the upgrades and their according texture locations. This is used primarily for model rendering, but could
     * be potentially useful in any case where every single upgrade is needed (or its textures).
     */
    private static Map<SteamToolUpgrade, ResourceLocation[]> upgrades = new HashMap<>();

    /**
     * Registers an {@link SteamToolUpgrade} to the {@link ToolUpgradeRegistry#upgrades} map.
     * @param upgrade The upgrade to register. Uses {@link SteamToolUpgrade#getBaseIcon()} to determine the locations
     *                to register. For universal upgrades ({@link SteamToolUpgrade#isUniversal()} returns `true`),
     *                it will use `Drill0`, `Drill1`, `Axe0`, Axe1`, `Shovel0`, and `Shovel1` accordingly. Otherwise,
     *                it will use `Drill`, `Axe`, or `Shovel` according to what is returned by
     *                {@link SteamToolUpgrade#getToolSlot()}.
     */
    public static void register(SteamToolUpgrade upgrade) {
        List<ResourceLocation> textures = new ArrayList<>();
        ResourceLocation base = upgrade.getBaseIcon();
        if (base != null) {
            String baseDomain = base.getResourceDomain();
            String basePath = base.getResourcePath();
            if (upgrade.isUniversal()) {
                textures.add(new ResourceLocation(baseDomain, basePath + "Drill0"));
                textures.add(new ResourceLocation(baseDomain, basePath + "Drill1"));
                textures.add(new ResourceLocation(baseDomain, basePath + "Axe0"));
                textures.add(new ResourceLocation(baseDomain, basePath + "Axe1"));
                textures.add(new ResourceLocation(baseDomain, basePath + "Shovel0"));
                textures.add(new ResourceLocation(baseDomain, basePath + "Shovel1"));
            } else {
                SteamToolSlot slot = upgrade.getToolSlot();
                String tool = "Drill";
                switch (slot.tool) {
                    case 1: {
                        tool = "Axe";
                        break;
                    }
                    case 2: {
                        tool = "Shovel";
                        break;
                    }
                    default: {
                        break;
                    }
                }
                textures.add(new ResourceLocation(baseDomain, basePath + tool + "0"));
                textures.add(new ResourceLocation(baseDomain, basePath + tool + "1"));
            }
        }

        upgrades.put(upgrade, textures.toArray(new ResourceLocation[textures.size()]));
    }

    public static ResourceLocation[] getResources(SteamToolUpgrade upgrade) {
        return upgrades.get(upgrade);
    }

    public static Set<SteamToolUpgrade> getUpgrades() {
        return upgrades.keySet();
    }
}
