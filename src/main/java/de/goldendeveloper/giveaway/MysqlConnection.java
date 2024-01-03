package de.goldendeveloper.giveaway;

import de.goldendeveloper.mysql.MYSQL;
import de.goldendeveloper.mysql.entities.Database;
import de.goldendeveloper.mysql.entities.Table;
import de.goldendeveloper.mysql.exceptions.NoConnectionException;

import java.sql.SQLException;

public class MysqlConnection {

    private final MYSQL mysql;
    public static String dbName = "GD-Giveaway";
    public static String tableName = "settings";
    public static String clmGuildID = "guild";
    public static String clmGiveawayChannel = "giveawaychannel";

    public MysqlConnection(String hostname, int port, String username, String password) throws NoConnectionException, SQLException {
        mysql = new MYSQL(hostname, username, password, port);
        if (!mysql.existsDatabase(dbName)) {
            mysql.createDatabase(dbName);
        }
        Database db = mysql.getDatabase(dbName);
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
