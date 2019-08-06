//package com.blakebr0.ironjetpacks.handler;
//
//import com.blakebr0.cucumber.helper.RenderHelper;
//import com.blakebr0.cucumber.helper.ResourceHelper;
//import com.blakebr0.ironjetpacks.IronJetpacks;
//import com.blakebr0.ironjetpacks.client.util.HudHelper;
//import com.blakebr0.ironjetpacks.client.util.HudHelper.HudPos;
//import com.blakebr0.ironjetpacks.config.ModConfigs;
//import com.blakebr0.ironjetpacks.item.JetpackItem;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.gui.Gui;
//import net.minecraft.client.gui.GuiChat;
//import net.minecraft.client.renderer.GlStateManager;
//import net.minecraft.inventory.EntityEquipmentSlot;
//import net.minecraft.item.ItemStack;
//import net.minecraft.util.ResourceLocation;
//import net.minecraftforge.client.event.RenderGameOverlayEvent;
//import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
//import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
//import net.minecraftforge.fml.relauncher.Side;
//
//@EventBusSubscriber(modid = IronJetpacks.MOD_ID, value = Side.CLIENT)
//public class HudHandler {
//
//	public static final ResourceLocation HUD_TEXTURE = ResourceHelper.getResource(IronJetpacks.MOD_ID, "textures/gui/hud.png");
//
//	@SubscribeEvent
//	public void onRenderGameOverlay(RenderGameOverlayEvent event) {
//		Minecraft mc = Minecraft.getMinecraft();
//		if (mc.player != null) {
//			if (ModConfigs.confEnableHud && (ModConfigs.confShowHudOnChat || !ModConfigs.confShowHudOnChat && !(mc.currentScreen instanceof GuiChat)) && !mc.gameSettings.hideGUI && !mc.gameSettings.showDebugInfo) {
//				ItemStack chest = mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
//				if (!chest.isEmpty() && chest.getItem() instanceof JetpackItem) {
//					JetpackItem jetpack = (JetpackItem) chest.getItem();
//					HudPos pos = HudHelper.getHudPos();
//					int xPos = (int) (pos.x / 0.33) - 18;
//					int yPos = (int) (pos.y / 0.33) - 78;
//
//					mc.entityRenderer.setupOverlayRendering();
//					mc.renderEngine.bindTexture(HUD_TEXTURE);
//
//					GlStateManager.pushMatrix();
//					GlStateManager.scale(0.33, 0.33, 1.0);
//					RenderHelper.drawTexturedModelRect(xPos, yPos, 0, 0, 28, 156);
//					int i2 = HudHelper.getEnergyBarScaled(jetpack, chest);
//					RenderHelper.drawTexturedModelRect(xPos, 166 - i2 + yPos - 10, 28, 156 - i2, 28, i2);
//					GlStateManager.popMatrix();
//
//					String fuel = HudHelper.getFuel(jetpack, chest);
//					String engine = "E: " + HudHelper.getOn(jetpack.isEngineOn(chest));
//					String hover = "H: " + HudHelper.getOn(jetpack.isHovering(chest));
//
//					if (pos.side == 1) {
//						mc.fontRenderer.drawStringWithShadow(fuel, pos.x - 8 - mc.fontRenderer.getStringWidth(fuel), pos.y - 21, 16383998);
//						mc.fontRenderer.drawStringWithShadow(engine, pos.x - 8 - mc.fontRenderer.getStringWidth(engine), pos.y + 4, 16383998);
//						mc.fontRenderer.drawStringWithShadow(hover, pos.x - 8 - mc.fontRenderer.getStringWidth(hover), pos.y + 14, 16383998);
//					} else {
//						mc.fontRenderer.drawStringWithShadow(fuel, pos.x + 6, pos.y - 21, 16383998);
//						mc.fontRenderer.drawStringWithShadow(engine, pos.x + 6, pos.y + 4, 16383998);
//						mc.fontRenderer.drawStringWithShadow(hover, pos.x + 6, pos.y + 14, 16383998);
//					}
//
//					GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
//
//					mc.renderEngine.bindTexture(Gui.ICONS);
//				}
//			}
//		}
//	}
//}
