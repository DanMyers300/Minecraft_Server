package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.level.World;

public class CriterionTriggerChangedDimension extends CriterionTriggerAbstract<CriterionTriggerChangedDimension.a> {

    static final MinecraftKey ID = new MinecraftKey("changed_dimension");

    public CriterionTriggerChangedDimension() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerChangedDimension.ID;
    }

    @Override
    public CriterionTriggerChangedDimension.a createInstance(JsonObject jsonobject, ContextAwarePredicate contextawarepredicate, LootDeserializationContext lootdeserializationcontext) {
        ResourceKey<World> resourcekey = jsonobject.has("from") ? ResourceKey.create(Registries.DIMENSION, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "from"))) : null;
        ResourceKey<World> resourcekey1 = jsonobject.has("to") ? ResourceKey.create(Registries.DIMENSION, new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "to"))) : null;

        return new CriterionTriggerChangedDimension.a(contextawarepredicate, resourcekey, resourcekey1);
    }

    public void trigger(EntityPlayer entityplayer, ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
        this.trigger(entityplayer, (criteriontriggerchangeddimension_a) -> {
            return criteriontriggerchangeddimension_a.matches(resourcekey, resourcekey1);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final ResourceKey<World> from;
        @Nullable
        private final ResourceKey<World> to;

        public a(ContextAwarePredicate contextawarepredicate, @Nullable ResourceKey<World> resourcekey, @Nullable ResourceKey<World> resourcekey1) {
            super(CriterionTriggerChangedDimension.ID, contextawarepredicate);
            this.from = resourcekey;
            this.to = resourcekey1;
        }

        public static CriterionTriggerChangedDimension.a changedDimension() {
            return new CriterionTriggerChangedDimension.a(ContextAwarePredicate.ANY, (ResourceKey) null, (ResourceKey) null);
        }

        public static CriterionTriggerChangedDimension.a changedDimension(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return new CriterionTriggerChangedDimension.a(ContextAwarePredicate.ANY, resourcekey, resourcekey1);
        }

        public static CriterionTriggerChangedDimension.a changedDimensionTo(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(ContextAwarePredicate.ANY, (ResourceKey) null, resourcekey);
        }

        public static CriterionTriggerChangedDimension.a changedDimensionFrom(ResourceKey<World> resourcekey) {
            return new CriterionTriggerChangedDimension.a(ContextAwarePredicate.ANY, resourcekey, (ResourceKey) null);
        }

        public boolean matches(ResourceKey<World> resourcekey, ResourceKey<World> resourcekey1) {
            return this.from != null && this.from != resourcekey ? false : this.to == null || this.to == resourcekey1;
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.from != null) {
                jsonobject.addProperty("from", this.from.location().toString());
            }

            if (this.to != null) {
                jsonobject.addProperty("to", this.to.location().toString());
            }

            return jsonobject;
        }
    }
}
