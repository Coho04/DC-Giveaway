package de.goldendeveloper.giveaway;

import de.goldendeveloper.giveaway.discord.Discord;
import net.dv8tion.jda.api.entities.Activity;

import java.io.IOException;
import java.util.Properties;

public class Main {

    private static Discord discord;
    private static MysqlConnection mysqlConnection;
    private static Config config;

    public static Boolean restart = false;
    public static Boolean production = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }
        if (System.getProperty("os.name").split(" ")[0].equalsIgnoreCase("windows")) {
            production = false;
        }

        config = new Config();
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlPort(), config.getMysqlUsername(), config.getMysqlPassword());
        discord = new Discord(config.getDiscordToken());
    }

    public static String getProjektVersion() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("version");
    }

    public static String getProjektName() {
        Properties properties = new Properties();
        try {
            properties.load(Main.class.getClass().getClassLoader().getResourceAsStream("project.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return properties.getProperty("name");
    }

    public static Discord getDiscord() {
        return discord;
    }

    public static MysqlConnection getMysqlConnection() {
        return mysqlConnection;
    }

    public static Config getConfig() {
        return config;
    }
}
