package io.github.coho04.giveaway;


import io.github.coho04.mysql.MYSQL;
import io.github.coho04.mysql.entities.Database;
import io.github.coho04.mysql.entities.Table;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String tableName = "settings";
    public static String clmGuildID = "guild";
    public static String clmGiveawayChannel = "giveawaychannel";

    public MysqlConnection(String hostname, int port, String username, String password) {
        mysql = new MYSQL(hostname, username, password, port);
        if (!mysql.existsDatabase(Main.getCustomConfig().getMysqlDatabase())) {
            mysql.createDatabase(Main.getCustomConfig().getMysqlDatabase());
        }
        Database db = mysql.getDatabase(Main.getCustomConfig().getMysqlDatabase());
        if (!db.existsTable(tableName)) {
            db.createTable(tableName);
        }
        Table table = db.getTable(tableName);
        if (!table.existsColumn(clmGuildID)) {
            table.addColumn(clmGuildID);
        }
        if (!table.existsColumn(clmGiveawayChannel)) {
            table.addColumn(clmGiveawayChannel);
        }
        System.out.println("MYSQL Finished");
    }

    public MYSQL getMysql() {
        return mysql;
    }
}
