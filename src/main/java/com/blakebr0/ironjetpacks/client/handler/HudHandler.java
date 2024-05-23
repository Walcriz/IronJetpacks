package com.blakebr0.ironjetpacks.client.handler;

import com.blakebr0.cucumber.lib.Colors;
import com.blakebr0.ironjetpacks.IronJetpacks;
import com.blakebr0.ironjetpacks.config.ModConfigs;
import com.blakebr0.ironjetpacks.item.JetpackItem;
import com.blakebr0.ironjetpacks.lib.ModTooltips;
import com.blakebr0.ironjetpacks.util.JetpackUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.joml.Math;

public final class HudHandler {
    private static final ResourceLocation HUD_TEXTURE = new ResourceLocation(IronJetpacks.MOD_ID, "textures/gui/hud.png");

    // Animation progress = 0 means that the hud is currently shown, 1 for hidden
    private static double animationProgress = 0;
    private static boolean wasHidden = false;
    private static final IGuiOverlay HUD_OVERLAY = (gui, gfx, partialTick, width, height) -> {
        var mc = Minecraft.getInstance();
        if (mc.player != null && isVisible(mc)) {
            var chest = JetpackUtils.getEquippedJetpack(mc.player);
            var item = chest.getItem();

            if (!chest.isEmpty() && item instanceof JetpackItem) {

                // Check if hud should be hidden
                if (ModConfigs.HIDE_HUD_ON_ENGINE_OFF.get() && JetpackUtils.isEngineOn(chest) && animationProgress > 0) {
                    wasHidden = false;
                } else if (!ModConfigs.HIDE_HUD_ON_ENGINE_OFF.get()) {
                    wasHidden = false;
                } else if (!JetpackUtils.isEngineOn(chest) && animationProgress == 0) {
                    wasHidden = true;
                }

                // Animate the hud
                var animationStep = wasHidden ? ModConfigs.HUD_ANIMATION_SPEED.get() : -ModConfigs.HUD_ANIMATION_SPEED.get();
                animationProgress = Math.clamp(0, animationStep + animationProgress, 1);

                if (animationProgress == 1)
                    return;

                var pos = getHudPos(animationProgress);
                if (pos != null) {
                    int xPos = (int) (pos.x / 0.33) - 18;
                    int yPos = (int) (pos.y / 0.33) - 78;

                    var matrix = gfx.pose();

                    matrix.pushPose();
                    matrix.scale(0.33F, 0.33F, 1.0F);
                    gfx.blit(HUD_TEXTURE, xPos, yPos, 0, 0, 28, 156, 256, 256);
                    int i2 = getEnergyBarScaled(chest);
                    gfx.blit(HUD_TEXTURE, xPos, 166 - i2 + yPos - 10, 28, 156 - i2, 28, i2, 256, 256);
                    matrix.popPose();

                    var fuel = Colors.GRAY + getFuelString(chest);
                    var throttle = Colors.GRAY + "T: " + (int) (JetpackUtils.getThrottle(chest) * 100) + "%";
                    var engine = Colors.GRAY + "E: " + getStatusString(JetpackUtils.isEngineOn(chest));
                    var hover = Colors.GRAY + "H: " + getStatusString(JetpackUtils.isHovering(chest));

                    if (pos.side == 1) {
                        gfx.drawString(mc.font, fuel, pos.x - 8 - mc.font.width(fuel), pos.y - 21, 16383998);
                        gfx.drawString(mc.font, fuel, pos.x - 8 - mc.font.width(throttle), pos.y - 6, 16383998);
                        gfx.drawString(mc.font, engine, pos.x - 8 - mc.font.width(engine), pos.y + 4, 16383998);
                        gfx.drawString(mc.font, hover, pos.x - 8 - mc.font.width(hover), pos.y + 14, 16383998);
                    } else {
                        gfx.drawString(mc.font, fuel, pos.x + 6, pos.y - 21, 16383998);
                        gfx.drawString(mc.font, throttle, pos.x + 6, pos.y - 6, 16383998);
                        gfx.drawString(mc.font, engine, pos.x + 6, pos.y + 4, 16383998);
                        gfx.drawString(mc.font, hover, pos.x + 6, pos.y + 14, 16383998);
                    }
                }
            } else {
                wasHidden = true;
                animationProgress = 1;
            }
        }
    };

    @SubscribeEvent
    public void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.HOTBAR.id(), "jetpack_hud", HUD_OVERLAY);
    }

    private static HudPos getHudPos(double animationProgress) {
        var window = Minecraft.getInstance().getWindow();
        int xOffset = ModConfigs.HUD_OFFSET_X.get();
        int yOffset = ModConfigs.HUD_OFFSET_Y.get();

        var pos = switch (ModConfigs.HUD_POSITION.get()) {
            case 0 -> new HudPos(10 + xOffset, 30 + yOffset, 0);
            case 1 -> new HudPos(10 + xOffset, window.getGuiScaledHeight() / 2 + yOffset, 0);
            case 2 -> new HudPos(10 + xOffset, window.getGuiScaledHeight() - 30 + yOffset, 0);
            case 3 -> new HudPos(window.getGuiScaledWidth() - 8 - xOffset, 30 + yOffset, 1);
            case 4 ->
                    new HudPos(window.getGuiScaledWidth() - 8 - xOffset, window.getGuiScaledHeight() / 2 + yOffset, 1);
            case 5 ->
                    new HudPos(window.getGuiScaledWidth() - 8 - xOffset, window.getGuiScaledHeight() - 30 + yOffset, 1);
            default -> null;
        };

        if (pos == null)
            return null;

        pos.x = getOffset(pos, animationProgress);
        return pos;
    }

    private static int getOffset(HudPos pos, double progress) {
        if (pos.side == 0)
            return (int) (pos.x - progress * 70);
        else
            return (int) (pos.x + progress * 70);
    }

    private static int getEnergyBarScaled(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        if (jetpack.creative)
            return 156;

        var energy = JetpackUtils.getEnergyStorage(stack);
        int i = energy.getEnergyStored();
        int j = energy.getMaxEnergyStored();

        return (int) (j != 0 && i != 0 ? (long) i * 156 / j : 0);
    }

    private static String getFuelString(ItemStack stack) {
        var jetpack = JetpackUtils.getJetpack(stack);
        if (jetpack.creative) {
            return ModTooltips.INFINITE.buildString() + Colors.GRAY + " FE";
        }

        int number = JetpackUtils.getEnergyStorage(stack).getEnergyStored();
        if (number >= 1000000000) {
            int big = number / 1000000000;
            int small = (number - (big * 1000000000)) / 100000000;
            return big + ((small != 0) ? "." + small : "") + Colors.GRAY + "G FE";
        } else if (number >= 1000000) {
            int big = number / 1000000;
            int small = (number - (big * 1000000)) / 100000;
            return big + ((small != 0) ? "." + small : "") + Colors.GRAY + "M FE";
        } else if (number >= 1000) {
            return number / 1000 + Colors.GRAY + "k FE";
        } else {
            return number + Colors.GRAY + " FE";
        }
    }

    private static String getStatusString(boolean on) {
        return on ? Colors.GREEN + ModTooltips.ON.buildString() : Colors.RED + ModTooltips.OFF.buildString();
    }

    private static boolean isVisible(Minecraft mc) {
        return ModConfigs.ENABLE_HUD.get()
                && (ModConfigs.SHOW_HUD_OVER_CHAT.get()
                || !ModConfigs.SHOW_HUD_OVER_CHAT.get()
                && !(mc.screen instanceof ChatScreen))
                && !mc.options.hideGui
                && !mc.options.renderDebug;
    }

    private static class HudPos {
        public int x;
        public int y;
        public int side;

        public HudPos(int x, int y, int side) {
            this.x = x;
            this.y = y;
            this.side = side;
        }
    }
}
