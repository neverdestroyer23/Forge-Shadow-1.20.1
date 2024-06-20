package net.shadowbeast.arcanemysteries.items;

import com.mojang.datafixers.util.Pair;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CompassItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.network.PacketDistributor;
import net.shadowbeast.arcanemysteries.networking.MessagesMod;
import net.shadowbeast.arcanemysteries.networking.compass.Async;
import net.shadowbeast.arcanemysteries.networking.compass.CompassMessages;
import net.shadowbeast.arcanemysteries.networking.compass.Reference;
import net.shadowbeast.arcanemysteries.networking.packet.ClientboundStatsPacket;
import net.shadowbeast.arcanemysteries.util.Compass.CompassConfig;
import net.shadowbeast.arcanemysteries.util.Compass.CompassStructureUtil;

import java.util.List;
import java.util.Optional;


public class ItemStrongholdCompass extends CompassItem {

    public ItemStrongholdCompass(Item.Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand hand) {
        ItemStack stack = playerIn.getItemInHand(hand);
        if (playerIn.isShiftKeyDown()) {
            if (!worldIn.isClientSide) {
                List<ResourceLocation> allStructures = CompassStructureUtil.getAvailableStructureList(worldIn);
                MessagesMod.INSTANCE.send(PacketDistributor.PLAYER.with(() -> (ServerPlayer) playerIn), new CompassMessages(hand, stack, allStructures));
            }
        } else {
            locateStructure(stack, playerIn);
        }

        return super.use(worldIn, playerIn, hand);
    }

    private void locateStructure(ItemStack stack, Player player) {
        if (!player.level().isClientSide) {
            if (stack.hasTag() && stack.getTag().contains(Reference.structure_tag)) {
                ServerLevel level = (ServerLevel) player.level();
                CompoundTag tag = stack.getTag();

                String boundStructure = tag.getString(Reference.structure_tag);
                ResourceLocation structureLocation = ResourceLocation.tryParse(boundStructure);

                if (structureLocation != null && structureLocation.equals(BuiltinStructures.STRONGHOLD.location()) && !CompassStructureUtil.isBlacklisted(structureLocation)) {
                    player.sendSystemMessage(Component.translatable("structurecompass.structure.locating", structureLocation).withStyle(ChatFormatting.YELLOW));
                    Registry<Structure> registry = level.registryAccess().registryOrThrow(Registries.STRUCTURE);
                    ResourceKey<Structure> structureKey = ResourceKey.create(Registries.STRUCTURE, structureLocation);
                    Optional<Holder.Reference<Structure>> structureHolder = registry.getHolder(structureKey);

                    if (structureHolder.isPresent()) {
                        HolderSet<Structure> featureHolderSet = HolderSet.direct(structureHolder.get());
                        boolean findUnexplored = CompassConfig.COMMON.locateUnexplored.get() != null && CompassConfig.COMMON.locateUnexplored.get();

                        if (CompassConfig.COMMON.locateAsync.get()) {
                            var async = Async.locate(level, featureHolderSet, player.blockPosition(), 100, findUnexplored);
                            async.thenOnServerThread(pair -> bindPosition(stack, tag, boundStructure, player, level, pair));
                        } else {
                            Pair<BlockPos, Holder<Structure>> pair = CompassStructureUtil.findNearestMapStructure(level, featureHolderSet, player.blockPosition(), 100, findUnexplored);
                            bindPosition(stack, tag, boundStructure, player, level, pair);
                        }
                    }
                } else {
                    player.sendSystemMessage(Component.translatable("structurecompass.locate.fail").withStyle(ChatFormatting.RED));
                }
            } else {
                player.sendSystemMessage(Component.translatable("structurecompass.structure.unset.tooltip").withStyle(ChatFormatting.YELLOW));
            }
        }
    }

    private void bindPosition(ItemStack stack, CompoundTag tag, String boundStructure, Player player, ServerLevel level, Pair<BlockPos, Holder<Structure>> pair) {
        if (pair != null) {
            BlockPos pos = pair.getFirst();
            tag.putLong("LodestonePos", pos.asLong());
            tag.putString("LodestoneDimension", level.dimension().location().toString());
            stack.setTag(tag);
            player.sendSystemMessage(Component.translatable("structurecompass.structure.found", boundStructure, pos).withStyle(ChatFormatting.GREEN));
        } else {
            player.sendSystemMessage(Component.translatable("structurecompass.structure.notfound", boundStructure).withStyle(ChatFormatting.RED));
        }
    }


}
