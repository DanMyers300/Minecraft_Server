package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import java.util.Optional;
import net.minecraft.advancements.Criterion;
import net.minecraft.advancements.CriterionTriggers;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.world.item.ItemStack;

public class CriterionTriggerFilledBucket extends CriterionTriggerAbstract<CriterionTriggerFilledBucket.a> {

    public CriterionTriggerFilledBucket() {}

    @Override
    public CriterionTriggerFilledBucket.a createInstance(JsonObject jsonobject, Optional<ContextAwarePredicate> optional, LootDeserializationContext lootdeserializationcontext) {
        Optional<CriterionConditionItem> optional1 = CriterionConditionItem.fromJson(jsonobject.get("item"));

        return new CriterionTriggerFilledBucket.a(optional, optional1);
    }

    public void trigger(EntityPlayer entityplayer, ItemStack itemstack) {
        this.trigger(entityplayer, (criteriontriggerfilledbucket_a) -> {
            return criteriontriggerfilledbucket_a.matches(itemstack);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        private final Optional<CriterionConditionItem> item;

        public a(Optional<ContextAwarePredicate> optional, Optional<CriterionConditionItem> optional1) {
            super(optional);
            this.item = optional1;
        }

        public static Criterion<CriterionTriggerFilledBucket.a> filledBucket(CriterionConditionItem.a criterionconditionitem_a) {
            return CriterionTriggers.FILLED_BUCKET.createCriterion(new CriterionTriggerFilledBucket.a(Optional.empty(), Optional.of(criterionconditionitem_a.build())));
        }

        public boolean matches(ItemStack itemstack) {
            return !this.item.isPresent() || ((CriterionConditionItem) this.item.get()).matches(itemstack);
        }

        @Override
        public JsonObject serializeToJson() {
            JsonObject jsonobject = super.serializeToJson();

            this.item.ifPresent((criterionconditionitem) -> {
                jsonobject.add("item", criterionconditionitem.serializeToJson());
            });
            return jsonobject;
        }
    }
}
