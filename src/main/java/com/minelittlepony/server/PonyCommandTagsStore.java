package com.minelittlepony.server;

import net.minecraft.entity.Entity;
import net.minecraft.text.*;

import org.jetbrains.annotations.Nullable;

import com.google.common.base.MoreObjects;
import com.google.common.cache.*;
import com.minelittlepony.api.config.PonyDisplayTags;
import com.minelittlepony.api.pony.meta.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.BiPredicate;
import java.util.function.Function;

public class PonyCommandTagsStore {
    private static final BiPredicate<String, String> STARTS_WITH_COMPARISON = String::startsWith;
    private static final BiPredicate<String, String> EQUALS_COMPARISON = String::contentEquals;
    public static final PonyCommandTagsStore INSTANCE = new PonyCommandTagsStore();

    private final LoadingCache<Text, PonyDisplayTags> cache = CacheBuilder.newBuilder()
            .expireAfterAccess(30, TimeUnit.SECONDS)
            .build(CacheLoader.from(text -> {
                return new PonyDisplayTags(
                        hasHiddenValue(text, PonyDisplayTags.ALWAYS_PONIFY),
                        hasHiddenValue(text, PonyDisplayTags.NEVER_PONIFY),
                        readHiddenValue(text, PonyDisplayTags.RACE_OVERRIDE, name -> {
                            Race race = Race.CODEC.byId(name.toUpperCase(Locale.ROOT), Race.HUMAN);
                            return race == Race.HUMAN ? Optional.empty() : Optional.of(race);
                        }, Optional.empty()),
                        readHiddenValue(text, PonyDisplayTags.SIZE_OVERRIDE, name -> {
                            Size size = SizePreset.CODEC.byId(name.toUpperCase(Locale.ROOT), SizePreset.UNSET);
                            return size == SizePreset.UNSET ? Optional.empty() : Optional.of(size);
                        }, Optional.empty()),
                        findHiddenValue(text, PonyDisplayTags.MAGIC_COLOR_OVERRIDE, style -> {
                            @Nullable TextColor textColor = style == null ? null : style.getColor();
                            return textColor == null ? OptionalInt.empty() : OptionalInt.of(textColor.getRgb());
                        }, OptionalInt.empty(), EQUALS_COMPARISON)
                );
            }));

    public PonyDisplayTags getTags(@Nullable Entity entity) {
        if (entity == null || entity.getCustomName() == null) {
            return PonyDisplayTags.EMPTY;
        }
        return MoreObjects.firstNonNull(cache.getUnchecked(entity.getCustomName()), PonyDisplayTags.EMPTY);
    }

    public static boolean hasHiddenValue(@Nullable Text text, String flag) {
        return findHiddenValue(text, flag, Function.identity(), null, EQUALS_COMPARISON) != null;
    }

    public static <T> T readHiddenValue(Text text, String flag, Function<String, @Nullable T> valueResolver, T fallback) {
        return findHiddenValue(text, flag + "/", style -> {
            return valueResolver.apply(((StyleSpriteSource.Font)style.getFont()).id().toString().split(flag + "/")[1]);
        }, fallback, STARTS_WITH_COMPARISON);
    }

    private static <T> T findHiddenValue(@Nullable Text text, String flag, Function<Style, T> valueGetter, T fallback, BiPredicate<String, String> comparison) {
        if (text == null) {
            return fallback;
        }
        Style s = text.getStyle();
        if (s.getFont() instanceof StyleSpriteSource.Font font
                && comparison.test(font.id().toString().toLowerCase(Locale.ROOT), flag)) {
            return valueGetter.apply(s);
        }
        @Nullable T value;
        for (Text sibling : text.getSiblings()) {
            value = findHiddenValue(sibling, flag, valueGetter, fallback, comparison);
            if (value != null) {
                return value;
            }
        }

        return fallback;
    }
}
