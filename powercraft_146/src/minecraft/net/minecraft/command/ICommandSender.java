package net.minecraft.command;

import net.minecraft.util.ChunkCoordinates;

public interface ICommandSender
{
    /**
     * Gets the name of this command sender (usually username, but possibly "Rcon")
     */
    String getCommandSenderName();

    void sendChatToPlayer(String var1);

    /**
     * Returns true if the command sender is allowed to use the given command.
     */
    boolean canCommandSenderUseCommand(int var1, String var2);

    /**
     * Translates and formats the given string key with the given arguments.
     */
    String translateString(String var1, Object ... var2);

    /**
     * Return the coordinates for this player as ChunkCoordinates.
     */
    ChunkCoordinates getPlayerCoordinates();
}
