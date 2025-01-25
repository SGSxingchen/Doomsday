package org.lanstard.doomsday.server.command.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.network.chat.Component;
import org.lanstard.doomsday.common.echo.EchoPreset;

import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class EchoPresetArgumentType implements ArgumentType<EchoPreset> {
    private static final Collection<String> EXAMPLES = Arrays.stream(EchoPreset.values())
            .map(Enum::name)
            .collect(Collectors.toList());

    private static final DynamicCommandExceptionType INVALID_PRESET = new DynamicCommandExceptionType(
            (value) -> Component.literal("未知的回响预设: " + value)
    );

    public static EchoPresetArgumentType echoPreset() {
        return new EchoPresetArgumentType();
    }

    @Override
    public EchoPreset parse(StringReader reader) throws CommandSyntaxException {
        String name = reader.readUnquotedString();
        try {
            return EchoPreset.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw INVALID_PRESET.create(name);
        }
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        String remaining = builder.getRemaining().toLowerCase();
        for (EchoPreset preset : EchoPreset.values()) {
            String name = preset.name().toLowerCase();
            String displayName = preset.getDisplayName();
            if (name.startsWith(remaining) || displayName.startsWith(remaining)) {
                builder.suggest(name, Component.literal(displayName));
            }
        }
        return builder.buildFuture();
    }

    @Override
    public Collection<String> getExamples() {
        return EXAMPLES;
    }
} 