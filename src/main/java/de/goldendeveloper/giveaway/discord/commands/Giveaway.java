package de.goldendeveloper.giveaway.discord.commands;

import de.goldendeveloper.dcbcore.DCBot;
import de.goldendeveloper.dcbcore.interfaces.CommandInterface;
import de.goldendeveloper.giveaway.Main;
import de.goldendeveloper.giveaway.MysqlConnection;
import io.github.coho04.mysql.entities.Database;
import io.github.coho04.mysql.entities.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;

import java.awt.Color;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;

public class Giveaway implements CommandInterface {

    public static String subCmdCreate = "create";
    public static String subCmdFinish = "finish";
    public static String subCmdFinishOptionID = "giveaway-id";

    public static final String modalGiveawayCreate = "giveaway-create";
    public static final String modalGiveawayCreateOptionMessage = "message";

    @Override
    public CommandData commandData() {
        return Commands.slash("giveaway", "Veranstalte ein Giveaway auf deinem Discord Server!")
                .addSubcommands(
                        new SubcommandData(subCmdCreate, "Erstellt ein neues Giveaway"),
                        new SubcommandData(subCmdFinish, "Beendet ein vorhandenes Giveaway").addOption(OptionType.STRING, subCmdFinishOptionID, "Giveaway ID", true)
                );
    }

    @Override
    public void runSlashCommand(SlashCommandInteractionEvent e, DCBot dcBot) {
        if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
            String subCmd = e.getSubcommandName();
            if (subCmd != null) {
                if (subCmd.equalsIgnoreCase(subCmdCreate)) {
                    createGiveaway(e);
                } else if (subCmd.equalsIgnoreCase(subCmdFinish)) {
                    finishGiveaway(e);
                }
            }
        } else {
            e.reply("Dazu hast du keine Rechte bitte informiere den Inhaber! Benötigtes Recht: ADMINISTRATOR").queue();
        }
    }


    private void createGiveaway(SlashCommandInteractionEvent e) {
        TextInput message = TextInput.create(modalGiveawayCreateOptionMessage, "Nachricht", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Hier deine Giveaway Nachricht eingeben!")
                .setMinLength(30)
                .setMaxLength(1000)
                .setRequired(true)
                .build();
        Modal modal = Modal.create(modalGiveawayCreate, "Giveaway erstellen")
                .addComponents(ActionRow.of(message))
                .build();
        e.replyModal(modal).queue();
    }

    private void finishGiveaway(SlashCommandInteractionEvent e) {
        String id = e.getOption(subCmdFinishOptionID).getAsString();
        Message msg = getMessageWithGiveawayID(e.getChannel().asTextChannel(), id);
        AtomicReference<String> message = new AtomicReference<>("");
        getMessageWithGiveawayID(e.getChannel().asTextChannel(), id).getEmbeds().get(0).getFields().forEach(field -> {
            if (field.getName().equalsIgnoreCase("**Neues Giveaway**")) {
                message.set(field.getValue());
            }
        });
        EmbedBuilder embed = new EmbedBuilder();
        embed.setFooter("@Golden-Developer");
        Database db = Main.getMysqlConnection().getMysql().getDatabase(MysqlConnection.dbName);
        Table table = db.getTable(MysqlConnection.tableName);
        if (table.existsRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())) {
            String textChannelID = table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).getData().get(MysqlConnection.clmGiveawayChannel).getAsString();
            TextChannel channel = e.getJDA().getTextChannelById(textChannelID);
            if (channel != null) {
                Role role = e.getGuild().getRolesByName("Giveaway | " + id, true).get(0);
                List<Member> members = role.getGuild().getMembersWithRoles(role);
                if (members.size() >= 1) {
                    Member m = members.get(new Random().nextInt(members.size()));
                    embed.setColor(Color.GREEN);
                    embed.addField("**Giveaway gewinner**", "Der Gewinner des Giveaways mit der ID: " + id + " ist " + m.getAsMention() + ". Bitte melde dich bei dem Event Veranstalter!", false);
                    embed.addField("", ">" + message, false);
                } else {
                    embed.setColor(Color.GREEN);
                    embed.addField("**Giveaway gewinner**", "Entschuldige etwas ist Schief gelaufen! Es konnte kein Gewinner bestimmt werden!", false);
                }
                channel.sendMessageEmbeds(embed.build()).failOnInvalidReply(false).queue();
                role.delete().queue();
                e.getInteraction().reply("Das Giveaway mit der ID: " + id + " wurde erfolgreich ausgewertet!").queue();
                assert msg != null;
                msg.delete().queue();
            } else {
                e.reply("Der Giveaway Channel wurde nicht gesetzt oder konnte nicht gefunden werden nutze /help für weitere Infos!").queue();
            }
        } else {
            e.reply("ERROR: Bitte lade den Discord Bot neu ein!").queue();
        }
    }

    public static Message getMessageWithGiveawayID(TextChannel channel, String GiveawayID) {
        MessageHistory history = MessageHistory.getHistoryFromBeginning(channel).complete();
        for (Message m : history.getRetrievedHistory()) {
            if (!m.getEmbeds().isEmpty()) {
                for (MessageEmbed embed : m.getEmbeds()) {
                    String embedID = embed.getFooter().getText().replace("@Golden-Developer | ID: ", "");
                    if (embedID.equalsIgnoreCase(GiveawayID)) {
                        return m;
                    }
                }
            }
        }
        return null;
    }
}
