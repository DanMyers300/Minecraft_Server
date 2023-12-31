package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.server.level.WorldServer;
import net.minecraft.tags.TagKey;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityInsentient;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTableInfo;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameterSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParameters;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionEntityProperty;
import net.minecraft.world.phys.Vec3D;
import net.minecraft.world.scores.ScoreboardTeamBase;

public class CriterionConditionEntity {

    public static final CriterionConditionEntity ANY = new CriterionConditionEntity(CriterionConditionEntityType.ANY, CriterionConditionDistance.ANY, CriterionConditionLocation.ANY, CriterionConditionLocation.ANY, CriterionConditionMobEffect.ANY, CriterionConditionNBT.ANY, CriterionConditionEntityFlags.ANY, CriterionConditionEntityEquipment.ANY, EntitySubPredicate.ANY, (String) null);
    private final CriterionConditionEntityType entityType;
    private final CriterionConditionDistance distanceToPlayer;
    private final CriterionConditionLocation location;
    private final CriterionConditionLocation steppingOnLocation;
    private final CriterionConditionMobEffect effects;
    private final CriterionConditionNBT nbt;
    private final CriterionConditionEntityFlags flags;
    private final CriterionConditionEntityEquipment equipment;
    private final EntitySubPredicate subPredicate;
    private final CriterionConditionEntity vehicle;
    private final CriterionConditionEntity passenger;
    private final CriterionConditionEntity targetedEntity;
    @Nullable
    private final String team;

    private CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt, CriterionConditionEntityFlags criterionconditionentityflags, CriterionConditionEntityEquipment criterionconditionentityequipment, EntitySubPredicate entitysubpredicate, @Nullable String s) {
        this.entityType = criterionconditionentitytype;
        this.distanceToPlayer = criterionconditiondistance;
        this.location = criterionconditionlocation;
        this.steppingOnLocation = criterionconditionlocation1;
        this.effects = criterionconditionmobeffect;
        this.nbt = criterionconditionnbt;
        this.flags = criterionconditionentityflags;
        this.equipment = criterionconditionentityequipment;
        this.subPredicate = entitysubpredicate;
        this.passenger = this;
        this.vehicle = this;
        this.targetedEntity = this;
        this.team = s;
    }

    CriterionConditionEntity(CriterionConditionEntityType criterionconditionentitytype, CriterionConditionDistance criterionconditiondistance, CriterionConditionLocation criterionconditionlocation, CriterionConditionLocation criterionconditionlocation1, CriterionConditionMobEffect criterionconditionmobeffect, CriterionConditionNBT criterionconditionnbt, CriterionConditionEntityFlags criterionconditionentityflags, CriterionConditionEntityEquipment criterionconditionentityequipment, EntitySubPredicate entitysubpredicate, CriterionConditionEntity criterionconditionentity, CriterionConditionEntity criterionconditionentity1, CriterionConditionEntity criterionconditionentity2, @Nullable String s) {
        this.entityType = criterionconditionentitytype;
        this.distanceToPlayer = criterionconditiondistance;
        this.location = criterionconditionlocation;
        this.steppingOnLocation = criterionconditionlocation1;
        this.effects = criterionconditionmobeffect;
        this.nbt = criterionconditionnbt;
        this.flags = criterionconditionentityflags;
        this.equipment = criterionconditionentityequipment;
        this.subPredicate = entitysubpredicate;
        this.vehicle = criterionconditionentity;
        this.passenger = criterionconditionentity1;
        this.targetedEntity = criterionconditionentity2;
        this.team = s;
    }

    public static ContextAwarePredicate fromJson(JsonObject jsonobject, String s, LootDeserializationContext lootdeserializationcontext) {
        JsonElement jsonelement = jsonobject.get(s);

        return fromElement(s, lootdeserializationcontext, jsonelement);
    }

    public static ContextAwarePredicate[] fromJsonArray(JsonObject jsonobject, String s, LootDeserializationContext lootdeserializationcontext) {
        JsonElement jsonelement = jsonobject.get(s);

        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonArray jsonarray = ChatDeserializer.convertToJsonArray(jsonelement, s);
            ContextAwarePredicate[] acontextawarepredicate = new ContextAwarePredicate[jsonarray.size()];

            for (int i = 0; i < jsonarray.size(); ++i) {
                acontextawarepredicate[i] = fromElement(s + "[" + i + "]", lootdeserializationcontext, jsonarray.get(i));
            }

            return acontextawarepredicate;
        } else {
            return new ContextAwarePredicate[0];
        }
    }

    private static ContextAwarePredicate fromElement(String s, LootDeserializationContext lootdeserializationcontext, @Nullable JsonElement jsonelement) {
        ContextAwarePredicate contextawarepredicate = ContextAwarePredicate.fromElement(s, lootdeserializationcontext, jsonelement, LootContextParameterSets.ADVANCEMENT_ENTITY);

        if (contextawarepredicate != null) {
            return contextawarepredicate;
        } else {
            CriterionConditionEntity criterionconditionentity = fromJson(jsonelement);

            return wrap(criterionconditionentity);
        }
    }

    public static ContextAwarePredicate wrap(CriterionConditionEntity criterionconditionentity) {
        if (criterionconditionentity == CriterionConditionEntity.ANY) {
            return ContextAwarePredicate.ANY;
        } else {
            LootItemCondition lootitemcondition = LootItemConditionEntityProperty.hasProperties(LootTableInfo.EntityTarget.THIS, criterionconditionentity).build();

            return new ContextAwarePredicate(new LootItemCondition[]{lootitemcondition});
        }
    }

    public boolean matches(EntityPlayer entityplayer, @Nullable Entity entity) {
        return this.matches(entityplayer.serverLevel(), entityplayer.position(), entity);
    }

    public boolean matches(WorldServer worldserver, @Nullable Vec3D vec3d, @Nullable Entity entity) {
        if (this == CriterionConditionEntity.ANY) {
            return true;
        } else if (entity == null) {
            return false;
        } else if (!this.entityType.matches(entity.getType())) {
            return false;
        } else {
            if (vec3d == null) {
                if (this.distanceToPlayer != CriterionConditionDistance.ANY) {
                    return false;
                }
            } else if (!this.distanceToPlayer.matches(vec3d.x, vec3d.y, vec3d.z, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            }

            if (!this.location.matches(worldserver, entity.getX(), entity.getY(), entity.getZ())) {
                return false;
            } else {
                if (this.steppingOnLocation != CriterionConditionLocation.ANY) {
                    Vec3D vec3d1 = Vec3D.atCenterOf(entity.getOnPos());

                    if (!this.steppingOnLocation.matches(worldserver, vec3d1.x(), vec3d1.y(), vec3d1.z())) {
                        return false;
                    }
                }

                if (!this.effects.matches(entity)) {
                    return false;
                } else if (!this.nbt.matches(entity)) {
                    return false;
                } else if (!this.flags.matches(entity)) {
                    return false;
                } else if (!this.equipment.matches(entity)) {
                    return false;
                } else if (!this.subPredicate.matches(entity, worldserver, vec3d)) {
                    return false;
                } else if (!this.vehicle.matches(worldserver, vec3d, entity.getVehicle())) {
                    return false;
                } else if (this.passenger != CriterionConditionEntity.ANY && entity.getPassengers().stream().noneMatch((entity1) -> {
                    return this.passenger.matches(worldserver, vec3d, entity1);
                })) {
                    return false;
                } else if (!this.targetedEntity.matches(worldserver, vec3d, entity instanceof EntityInsentient ? ((EntityInsentient) entity).getTarget() : null)) {
                    return false;
                } else {
                    if (this.team != null) {
                        ScoreboardTeamBase scoreboardteambase = entity.getTeam();

                        if (scoreboardteambase == null || !this.team.equals(scoreboardteambase.getName())) {
                            return false;
                        }
                    }

                    return true;
                }
            }
        }
    }

    public static CriterionConditionEntity fromJson(@Nullable JsonElement jsonelement) {
        if (jsonelement != null && !jsonelement.isJsonNull()) {
            JsonObject jsonobject = ChatDeserializer.convertToJsonObject(jsonelement, "entity");
            CriterionConditionEntityType criterionconditionentitytype = CriterionConditionEntityType.fromJson(jsonobject.get("type"));
            CriterionConditionDistance criterionconditiondistance = CriterionConditionDistance.fromJson(jsonobject.get("distance"));
            CriterionConditionLocation criterionconditionlocation = CriterionConditionLocation.fromJson(jsonobject.get("location"));
            CriterionConditionLocation criterionconditionlocation1 = CriterionConditionLocation.fromJson(jsonobject.get("stepping_on"));
            CriterionConditionMobEffect criterionconditionmobeffect = CriterionConditionMobEffect.fromJson(jsonobject.get("effects"));
            CriterionConditionNBT criterionconditionnbt = CriterionConditionNBT.fromJson(jsonobject.get("nbt"));
            CriterionConditionEntityFlags criterionconditionentityflags = CriterionConditionEntityFlags.fromJson(jsonobject.get("flags"));
            CriterionConditionEntityEquipment criterionconditionentityequipment = CriterionConditionEntityEquipment.fromJson(jsonobject.get("equipment"));
            EntitySubPredicate entitysubpredicate = EntitySubPredicate.fromJson(jsonobject.get("type_specific"));
            CriterionConditionEntity criterionconditionentity = fromJson(jsonobject.get("vehicle"));
            CriterionConditionEntity criterionconditionentity1 = fromJson(jsonobject.get("passenger"));
            CriterionConditionEntity criterionconditionentity2 = fromJson(jsonobject.get("targeted_entity"));
            String s = ChatDeserializer.getAsString(jsonobject, "team", (String) null);

            return (new CriterionConditionEntity.a()).entityType(criterionconditionentitytype).distance(criterionconditiondistance).located(criterionconditionlocation).steppingOn(criterionconditionlocation1).effects(criterionconditionmobeffect).nbt(criterionconditionnbt).flags(criterionconditionentityflags).equipment(criterionconditionentityequipment).subPredicate(entitysubpredicate).team(s).vehicle(criterionconditionentity).passenger(criterionconditionentity1).targetedEntity(criterionconditionentity2).build();
        } else {
            return CriterionConditionEntity.ANY;
        }
    }

    public JsonElement serializeToJson() {
        if (this == CriterionConditionEntity.ANY) {
            return JsonNull.INSTANCE;
        } else {
            JsonObject jsonobject = new JsonObject();

            jsonobject.add("type", this.entityType.serializeToJson());
            jsonobject.add("distance", this.distanceToPlayer.serializeToJson());
            jsonobject.add("location", this.location.serializeToJson());
            jsonobject.add("stepping_on", this.steppingOnLocation.serializeToJson());
            jsonobject.add("effects", this.effects.serializeToJson());
            jsonobject.add("nbt", this.nbt.serializeToJson());
            jsonobject.add("flags", this.flags.serializeToJson());
            jsonobject.add("equipment", this.equipment.serializeToJson());
            jsonobject.add("type_specific", this.subPredicate.serialize());
            jsonobject.add("vehicle", this.vehicle.serializeToJson());
            jsonobject.add("passenger", this.passenger.serializeToJson());
            jsonobject.add("targeted_entity", this.targetedEntity.serializeToJson());
            jsonobject.addProperty("team", this.team);
            return jsonobject;
        }
    }

    public static LootTableInfo createContext(EntityPlayer entityplayer, Entity entity) {
        LootParams lootparams = (new LootParams.a(entityplayer.serverLevel())).withParameter(LootContextParameters.THIS_ENTITY, entity).withParameter(LootContextParameters.ORIGIN, entityplayer.position()).create(LootContextParameterSets.ADVANCEMENT_ENTITY);

        return (new LootTableInfo.Builder(lootparams)).create((MinecraftKey) null);
    }

    public static class a {

        private CriterionConditionEntityType entityType;
        private CriterionConditionDistance distanceToPlayer;
        private CriterionConditionLocation location;
        private CriterionConditionLocation steppingOnLocation;
        private CriterionConditionMobEffect effects;
        private CriterionConditionNBT nbt;
        private CriterionConditionEntityFlags flags;
        private CriterionConditionEntityEquipment equipment;
        private EntitySubPredicate subPredicate;
        private CriterionConditionEntity vehicle;
        private CriterionConditionEntity passenger;
        private CriterionConditionEntity targetedEntity;
        @Nullable
        private String team;

        public a() {
            this.entityType = CriterionConditionEntityType.ANY;
            this.distanceToPlayer = CriterionConditionDistance.ANY;
            this.location = CriterionConditionLocation.ANY;
            this.steppingOnLocation = CriterionConditionLocation.ANY;
            this.effects = CriterionConditionMobEffect.ANY;
            this.nbt = CriterionConditionNBT.ANY;
            this.flags = CriterionConditionEntityFlags.ANY;
            this.equipment = CriterionConditionEntityEquipment.ANY;
            this.subPredicate = EntitySubPredicate.ANY;
            this.vehicle = CriterionConditionEntity.ANY;
            this.passenger = CriterionConditionEntity.ANY;
            this.targetedEntity = CriterionConditionEntity.ANY;
        }

        public static CriterionConditionEntity.a entity() {
            return new CriterionConditionEntity.a();
        }

        public CriterionConditionEntity.a of(EntityTypes<?> entitytypes) {
            this.entityType = CriterionConditionEntityType.of(entitytypes);
            return this;
        }

        public CriterionConditionEntity.a of(TagKey<EntityTypes<?>> tagkey) {
            this.entityType = CriterionConditionEntityType.of(tagkey);
            return this;
        }

        public CriterionConditionEntity.a entityType(CriterionConditionEntityType criterionconditionentitytype) {
            this.entityType = criterionconditionentitytype;
            return this;
        }

        public CriterionConditionEntity.a distance(CriterionConditionDistance criterionconditiondistance) {
            this.distanceToPlayer = criterionconditiondistance;
            return this;
        }

        public CriterionConditionEntity.a located(CriterionConditionLocation criterionconditionlocation) {
            this.location = criterionconditionlocation;
            return this;
        }

        public CriterionConditionEntity.a steppingOn(CriterionConditionLocation criterionconditionlocation) {
            this.steppingOnLocation = criterionconditionlocation;
            return this;
        }

        public CriterionConditionEntity.a effects(CriterionConditionMobEffect criterionconditionmobeffect) {
            this.effects = criterionconditionmobeffect;
            return this;
        }

        public CriterionConditionEntity.a nbt(CriterionConditionNBT criterionconditionnbt) {
            this.nbt = criterionconditionnbt;
            return this;
        }

        public CriterionConditionEntity.a flags(CriterionConditionEntityFlags criterionconditionentityflags) {
            this.flags = criterionconditionentityflags;
            return this;
        }

        public CriterionConditionEntity.a equipment(CriterionConditionEntityEquipment criterionconditionentityequipment) {
            this.equipment = criterionconditionentityequipment;
            return this;
        }

        public CriterionConditionEntity.a subPredicate(EntitySubPredicate entitysubpredicate) {
            this.subPredicate = entitysubpredicate;
            return this;
        }

        public CriterionConditionEntity.a vehicle(CriterionConditionEntity criterionconditionentity) {
            this.vehicle = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a passenger(CriterionConditionEntity criterionconditionentity) {
            this.passenger = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a targetedEntity(CriterionConditionEntity criterionconditionentity) {
            this.targetedEntity = criterionconditionentity;
            return this;
        }

        public CriterionConditionEntity.a team(@Nullable String s) {
            this.team = s;
            return this;
        }

        public CriterionConditionEntity build() {
            return new CriterionConditionEntity(this.entityType, this.distanceToPlayer, this.location, this.steppingOnLocation, this.effects, this.nbt, this.flags, this.equipment, this.subPredicate, this.vehicle, this.passenger, this.targetedEntity, this.team);
        }
    }
}
