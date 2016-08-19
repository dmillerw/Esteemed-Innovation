package flaxbeard.steamcraft.item.armor.exosuit;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import flaxbeard.steamcraft.Config;
import flaxbeard.steamcraft.Steamcraft;
import flaxbeard.steamcraft.api.IEngineerable;
import flaxbeard.steamcraft.api.ISteamChargable;
import flaxbeard.steamcraft.api.exosuit.*;
import flaxbeard.steamcraft.client.render.model.exosuit.ExosuitModelCache;
import flaxbeard.steamcraft.client.render.model.exosuit.ModelExosuit;
import flaxbeard.steamcraft.gui.GuiEngineeringTable;
import flaxbeard.steamcraft.handler.SteamcraftEventHandler;
import flaxbeard.steamcraft.init.items.armor.ExosuitUpgradeItems;
import flaxbeard.steamcraft.init.misc.integration.CrossMod;
import flaxbeard.steamcraft.item.BlockTankItem;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.*;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import org.apache.commons.lang3.tuple.MutablePair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ItemExosuitArmor extends ItemArmor implements ISpecialArmor, IEngineerable, ISteamChargable {
    public static final ResourceLocation LARGE_ICONS = new ResourceLocation("steamcraft:textures/gui/engineering2.png");

    public EntityEquipmentSlot slot;

    public ItemExosuitArmor(EntityEquipmentSlot slot, ArmorMaterial mat) {
        super(mat, 1, slot);
        this.slot = slot;
        setMaxDamage(0);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return false;
    }

    public String getString() {
        String slotName = slot.getName();
        return Steamcraft.MOD_ID + ":textures/items/exoArmor" + slotName.substring(0, 1).toUpperCase() + slotName.substring(1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, ModelBiped _default) {
        if (!(entityLiving instanceof EntityPlayer)) {
            return null;
        }

        ModelExosuit modelExosuit = ExosuitModelCache.INSTANCE.getModel((EntityPlayer) entityLiving, armorSlot);

        boolean head = armorSlot == EntityEquipmentSlot.HEAD;
        boolean body = armorSlot == EntityEquipmentSlot.CHEST;
        boolean legs = armorSlot == EntityEquipmentSlot.LEGS;
        boolean feet = armorSlot == EntityEquipmentSlot.FEET;
        modelExosuit.bipedHead.showModel = head;
        modelExosuit.bipedHeadwear.showModel = head;
        modelExosuit.bipedBody.showModel = body || legs;
        modelExosuit.bipedRightArm.showModel = body;
        modelExosuit.bipedLeftArm.showModel = body;
        modelExosuit.bipedRightLeg.showModel = legs || feet;
        modelExosuit.bipedLeftLeg.showModel = legs || feet;

        return modelExosuit;
    }

    @Override
    public ArmorProperties getProperties(EntityLivingBase player, ItemStack armor, DamageSource source, double damage, int armorType) {
        if (armor.hasTagCompound()) {
            if (armor.getTagCompound().hasKey("plate")) {
                ExosuitPlate plate = UtilPlates.getPlate(armor.getTagCompound().getString("plate"));
                return new ArmorProperties(0, plate.getDamageReductionAmount(slot, source) / 25.0D, ItemArmor.ArmorMaterial.IRON.getDurability(slot));
            }
        }
        return new ArmorProperties(0, ItemArmor.ArmorMaterial.IRON.getDamageReductionAmount(slot) / 25.0D, ItemArmor.ArmorMaterial.IRON.getDurability(slot));
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        updateSteamNBT(stack);
        //return 0.9D;
        return 1.0D - (stack.getTagCompound().getInteger("steamFill") / (double) stack.getTagCompound().getInteger("maxFill"));
    }

    @Override
    public boolean showDurabilityBar(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        if (!stack.getTagCompound().hasKey("maxFill")) {
            stack.getTagCompound().setInteger("maxFill", 0);
        }
        return stack.getTagCompound().getInteger("maxFill") > 0;
    }

    @Override
    public int getArmorDisplay(EntityPlayer player, ItemStack armor, int armorType) {
        if (armor.hasTagCompound()) {
            if (armor.getTagCompound().hasKey("plate")) {
                ExosuitPlate plate = UtilPlates.getPlate(armor.getTagCompound().getString("plate"));
                return plate.getDamageReductionAmount(slot, DamageSource.generic);
            }
        }
        return ItemArmor.ArmorMaterial.LEATHER.getDamageReductionAmount(slot);
    }

    @Override
    public void damageArmor(EntityLivingBase entity, ItemStack stack, DamageSource source, int damage, int slot) {
        if (this.slot == EntityEquipmentSlot.CHEST) {
            SteamcraftEventHandler.drainSteam(stack, damage * 40);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public MutablePair<Integer, Integer>[] engineerCoordinates() {
        switch (slot) {
            case HEAD: {
                return new MutablePair[] {
                  MutablePair.of(1, 19),
                  MutablePair.of(1, 1),
                  MutablePair.of(39, 16),
                  MutablePair.of(59, 36)
                };
            }
            case CHEST: {
                return new MutablePair[] {
                  MutablePair.of(1, 19),
                  MutablePair.of(1, 1),
                  MutablePair.of(49, 33),
                  MutablePair.of(75, 26),
                  MutablePair.of(1, 37)
                };
            }
            case LEGS: {
                return new MutablePair[] {
                  MutablePair.of(1, 19),
                  MutablePair.of(1, 1),
                  MutablePair.of(60, 12),
                  MutablePair.of(37, 40)
                };
            }
            case FEET: {
                return new MutablePair[] {
                  MutablePair.of(1, 19),
                  MutablePair.of(1, 1),
                  MutablePair.of(60, 18),
                  MutablePair.of(28, 40)
                };
            }
		default:
			break;
        }
        return new MutablePair[]{MutablePair.of(49, 26)};
    }

    public boolean hasPlates(ItemStack me) {
        if (this.getStackInSlot(me, 1) != null) {
            if (!me.hasTagCompound()) {
                me.setTagCompound(new NBTTagCompound());
            }
            ItemStack clone = this.getStackInSlot(me, 1).copy();
            clone.stackSize = 1;
            if (UtilPlates.getPlate(clone) != null) {
                me.getTagCompound().setString("plate", UtilPlates.getPlate(clone).getIdentifier());
                return true;
            } else {
                UtilPlates.removePlate(me);
                return false;
            }
        } else {
            if (!me.hasTagCompound()) {
                me.setTagCompound(new NBTTagCompound());
            }
            UtilPlates.removePlate(me);
            return false;
        }
    }

    @Override
    public ItemStack getStackInSlot(ItemStack me, int var1) {
        if (me.hasTagCompound()) {
            if (me.getTagCompound().hasKey("inv")) {
                if (me.getTagCompound().getCompoundTag("inv").hasKey(Integer.toString(var1))) {
                    return ItemStack.loadItemStackFromNBT(me.getTagCompound().getCompoundTag("inv").getCompoundTag(Integer.toString(var1)));
                }
            }
        }
        return null;
    }

    @Override
    public void setInventorySlotContents(ItemStack me, int var1, ItemStack stack) {
        if (!me.hasTagCompound()) {
            me.setTagCompound(new NBTTagCompound());
        }
        if (!me.getTagCompound().hasKey("inv")) {
            me.getTagCompound().setTag("inv", new NBTTagCompound());
        }
        if (me.getTagCompound().getCompoundTag("inv").hasKey(Integer.toString(var1))) {
            me.getTagCompound().getCompoundTag("inv").removeTag(Integer.toString(var1));
        }
        NBTTagCompound stc = new NBTTagCompound();
        if (stack != null) {
            stack.writeToNBT(stc);
            me.getTagCompound().getCompoundTag("inv").setTag(Integer.toString(var1), stc);
            if (var1 == 5 && slot == EntityEquipmentSlot.CHEST) {
                me.getTagCompound().setInteger("steamFill", 0);
                me.getTagCompound().setInteger("maxFill", ((IExosuitTank) stack.getItem()).getStorage(me));
                if (stack.getItem() instanceof BlockTankItem && stack.getItemDamage() == 1) {
                    me.getTagCompound().setInteger("steamFill", me.getTagCompound().getInteger("maxFill"));
                }
            }
        }
        this.hasPlates(me);
    }

    @Override
    public boolean isItemValidForSlot(ItemStack me, int var1, ItemStack var2) {
        return true;
    }

    @Override
    public ItemStack decrStackSize(ItemStack me, int var1, int var2) {
        if (this.getStackInSlot(me, var1) != null) {
            ItemStack itemstack;
            if (this.getStackInSlot(me, var1).stackSize <= var2) {
                itemstack = this.getStackInSlot(me, var1);
                this.setInventorySlotContents(me, var1, null);
                this.hasPlates(me);
                return itemstack;
            } else {
                ItemStack stack2 = this.getStackInSlot(me, var1);
                itemstack = stack2.splitStack(var2);
                this.setInventorySlotContents(me, var1, stack2);

                if (this.getStackInSlot(me, var1).stackSize == 0) {
                    this.setInventorySlotContents(me, var1, null);
                }
                this.hasPlates(me);
                return itemstack;
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean canPutInSlot(ItemStack me, int slotNum, ItemStack upgrade) {
        if (slotNum == 0) {
            ItemStack clone = upgrade.copy();
            clone.stackSize = 1;
            return UtilPlates.getPlate(clone) != null;
        }
        if (upgrade.getItem() instanceof IExosuitUpgrade) {
            IExosuitUpgrade upgradeItem = (IExosuitUpgrade) upgrade.getItem();
            return (upgradeItem.getSlot().armor == this.slot && upgradeItem.getSlot().slot == slotNum) || (upgradeItem.getSlot() == ExosuitSlot.VANITY && upgradeItem.getSlot().slot == slotNum);
        } else if (slotNum == ExosuitSlot.VANITY.slot) {
            int[] ids = OreDictionary.getOreIDs(upgrade);
            for (int id : ids) {
                String str = OreDictionary.getOreName(id);
                if (str.contains("dye")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if the stack has the steam-related NBT values, and if not, sets them to 0.
     * @param me The ItemStack to check.
     */
    public void updateSteamNBT(ItemStack me) {
        if (!me.hasTagCompound()) {
            me.setTagCompound(new NBTTagCompound());
        }
        if (!me.getTagCompound().hasKey("steamFill")) {
            me.getTagCompound().setInteger("steamFill", 0);
        }
        if (!me.getTagCompound().hasKey("maxFill")) {
            me.getTagCompound().setInteger("maxFill", 0);
        }
    }

    /**
     * Checks whether the ItemStack has the amount of power in its steam storage. The opposite of
     * @param me The ItemStack
     * @param powerNeeded The amount of power needed
     * @return True if it has power, false if it doesn't, or isn't a chestplate.
     * @see #needsPower(ItemStack, int)
     */
    public boolean hasPower(ItemStack me, int powerNeeded) {
        if (slot == EntityEquipmentSlot.CHEST) {
            updateSteamNBT(me);
            if (me.getTagCompound().getInteger("steamFill") > powerNeeded) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the ItemStack can have the amount of power added to its steam storage.
     * @param me The ItemStack
     * @param powerNeeded The amount of power to add
     * @return True if it will not exceed the limit with this amount of power added to it, false if
     *         it will, or if it is not a chestplate.
     */
    public boolean needsPower(ItemStack me, int powerNeeded) {
        if (slot == EntityEquipmentSlot.CHEST) {
            updateSteamNBT(me);
            if (me.getTagCompound().getInteger("steamFill") + powerNeeded < me.getTagCompound().getInteger("maxFill")) {
                return true;
            }
        }
        return false;
    }

    public boolean hasUpgrade(ItemStack me, Item check) {
        if (check == null) {
            return false;
        }

        if (me.hasTagCompound()) {
            if (me.getTagCompound().hasKey("inv")) {
                for (int i = 1; i < 10; i++) {
                    if (me.getTagCompound().getCompoundTag("inv").hasKey(Integer.toString(i))) {
                        ItemStack stack = ItemStack.loadItemStackFromNBT(me.getTagCompound().getCompoundTag("inv").getCompoundTag(Integer.toString(i)));
                        if (stack.getItem() == check) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets whether the given armor has a tank upgrade.
     * @param me The ItemStack
     */
    public boolean hasTank(ItemStack me) {
        if (slot != EntityEquipmentSlot.CHEST) {
            return false;
        }
        if (!me.hasTagCompound()) {
            return false;
        }
        if (!me.getTagCompound().hasKey("inv")) {
            return false;
        }
        NBTTagCompound inv = me.getTagCompound().getCompoundTag("inv");
        for (int i = 1; i < 10; i++) {
            String s = Integer.toString(i);
            if (inv.hasKey(s)) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(inv.getCompoundTag(s));
                if (stack != null && stack.getItem() != null && stack.getItem() instanceof IExosuitTank) {
                    return true;
                }
            }
        }
        return false;
    }

    public IExosuitUpgrade[] getUpgrades(ItemStack me) {
        ArrayList<IExosuitUpgrade> upgrades = new ArrayList<>();
        if (me.hasTagCompound()) {
            if (me.getTagCompound().hasKey("inv")) {
                for (int i = 2; i < 10; i++) {
                    if (me.getTagCompound().getCompoundTag("inv").hasKey(Integer.toString(i))) {
                        ItemStack stack = ItemStack.loadItemStackFromNBT(me.getTagCompound().getCompoundTag("inv").getCompoundTag(Integer.toString(i)));
                        if (stack.getItem() instanceof IExosuitUpgrade) {
                            upgrades.add((IExosuitUpgrade) stack.getItem());
                        }
                    }
                }
            }
        }
        return upgrades.toArray(new IExosuitUpgrade[0]);
    }

    @Override
    public void drawSlot(GuiContainer guiEngineeringTable, int slotNum, int i, int j) {
        guiEngineeringTable.mc.getTextureManager().bindTexture(GuiEngineeringTable.GUI_TEXTURES);
        switch (slot) {
            case HEAD:
            case LEGS:
            case FEET: {
                switch (slotNum) {
                    case 0: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 194, 0, 18, 18);
                        break;
                    }
                    case 1: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 230, 36, 18, 18);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
            case CHEST: {
                switch (slotNum) {
                    case 0: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 194, 0, 18, 18);
                        break;
                    }
                    case 1: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 230, 36, 18, 18);
                        break;
                    }
                    case 4: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 176, 36, 18, 18);
                        break;
                    }
                    default: {
                        guiEngineeringTable.drawTexturedModalRect(i, j, 176, 0, 18, 18);
                    }
                }
            }
		default:
			break;
        }
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        if (slot == EntityEquipmentSlot.CHEST) {
            return 10000;
        }
        return 0;
    }

    @Override
    public int getDamage(ItemStack stack) {
        updateSteamNBT(stack);
        return (int) (((double) stack.getTagCompound().getInteger("steamFill")) /
          (double) stack.getTagCompound().getInteger("maxFill") * 10000.0D);
    }

    @Override
    public int steamPerDurability() {
        return Config.exoConsumption;
    }

    @Override
    public boolean canCharge(ItemStack stack) {
        if (slot == EntityEquipmentSlot.CHEST) {
            ItemExosuitArmor item = (ItemExosuitArmor) stack.getItem();
            if (item.getStackInSlot(stack, 5) != null && item.getStackInSlot(stack, 5).getItem() instanceof IExosuitTank) {
                IExosuitTank tank = (IExosuitTank) item.getStackInSlot(stack, 5).getItem();
                return tank.canFill(stack);
            }
        }
        return false;
    }

    @Override
    public boolean addSteam(ItemStack me, int amount, EntityPlayer player) {
        int curSteam = me.getTagCompound().getInteger("steamFill");
        int newSteam = curSteam + amount;
        if (needsPower(me, amount)) {
            me.getTagCompound().setInteger("steamFill", newSteam);
            return true;
        }
        return false;
    }

    @Override
    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot armorSlot, ItemStack stack) {
        Multimap<String, AttributeModifier> map = HashMultimap.create();
        ItemExosuitArmor armor = (ItemExosuitArmor) stack.getItem();
        boolean hasKnockback = false;
        double knockbackAmount = 0.0D;
        if (armor.hasPlates(stack) &&
          UtilPlates.getPlate(stack.getTagCompound().getString("plate")).getIdentifier().equals("Lead")) {
            hasKnockback = true;
            knockbackAmount += 0.25D;
        }
        if (armor.hasUpgrade(stack, ExosuitUpgradeItems.Items.ANCHOR_HEELS.getItem())) {
            hasKnockback = true;
            knockbackAmount += 0.25D;
        }
        if (hasKnockback) {
            map.put(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getAttributeUnlocalizedName(),
              new AttributeModifier(new UUID(776437, armorType.getSlotIndex()), "Lead exosuit " + armorType.getName(),
                knockbackAmount, 0));
        }
        return map;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack me, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(me, player, list, advanced);
        if (me.hasTagCompound()) {
            if (hasPlates(me) && !"Thaumium".equals(UtilPlates.getPlate(me.getTagCompound().getString("plate")).getIdentifier()) &&
              !"Terrasteel".equals(UtilPlates.getPlate(me.getTagCompound().getString("plate")).getIdentifier())) {
                list.add(TextFormatting.BLUE + UtilPlates.getPlate(me.getTagCompound().getString("plate")).effect());
            }
            if (me.getTagCompound().hasKey("inv")) {
                for (int i = 3; i < 10; i++) {
                    if (me.getTagCompound().getCompoundTag("inv").hasKey(Integer.toString(i))) {
                        ItemStack stack = ItemStack.loadItemStackFromNBT(me.getTagCompound().getCompoundTag("inv").getCompoundTag(Integer.toString(i)));
                        list.add(TextFormatting.RED + stack.getDisplayName());
                    }
                }
            }
            if (me.getTagCompound().getCompoundTag("inv").hasKey("2")) {
                ItemStack stack = ItemStack.loadItemStackFromNBT(me.getTagCompound().getCompoundTag("inv").getCompoundTag("2"));
                if (stack.getItem() == ExosuitUpgradeItems.Items.ENDER_SHROUD.getItem()) {
                    list.add(TextFormatting.DARK_GREEN + I18n.format("steamcraft.exosuit.shroud"));
                } else {
                    int[] ids = OreDictionary.getOreIDs(stack);
                    int dye = -1;
                    outerloop:
                    for (int id : ids) {
                        String str = OreDictionary.getOreName(id);
                        if (str.contains("dye")) {
                            for (int i = 0; i < ModelExosuit.DYES.length; i++) {
                                if (ModelExosuit.DYES[i].equals(str.substring(3))) {
                                    dye = 15 - i;
                                    break outerloop;
                                }
                            }
                        }
                    }
                    if (dye != -1) {
                        list.add(TextFormatting.DARK_GREEN + I18n.format("steamcraft.color." + ModelExosuit.DYES[15 - dye].toLowerCase()));
                    } else {
                        list.add(TextFormatting.DARK_GREEN + stack.getDisplayName());
                    }
                }
            }
        }
        updateSteamNBT(me);
        if (slot == EntityEquipmentSlot.CHEST) {
           list.add(TextFormatting.WHITE + "" + me.getTagCompound().getInteger("steamFill") * 5 + "/" + me.getTagCompound().getInteger("maxFill") * 5 + " SU");
        }
    }

    @Override
    public void drawBackground(GuiEngineeringTable guiEngineeringTable, int i, int j, int k) {
        guiEngineeringTable.mc.getTextureManager().bindTexture(LARGE_ICONS);
        guiEngineeringTable.drawTexturedModalRect(j + 26, k + 3, 64 * slot.getSlotIndex(), 0, 64, 64);
    }

}