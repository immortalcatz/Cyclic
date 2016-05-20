package com.lothrazar.cyclicmagic.gui.player;

import com.lothrazar.cyclicmagic.ModMain;
import com.lothrazar.cyclicmagic.gui.waypoints.GuiEnderBook;
import com.lothrazar.cyclicmagic.net.PacketOpenWaypoints;
import com.lothrazar.cyclicmagic.net.PacketOpenNormalInventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ButtonTabToggleWaypoints extends GuiButton{

	private EntityPlayer gui;
	public ButtonTabToggleWaypoints(EntityPlayer g, int x, int y) {
		super(51, x, y, 10, 10, "W");
		gui = g;

	}
	@SideOnly(Side.CLIENT)
	@Override
	public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
		boolean pressed = super.mousePressed(mc, mouseX, mouseY);

		if (pressed) {
			 Minecraft.getMinecraft().displayGuiScreen(new GuiEnderBook(gui));
//			if (this.gui instanceof GuiInventory) {
//				ModMain.network.sendToServer(new PacketOpenWaypoints(this.gui.mc.thePlayer));
//			}
//			else {
//				this.gui.mc.displayGuiScreen(new GuiInventory(gui.mc.thePlayer));
//				ModMain.network.sendToServer(new PacketOpenNormalInventory(this.gui.mc.thePlayer));		 
//			}
		}

		return pressed;
	}
}
