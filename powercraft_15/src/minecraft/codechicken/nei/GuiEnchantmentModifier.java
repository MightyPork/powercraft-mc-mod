package codechicken.nei;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.world.World;

import org.lwjgl.opengl.GL11;

public class GuiEnchantmentModifier extends GuiContainer
{
    ContainerEnchantmentModifier container;
    
    public GuiEnchantmentModifier(InventoryPlayer inventoryplayer, World world, int i, int j, int k)
    {
        super(new ContainerEnchantmentModifier(inventoryplayer, world, i, j, k));
        container = (ContainerEnchantmentModifier) inventorySlots;
        container.parentscreen = this;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRenderer.drawString("Enchant", 12, 6, 0x404040);
        fontRenderer.drawString("Level", 19, 20, 0x404040);
        //fontRenderer.drawString("Inventory", 8, (ySize - 96) + 2, 0x404040);
    }
    
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        mc.renderEngine.bindTexture("/gui/enchant.png");
        GL11.glTranslatef(guiLeft, guiTop, 0);
        drawTexturedModalRect(0, 0, 0, 0, xSize, ySize);
        
        container.onUpdate(i, j);
        container.drawSlots(this);
        container.drawScrollBar(this);
        
        String levelstring = ""+container.level;
        fontRenderer.drawString(levelstring, 33 - fontRenderer.getStringWidth(levelstring) / 2, 34, 0xFF606060);
        
        GL11.glTranslatef(-guiLeft, -guiTop, 0);
    }

    @SuppressWarnings("unchecked")
    public void initGui()
    {
        super.initGui();
        
        buttonList.add(new GuiNEIButton(0, width/2 - 78, height / 2 - 52, 12, 12, "<"));
        buttonList.add(new GuiNEIButton(1, width/2 - 44, height / 2 - 52, 12, 12, ">"));
        buttonList.add(new GuiNEIButton(2, width/2 - 80, height / 2 - 15, 50, 12, NEIClientConfig.validateEnchantments() ? "Locked" : "Unlocked"));
    }
    
    protected void actionPerformed(GuiButton guibutton)
    {
        if(guibutton.id == 0)
        {
            changeLevel(-1);
        }
        else if(guibutton.id == 1)
        {
            changeLevel(1);
        }
        else if(guibutton.id == 2)
        {
            NEIClientConfig.toggleEnchantmentValidation();
            container.updateEnchantmentOptions();
            guibutton.displayString = NEIClientConfig.validateEnchantments() ? "Locked" : "Unlocked";
        }
    }
    
    private void changeLevel(int i)
    {
        container.level+=i;
        ((GuiButton)buttonList.get(0)).enabled = container.level != 1;
        ((GuiButton)buttonList.get(1)).enabled = container.level != 10;
    }

    protected void mouseClicked(int i, int j, int k)
    {
        if(container.clickButton(i, j, k))return;
        if(container.clickScrollBar(i, j, k))return;
        
        super.mouseClicked(i, j, k);
    }
    
    protected void mouseMovedOrUp(int i, int j, int k)
    {
        container.mouseUp(i, j, k);
        super.mouseMovedOrUp(i, j, k);
    }
}
