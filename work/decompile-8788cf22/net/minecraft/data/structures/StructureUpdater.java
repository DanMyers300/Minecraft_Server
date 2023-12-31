package net.minecraft.data.structures;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.GameProfileSerializer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataConverterRegistry;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.levelgen.structure.templatesystem.DefinedStructure;
import org.slf4j.Logger;

public class StructureUpdater implements SnbtToNbt.a {

    private static final Logger LOGGER = LogUtils.getLogger();

    public StructureUpdater() {}

    @Override
    public NBTTagCompound apply(String s, NBTTagCompound nbttagcompound) {
        return s.startsWith("data/minecraft/structures/") ? update(s, nbttagcompound) : nbttagcompound;
    }

    public static NBTTagCompound update(String s, NBTTagCompound nbttagcompound) {
        DefinedStructure definedstructure = new DefinedStructure();
        int i = GameProfileSerializer.getDataVersion(nbttagcompound, 500);
        boolean flag = true;

        if (i < 3437) {
            StructureUpdater.LOGGER.warn("SNBT Too old, do not forget to update: {} < {}: {}", new Object[]{i, 3437, s});
        }

        NBTTagCompound nbttagcompound1 = DataFixTypes.STRUCTURE.updateToCurrentVersion(DataConverterRegistry.getDataFixer(), nbttagcompound, i);

        definedstructure.load(BuiltInRegistries.BLOCK.asLookup(), nbttagcompound1);
        return definedstructure.save(new NBTTagCompound());
    }
}
