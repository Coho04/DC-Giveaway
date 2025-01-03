package io.github.coho04.giveaway.discord.commands;

import io.github.coho04.giveaway.Main;
import io.github.coho04.giveaway.MysqlConnection;
import io.github.coho04.dcbcore.DCBot;
import io.github.coho04.dcbcore.interfaces.CommandInterface;
import io.github.coho04.mysql.entities.Table;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class Settings implements CommandInterface {

    private static final String subCmdSetGiveawayChannel = "set-giveaway-channel";
    public static final String subCmdSetGiveawayChannelOptionTextChannel = "text-channel";

    @Override
    public CommandData commandData() {
        return Commands.slash("settings", "Stellt den Discord Bot für deinen Server ein!")
                .addSubcommands(
                        new SubcommandData(subCmdSetGiveawayChannel, "Setzt den TextChannel für die Giveaways")
                                .addOption(OptionType.CHANNEL, subCmdSetGiveawayChannelOptionTextChannel, "Setzt den TextChannel für die Giveaways", true)
                );
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            if (e.getSubcommandName().equalsIgnoreCase(subCmdSetGiveawayChannel)) {
                String textChannelId = e.getOption(subCmdSetGiveawayChannelOptionTextChannel).getAsChannel().asTextChannel().getId();
                Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.getCustomConfig().getMysqlDatabase()).getTable(MysqlConnection.tableName);
                if (table.existsRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())) {
                    table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).set(table.getColumn(MysqlConnection.clmGiveawayChannel), textChannelId);
                    e.reply("Der Giveaway Channel wurde erfolgreich gespeichert!").queue();
                } else {
                    e.reply("ERROR: Bitte lade den Discord Bot neu ein!").queue();
                }
            }
        } else {
            e.reply("Dazu hast du keine Rechte bitte informiere den Inhaber! Benötigtes Recht: ADMINISTRATOR").queue();
        }
    }
}
