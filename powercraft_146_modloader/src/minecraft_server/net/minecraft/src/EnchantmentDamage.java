package net.minecraft.src;

public class EnchantmentDamage extends Enchantment
{
    /** Holds the name to be translated of each protection type. */
    private static final String[] protectionName = new String[] {"all", "undead", "arthropods"};

    /**
     * Holds the base factor of enchantability needed to be able to use the enchant.
     */
    private static final int[] baseEnchantability = new int[] {1, 5, 5};

    /**
     * Holds how much each level increased the enchantability factor to be able to use this enchant.
     */
    private static final int[] levelEnchantability = new int[] {11, 8, 8};

    /**
     * Used on the formula of base enchantability, this is the 'window' factor of values to be able to use thing
     * enchant.
     */
    private static final int[] thresholdEnchantability = new int[] {20, 20, 20};

    /**
     * Defines the type of damage of the enchantment, 0 = all, 1 = undead, 3 = arthropods
     */
    public final int damageType;

    public EnchantmentDamage(int par1, int par2, int par3)
    {
        super(par1, par2, EnumEnchantmentType.weapon);
        this.damageType = par3;
    }

    /**
     * Returns the minimal value of enchantability needed on the enchantment level passed.
     */
    public int getMinEnchantability(int par1)
    {
        return baseEnchantability[this.damageType] + (par1 - 1) * levelEnchantability[this.damageType];
    }

    /**
     * Returns the maximum value of enchantability nedded on the enchantment level passed.
     */
    public int getMaxEnchantability(int par1)
    {
        return this.getMinEnchantability(par1) + thresholdEnchantability[this.damageType];
    }

    /**
     * Returns the maximum level that the enchantment can have.
     */
    public int getMaxLevel()
    {
        return 5;
    }

    /**
     * Calculates de (magic) damage done by the enchantment on a living entity based on level and entity passed.
     */
    public int calcModifierLiving(int par1, EntityLiving par2EntityLiving)
    {
        return this.damageType == 0 ? MathHelper.floor_float((float)par1 * 2.75F) : (this.damageType == 1 && par2EntityLiving.getCreatureAttribute() == EnumCreatureAttribute.UNDEAD ? MathHelper.floor_float((float)par1 * 4.5F) : (this.damageType == 2 && par2EntityLiving.getCreatureAttribute() == EnumCreatureAttribute.ARTHROPOD ? MathHelper.floor_float((float)par1 * 4.5F) : 0));
    }

    public String func_77320_a()
    {
        return "enchantment.damage." + protectionName[this.damageType];
    }

    /**
     * Determines if the enchantment passed can be applyied together with this enchantment.
     */
    public boolean canApplyTogether(Enchantment par1Enchantment)
    {
        return !(par1Enchantment instanceof EnchantmentDamage);
    }

    public boolean func_92037_a(ItemStack par1ItemStack)
    {
        return par1ItemStack.getItem() instanceof ItemAxe ? true : super.func_92037_a(par1ItemStack);
    }
}
