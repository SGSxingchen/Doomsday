package org.lanstard.doomsday.server.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.client.gui.text.ScreenTextPreset;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class PresetArgumentType implements ArgumentType<ScreenTextPreset> {
    private static final Collection<String> EXAMPLES = Arrays.stream(ScreenTextPreset.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private static final DynamicCommandExceptionType INVALID_PRESET = new DynamicCommandExceptionType(
            (value) -> Component.literal("未知的预设效果: " + value)
    );

    public static PresetArgumentType preset() {
        return new PresetArgumentType();
    }

    @Override
    public ScreenTextPreset parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return ScreenTextPreset.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_PRESET.create(name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        for (ScreenTextPreset preset : ScreenTextPreset.values()) {
            if (preset.name().toLowerCase().startsWith(builder.getRemaining().toLowerCase())) {
                builder.suggest(preset.name());
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 