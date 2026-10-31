/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  metro.common.player.ExtendedPlayer
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.IChatComponent
 *  net.minecraft.world.World
 */
package metro.common.blocks;

import metro.common.Main;
import metro.common.player.ExtendedPlayer;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;

public class RegBlock
extends Block {
    public String model_name;
    public String texture_name;
    public float scale;
    public static String texture = "metro:pass";

    public RegBlock(Material p_i45394_1_) {
        super(Material.field_151576_e);
        this.func_149647_a(Main.dekorTab);
        this.func_149722_s();
        this.func_149752_b(6000000.0f);
        this.func_149672_a(field_149777_j);
        this.func_149658_d(texture);
        this.func_149676_a(0.0f, 0.0f, 0.0f, 1.0f, 0.1f, 0.5f);
    }

    public boolean func_149727_a(World w, int p_149727_2_, int p_149727_3_, int p_149727_4_, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
        ExtendedPlayer props = ExtendedPlayer.get((EntityPlayer)player);
        props.setX((float)player.field_70165_t);
        props.setY((float)player.field_70163_u);
        props.setZ((float)player.field_70161_v);
        System.out.println("\u0418\u0433\u0440\u043e\u043a - " + player.func_70005_c_() + " \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u0438\u043b \u0440\u0435\u0441\u043f\u0430\u0432\u043d:" + props.getX() + ", " + props.getY() + ", " + props.getZ());
        player.func_145747_a((IChatComponent)new ChatComponentText("\u0421\u043e\u0445\u0440\u0430\u043d\u0435\u043d\u0438\u0435 \u0442\u043e\u0447\u043a\u0438 \u0432\u043e\u0437\u0440\u043e\u0436\u0434\u0435\u043d\u0438\u044f..."));
        return false;
    }

    public boolean func_149662_c() {
        return false;
    }

    public boolean func_149686_d() {
        return true;
    }
}
