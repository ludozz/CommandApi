package me.ludozz.commandapi;

import me.ludozz.commandapi.paper.PaperHandler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class CommandManagerPlugin extends JavaPlugin {

    private CommandManager commandManager;

    @Override
    public void onLoad() {
        commandManager = new CommandManager(this);
        CommandManager.setInstance(commandManager);
        //todo
        commandManager.registerCommand(this, new TestCommand());
    }

    @Override
    public void onEnable() {
        if (commandManager.isPaper()) {
            Bukkit.getPluginManager().registerEvents(new PaperHandler(), this);
        }
    }

    @Override
    public void onDisable() {
        commandManager.unregister();
        commandManager = null;
    }

}
