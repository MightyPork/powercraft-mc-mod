package net.minecraft.scoreboard;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Scoreboard
{
    /** Map of objective names to ScoreObjective objects. */
    private final Map scoreObjectives = new HashMap();
    private final Map field_96543_b = new HashMap();
    private final Map field_96544_c = new HashMap();
    private final ScoreObjective[] field_96541_d = new ScoreObjective[3];
    private final Map field_96542_e = new HashMap();

    /** Map of usernames to ScorePlayerTeam objects. */
    private final Map teamMemberships = new HashMap();

    /**
     * Returns a ScoreObjective for the objective name
     */
    public ScoreObjective getObjective(String par1Str)
    {
        return (ScoreObjective)this.scoreObjectives.get(par1Str);
    }

    public ScoreObjective func_96535_a(String par1Str, ScoreObjectiveCriteria par2ScoreObjectiveCriteria)
    {
        ScoreObjective scoreobjective = this.getObjective(par1Str);

        if (scoreobjective != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreobjective = new ScoreObjective(this, par1Str, par2ScoreObjectiveCriteria);
            Object object = (List)this.field_96543_b.get(par2ScoreObjectiveCriteria);

            if (object == null)
            {
                object = new ArrayList();
                this.field_96543_b.put(par2ScoreObjectiveCriteria, object);
            }

            ((List)object).add(scoreobjective);
            this.scoreObjectives.put(par1Str, scoreobjective);
            this.func_96522_a(scoreobjective);
            return scoreobjective;
        }
    }

    public Collection func_96520_a(ScoreObjectiveCriteria par1ScoreObjectiveCriteria)
    {
        Collection collection = (Collection)this.field_96543_b.get(par1ScoreObjectiveCriteria);
        return collection == null ? new ArrayList() : new ArrayList(collection);
    }

    public Score func_96529_a(String par1Str, ScoreObjective par2ScoreObjective)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
            this.field_96544_c.put(par1Str, object);
        }

        Score score = (Score)((Map)object).get(par2ScoreObjective);

        if (score == null)
        {
            score = new Score(this, par2ScoreObjective, par1Str);
            ((Map)object).put(par2ScoreObjective, score);
        }

        return score;
    }

    public Collection func_96534_i(ScoreObjective par1ScoreObjective)
    {
        ArrayList arraylist = new ArrayList();
        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            Score score = (Score)map.get(par1ScoreObjective);

            if (score != null)
            {
                arraylist.add(score);
            }
        }

        Collections.sort(arraylist, Score.field_96658_a);
        return arraylist;
    }

    public Collection getScoreObjectives()
    {
        return this.scoreObjectives.values();
    }

    public Collection getObjectiveNames()
    {
        return this.field_96544_c.keySet();
    }

    public void func_96515_c(String par1Str)
    {
        Map map = (Map)this.field_96544_c.remove(par1Str);

        if (map != null)
        {
            this.func_96516_a(par1Str);
        }
    }

    public Collection func_96528_e()
    {
        Collection collection = this.field_96544_c.values();
        ArrayList arraylist = new ArrayList();

        if (collection != null)
        {
            Iterator iterator = collection.iterator();

            while (iterator.hasNext())
            {
                Map map = (Map)iterator.next();
                arraylist.addAll(map.values());
            }
        }

        return arraylist;
    }

    public Map func_96510_d(String par1Str)
    {
        Object object = (Map)this.field_96544_c.get(par1Str);

        if (object == null)
        {
            object = new HashMap();
        }

        return (Map)object;
    }

    public void func_96519_k(ScoreObjective par1ScoreObjective)
    {
        this.scoreObjectives.remove(par1ScoreObjective.getName());

        for (int i = 0; i < 3; ++i)
        {
            if (this.func_96539_a(i) == par1ScoreObjective)
            {
                this.func_96530_a(i, (ScoreObjective)null);
            }
        }

        List list = (List)this.field_96543_b.get(par1ScoreObjective.getCriteria());

        if (list != null)
        {
            list.remove(par1ScoreObjective);
        }

        Iterator iterator = this.field_96544_c.values().iterator();

        while (iterator.hasNext())
        {
            Map map = (Map)iterator.next();
            map.remove(par1ScoreObjective);
        }

        this.func_96533_c(par1ScoreObjective);
    }

    public void func_96530_a(int par1, ScoreObjective par2ScoreObjective)
    {
        this.field_96541_d[par1] = par2ScoreObjective;
    }

    public ScoreObjective func_96539_a(int par1)
    {
        return this.field_96541_d[par1];
    }

    public ScorePlayerTeam func_96508_e(String par1Str)
    {
        return (ScorePlayerTeam)this.field_96542_e.get(par1Str);
    }

    public ScorePlayerTeam func_96527_f(String par1Str)
    {
        ScorePlayerTeam scoreplayerteam = this.func_96508_e(par1Str);

        if (scoreplayerteam != null)
        {
            throw new IllegalArgumentException("An objective with the name \'" + par1Str + "\' already exists!");
        }
        else
        {
            scoreplayerteam = new ScorePlayerTeam(this, par1Str);
            this.field_96542_e.put(par1Str, scoreplayerteam);
            this.func_96523_a(scoreplayerteam);
            return scoreplayerteam;
        }
    }

    public void func_96511_d(ScorePlayerTeam par1ScorePlayerTeam)
    {
        this.field_96542_e.remove(par1ScorePlayerTeam.func_96661_b());
        Iterator iterator = par1ScorePlayerTeam.getMembershipCollection().iterator();

        while (iterator.hasNext())
        {
            String s = (String)iterator.next();
            this.teamMemberships.remove(s);
        }

        this.func_96513_c(par1ScorePlayerTeam);
    }

    public void func_96521_a(String par1Str, ScorePlayerTeam par2ScorePlayerTeam)
    {
        if (this.getPlayersTeam(par1Str) != null)
        {
            this.func_96524_g(par1Str);
        }

        this.teamMemberships.put(par1Str, par2ScorePlayerTeam);
        par2ScorePlayerTeam.getMembershipCollection().add(par1Str);
    }

    public boolean func_96524_g(String par1Str)
    {
        ScorePlayerTeam scoreplayerteam = this.getPlayersTeam(par1Str);

        if (scoreplayerteam != null)
        {
            this.removePlayerFromTeam(par1Str, scoreplayerteam);
            return true;
        }
        else
        {
            return false;
        }
    }

    /**
     * Removes the given username from the given ScorePlayerTeam. If the player is not on the team then an
     * IllegalStateException is thrown.
     */
    public void removePlayerFromTeam(String par1Str, ScorePlayerTeam par2ScorePlayerTeam)
    {
        if (this.getPlayersTeam(par1Str) != par2ScorePlayerTeam)
        {
            throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team \'" + par2ScorePlayerTeam.func_96661_b() + "\'.");
        }
        else
        {
            this.teamMemberships.remove(par1Str);
            par2ScorePlayerTeam.getMembershipCollection().remove(par1Str);
        }
    }

    public Collection func_96531_f()
    {
        return this.field_96542_e.keySet();
    }

    public Collection func_96525_g()
    {
        return this.field_96542_e.values();
    }

    /**
     * Gets the ScorePlayerTeam object for the given username.
     */
    public ScorePlayerTeam getPlayersTeam(String par1Str)
    {
        return (ScorePlayerTeam)this.teamMemberships.get(par1Str);
    }

    public void func_96522_a(ScoreObjective par1ScoreObjective) {}

    public void func_96532_b(ScoreObjective par1ScoreObjective) {}

    public void func_96533_c(ScoreObjective par1ScoreObjective) {}

    public void func_96536_a(Score par1Score) {}

    public void func_96516_a(String par1Str) {}

    public void func_96523_a(ScorePlayerTeam par1ScorePlayerTeam) {}

    public void func_96538_b(ScorePlayerTeam par1ScorePlayerTeam) {}

    public void func_96513_c(ScorePlayerTeam par1ScorePlayerTeam) {}

    /**
     * Returns 'list' for 0, 'sidebar' for 1, 'belowName for 2, otherwise null.
     */
    public static String getObjectiveDisplaySlot(int par0)
    {
        switch (par0)
        {
            case 0:
                return "list";
            case 1:
                return "sidebar";
            case 2:
                return "belowName";
            default:
                return null;
        }
    }

    /**
     * Returns 0 for (case-insensitive) 'list', 1 for 'sidebar', 2 for 'belowName', otherwise -1.
     */
    public static int getObjectiveDisplaySlotNumber(String par0Str)
    {
        return par0Str.equalsIgnoreCase("list") ? 0 : (par0Str.equalsIgnoreCase("sidebar") ? 1 : (par0Str.equalsIgnoreCase("belowName") ? 2 : -1));
    }
}
