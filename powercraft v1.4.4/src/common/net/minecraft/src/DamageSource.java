package net.minecraft.src;

public class DamageSource
{
    public static DamageSource inFire = (new DamageSource("inFire")).setFireDamage();
    public static DamageSource onFire = (new DamageSource("onFire")).setDamageBypassesArmor().setFireDamage();
    public static DamageSource lava = (new DamageSource("lava")).setFireDamage();
    public static DamageSource inWall = (new DamageSource("inWall")).setDamageBypassesArmor();
    public static DamageSource drown = (new DamageSource("drown")).setDamageBypassesArmor();
    public static DamageSource starve = (new DamageSource("starve")).setDamageBypassesArmor();
    public static DamageSource cactus = new DamageSource("cactus");
    public static DamageSource fall = (new DamageSource("fall")).setDamageBypassesArmor();
    public static DamageSource outOfWorld = (new DamageSource("outOfWorld")).setDamageBypassesArmor().setDamageAllowedInCreativeMode();
    public static DamageSource generic = (new DamageSource("generic")).setDamageBypassesArmor();
    public static DamageSource explosion = (new DamageSource("explosion")).func_76351_m();
    public static DamageSource field_76375_l = new DamageSource("explosion");
    public static DamageSource magic = (new DamageSource("magic")).setDamageBypassesArmor().setMagicDamage();
    public static DamageSource wither = (new DamageSource("wither")).setDamageBypassesArmor();
    public static DamageSource anvil = new DamageSource("anvil");
    public static DamageSource fallingBlock = new DamageSource("fallingBlock");

    private boolean isUnblockable = false;
    private boolean isDamageAllowedInCreativeMode = false;
    private float hungerDamage = 0.3F;

    private boolean fireDamage;

    private boolean projectile;
    private boolean field_76381_t;
    private boolean magicDamage = false;
    public String damageType;

    public static DamageSource causeMobDamage(EntityLiving par0EntityLiving)
    {
        return new EntityDamageSource("mob", par0EntityLiving);
    }

    public static DamageSource causePlayerDamage(EntityPlayer par0EntityPlayer)
    {
        return new EntityDamageSource("player", par0EntityPlayer);
    }

    public static DamageSource causeArrowDamage(EntityArrow par0EntityArrow, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("arrow", par0EntityArrow, par1Entity)).setProjectile();
    }

    public static DamageSource causeFireballDamage(EntityFireball par0EntityFireball, Entity par1Entity)
    {
        return par1Entity == null ? (new EntityDamageSourceIndirect("onFire", par0EntityFireball, par0EntityFireball)).setFireDamage().setProjectile() : (new EntityDamageSourceIndirect("fireball", par0EntityFireball, par1Entity)).setFireDamage().setProjectile();
    }

    public static DamageSource causeThrownDamage(Entity par0Entity, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("thrown", par0Entity, par1Entity)).setProjectile();
    }

    public static DamageSource causeIndirectMagicDamage(Entity par0Entity, Entity par1Entity)
    {
        return (new EntityDamageSourceIndirect("indirectMagic", par0Entity, par1Entity)).setDamageBypassesArmor().setMagicDamage();
    }

    public boolean isProjectile()
    {
        return this.projectile;
    }

    public DamageSource setProjectile()
    {
        this.projectile = true;
        return this;
    }

    public boolean isUnblockable()
    {
        return this.isUnblockable;
    }

    public float getHungerDamage()
    {
        return this.hungerDamage;
    }

    public boolean canHarmInCreative()
    {
        return this.isDamageAllowedInCreativeMode;
    }

    protected DamageSource(String par1Str)
    {
        this.damageType = par1Str;
    }

    public Entity getSourceOfDamage()
    {
        return this.getEntity();
    }

    public Entity getEntity()
    {
        return null;
    }

    protected DamageSource setDamageBypassesArmor()
    {
        this.isUnblockable = true;
        this.hungerDamage = 0.0F;
        return this;
    }

    protected DamageSource setDamageAllowedInCreativeMode()
    {
        this.isDamageAllowedInCreativeMode = true;
        return this;
    }

    protected DamageSource setFireDamage()
    {
        this.fireDamage = true;
        return this;
    }

    public String getDeathMessage(EntityPlayer par1EntityPlayer)
    {
        return StatCollector.translateToLocalFormatted("death." + this.damageType, new Object[] {par1EntityPlayer.username});
    }

    public boolean isFireDamage()
    {
        return this.fireDamage;
    }

    public String getDamageType()
    {
        return this.damageType;
    }

    public DamageSource func_76351_m()
    {
        this.field_76381_t = true;
        return this;
    }

    public boolean func_76350_n()
    {
        return this.field_76381_t;
    }

    public boolean isMagicDamage()
    {
        return this.magicDamage;
    }

    public DamageSource setMagicDamage()
    {
        this.magicDamage = true;
        return this;
    }
}
