package net.minecraft.command;

import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandXP extends CommandBase
{
    public String getCommandName()
    {
        return "xp";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public String getCommandUsage(ICommandSender par1ICommandSender)
    {
        return par1ICommandSender.translateString("commands.xp.usage", new Object[0]);
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length <= 0)
        {
            throw new WrongUsageException("commands.xp.usage", new Object[0]);
        }
        else
        {
            String s = par2ArrayOfStr[0];
            boolean flag = s.endsWith("l") || s.endsWith("L");

            if (flag && s.length() > 1)
            {
                s = s.substring(0, s.length() - 1);
            }

            int i = parseInt(par1ICommandSender, s);
            boolean flag1 = i < 0;

            if (flag1)
            {
                i *= -1;
            }

            EntityPlayerMP entityplayermp;

            if (par2ArrayOfStr.length > 1)
            {
                entityplayermp = func_82359_c(par1ICommandSender, par2ArrayOfStr[1]);
            }
            else
            {
                entityplayermp = getCommandSenderAsPlayer(par1ICommandSender);
            }

            if (flag)
            {
                if (flag1)
                {
                    entityplayermp.addExperienceLevel(-i);
                    notifyAdmins(par1ICommandSender, "commands.xp.success.negative.levels", new Object[] {Integer.valueOf(i), entityplayermp.getEntityName()});
                }
                else
                {
                    entityplayermp.addExperienceLevel(i);
                    notifyAdmins(par1ICommandSender, "commands.xp.success.levels", new Object[] {Integer.valueOf(i), entityplayermp.getEntityName()});
                }
            }
            else
            {
                if (flag1)
                {
                    throw new WrongUsageException("commands.xp.failure.widthdrawXp", new Object[0]);
                }

                entityplayermp.addExperience(i);
                notifyAdmins(par1ICommandSender, "commands.xp.success", new Object[] {Integer.valueOf(i), entityplayermp.getEntityName()});
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 2 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, this.getAllUsernames()) : null;
    }

    protected String[] getAllUsernames()
    {
        return MinecraftServer.getServer().getAllUsernames();
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     */
    public boolean isUsernameIndex(String[] par1ArrayOfStr, int par2)
    {
        return par2 == 1;
    }
}
