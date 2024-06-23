package net.shadowbeast.arcanemysteries.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.atomic.AtomicBoolean;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Shadow
    public abstract void resetFallDistance();

    @Shadow
    public float fallDistance;
    Entity entity = ((Entity) (Object) this);

    @Inject(method = "checkFallDamage", at = @At(value = "HEAD"))
    private void cancelFallDamage(double pY, boolean pOnGround, BlockState pState, BlockPos pPos, CallbackInfo ci) {
        AtomicBoolean levitationTagged = new AtomicBoolean(false);
        if (this.entity instanceof ServerPlayer player) {
//            player.getCapability(PlayerLevitationTagProvider.PLAYER_THIRST).ifPresent(levitationTag -> {
//                levitationTagged.set(levitationTag.isLevitationTagged());
//            });
        }
        if (levitationTagged.get()) {
            if (pOnGround && this.fallDistance > 0.0F) {
                if (entity instanceof ServerPlayer player) {
//                    player.getCapability(PlayerLevitationTagProvider.PLAYER_THIRST).ifPresent(levitationTag -> {
//                        resetFallDistance();
//                        levitationTag.setLevitationTagged(false);
//                        MessagesMod.sendToPlayer(new LevitationDataSyncS2CPacket(levitationTag.isLevitationTagged()), player);
//                    });
                }
            }
        }
    }
}