package net.minecraft.src;

import net.minecraft.src.BehaviorDefaultDispenseItem;
import net.minecraft.src.BlockDispenser;
import net.minecraft.src.Entity;
import net.minecraft.src.EnumFacing;
import net.minecraft.src.IBlockSource;
import net.minecraft.src.IPosition;
import net.minecraft.src.IProjectile;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public abstract class BehaviorProjectileDispense extends BehaviorDefaultDispenseItem {

   public ItemStack func_82487_b(IBlockSource p_82487_1_, ItemStack p_82487_2_) {
      World var3 = p_82487_1_.func_82618_k();
      IPosition var4 = BlockDispenser.func_82525_a(p_82487_1_);
      EnumFacing var5 = EnumFacing.func_82600_a(p_82487_1_.func_82620_h());
      IProjectile var6 = this.func_82499_a(var3, var4);
      var6.func_70186_c((double)var5.func_82601_c(), 0.10000000149011612D, (double)var5.func_82599_e(), this.func_82500_b(), this.func_82498_a());
      var3.func_72838_d((Entity)var6);
      p_82487_2_.func_77979_a(1);
      return p_82487_2_;
   }

   protected void func_82485_a(IBlockSource p_82485_1_) {
      p_82485_1_.func_82618_k().func_72926_e(1002, p_82485_1_.func_82623_d(), p_82485_1_.func_82622_e(), p_82485_1_.func_82621_f(), 0);
   }

   protected abstract IProjectile func_82499_a(World var1, IPosition var2);

   protected float func_82498_a() {
      return 6.0F;
   }

   protected float func_82500_b() {
      return 1.1F;
   }
}
