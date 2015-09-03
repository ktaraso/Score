package com.github.games647.scoreboardstats.commands;

import com.github.games647.scoreboardstats.SbManager;
import com.github.games647.scoreboardstats.ScoreboardStats;
import com.github.games647.scoreboardstats.config.Lang;
import com.github.games647.scoreboardstats.scoreboard.Group;
import com.github.games647.scoreboardstats.scoreboard.Item;
import com.github.games647.scoreboardstats.scoreboard.Scoreboard;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ScoreboardCommand extends CommandHandler {

    public ScoreboardCommand(ScoreboardStats plugin) {
        super("scoreboard", "&aCustomize your scoreboard", plugin);
    }

    @Override
    public void onCommand(CommandSender sender, String subCommand, String... args) {
        if (!(sender instanceof Player)) {
            //the console cannot have a scoreboard
            sender.sendMessage(Lang.get("noConsole"));
            return;
        }

        SbManager scoreboardManager = plugin.getScoreboardManager();
        Scoreboard scoreboard = scoreboardManager.getOrCreateScoreboard((Player) sender);
        Group sidebar = scoreboard.getCurrentSidebar();
        if (sidebar == null) {
            sender.sendMessage(ChatColor.DARK_RED + "You don't have a sidebar");
        } else {
            sender.sendMessage("Title: " + sidebar.getDisplayName());
            sender.sendMessage("Items: " + sidebar.getItems().size());

            for (Item item : sidebar.getItems()) {
                sender.sendMessage("");

                sender.sendMessage("ID: " + item.getUniqueId());
                sender.sendMessage("Displayname" + item.getDisplayName());

                sender.sendMessage("Score: " + item.getScore());
                sender.sendMessage("======================");
            }
        }
    }
}
