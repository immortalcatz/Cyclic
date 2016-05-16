package com.lothrazar.cyclicmagic.item;

import com.lothrazar.cyclicmagic.ICanRegister;
import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.registry.ItemRegistry;
import com.lothrazar.cyclicmagic.util.Const;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public  class BaseItem extends Item implements ICanRegister{
  
	private boolean isEnabled = true;

	@Override
	public void tryRegister(String rawName){
		if(this.isEnabled){
			ModMain.itemRegistry.registerItem(this, rawName);
		}
	}
	 
	@Override
	public void setEnabled(boolean e) {
		this.isEnabled = e;
	} 
}
