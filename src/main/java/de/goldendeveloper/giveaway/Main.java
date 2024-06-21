package de.goldendeveloper.giveaway;

import de.goldendeveloper.giveaway.discord.Events;
import de.goldendeveloper.giveaway.discord.commands.Giveaway;
import de.goldendeveloper.giveaway.discord.commands.Settings;
import io.github.coho04.dcbcore.DCBotBuilder;

import java.sql.SQLException;

public class Main {

    private static MysqlConnection mysqlConnection;

    public static void main(String[] args) throws SQLException {
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
