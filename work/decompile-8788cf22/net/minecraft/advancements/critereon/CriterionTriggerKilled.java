package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.loot.LootTableInfo;

public class CriterionTriggerKilled extends CriterionTriggerAbstract<CriterionTriggerKilled.a> {

    final MinecraftKey id;

    public CriterionTriggerKilled(MinecraftKey minecraftkey) {
        this.id = minecraftkey;
    }

    @Override
    public MinecraftKey getId() {
        return this.id;
    }

    @Override
    public CriterionTriggerKilled.a createInstance(JsonObject jsonobject, ContextAwarePredicate contextawarepredicate, LootDeserializationContext lootdeserializationcontext) {
        return new CriterionTriggerKilled.a(this.id, contextawarepredicate, CriterionConditionEntity.fromJson(jsonobject, "entity", lootdeserializationcontext), CriterionConditionDamageSource.fromJson(jsonobject.get("killing_blow")));
    }

    public void trigger(EntityPlayer entityplayer, Entity entity, DamageSource damagesource) {
        LootTableInfo loottableinfo = CriterionConditionEntity.createContext(entityplayer, entity);

        this.trigger(entityplayer, (criteriontriggerkilled_a) -> {
            return criteriontriggerkilled_a.matches(entityplayer, loottableinfo, damagesource);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final ContextAwarePredicate entityPredicate;
        private final CriterionConditionDamageSource killingBlow;

        public a(MinecraftKey minecraftkey, ContextAwarePredicate contextawarepredicate, ContextAwarePredicate contextawarepredicate1, CriterionConditionDamageSource criterionconditiondamagesource) {
            super(minecraftkey, contextawarepredicate);
            this.entityPredicate = contextawarepredicate1;
            this.killingBlow = criterionconditiondamagesource;
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a playerKilledEntity() {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), criterionconditiondamagesource_a.build());
        }

        public static CriterionTriggerKilled.a playerKilledEntity(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.PLAYER_KILLED_ENTITY.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), criterionconditiondamagesource_a.build());
        }

        public static CriterionTriggerKilled.a playerKilledEntityNearSculkCatalyst() {
            return new CriterionTriggerKilled.a(CriterionTriggers.KILL_MOB_NEAR_SCULK_CATALYST.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity criterionconditionentity) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity.a criterionconditionentity_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer() {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, ContextAwarePredicate.ANY, CriterionConditionDamageSource.ANY);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource criterionconditiondamagesource) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), criterionconditiondamagesource);
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity criterionconditionentity, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity), criterionconditiondamagesource_a.build());
        }

        public static CriterionTriggerKilled.a entityKilledPlayer(CriterionConditionEntity.a criterionconditionentity_a, CriterionConditionDamageSource.a criterionconditiondamagesource_a) {
            return new CriterionTriggerKilled.a(CriterionTriggers.ENTITY_KILLED_PLAYER.id, ContextAwarePredicate.ANY, CriterionConditionEntity.wrap(criterionconditionentity_a.build()), criterionconditiondamagesource_a.build());
        }

        public boolean matches(EntityPlayer entityplayer, LootTableInfo loottableinfo, DamageSource damagesource) {
            return !this.killingBlow.matches(entityplayer, damagesource) ? false : this.entityPredicate.matches(loottableinfo);
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            jsonobject.add("entity", this.entityPredicate.toJson(lootserializationcontext));
            jsonobject.add("killing_blow", this.killingBlow.serializeToJson());
            return jsonobject;
        }
    }
}
