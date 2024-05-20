package net.shadowbeast.projectshadow.enums;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.shadowbeast.projectshadow.blocks.ModBlocks;
import net.shadowbeast.projectshadow.items.ModItems;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public enum ToolStats implements Tier {

    STEEL(2, 826, 6.0F, 2.0F, 15, ModItems.STEEL_INGOT.get()),
    PLATINUM(2, 510, 6.0F, 2.0F, 14, ModItems.PLATINUM_INGOT.get()),
    TITANIUM(2, 1120, 6.0F, 2.0F, 15, ModItems.TITANIUM_INGOT.get()),
    SILVER(0, 210, 12.0F, 2.0F, 20, ModItems.SILVER_INGOT.get()),
    ENDERIUM(5, 2530, 11.0F, 5.0F, 15, ModItems.ENDERIUM_INGOT.get()),
    BEDROCK(5, 2530, 11.0F, 5.0F, 15, ModItems.BEDROCK_CHUNK.get()),
    COPPER(2, 185, 8.0F, 2.0F, 15, Items.COPPER_INGOT),
    LUMINITE(3, 1720, 9.0F, 3.0F, 10, ModItems.LUMINITE_INGOT.get()),
    AQUANIUM(4, 2210, 10.0F, 4.0F, 15, ModItems.AQUANIUM_INGOT.get()),
    FIRERITE(3, 1780, 10.0F, 3.0F, 15, ModItems.FIRERITE_GEM.get()),
    FROZEN(3, 1820, 10.0F, 3.0F, 15, ModItems.FROZEN_GEM.get());

    private final int harvestLevel;
    private final int maxUses;
    private final float efficiency;
    private final float attackDamage;
    private final int enchantability;
    private final Ingredient repairMaterial;

    ToolStats(int harvestLevelIn, int maxUsesIn, float efficiencyIn, float attackDamageIn, int enchantabilityIn, Item repairMaterialIn)
    {
        this.harvestLevel = harvestLevelIn;
        this.maxUses = maxUsesIn;
        this.efficiency = efficiencyIn;
        this.attackDamage = attackDamageIn;
        this.enchantability = enchantabilityIn;
        this.repairMaterial = Ingredient.of(repairMaterialIn);
    }
    @Override
    public int getUses() {
        return maxUses;
    }
    @Override
    public float getSpeed() {
        return efficiency;
    }
    @Override
    public float getAttackDamageBonus() {
        return attackDamage;
    }
    @Override
    public int getLevel() {
        return harvestLevel;
    }
    @Override
    public int getEnchantmentValue() {
        return enchantability;
    }
    @Override
    public @NotNull Ingredient getRepairIngredient() {
        return repairMaterial;
    }
}