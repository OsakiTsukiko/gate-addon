package gate.addon;

import gate.addon.commands.Gate;
import gate.addon.hud.AvailableGates;
import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.GithubRepo;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.commands.Commands;
import meteordevelopment.meteorclient.systems.hud.HudGroup;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import org.slf4j.Logger;

public class GateAddon extends MeteorAddon {
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("Gate Addon");
    public static final HudGroup HUD_GROUP = new HudGroup("Gate Addon");

    @Override
    public void onInitialize() {
        LOG.info("Initializing Gate Addon.");

        // Modules
        // Modules.get().add(new GateSettings());

        // Commands
        Commands.add(new Gate());

        // HUD
        Hud.get().register(AvailableGates.INFO);
    }

    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "gate.addon";
    }

    @Override
    public GithubRepo getRepo() {
        return new GithubRepo("OsakiTsukiko", "gate-addon");
    }
}
