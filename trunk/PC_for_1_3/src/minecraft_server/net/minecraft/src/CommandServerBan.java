package net.minecraft.src;

import java.util.List;
import net.minecraft.server.MinecraftServer;

public class CommandServerBan extends CommandBase
{
    public String getCommandName()
    {
        return "ban";
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("commands.ban.usage", new Object[0]);
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     */
    public boolean canCommandSenderUseCommand(ICommandSender par1ICommandSender)
    {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().func_73710_b() && super.canCommandSenderUseCommand(par1ICommandSender);
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length >= 1 && par2ArrayOfStr[0].length() > 0)
        {
            EntityPlayerMP var3 = MinecraftServer.getServer().getConfigurationManager().getPlayerEntity(par2ArrayOfStr[0]);
            BanEntry var4 = new BanEntry(par2ArrayOfStr[0]);
            var4.func_73687_a(par1ICommandSender.getCommandSenderName());

            if (par2ArrayOfStr.length >= 2)
            {
                var4.func_73689_b(joinString(par2ArrayOfStr, 1));
            }

            MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().func_73706_a(var4);

            if (var3 != null)
            {
                var3.playerNetServerHandler.kickPlayer("You are banned from this server.");
            }

            func_71522_a(par1ICommandSender, "commands.ban.success", new Object[] {par2ArrayOfStr[0]});
        }
        else
        {
            throw new WrongUsageException("commands.ban.usage", new Object[0]);
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length >= 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, MinecraftServer.getServer().getPlayerNamesAsList()) : null;
    }
}
