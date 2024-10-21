package io.github.coho04.giveaway.discord;

import io.github.coho04.giveaway.discord.commands.Giveaway;
import io.github.coho04.giveaway.Main;
import io.github.coho04.giveaway.MysqlConnection;
import io.github.coho04.mysql.entities.Database;
import io.github.coho04.mysql.entities.RowBuilder;
import io.github.coho04.mysql.entities.SearchResult;
import io.github.coho04.mysql.entities.Table;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.Color;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;


public class Events extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        Database db = Main.getMysqlConnection().getMysql().getDatabase(Main.getCustomConfig().getMysqlDatabase());
        Table table = db.getTable(MysqlConnection.tableName);
        if (!table.existsRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())) {
            table.insert(new RowBuilder().with(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())
                    .with(table.getColumn(MysqlConnection.clmGiveawayChannel), "")
                    .build());
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (!e.getMember().getUser().isBot() && e.getReaction().getEmoji().asCustom().getId().equals("980209183481823242")) {
            List<MessageEmbed> embeds = e.getChannel().retrieveMessageById(e.getMessageId()).complete().getEmbeds();
            if (embeds.size() <= 1) {
                String gwID = embeds.get(0).getFooter().getText().replace("@Golden-Developer | ID: ", "");
                Role role = e.getGuild().getRolesByName("Giveaway | " + gwID, true).get(0);
                if (role != null && !hasRole(e.getMember(), role) && e.getGuild().getSelfMember().canInteract(role)) {
                    e.getGuild().addRoleToMember(e.getMember(), role).queue();
                }
            } else {
                e.getReaction().removeReaction().queue();
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if (!e.getMember().getUser().isBot() && e.getReaction().getEmoji().asCustom().getId().equals("980209183481823242")) {
            List<MessageEmbed> embeds = e.getChannel().getHistory().getMessageById(e.getMessageId()).getEmbeds();
            if (embeds.size() <= 1) {
                String gwID = embeds.get(0).getFooter().getText().replace("@Golden-Developer | ID: ", "");
                Role role = e.getGuild().getRolesByName("Giveaway | " + gwID, true).get(0);
                if (role != null && hasRole(e.getMember(), role) && e.getGuild().getSelfMember().canInteract(role)) {
                    e.getGuild().removeRoleFromMember(e.getMember(), role).queue();
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equalsIgnoreCase(Giveaway.modalGiveawayCreate)) {
            String message = e.getValue(Giveaway.modalGiveawayCreateOptionMessage).getAsString();
            long id = Instant.now().getEpochSecond();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.orange);
            embed.addField("**Neues Giveaway**", message, false);
            embed.setFooter("@Golden-Developer | ID: #" + id);
            Table table = Main.getMysqlConnection().getMysql().getDatabase(Main.getCustomConfig().getMysqlDatabase()).getTable(MysqlConnection.tableName);
            if (table.existsRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId())) {
                HashMap<String, SearchResult> row = table.getRow(table.getColumn(MysqlConnection.clmGuildID), e.getGuild().getId()).getData();
                TextChannel channel = e.getJDA().getTextChannelById(row.get(MysqlConnection.clmGiveawayChannel).getAsString());
                if (channel != null) {
                    channel.sendMessageEmbeds(embed.build()).queue(msg -> {
                        Emoji emote = e.getJDA().getEmojiById("980209183481823242");
                        if (emote != null) {
                            msg.addReaction(emote).queue();
                        }
                    });
                    e.getGuild().createRole().queue(role -> {
                        role.getManager().setName("Giveaway | #" + id).queue();
                    });
                    e.deferEdit().queue();
                } else {
                    e.reply("Der Giveaway Channel wurde nicht gesetzt oder konnte nicht gefunden werden nutze /help für weitere Infos!").queue();
                }
            } else {
                e.reply("ERROR: Bitte lade den Discord Bot neu ein!").queue();
            }
        }
    }

    private Boolean hasRole(Member m, Role role) {
        return m.getRoles().contains(role);
    }
}
