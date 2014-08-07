package flaxbeard.steamcraft.tile;

import net.minecraft.block.BlockContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import flaxbeard.steamcraft.api.tile.SteamTransporterTileEntity;

public class TileEntityBlockPlacer extends SteamTransporterTileEntity implements IInventory{
	
	private ItemStack[] inventory = new ItemStack[1];
	int tick = 0;
	
	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return this.inventory.length;
	}
	@Override
	public ItemStack getStackInSlot(int slot) {
		if (inventory.length -1 >= slot){
			return inventory[slot];
		}
		return null;
	}
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = this.getStackInSlot(slot);
		if (stack != null){
			if (stack.stackSize <= amount){
				this.inventory[slot] = null;
			} else {
				stack = stack.splitStack(amount);
				if (getStackInSlot(slot).stackSize == 0){
					this.inventory[slot] = null;
				}
			}
			return stack;
		}
		return null;
	}
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		return getStackInSlot(slot);
	}
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack) {
		if (this.inventory.length -1 >= slot){
			this.inventory[slot] = stack;
		}
	}
	
	@Override
	public String getInventoryName() {
		return null;
	}
	
	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return false;
	}
	
	@Override
	public void openInventory() {}
	
	@Override
	public void closeInventory() {}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		Item item = stack.getItem();
		if (item instanceof ItemBlock){
			//log.debug("it's a block");
			ItemBlock ib = (ItemBlock) item;
			if (! (ib.field_150939_a instanceof BlockContainer)){
				log.debug("Just a regular block, boss");
				return true;
			}
		}
		return false;
	}
	
	private boolean hasItem(){
		if (this.getStackInSlot(0) != null){
			return true;
		}
		return false;
	}
	
	@Override
	public void updateEntity(){
		super.updateEntity();
		if (tick == 0 && )
	}
	
	

}
