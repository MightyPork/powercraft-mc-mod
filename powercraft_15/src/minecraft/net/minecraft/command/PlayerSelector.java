package net.minecraft.command;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.util.MathHelper;
import net.minecraft.world.EnumGameType;

public class PlayerSelector
{
    /**
     * This matches the at-tokens introduced for command blocks, including their arguments, if any.
     */
    private static final Pattern tokenPattern = Pattern.compile("^@([parf])(?:\\[([\\w=,!-]*)\\])?$");

    /**
     * This matches things like "-1,,4", and is used for getting x,y,z,range from the token's argument list.
     */
    private static final Pattern intListPattern = Pattern.compile("\\G([-!]?\\w*)(?:$|,)");

    /**
     * This matches things like "rm=4,c=2" and is used for handling named token arguments.
     */
    private static final Pattern keyValueListPattern = Pattern.compile("\\G(\\w+)=([-!]?\\w*)(?:$|,)");

    /**
     * Returns the one player that matches the given at-token.  Returns null if more than one player matches.
     */
    public static EntityPlayerMP matchOnePlayer(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP[] aentityplayermp = matchPlayers(par0ICommandSender, par1Str);
        return aentityplayermp != null && aentityplayermp.length == 1 ? aentityplayermp[0] : null;
    }

    /**
     * Returns a nicely-formatted string listing the matching players.
     */
    public static String matchPlayersAsString(ICommandSender par0ICommandSender, String par1Str)
    {
        EntityPlayerMP[] aentityplayermp = matchPlayers(par0ICommandSender, par1Str);

        if (aentityplayermp != null && aentityplayermp.length != 0)
        {
            String[] astring = new String[aentityplayermp.length];

            for (int i = 0; i < astring.length; ++i)
            {
                astring[i] = aentityplayermp[i].getTranslatedEntityName();
            }

            return CommandBase.joinNiceString(astring);
        }
        else
        {
            return null;
        }
    }

    /**
     * Returns an array of all players matched by the given at-token.
     */
    public static EntityPlayerMP[] matchPlayers(ICommandSender par0ICommandSender, String par1Str)
    {
        Matcher matcher = tokenPattern.matcher(par1Str);

        if (matcher.matches())
        {
            Map map = getArgumentMap(matcher.group(2));
            String s1 = matcher.group(1);
            int i = getDefaultMinimumRange(s1);
            int j = getDefaultMaximumRange(s1);
            int k = getDefaultMinimumLevel(s1);
            int l = getDefaultMaximumLevel(s1);
            int i1 = getDefaultCount(s1);
            int j1 = EnumGameType.NOT_SET.getID();
            ChunkCoordinates chunkcoordinates = par0ICommandSender.getPlayerCoordinates();
            Map map1 = func_96560_a(map);
            String s2 = null;
            String s3 = null;

            if (map.containsKey("rm"))
            {
                i = MathHelper.parseIntWithDefault((String)map.get("rm"), i);
            }

            if (map.containsKey("r"))
            {
                j = MathHelper.parseIntWithDefault((String)map.get("r"), j);
            }

            if (map.containsKey("lm"))
            {
                k = MathHelper.parseIntWithDefault((String)map.get("lm"), k);
            }

            if (map.containsKey("l"))
            {
                l = MathHelper.parseIntWithDefault((String)map.get("l"), l);
            }

            if (map.containsKey("x"))
            {
                chunkcoordinates.posX = MathHelper.parseIntWithDefault((String)map.get("x"), chunkcoordinates.posX);
            }

            if (map.containsKey("y"))
            {
                chunkcoordinates.posY = MathHelper.parseIntWithDefault((String)map.get("y"), chunkcoordinates.posY);
            }

            if (map.containsKey("z"))
            {
                chunkcoordinates.posZ = MathHelper.parseIntWithDefault((String)map.get("z"), chunkcoordinates.posZ);
            }

            if (map.containsKey("m"))
            {
                j1 = MathHelper.parseIntWithDefault((String)map.get("m"), j1);
            }

            if (map.containsKey("c"))
            {
                i1 = MathHelper.parseIntWithDefault((String)map.get("c"), i1);
            }

            if (map.containsKey("team"))
            {
                s3 = (String)map.get("team");
            }

            if (map.containsKey("name"))
            {
                s2 = (String)map.get("name");
            }

            List list;

            if (!s1.equals("p") && !s1.equals("a"))
            {
                if (!s1.equals("r"))
                {
                    return null;
                }
                else
                {
                    list = MinecraftServer.getServer().getConfigurationManager().findPlayers(chunkcoordinates, i, j, 0, j1, k, l, map1, s2, s3);
                    Collections.shuffle(list);
                    list = list.subList(0, Math.min(i1, list.size()));
                    return list != null && !list.isEmpty() ? (EntityPlayerMP[])list.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
                }
            }
            else
            {
                list = MinecraftServer.getServer().getConfigurationManager().findPlayers(chunkcoordinates, i, j, i1, j1, k, l, map1, s2, s3);
                return list != null && !list.isEmpty() ? (EntityPlayerMP[])list.toArray(new EntityPlayerMP[0]) : new EntityPlayerMP[0];
            }
        }
        else
        {
            return null;
        }
    }

    public static Map func_96560_a(Map par0Map)
    {
        HashMap hashmap = new HashMap();
        Iterator iterator = par0Map.keySet().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();

            if (s.startsWith("score_") && s.length() > "score_".length())
            {
                String s1 = s.substring("score_".length());
                hashmap.put(s1, Integer.valueOf(MathHelper.parseIntWithDefault((String)par0Map.get(s), 1)));
            }
        }

        return hashmap;
    }

    /**
     * Returns whether the given pattern can match more than one player.
     */
    public static boolean matchesMultiplePlayers(String par0Str)
    {
        Matcher matcher = tokenPattern.matcher(par0Str);

        if (matcher.matches())
        {
            Map map = getArgumentMap(matcher.group(2));
            String s1 = matcher.group(1);
            int i = getDefaultCount(s1);

            if (map.containsKey("c"))
            {
                i = MathHelper.parseIntWithDefault((String)map.get("c"), i);
            }

            return i != 1;
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether the given token (parameter 1) has exactly the given arguments (parameter 2).
     */
    public static boolean hasTheseArguments(String par0Str, String par1Str)
    {
        Matcher matcher = tokenPattern.matcher(par0Str);

        if (matcher.matches())
        {
            String s2 = matcher.group(1);
            return par1Str == null || par1Str.equals(s2);
        }
        else
        {
            return false;
        }
    }

    /**
     * Returns whether the given token has any arguments set.
     */
    public static boolean hasArguments(String par0Str)
    {
        return hasTheseArguments(par0Str, (String)null);
    }

    /**
     * Gets the default minimum range (argument rm).
     */
    private static final int getDefaultMinimumRange(String par0Str)
    {
        return 0;
    }

    /**
     * Gets the default maximum range (argument r).
     */
    private static final int getDefaultMaximumRange(String par0Str)
    {
        return 0;
    }

    /**
     * Gets the default maximum experience level (argument l)
     */
    private static final int getDefaultMaximumLevel(String par0Str)
    {
        return Integer.MAX_VALUE;
    }

    /**
     * Gets the default minimum experience level (argument lm)
     */
    private static final int getDefaultMinimumLevel(String par0Str)
    {
        return 0;
    }

    /**
     * Gets the default number of players to return (argument c, 0 for infinite)
     */
    private static final int getDefaultCount(String par0Str)
    {
        return par0Str.equals("a") ? 0 : 1;
    }

    /**
     * Parses the given argument string, turning it into a HashMap&lt;String, String&gt; of name-&gt;value.
     */
    private static Map getArgumentMap(String par0Str)
    {
        HashMap hashmap = new HashMap();

        if (par0Str == null)
        {
            return hashmap;
        }
        else
        {
            Matcher matcher = intListPattern.matcher(par0Str);
            int i = 0;
            int j;

            for (j = -1; matcher.find(); j = matcher.end())
            {
                String s1 = null;

                switch (i++)
                {
                    case 0:
                        s1 = "x";
                        break;
                    case 1:
                        s1 = "y";
                        break;
                    case 2:
                        s1 = "z";
                        break;
                    case 3:
                        s1 = "r";
                }

                if (s1 != null && matcher.group(1).length() > 0)
                {
                    hashmap.put(s1, matcher.group(1));
                }
            }

            if (j < par0Str.length())
            {
                matcher = keyValueListPattern.matcher(j == -1 ? par0Str : par0Str.substring(j));

                while (matcher.find())
                {
                    hashmap.put(matcher.group(1), matcher.group(2));
                }
            }

            return hashmap;
        }
    }
}
