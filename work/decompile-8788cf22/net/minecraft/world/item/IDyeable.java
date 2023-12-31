package net.minecraft.world.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;

public interface IDyeable {

    String TAG_COLOR = "color";
    String TAG_DISPLAY = "display";
    int DEFAULT_LEATHER_COLOR = 10511680;

    default boolean hasCustomColor(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTagElement("display");

        return nbttagcompound != null && nbttagcompound.contains("color", 99);
    }

    default int getColor(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTagElement("display");

        return nbttagcompound != null && nbttagcompound.contains("color", 99) ? nbttagcompound.getInt("color") : 10511680;
    }

    default void clearColor(ItemStack itemstack) {
        NBTTagCompound nbttagcompound = itemstack.getTagElement("display");

        if (nbttagcompound != null && nbttagcompound.contains("color")) {
            nbttagcompound.remove("color");
        }

    }

    default void setColor(ItemStack itemstack, int i) {
        itemstack.getOrCreateTagElement("display").putInt("color", i);
    }

    static ItemStack dyeArmor(ItemStack itemstack, List<ItemDye> list) {
        ItemStack itemstack1 = ItemStack.EMPTY;
        int[] aint = new int[3];
        int i = 0;
        int j = 0;
        IDyeable idyeable = null;
        Item item = itemstack.getItem();
        int k;
        float f;
        int l;

        if (item instanceof IDyeable) {
            idyeable = (IDyeable) item;
            itemstack1 = itemstack.copyWithCount(1);
            if (idyeable.hasCustomColor(itemstack)) {
                k = idyeable.getColor(itemstack1);
                float f1 = (float) (k >> 16 & 255) / 255.0F;
                float f2 = (float) (k >> 8 & 255) / 255.0F;

                f = (float) (k & 255) / 255.0F;
                i += (int) (Math.max(f1, Math.max(f2, f)) * 255.0F);
                aint[0] += (int) (f1 * 255.0F);
                aint[1] += (int) (f2 * 255.0F);
                aint[2] += (int) (f * 255.0F);
                ++j;
            }

            for (Iterator iterator = list.iterator(); iterator.hasNext(); ++j) {
                ItemDye itemdye = (ItemDye) iterator.next();
                float[] afloat = itemdye.getDyeColor().getTextureDiffuseColors();
                int i1 = (int) (afloat[0] * 255.0F);
                int j1 = (int) (afloat[1] * 255.0F);

                l = (int) (afloat[2] * 255.0F);
                i += Math.max(i1, Math.max(j1, l));
                aint[0] += i1;
                aint[1] += j1;
                aint[2] += l;
            }
        }

        if (idyeable == null) {
            return ItemStack.EMPTY;
        } else {
            k = aint[0] / j;
            int k1 = aint[1] / j;
            int l1 = aint[2] / j;

            f = (float) i / (float) j;
            float f3 = (float) Math.max(k, Math.max(k1, l1));

            k = (int) ((float) k * f / f3);
            k1 = (int) ((float) k1 * f / f3);
            l1 = (int) ((float) l1 * f / f3);
            l = (k << 8) + k1;
            l = (l << 8) + l1;
            idyeable.setColor(itemstack1, l);
            return itemstack1;
        }
    }
}
