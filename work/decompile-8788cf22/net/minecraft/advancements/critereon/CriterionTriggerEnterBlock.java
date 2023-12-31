package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;

public class CriterionTriggerEnterBlock extends CriterionTriggerAbstract<CriterionTriggerEnterBlock.a> {

    static final MinecraftKey ID = new MinecraftKey("enter_block");

    public CriterionTriggerEnterBlock() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerEnterBlock.ID;
    }

    @Override
    public CriterionTriggerEnterBlock.a createInstance(JsonObject jsonobject, ContextAwarePredicate contextawarepredicate, LootDeserializationContext lootdeserializationcontext) {
        Block block = deserializeBlock(jsonobject);
        CriterionTriggerProperties criteriontriggerproperties = CriterionTriggerProperties.fromJson(jsonobject.get("state"));

        if (block != null) {
            criteriontriggerproperties.checkState(block.getStateDefinition(), (s) -> {
                throw new JsonSyntaxException("Block " + block + " has no property " + s);
            });
        }

        return new CriterionTriggerEnterBlock.a(contextawarepredicate, block, criteriontriggerproperties);
    }

    @Nullable
    private static Block deserializeBlock(JsonObject jsonobject) {
        if (jsonobject.has("block")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "block"));

            return (Block) BuiltInRegistries.BLOCK.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown block type '" + minecraftkey + "'");
            });
        } else {
            return null;
        }
    }

    public void trigger(EntityPlayer entityplayer, IBlockData iblockdata) {
        this.trigger(entityplayer, (criteriontriggerenterblock_a) -> {
            return criteriontriggerenterblock_a.matches(iblockdata);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final Block block;
        private final CriterionTriggerProperties state;

        public a(ContextAwarePredicate contextawarepredicate, @Nullable Block block, CriterionTriggerProperties criteriontriggerproperties) {
            super(CriterionTriggerEnterBlock.ID, contextawarepredicate);
            this.block = block;
            this.state = criteriontriggerproperties;
        }

        public static CriterionTriggerEnterBlock.a entersBlock(Block block) {
            return new CriterionTriggerEnterBlock.a(ContextAwarePredicate.ANY, block, CriterionTriggerProperties.ANY);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.block != null) {
                jsonobject.addProperty("block", BuiltInRegistries.BLOCK.getKey(this.block).toString());
            }

            jsonobject.add("state", this.state.serializeToJson());
            return jsonobject;
        }

        public boolean matches(IBlockData iblockdata) {
            return this.block != null && !iblockdata.is(this.block) ? false : this.state.matches(iblockdata);
        }
    }
}
