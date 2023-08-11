package de.goldendeveloper.giveaway;

import de.goldendeveloper.dcbcore.DCBotBuilder;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.giveaway.discord.Events;
import de.goldendeveloper.giveaway.discord.commands.Giveaway;
import de.goldendeveloper.giveaway.discord.commands.Settings;

import java.util.LinkedList;

public class Main {

    private static MysqlConnection mysqlConnection;

    public static void main(String[] args) {
        CustomConfig customConfig = new CustomConfig();
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerEvents(new Events());
        dcBotBuilder.registerCommands(new Giveaway(), new Settings());
        dcBotBuilder.build();
        mysqlConnection = new MysqlConnection(customConfig.getMysqlHostname(), customConfig.getMysqlPort(), customConfig.getMysqlUsername(), customConfig.getMysqlPassword());
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }
}
