package gate.addon.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import gate.addon.hud.AvailableGates;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.command.CommandSource;
import org.jetbrains.annotations.NotNull;

import java.net.http.HttpResponse;
import java.util.Iterator;

/**
 * The Meteor Client command API uses the <a href="https://github.com/Mojang/brigadier">same command system as Minecraft does</a>.
 */
public class Gate extends Command {
    /**
     * The {@code name} parameter should be in kebab-case.
     */
    public Gate() {
        super("sb", "Teleports to a diffrent gate.");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(literal("gate").then(argument("gateName", StringArgumentType.word()).executes(context -> {
            String argument = StringArgumentType.getString(context, "gateName");

            String str_url = null;
            String token = null;

            for (@NotNull Iterator<HudElement> it = Hud.get().iterator(); it.hasNext(); ) {
                HudElement he = it.next();

                if (he instanceof AvailableGates ag) {
                    str_url = ag.instance.get();
                    token = ag.token.get();
                    break;
                }
            }

            if (str_url == null || token == null) {
                info("Initialize HUD element first (with valid settings)!");
                return SINGLE_SUCCESS;
            }

            String body = "{\"label\": \"" + argument + "\"}";

            Http.Request req = Http.post("http://" + str_url + "/sb");
            req.bearer(token);
            req.bodyJson(body);

            // Send the request and get the response, passing the type for deserialization

            try {
                HttpResponse<Void> response = req.sendResponse();
                if (response.statusCode() == 200) {
                    info("AOK! You can now use /clan base");
                    return SINGLE_SUCCESS;
                } else {
                    info("Invalid request! Is the gate name correct? (Status Code: " + String.valueOf(response.statusCode()) + ")");
                    return SINGLE_SUCCESS;
                }
            } catch (Exception e) {
                info("Error accessing the server!");
                return SINGLE_SUCCESS;
            }
        })));
    }
}
