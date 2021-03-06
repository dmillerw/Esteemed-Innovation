package eiteam.esteemedinnovation.firearms;

import eiteam.esteemedinnovation.api.book.BookCategory;
import eiteam.esteemedinnovation.api.book.BookSection;
import eiteam.esteemedinnovation.book.BookPieceUnlockedStateChangePacket;
import eiteam.esteemedinnovation.commons.EsteemedInnovation;
import eiteam.esteemedinnovation.commons.capabilities.player.PlayerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FlintlockBookSection extends BookSection {
    public static final String NAME = "section.Flintlock.name";

    public FlintlockBookSection(BookCategory... categories) {
        super(NAME, categories);
    }

    @Override
    public boolean isUnlocked(EntityPlayer player) {
        PlayerData data = player.getCapability(EsteemedInnovation.PLAYER_DATA, null);
        return data.getAllUnlockedPieces().contains(NAME);
    }

    public static class EventHandlers {
        @SubscribeEvent
        public void inspirePlayerWithBoom(ExplosionEvent.Detonate event) {
            for (Entity entity : event.getAffectedEntities()) {
                if (entity instanceof EntityPlayerMP) {
                    EntityPlayerMP player = (EntityPlayerMP) entity;
                    PlayerData data = player.getCapability(EsteemedInnovation.PLAYER_DATA, null);
                    if (data.setHasUnlockedBookPiece(NAME, true)) {
                        EsteemedInnovation.channel.sendTo(new BookPieceUnlockedStateChangePacket(NAME, true), player);
                    }
                }
            }
        }
    }
}
