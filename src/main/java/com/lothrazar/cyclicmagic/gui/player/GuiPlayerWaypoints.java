package com.lothrazar.cyclicmagic.gui.player;

import com.lothrazar.cyclicmagic.util.Const;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.achievement.GuiAchievements;
import net.minecraft.client.gui.achievement.GuiStats;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;

public class GuiPlayerWaypoints extends InventoryEffectRenderer {

	public static final ResourceLocation background = new ResourceLocation(Const.MODID, "textures/gui/inventory.png");

	public GuiPlayerWaypoints(EntityPlayer player) {
		//!player.worldObj.isRemote, player
		super(new ContainerPlayerWaypoints(player.inventory,player.worldObj ));
		this.allowUserInput = true;
	}

	@Override
	public void updateScreen() {
	
		this.updateActivePotionEffects();
	}

	@Override
	public void initGui() {
		this.buttonList.clear();
		super.initGui();

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		this.fontRendererObj.drawString(I18n.translateToLocal("container.WAYPOINTS"), 97, 8, 4210752);
	}

	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(background);
		int k = this.guiLeft;
		int l = this.guiTop;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

	}

	@Override
	protected void actionPerformed(GuiButton button) {
		
		if (button.id == 0) {
			this.mc.displayGuiScreen(new GuiAchievements(this, this.mc.thePlayer.getStatFileWriter()));
		}

		if (button.id == 1) {
			this.mc.displayGuiScreen(new GuiStats(this, this.mc.thePlayer.getStatFileWriter()));
		}
		
	}
}
