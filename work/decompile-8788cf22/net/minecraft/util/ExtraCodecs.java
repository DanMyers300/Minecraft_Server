package net.minecraft.util;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Codec.ResultFunction;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.floats.FloatArrayList;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Base64;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Stream;
import net.minecraft.SystemUtils;
import net.minecraft.core.HolderSet;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.resources.MinecraftKey;
import org.apache.commons.lang3.mutable.MutableObject;
import org.joml.AxisAngle4f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class ExtraCodecs {

    public static final Codec<JsonElement> JSON = Codec.PASSTHROUGH.xmap((dynamic) -> {
        return (JsonElement) dynamic.convert(JsonOps.INSTANCE).getValue();
    }, (jsonelement) -> {
        return new Dynamic(JsonOps.INSTANCE, jsonelement);
    });
    public static final Codec<IChatBaseComponent> COMPONENT = ExtraCodecs.JSON.flatXmap((jsonelement) -> {
        try {
            return DataResult.success(IChatBaseComponent.ChatSerializer.fromJson(jsonelement));
        } catch (JsonParseException jsonparseexception) {
            Objects.requireNonNull(jsonparseexception);
            return DataResult.error(jsonparseexception::getMessage);
        }
    }, (ichatbasecomponent) -> {
        try {
            return DataResult.success(IChatBaseComponent.ChatSerializer.toJsonTree(ichatbasecomponent));
        } catch (IllegalArgumentException illegalargumentexception) {
            Objects.requireNonNull(illegalargumentexception);
            return DataResult.error(illegalargumentexception::getMessage);
        }
    });
    public static final Codec<IChatBaseComponent> FLAT_COMPONENT = Codec.STRING.flatXmap((s) -> {
        try {
            return DataResult.success(IChatBaseComponent.ChatSerializer.fromJson(s));
        } catch (JsonParseException jsonparseexception) {
            Objects.requireNonNull(jsonparseexception);
            return DataResult.error(jsonparseexception::getMessage);
        }
    }, (ichatbasecomponent) -> {
        try {
            return DataResult.success(IChatBaseComponent.ChatSerializer.toJson(ichatbasecomponent));
        } catch (IllegalArgumentException illegalargumentexception) {
            Objects.requireNonNull(illegalargumentexception);
            return DataResult.error(illegalargumentexception::getMessage);
        }
    });
    public static final Codec<Vector3f> VECTOR3F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        return SystemUtils.fixedSize(list, 3).map((list1) -> {
            return new Vector3f((Float) list1.get(0), (Float) list1.get(1), (Float) list1.get(2));
        });
    }, (vector3f) -> {
        return List.of(vector3f.x(), vector3f.y(), vector3f.z());
    });
    public static final Codec<Quaternionf> QUATERNIONF_COMPONENTS = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        return SystemUtils.fixedSize(list, 4).map((list1) -> {
            return new Quaternionf((Float) list1.get(0), (Float) list1.get(1), (Float) list1.get(2), (Float) list1.get(3));
        });
    }, (quaternionf) -> {
        return List.of(quaternionf.x, quaternionf.y, quaternionf.z, quaternionf.w);
    });
    public static final Codec<AxisAngle4f> AXISANGLE4F = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.FLOAT.fieldOf("angle").forGetter((axisangle4f) -> {
            return axisangle4f.angle;
        }), ExtraCodecs.VECTOR3F.fieldOf("axis").forGetter((axisangle4f) -> {
            return new Vector3f(axisangle4f.x, axisangle4f.y, axisangle4f.z);
        })).apply(instance, AxisAngle4f::new);
    });
    public static final Codec<Quaternionf> QUATERNIONF = Codec.either(ExtraCodecs.QUATERNIONF_COMPONENTS, ExtraCodecs.AXISANGLE4F.xmap(Quaternionf::new, AxisAngle4f::new)).xmap((either) -> {
        return (Quaternionf) either.map((quaternionf) -> {
            return quaternionf;
        }, (quaternionf) -> {
            return quaternionf;
        });
    }, Either::left);
    public static Codec<Matrix4f> MATRIX4F = Codec.FLOAT.listOf().comapFlatMap((list) -> {
        return SystemUtils.fixedSize(list, 16).map((list1) -> {
            Matrix4f matrix4f = new Matrix4f();

            for (int i = 0; i < list1.size(); ++i) {
                matrix4f.setRowColumn(i >> 2, i & 3, (Float) list1.get(i));
            }

            return matrix4f.determineProperties();
        });
    }, (matrix4f) -> {
        FloatArrayList floatarraylist = new FloatArrayList(16);

        for (int i = 0; i < 16; ++i) {
            floatarraylist.add(matrix4f.getRowColumn(i >> 2, i & 3));
        }

        return floatarraylist;
    });
    public static final Codec<Integer> NON_NEGATIVE_INT = intRangeWithMessage(0, Integer.MAX_VALUE, (integer) -> {
        return "Value must be non-negative: " + integer;
    });
    public static final Codec<Integer> POSITIVE_INT = intRangeWithMessage(1, Integer.MAX_VALUE, (integer) -> {
        return "Value must be positive: " + integer;
    });
    public static final Codec<Float> POSITIVE_FLOAT = floatRangeMinExclusiveWithMessage(0.0F, Float.MAX_VALUE, (ofloat) -> {
        return "Value must be positive: " + ofloat;
    });
    public static final Codec<Pattern> PATTERN = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(Pattern.compile(s));
        } catch (PatternSyntaxException patternsyntaxexception) {
            return DataResult.error(() -> {
                return "Invalid regex pattern '" + s + "': " + patternsyntaxexception.getMessage();
            });
        }
    }, Pattern::pattern);
    public static final Codec<Instant> INSTANT_ISO8601 = instantCodec(DateTimeFormatter.ISO_INSTANT);
    public static final Codec<byte[]> BASE64_STRING = Codec.STRING.comapFlatMap((s) -> {
        try {
            return DataResult.success(Base64.getDecoder().decode(s));
        } catch (IllegalArgumentException illegalargumentexception) {
            return DataResult.error(() -> {
                return "Malformed base64 string";
            });
        }
    }, (abyte) -> {
        return Base64.getEncoder().encodeToString(abyte);
    });
    public static final Codec<ExtraCodecs.d> TAG_OR_ELEMENT_ID = Codec.STRING.comapFlatMap((s) -> {
        return s.startsWith("#") ? MinecraftKey.read(s.substring(1)).map((minecraftkey) -> {
            return new ExtraCodecs.d(minecraftkey, true);
        }) : MinecraftKey.read(s).map((minecraftkey) -> {
            return new ExtraCodecs.d(minecraftkey, false);
        });
    }, ExtraCodecs.d::decoratedId);
    public static final Function<Optional<Long>, OptionalLong> toOptionalLong = (optional) -> {
        return (OptionalLong) optional.map(OptionalLong::of).orElseGet(OptionalLong::empty);
    };
    public static final Function<OptionalLong, Optional<Long>> fromOptionalLong = (optionallong) -> {
        return optionallong.isPresent() ? Optional.of(optionallong.getAsLong()) : Optional.empty();
    };
    public static final Codec<BitSet> BIT_SET = Codec.LONG_STREAM.xmap((longstream) -> {
        return BitSet.valueOf(longstream.toArray());
    }, (bitset) -> {
        return Arrays.stream(bitset.toLongArray());
    });
    private static final Codec<Property> PROPERTY = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.STRING.fieldOf("name").forGetter(Property::getName), Codec.STRING.fieldOf("value").forGetter(Property::getValue), Codec.STRING.optionalFieldOf("signature").forGetter((property) -> {
            return Optional.ofNullable(property.getSignature());
        })).apply(instance, (s, s1, optional) -> {
            return new Property(s, s1, (String) optional.orElse((Object) null));
        });
    });
    @VisibleForTesting
    public static final Codec<PropertyMap> PROPERTY_MAP = Codec.either(Codec.unboundedMap(Codec.STRING, Codec.STRING.listOf()), ExtraCodecs.PROPERTY.listOf()).xmap((either) -> {
        PropertyMap propertymap = new PropertyMap();

        either.ifLeft((map) -> {
            map.forEach((s, list) -> {
                Iterator iterator = list.iterator();

                while (iterator.hasNext()) {
                    String s1 = (String) iterator.next();

                    propertymap.put(s, new Property(s, s1));
                }

            });
        }).ifRight((list) -> {
            Iterator iterator = list.iterator();

            while (iterator.hasNext()) {
                Property property = (Property) iterator.next();

                propertymap.put(property.getName(), property);
            }

        });
        return propertymap;
    }, (propertymap) -> {
        return Either.right(propertymap.values().stream().toList());
    });
    public static final Codec<GameProfile> GAME_PROFILE = RecordCodecBuilder.create((instance) -> {
        return instance.group(Codec.mapPair(UUIDUtil.AUTHLIB_CODEC.xmap(Optional::of, (optional) -> {
            return (UUID) optional.orElse((Object) null);
        }).optionalFieldOf("id", Optional.empty()), Codec.STRING.xmap(Optional::of, (optional) -> {
            return (String) optional.orElse((Object) null);
        }).optionalFieldOf("name", Optional.empty())).flatXmap(ExtraCodecs::mapIdNameToGameProfile, ExtraCodecs::mapGameProfileToIdName).forGetter(Function.identity()), ExtraCodecs.PROPERTY_MAP.optionalFieldOf("properties", new PropertyMap()).forGetter(GameProfile::getProperties)).apply(instance, (gameprofile, propertymap) -> {
            propertymap.forEach((s, property) -> {
                gameprofile.getProperties().put(s, property);
            });
            return gameprofile;
        });
    });
    public static final Codec<String> NON_EMPTY_STRING = validate((Codec) Codec.STRING, (s) -> {
        return s.isEmpty() ? DataResult.error(() -> {
            return "Expected non-empty string";
        }) : DataResult.success(s);
    });
    public static final Codec<Integer> CODEPOINT = Codec.STRING.comapFlatMap((s) -> {
        int[] aint = s.codePoints().toArray();

        return aint.length != 1 ? DataResult.error(() -> {
            return "Expected one codepoint, got: " + s;
        }) : DataResult.success(aint[0]);
    }, Character::toString);

    public ExtraCodecs() {}

    public static <F, S> Codec<Either<F, S>> xor(Codec<F> codec, Codec<S> codec1) {
        return new ExtraCodecs.e<>(codec, codec1);
    }

    public static <P, I> Codec<I> intervalCodec(Codec<P> codec, String s, String s1, BiFunction<P, P, DataResult<I>> bifunction, Function<I, P> function, Function<I, P> function1) {
        Codec<I> codec1 = Codec.list(codec).comapFlatMap((list) -> {
            return SystemUtils.fixedSize(list, 2).flatMap((list1) -> {
                P p0 = list1.get(0);
                P p1 = list1.get(1);

                return (DataResult) bifunction.apply(p0, p1);
            });
        }, (object) -> {
            return ImmutableList.of(function.apply(object), function1.apply(object));
        });
        Codec<I> codec2 = RecordCodecBuilder.create((instance) -> {
            return instance.group(codec.fieldOf(s).forGetter(Pair::getFirst), codec.fieldOf(s1).forGetter(Pair::getSecond)).apply(instance, Pair::of);
        }).comapFlatMap((pair) -> {
            return (DataResult) bifunction.apply(pair.getFirst(), pair.getSecond());
        }, (object) -> {
            return Pair.of(function.apply(object), function1.apply(object));
        });
        Codec<I> codec3 = (new ExtraCodecs.b<>(codec1, codec2)).xmap((either) -> {
            return either.map((object) -> {
                return object;
            }, (object) -> {
                return object;
            });
        }, Either::left);

        return Codec.either(codec, codec3).comapFlatMap((either) -> {
            return (DataResult) either.map((object) -> {
                return (DataResult) bifunction.apply(object, object);
            }, DataResult::success);
        }, (object) -> {
            P p0 = function.apply(object);
            P p1 = function1.apply(object);

            return Objects.equals(p0, p1) ? Either.left(p0) : Either.right(object);
        });
    }

    public static <A> ResultFunction<A> orElsePartial(final A a0) {
        return new ResultFunction<A>() {
            public <T> DataResult<Pair<A, T>> apply(DynamicOps<T> dynamicops, T t0, DataResult<Pair<A, T>> dataresult) {
                MutableObject<String> mutableobject = new MutableObject();

                Objects.requireNonNull(mutableobject);
                Optional<Pair<A, T>> optional = dataresult.resultOrPartial(mutableobject::setValue);

                return optional.isPresent() ? dataresult : DataResult.error(() -> {
                    return "(" + (String) mutableobject.getValue() + " -> using default)";
                }, Pair.of(a0, t0));
            }

            public <T> DataResult<T> coApply(DynamicOps<T> dynamicops, A a1, DataResult<T> dataresult) {
                return dataresult;
            }

            public String toString() {
                return "OrElsePartial[" + a0 + "]";
            }
        };
    }

    public static <E> Codec<E> idResolverCodec(ToIntFunction<E> tointfunction, IntFunction<E> intfunction, int i) {
        return Codec.INT.flatXmap((integer) -> {
            return (DataResult) Optional.ofNullable(intfunction.apply(integer)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    return "Unknown element id: " + integer;
                });
            });
        }, (object) -> {
            int j = tointfunction.applyAsInt(object);

            return j == i ? DataResult.error(() -> {
                return "Element with unknown id: " + object;
            }) : DataResult.success(j);
        });
    }

    public static <E> Codec<E> stringResolverCodec(Function<E, String> function, Function<String, E> function1) {
        return Codec.STRING.flatXmap((s) -> {
            return (DataResult) Optional.ofNullable(function1.apply(s)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    return "Unknown element name:" + s;
                });
            });
        }, (object) -> {
            return (DataResult) Optional.ofNullable((String) function.apply(object)).map(DataResult::success).orElseGet(() -> {
                return DataResult.error(() -> {
                    return "Element with unknown name: " + object;
                });
            });
        });
    }

    public static <E> Codec<E> orCompressed(final Codec<E> codec, final Codec<E> codec1) {
        return new Codec<E>() {
            public <T> DataResult<T> encode(E e0, DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? codec1.encode(e0, dynamicops, t0) : codec.encode(e0, dynamicops, t0);
            }

            public <T> DataResult<Pair<E, T>> decode(DynamicOps<T> dynamicops, T t0) {
                return dynamicops.compressMaps() ? codec1.decode(dynamicops, t0) : codec.decode(dynamicops, t0);
            }

            public String toString() {
                return codec + " orCompressed " + codec1;
            }
        };
    }

    public static <E> Codec<E> overrideLifecycle(Codec<E> codec, final Function<E, Lifecycle> function, final Function<E, Lifecycle> function1) {
        return codec.mapResult(new ResultFunction<E>() {
            public <T> DataResult<Pair<E, T>> apply(DynamicOps<T> dynamicops, T t0, DataResult<Pair<E, T>> dataresult) {
                return (DataResult) dataresult.result().map((pair) -> {
                    return dataresult.setLifecycle((Lifecycle) function.apply(pair.getFirst()));
                }).orElse(dataresult);
            }

            public <T> DataResult<T> coApply(DynamicOps<T> dynamicops, E e0, DataResult<T> dataresult) {
                return dataresult.setLifecycle((Lifecycle) function1.apply(e0));
            }

            public String toString() {
                return "WithLifecycle[" + function + " " + function1 + "]";
            }
        });
    }

    public static <T> Codec<T> validate(Codec<T> codec, Function<T, DataResult<T>> function) {
        return codec.flatXmap(function, function);
    }

    public static <T> MapCodec<T> validate(MapCodec<T> mapcodec, Function<T, DataResult<T>> function) {
        return mapcodec.flatXmap(function, function);
    }

    private static Codec<Integer> intRangeWithMessage(int i, int j, Function<Integer, String> function) {
        return validate((Codec) Codec.INT, (integer) -> {
            return integer.compareTo(i) >= 0 && integer.compareTo(j) <= 0 ? DataResult.success(integer) : DataResult.error(() -> {
                return (String) function.apply(integer);
            });
        });
    }

    public static Codec<Integer> intRange(int i, int j) {
        return intRangeWithMessage(i, j, (integer) -> {
            return "Value must be within range [" + i + ";" + j + "]: " + integer;
        });
    }

    private static Codec<Float> floatRangeMinExclusiveWithMessage(float f, float f1, Function<Float, String> function) {
        return validate((Codec) Codec.FLOAT, (ofloat) -> {
            return ofloat.compareTo(f) > 0 && ofloat.compareTo(f1) <= 0 ? DataResult.success(ofloat) : DataResult.error(() -> {
                return (String) function.apply(ofloat);
            });
        });
    }

    public static <T> Codec<List<T>> nonEmptyList(Codec<List<T>> codec) {
        return validate(codec, (list) -> {
            return list.isEmpty() ? DataResult.error(() -> {
                return "List must have contents";
            }) : DataResult.success(list);
        });
    }

    public static <T> Codec<HolderSet<T>> nonEmptyHolderSet(Codec<HolderSet<T>> codec) {
        return validate(codec, (holderset) -> {
            return holderset.unwrap().right().filter(List::isEmpty).isPresent() ? DataResult.error(() -> {
                return "List must have contents";
            }) : DataResult.success(holderset);
        });
    }

    public static <A> Codec<A> lazyInitializedCodec(Supplier<Codec<A>> supplier) {
        return new ExtraCodecs.c<>(supplier);
    }

    public static <E> MapCodec<E> retrieveContext(final Function<DynamicOps<?>, DataResult<E>> function) {
        class a extends MapCodec<E> {

            a() {}

            public <T> RecordBuilder<T> encode(E e0, DynamicOps<T> dynamicops, RecordBuilder<T> recordbuilder) {
                return recordbuilder;
            }

            public <T> DataResult<E> decode(DynamicOps<T> dynamicops, MapLike<T> maplike) {
                return (DataResult) function.apply(dynamicops);
            }

            public String toString() {
                return "ContextRetrievalCodec[" + function + "]";
            }

            public <T> Stream<T> keys(DynamicOps<T> dynamicops) {
                return Stream.empty();
            }
        }

        return new a();
    }

    public static <E, L extends Collection<E>, T> Function<L, DataResult<L>> ensureHomogenous(Function<E, T> function) {
        return (collection) -> {
            Iterator<E> iterator = collection.iterator();

            if (iterator.hasNext()) {
                Object object = function.apply(iterator.next());

                while (iterator.hasNext()) {
                    E e0 = iterator.next();
                    T t0 = function.apply(e0);

                    if (t0 != object) {
                        return DataResult.error(() -> {
                            return "Mixed type list: element " + e0 + " had type " + t0 + ", but list is of type " + object;
                        });
                    }
                }
            }

            return DataResult.success(collection, Lifecycle.stable());
        };
    }

    public static <A> Codec<A> catchDecoderException(final Codec<A> codec) {
        return Codec.of(codec, new Decoder<A>() {
            public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicops, T t0) {
                try {
                    return codec.decode(dynamicops, t0);
                } catch (Exception exception) {
                    return DataResult.error(() -> {
                        return "Caught exception decoding " + t0 + ": " + exception.getMessage();
                    });
                }
            }
        });
    }

    public static Codec<Instant> instantCodec(DateTimeFormatter datetimeformatter) {
        PrimitiveCodec primitivecodec = Codec.STRING;
        Function function = (s) -> {
            try {
                return DataResult.success(Instant.from(datetimeformatter.parse(s)));
            } catch (Exception exception) {
                Objects.requireNonNull(exception);
                return DataResult.error(exception::getMessage);
            }
        };

        Objects.requireNonNull(datetimeformatter);
        return primitivecodec.comapFlatMap(function, datetimeformatter::format);
    }

    public static MapCodec<OptionalLong> asOptionalLong(MapCodec<Optional<Long>> mapcodec) {
        return mapcodec.xmap(ExtraCodecs.toOptionalLong, ExtraCodecs.fromOptionalLong);
    }

    private static DataResult<GameProfile> mapIdNameToGameProfile(Pair<Optional<UUID>, Optional<String>> pair) {
        try {
            return DataResult.success(new GameProfile((UUID) ((Optional) pair.getFirst()).orElse((Object) null), (String) ((Optional) pair.getSecond()).orElse((Object) null)));
        } catch (Throwable throwable) {
            Objects.requireNonNull(throwable);
            return DataResult.error(throwable::getMessage);
        }
    }

    private static DataResult<Pair<Optional<UUID>, Optional<String>>> mapGameProfileToIdName(GameProfile gameprofile) {
        return DataResult.success(Pair.of(Optional.ofNullable(gameprofile.getId()), Optional.ofNullable(gameprofile.getName())));
    }

    public static Codec<String> sizeLimitedString(int i, int j) {
        return validate((Codec) Codec.STRING, (s) -> {
            int k = s.length();

            return k < i ? DataResult.error(() -> {
                return "String \"" + s + "\" is too short: " + k + ", expected range [" + i + "-" + j + "]";
            }) : (k > j ? DataResult.error(() -> {
                return "String \"" + s + "\" is too long: " + k + ", expected range [" + i + "-" + j + "]";
            }) : DataResult.success(s));
        });
    }

    private static final class e<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public e(Codec<F> codec, Codec<S> codec1) {
            this.first = codec;
            this.second = codec1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicops, T t0) {
            DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::left);
            });
            DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::right);
            });
            Optional<Pair<Either<F, S>, T>> optional = dataresult.result();
            Optional<Pair<Either<F, S>, T>> optional1 = dataresult1.result();

            return optional.isPresent() && optional1.isPresent() ? DataResult.error(() -> {
                Object object = optional.get();

                return "Both alternatives read successfully, can not pick the correct one; first: " + object + " second: " + optional1.get();
            }, (Pair) optional.get()) : (optional.isPresent() ? dataresult : dataresult1);
        }

        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicops, T t0) {
            return (DataResult) either.map((object) -> {
                return this.first.encode(object, dynamicops, t0);
            }, (object) -> {
                return this.second.encode(object, dynamicops, t0);
            });
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.e<?, ?> extracodecs_e = (ExtraCodecs.e) object;

                return Objects.equals(this.first, extracodecs_e.first) && Objects.equals(this.second, extracodecs_e.second);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.first, this.second});
        }

        public String toString() {
            return "XorCodec[" + this.first + ", " + this.second + "]";
        }
    }

    private static final class b<F, S> implements Codec<Either<F, S>> {

        private final Codec<F> first;
        private final Codec<S> second;

        public b(Codec<F> codec, Codec<S> codec1) {
            this.first = codec;
            this.second = codec1;
        }

        public <T> DataResult<Pair<Either<F, S>, T>> decode(DynamicOps<T> dynamicops, T t0) {
            DataResult<Pair<Either<F, S>, T>> dataresult = this.first.decode(dynamicops, t0).map((pair) -> {
                return pair.mapFirst(Either::left);
            });

            if (!dataresult.error().isPresent()) {
                return dataresult;
            } else {
                DataResult<Pair<Either<F, S>, T>> dataresult1 = this.second.decode(dynamicops, t0).map((pair) -> {
                    return pair.mapFirst(Either::right);
                });

                return !dataresult1.error().isPresent() ? dataresult1 : dataresult.apply2((pair, pair1) -> {
                    return pair1;
                }, dataresult1);
            }
        }

        public <T> DataResult<T> encode(Either<F, S> either, DynamicOps<T> dynamicops, T t0) {
            return (DataResult) either.map((object) -> {
                return this.first.encode(object, dynamicops, t0);
            }, (object) -> {
                return this.second.encode(object, dynamicops, t0);
            });
        }

        public boolean equals(Object object) {
            if (this == object) {
                return true;
            } else if (object != null && this.getClass() == object.getClass()) {
                ExtraCodecs.b<?, ?> extracodecs_b = (ExtraCodecs.b) object;

                return Objects.equals(this.first, extracodecs_b.first) && Objects.equals(this.second, extracodecs_b.second);
            } else {
                return false;
            }
        }

        public int hashCode() {
            return Objects.hash(new Object[]{this.first, this.second});
        }

        public String toString() {
            return "EitherCodec[" + this.first + ", " + this.second + "]";
        }
    }

    private static record c<A> (Supplier<Codec<A>> delegate) implements Codec<A> {

        c(Supplier<Codec<A>> supplier) {
            Objects.requireNonNull(supplier);
            Supplier<Codec<A>> supplier1 = Suppliers.memoize(supplier::get);

            this.delegate = supplier1;
        }

        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> dynamicops, T t0) {
            return ((Codec) this.delegate.get()).decode(dynamicops, t0);
        }

        public <T> DataResult<T> encode(A a0, DynamicOps<T> dynamicops, T t0) {
            return ((Codec) this.delegate.get()).encode(a0, dynamicops, t0);
        }
    }

    public static record d(MinecraftKey id, boolean tag) {

        public String toString() {
            return this.decoratedId();
        }

        private String decoratedId() {
            return this.tag ? "#" + this.id : this.id.toString();
        }
    }
}
