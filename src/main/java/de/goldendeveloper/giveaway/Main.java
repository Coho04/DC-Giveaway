package de.goldendeveloper.giveaway;

import de.goldendeveloper.giveaway.discord.Discord;

public class Main {

    private static Discord discord;
    private static MysqlConnection mysqlConnection;
    private static Config config;

    private static Boolean restart = false;
    private static Boolean deployment = true;

    public static void main(String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("restart")) {
            restart = true;
        }
        if (System.getProperty("os.name").split(" ")[0].equalsIgnoreCase("windows")) {
            deployment = false;
        }

        config = new Config();
        mysqlConnection = new MysqlConnection(config.getMysqlHostname(), config.getMysqlPort(), config.getMysqlUsername(), config.getMysqlPassword());
        discord = new Discord(config.getDiscordToken());
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

    public static Boolean getRestart() {
        return restart;
    }

    public static Boolean getDeployment() {
        return deployment;
    }
}
