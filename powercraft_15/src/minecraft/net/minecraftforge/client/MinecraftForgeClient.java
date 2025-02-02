/**
 * This software is provided under the terms of the Minecraft Forge Public
 * License v1.0.
 */

package net.minecraftforge.client;

import org.lwjgl.opengl.Display;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.common.MinecraftForge;

public class MinecraftForgeClient
{
    /**
     * NO-OP now. Not needed with new texturing system in MC 1.5
     */
    @Deprecated // without replacement
    public static void preloadTexture(String texture)
    {
//        ForgeHooksClient.engine().getTexture(texture);
    }

    private static IItemRenderer[] customItemRenderers = new IItemRenderer[Item.itemsList.length];

    /**
     * Register a custom renderer for a specific item. This can be used to
     * render the item in-world as an EntityItem, when the item is equipped, or
     * when the item is in an inventory slot.
     * @param itemID The item ID (shifted index) to handle rendering.
     * @param renderer The IItemRenderer interface that handles rendering for
     * this item.
     */
    public static void registerItemRenderer(int itemID, IItemRenderer renderer)
    {
        customItemRenderers[itemID] = renderer;
    }

    public static IItemRenderer getItemRenderer(ItemStack item, ItemRenderType type)
    {
        IItemRenderer renderer = customItemRenderers[item.itemID];
        if (renderer != null && renderer.handleRenderType(item, type))
        {
            return customItemRenderers[item.itemID];
        }
        return null;
    }

    public static int getRenderPass()
    {
        return ForgeHooksClient.renderPass;
    }

    public int getStencilBits()
    {
        return ForgeHooksClient.stencilBits;
    }
}
