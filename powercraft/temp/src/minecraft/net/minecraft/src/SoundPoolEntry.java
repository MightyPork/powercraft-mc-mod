package net.minecraft.src;

import cpw.mods.fml.common.Side;
import cpw.mods.fml.common.asm.SideOnly;
import java.net.URL;

@SideOnly(Side.CLIENT)
public class SoundPoolEntry {

   public String field_77385_a;
   public URL field_77384_b;


   public SoundPoolEntry(String p_i3127_1_, URL p_i3127_2_) {
      this.field_77385_a = p_i3127_1_;
      this.field_77384_b = p_i3127_2_;
   }
}
