package meteordevelopment.meteorclient.commands.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.commands.arguments.ScriptArgumentType;
import meteordevelopment.meteorclient.systems.scripting.Script;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;

public class ScriptsCommand extends Command {

    public ScriptsCommand() {
        super("scripts", "Executes scripts.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("run")
            .then(argument("script", ScriptArgumentType.create())
                .executes(context -> {
                    Script script = ScriptArgumentType.get(context);

                    if (script != null) {
                        info("Executing " + script.name.get() + ".");
                        script.run();
                    } else {
                        error("Script not found.");
                    }

                    return SINGLE_SUCCESS;
                })
            )
        );

        builder.then(literal("reload")
            .then(argument("script", ScriptArgumentType.create())
                .executes(context -> {
                    Script script = ScriptArgumentType.get(context);

                    if (script != null) {
                        script.reload();
                        info("Script " + script.name.get() + " reloaded successfully.");
                    } else {
                        error("Script not found.");
                    }

                    return SINGLE_SUCCESS;
                })
            )
        );
    }
}
