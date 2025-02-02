package net.minecraft.src;

import net.minecraft.src.Block;
import net.minecraft.src.CreativeTabs;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.ItemStack;
import net.minecraft.src.World;

public class ItemFlintAndSteel extends Item {

   public ItemFlintAndSteel(int p_i3652_1_) {
      super(p_i3652_1_);
      this.field_77777_bU = 1;
      this.func_77656_e(64);
      this.func_77637_a(CreativeTabs.field_78040_i);
   }

   public boolean func_77648_a(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_, int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
      if(p_77648_7_ == 0) {
         --p_77648_5_;
      }

      if(p_77648_7_ == 1) {
         ++p_77648_5_;
      }

      if(p_77648_7_ == 2) {
         --p_77648_6_;
      }

      if(p_77648_7_ == 3) {
         ++p_77648_6_;
      }

      if(p_77648_7_ == 4) {
         --p_77648_4_;
      }

      if(p_77648_7_ == 5) {
         ++p_77648_4_;
      }

      if(!p_77648_2_.func_82247_a(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_)) {
         return false;
      } else {
         int var11 = p_77648_3_.func_72798_a(p_77648_4_, p_77648_5_, p_77648_6_);
         if(var11 == 0) {
            p_77648_3_.func_72908_a((double)p_77648_4_ + 0.5D, (double)p_77648_5_ + 0.5D, (double)p_77648_6_ + 0.5D, "fire.ignite", 1.0F, field_77697_d.nextFloat() * 0.4F + 0.8F);
            p_77648_3_.func_72859_e(p_77648_4_, p_77648_5_, p_77648_6_, Block.field_72067_ar.field_71990_ca);
         }

         p_77648_1_.func_77972_a(1, p_77648_2_);
         return true;
      }
   }
}
