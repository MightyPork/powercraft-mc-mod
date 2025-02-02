package net.minecraft.command;

import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumChatFormatting;

public class CommandServerMessage extends CommandBase
{
    public List getCommandAliases()
    {
        return Arrays.asList(new String[] {"w", "msg"});
    }

    public String getCommandName()
    {
        return "tell";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 0;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 2)
        {
            throw new WrongUsageException("commands.message.usage", new Object[0]);
        }
        else
        {
            EntityPlayerMP entityplayermp = func_82359_c(par1ICommandSender, par2ArrayOfStr[0]);

            if (entityplayermp == null)
            {
                throw new PlayerNotFoundException();
            }
            else if (entityplayermp == par1ICommandSender)
            {
                throw new PlayerNotFoundException("commands.message.sameTarget", new Object[0]);
            }
            else
            {
                String s = func_82361_a(par1ICommandSender, par2ArrayOfStr, 1, !(par1ICommandSender instanceof EntityPlayer));
                entityplayermp.sendChatToPlayer(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + entityplayermp.translateString("commands.message.display.incoming", new Object[] {par1ICommandSender.getCommandSenderName(), s}));
                par1ICommandSender.sendChatToPlayer(EnumChatFormatting.GRAY + "" + EnumChatFormatting.ITALIC + par1ICommandSender.translateString("commands.message.display.outgoing", new Object[] {entityplayermp.getCommandSenderName(), s}));
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getAllUsernames());
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 0;
    }
}
