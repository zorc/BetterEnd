package ru.betterend.item;

import net.fabricmc.fabric.api.tool.attribute.v1.DynamicAttributeTool;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.tag.Tag;
import ru.betterend.patterns.Patterned;
import ru.betterend.patterns.Patterns;

public class EndShovelItem extends ShovelItem implements DynamicAttributeTool, Patterned {
	public EndShovelItem(ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
		super(material, attackDamage, attackSpeed, settings);
	}
	
	@Override
	public int getMiningLevel(Tag<Item> tag, BlockState state, ItemStack stack, LivingEntity user) {
		if (tag.equals(FabricToolTags.SHOVELS)) {
			return this.getMaterial().getMiningLevel();
		}
		return 0;
	}
	
	@Override
	public String getModelPattern(String name) {
		return Patterns.createJson(Patterns.ITEM_HANDHELD, name);
	}
}
