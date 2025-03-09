package org.jimmybobjim.oreminers.util;

import net.minecraft.world.item.ItemStack;

public record ChancedItemDrop(ItemStack stack, int chance, int tierChanceBoost) {
}
