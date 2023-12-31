package net.minecraft.advancements.critereon;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import javax.annotation.Nullable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.MinecraftKey;
import net.minecraft.server.level.EntityPlayer;
import net.minecraft.util.ChatDeserializer;
import net.minecraft.world.item.alchemy.PotionRegistry;

public class CriterionTriggerBrewedPotion extends CriterionTriggerAbstract<CriterionTriggerBrewedPotion.a> {

    static final MinecraftKey ID = new MinecraftKey("brewed_potion");

    public CriterionTriggerBrewedPotion() {}

    @Override
    public MinecraftKey getId() {
        return CriterionTriggerBrewedPotion.ID;
    }

    @Override
    public CriterionTriggerBrewedPotion.a createInstance(JsonObject jsonobject, ContextAwarePredicate contextawarepredicate, LootDeserializationContext lootdeserializationcontext) {
        PotionRegistry potionregistry = null;

        if (jsonobject.has("potion")) {
            MinecraftKey minecraftkey = new MinecraftKey(ChatDeserializer.getAsString(jsonobject, "potion"));

            potionregistry = (PotionRegistry) BuiltInRegistries.POTION.getOptional(minecraftkey).orElseThrow(() -> {
                return new JsonSyntaxException("Unknown potion '" + minecraftkey + "'");
            });
        }

        return new CriterionTriggerBrewedPotion.a(contextawarepredicate, potionregistry);
    }

    public void trigger(EntityPlayer entityplayer, PotionRegistry potionregistry) {
        this.trigger(entityplayer, (criteriontriggerbrewedpotion_a) -> {
            return criteriontriggerbrewedpotion_a.matches(potionregistry);
        });
    }

    public static class a extends CriterionInstanceAbstract {

        @Nullable
        private final PotionRegistry potion;

        public a(ContextAwarePredicate contextawarepredicate, @Nullable PotionRegistry potionregistry) {
            super(CriterionTriggerBrewedPotion.ID, contextawarepredicate);
            this.potion = potionregistry;
        }

        public static CriterionTriggerBrewedPotion.a brewedPotion() {
            return new CriterionTriggerBrewedPotion.a(ContextAwarePredicate.ANY, (PotionRegistry) null);
        }

        public boolean matches(PotionRegistry potionregistry) {
            return this.potion == null || this.potion == potionregistry;
        }

        @Override
        public JsonObject serializeToJson(LootSerializationContext lootserializationcontext) {
            JsonObject jsonobject = super.serializeToJson(lootserializationcontext);

            if (this.potion != null) {
                jsonobject.addProperty("potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
            }

            return jsonobject;
        }
    }
}
