package meteordevelopment.meteorclient.commands.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import meteordevelopment.meteorclient.systems.scripting.Script;
import meteordevelopment.meteorclient.systems.scripting.Scripts;
import net.minecraft.text.Text;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.command.CommandSource.suggestMatching;

public class ScriptArgumentType implements ArgumentType<String> {
    private static final ScriptArgumentType INSTANCE = new ScriptArgumentType();
    private static final DynamicCommandExceptionType NO_SUCH_SCRIPT = new DynamicCommandExceptionType(name -> Text.literal("Script with name '" + name + "' doesn't exist."));

    public static ScriptArgumentType create() {
        return INSTANCE;
    }

    public static Script get(CommandContext<?> context) {
        String name = context.getArgument("script", String.class);
        Script script = Scripts.get().get(name);

        if (script == null) throw new IllegalArgumentException("Script with name '" + name + "' doesn't exist.");
        return script;
    }

    @Override
    public String parse(StringReader reader) throws CommandSyntaxException {
        String argument = reader.readUnquotedString(); // Changed to read a single word as script name.
        if (Scripts.get().get(argument) == null) throw NO_SUCH_SCRIPT.create(argument);

        return argument;
    }

    @Override
    public <S> CompletableFuture<Suggestions> listSuggestions(CommandContext<S> context, SuggestionsBuilder builder) {
        return suggestMatching(Scripts.get().getAll().stream().map(script -> script.name.get()), builder);
    }
}
