package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather extends CommandBase
{
    public String getCommandName()
    {
        return "weather";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel()
    {
        return 2;
    }

    public void processCommand(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        if (par2ArrayOfStr.length < 1)
        {
            throw new WrongUsageException("commands.weather.usage", new Object[0]);
        }
        else
        {
            int i = (300 + (new Random()).nextInt(600)) * 20;

            if (par2ArrayOfStr.length >= 2)
            {
                i = parseIntBounded(par1ICommandSender, par2ArrayOfStr[1], 1, 1000000) * 20;
            }

            WorldServer worldserver = MinecraftServer.getServer().worldServers[0];
            WorldInfo worldinfo = worldserver.getWorldInfo();
            worldinfo.setRainTime(i);
            worldinfo.setThunderTime(i);

            if ("clear".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
                worldinfo.setRaining(false);
                worldinfo.setThundering(false);
                notifyAdmins(par1ICommandSender, "commands.weather.clear", new Object[0]);
            }
            else if ("rain".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
                worldinfo.setRaining(true);
                worldinfo.setThundering(false);
                notifyAdmins(par1ICommandSender, "commands.weather.rain", new Object[0]);
            }
            else if ("thunder".equalsIgnoreCase(par2ArrayOfStr[0]))
            {
                worldinfo.setRaining(true);
                worldinfo.setThundering(true);
                notifyAdmins(par1ICommandSender, "commands.weather.thunder", new Object[0]);
            }
        }
    }

    /**
     * Adds the strings available in this command to the given list of tab completion options.
     */
    public List addTabCompletionOptions(ICommandSender par1ICommandSender, String[] par2ArrayOfStr)
    {
        return par2ArrayOfStr.length == 1 ? getListOfStringsMatchingLastWord(par2ArrayOfStr, new String[] {"clear", "rain", "thunder"}): null;
    }
}
