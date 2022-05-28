package de.goldendeveloper.giveaway.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.giveaway.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionRemoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Random;


public class Events extends ListenerAdapter {

    //giveaway create <Emote> <Message>
    //giveaway finish <ID>

    private final String modalGiveawayCreate = "giveaway-create";
    private final String modalGiveawayCreateOptionMessage = "message";

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        e.getJDA().getShardManager().setActivity(Activity.playing("/help | " + e.getJDA().getGuilds().size() + " Servern"));
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(Main.getDiscord().getBot().getSelfUser().getName(), Main.getDiscord().getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestoppt als", Main.getDiscord().getBot().getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDD34 Offline"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getDiscord().getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", Main.getDiscord().getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        embed.setColor(0xFF0000);

        if (new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build()).isDone()) {
            System.exit(0);
        }
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent e) {
        User _Coho04_ = e.getJDA().getUserById("513306244371447828");
        User zRazzer = e.getJDA().getUserById("428811057700536331");
        String cmd = e.getName();
        if (cmd.equalsIgnoreCase(Discord.cmdGiveAway)) {
            if (e.getMember().hasPermission(Permission.ADMINISTRATOR)) {
                String subCmd = e.getSubcommandName();
                if (subCmd.equalsIgnoreCase(Discord.cmdGiveAwaySubCmdCreate)) {
                    createGiveaway(e);
                } else if (subCmd.equalsIgnoreCase(Discord.cmdGiveAwaySubCmdFinish)) {
                    finishGiveaway(e);
                }
            } else {
                e.reply("Dazu hast du keine Rechte bitte informiere den Inhaber! Benötigtes Recht: ADMINISTRATOR").queue();
            }
        } else if (cmd.equalsIgnoreCase(Discord.cmdHelp)) {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("**Help Commands**");
            embed.setColor(Color.MAGENTA);
            for (Command cm : Main.getDiscord().getBot().retrieveCommands().complete()) {
                embed.addField("/" + cm.getName(), cm.getDescription(), true);
            }
            embed.setFooter("@Golden-Developer", e.getJDA().getSelfUser().getAvatarUrl());
            e.getInteraction().replyEmbeds(embed.build()).addActionRow(
                    Button.link("https://wiki.Golden-Developer.de/", "Online Übersicht"),
                    Button.link("https://support.Golden-Developer.de", "Support Anfragen")
            ).queue();
        } else if (e.getName().equalsIgnoreCase(Discord.cmdShutdown)) {
            if (Main.production) {
                if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                    e.getInteraction().reply("Der Bot wird nun heruntergefahren").queue();
                    e.getJDA().shutdown();
                } else {
                    e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                }
            } else {
                e.reply("Dieser Bot kann nicht heruntergefahren werden, da er sich im Entwickler Modus befindet!").queue();
            }
        } else if (e.getName().equalsIgnoreCase(Discord.cmdRestart)) {
            if (Main.production) {
                if (e.getUser() == zRazzer || e.getUser() == _Coho04_) {
                    try {
                        e.getInteraction().reply("Der Discord Bot wird nun neugestartet!").queue();
                        Process p = Runtime.getRuntime().exec("screen -AmdS " + Main.getDiscord().getProjektName() + " java -Xms1096M -Xmx1096M -jar " + Main.getDiscord().getProjektName() + "-" + Main.getDiscord().getProjektVersion() + ".jar restart");
                        p.waitFor();
                        e.getJDA().shutdown();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                } else {
                    e.getInteraction().reply("Dazu hast du keine Rechte du musst für diesen Befehl der Bot Inhaber sein!").queue();
                }
            } else {
                e.reply("Dieser Bot kann nicht neugestartet werden, da er sich im Entwickler Modus befindet!").queue();
            }
        }
    }

    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        if (!e.getMember().getUser().isBot()) {
            if (e.getReactionEmote().getId().equals("980209183481823242")) {
                List<MessageEmbed> embeds = e.getChannel().retrieveMessageById(e.getMessageId()).complete().getEmbeds();
                if (embeds.size() <= 1) {
                    MessageEmbed embed = embeds.get(0);
                    String GwID = embed.getFooter().getText().replace("@Golden-Developer | ID: ", "");
                    Role role = e.getGuild().getRolesByName("Giveaway | " + GwID, true).get(0);
                    if (role != null) {
                        if (!hasRole(e.getMember(), role)) {
                            if (e.getGuild().getSelfMember().canInteract(role)) {
                                e.getGuild().addRoleToMember(e.getMember(), role).queue();
                            }
                        }
                    }
                } else {
                    e.getReaction().removeReaction().queue();
                }
            }
        }
    }

    @Override
    public void onMessageReactionRemove(MessageReactionRemoveEvent e) {
        if (!e.getMember().getUser().isBot()) {
            if (e.getReactionEmote().getId().equals("980209183481823242")) {
                List<MessageEmbed> embeds = e.getChannel().getHistory().getMessageById(e.getMessageId()).getEmbeds();
                if (embeds.size() <= 1) {
                    MessageEmbed embed = embeds.get(0);
                    String GwID = embed.getFooter().getText().replace("@Golden-Developer | ID: ", "");
                    Role role = e.getGuild().getRolesByName("Giveaway | " + GwID, true).get(0);
                    if (role != null) {
                        if (hasRole(e.getMember(), role)) {
                            if (e.getGuild().getSelfMember().canInteract(role)) {
                                e.getGuild().removeRoleFromMember(e.getMember(), role).queue();
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent e) {
        if (e.getModalId().equalsIgnoreCase(modalGiveawayCreate)) {
            String message = e.getValue(modalGiveawayCreateOptionMessage).getAsString();
            long id = Instant.now().getEpochSecond();
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.orange);
            embed.addField("**Neues Giveaway**", message, false);
            embed.setFooter("@Golden-Developer | ID: #" + id);
            e.getMessageChannel().sendMessageEmbeds(embed.build()).queue(msg -> {
                Emote emote = e.getJDA().getEmoteById("980209183481823242");
                if (emote != null) {
                    msg.addReaction(emote).queue();
                }
            });
            e.getGuild().createRole().queue(role -> {
                role.getManager().setName("Giveaway | #" + id).queue();
            });
            e.deferEdit().queue();
        }
    }

    private Boolean hasRole(Member m, Role role) {
        for (Role r : m.getRoles()) {
            if (r == role) {
                return true;
            }
        }
        return false;
    }

    private void createGiveaway(SlashCommandInteractionEvent e) {
        TextInput message = TextInput.create(modalGiveawayCreateOptionMessage, "Nachricht", TextInputStyle.PARAGRAPH)
                .setPlaceholder("Hier deine Giveaway Nachricht eingeben!")
                .setMinLength(30)
                .setMaxLength(1000)
                .setRequired(true)
                .build();
        Modal modal = Modal.create(modalGiveawayCreate, "Giveaway erstellen")
                .addActionRows(ActionRow.of(message))
                .build();
        e.replyModal(modal).queue();
    }

    private void finishGiveaway(SlashCommandInteractionEvent e) {
        String id = e.getOption(Discord.cmdGiveAwaySubCmdFinishOptionID).getAsString();
        Message msg = getMessageWithGiveawayID(e.getTextChannel(), id);
        String message  = "";
        for (MessageEmbed.Field f : getMessageWithGiveawayID(e.getTextChannel(), id).getEmbeds().get(0).getFields()) {
            if (f.getName().equalsIgnoreCase("**Neues Giveaway**")) {
                message = f.getValue();
            }
        }
        EmbedBuilder embed = new EmbedBuilder();
        embed.setFooter("@Golden-Developer");

        Role role = e.getGuild().getRolesByName("Giveaway | " + id, true).get(0);
        List<Member> members = role.getGuild().getMembersWithRoles(role);
        if (members.size() >= 1) {
            Member m = members.get(new Random().nextInt(members.size()));
            embed.setColor(Color.GREEN);
            embed.addField("**Giveaway gewinner**", "Der Gewinner des Giveaways mit der ID: " + id + " ist " + m.getAsMention() + ". Bitte melde dich bei dem Event Veranstalter!", false);
            embed.addField("", ">" + message, false);
            e.getTextChannel().sendMessageEmbeds(embed.build()).failOnInvalidReply(false).queue();
        } else {
            embed.setColor(Color.GREEN);
            embed.addField("**Giveaway gewinner**", "Entschuldige etwas ist Schief gelaufen! Es konnte kein Gewinner bestimmt werden!", false);
            e.getTextChannel().sendMessageEmbeds(embed.build()).failOnInvalidReply(false).queue();
        }
        e.getInteraction().reply("Das Giveaway mit der ID: " + id + " wurde erfolgreich ausgewertet!").queue();
        msg.delete().queue();
        role.delete().queue();
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
