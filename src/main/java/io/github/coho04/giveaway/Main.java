package io.github.coho04.giveaway;

import io.github.coho04.giveaway.discord.Events;
import io.github.coho04.giveaway.discord.commands.Giveaway;
import io.github.coho04.giveaway.discord.commands.Settings;
import io.github.coho04.dcbcore.DCBotBuilder;

import java.sql.SQLException;

public class Main {

    private static MysqlConnection mysqlConnection;
    private static CustomConfig customConfig;

    public static void main(String[] args) throws SQLException {
        customConfig = new CustomConfig();
        DCBotBuilder dcBotBuilder = new DCBotBuilder(args, true);
        dcBotBuilder.registerEvents(new Events());
        dcBotBuilder.registerCommands(new Giveaway(), new Settings());
        dcBotBuilder.build();
        mysqlConnection = new MysqlConnection(customConfig.getMysqlHostname(), customConfig.getMysqlPort(), customConfig.getMysqlUsername(), customConfig.getMysqlPassword());
        System.out.println("Java application started successfully");
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public static CustomConfig getCustomConfig() {
        return customConfig;
    }
}
