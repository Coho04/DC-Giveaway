package de.goldendeveloper.giveaway.discord;

import club.minnced.discord.webhook.WebhookClientBuilder;
import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import de.goldendeveloper.giveaway.Main;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;
import java.util.Date;

public class Discord {

    private JDA bot;

    public static String cmdHelp = "help";
    public static String cmdRestart = "restart";
    public static String cmdShutdown = "shutdown";
    public static String cmdGiveAway = "giveaway";
    public static String cmdGiveAwaySubCmdCreate = "create";
    public static String cmdGiveAwaySubCmdFinish = "finish";
    public static String cmdGiveAwaySubCmdFinishOptionID = "giveaway-id";

    public Discord(String Token) {
        try {
            bot = JDABuilder.createDefault(Token)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.ROLE_TAGS, CacheFlag.EMOTE, CacheFlag.ACTIVITY, CacheFlag.CLIENT_STATUS, CacheFlag.ONLINE_STATUS)
                    .enableIntents(GatewayIntent.GUILD_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS,
                            GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_PRESENCES,
                            GatewayIntent.GUILD_BANS, GatewayIntent.DIRECT_MESSAGE_REACTIONS,
                            GatewayIntent.GUILD_INVITES, GatewayIntent.DIRECT_MESSAGE_TYPING,
                            GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.GUILD_VOICE_STATES,
                            GatewayIntent.GUILD_WEBHOOKS, GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.GUILD_MESSAGE_TYPING)
                    .addEventListeners(new Events())
                    .setAutoReconnect(true)
                    .build().awaitReady();
            registerCommands();
            if (Main.production) {
                Online();
            }
            bot.getShardManager().setActivity(Activity.playing("/help | " + bot.getGuilds().size() + " Servern"));
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public JDA getBot() {
        return bot;
    }

    private void registerCommands() {
        bot.upsertCommand(cmdHelp, "Zeigt dir eine Liste möglicher Befehle an!").queue();
        bot.upsertCommand(cmdRestart, "Startet den Discord Bot neu!").queue();
        bot.upsertCommand(cmdShutdown, "Fährt den Discord Bot herunter!").queue();
        bot.upsertCommand(cmdGiveAway, "Veranstalte ein Giveaway auf deinem Discord Server!").addSubcommands(
                new SubcommandData(cmdGiveAwaySubCmdCreate, "Erstellt ein neues Giveaway"),
                new SubcommandData(cmdGiveAwaySubCmdFinish, "Beendet ein vorhandenes Giveaway").addOption(OptionType.STRING, cmdGiveAwaySubCmdFinishOptionID, "Giveaway ID", true)
        ).queue();
    }

    private void Online() {
        WebhookEmbedBuilder embed = new WebhookEmbedBuilder();

        if (Main.restart) {
            embed.setColor(0x33FFFF);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "Neustart Erfolgreich"));
        } else {
            embed.setColor(0x00FF00);
            embed.addField(new WebhookEmbed.EmbedField(false, "[Status]", "ONLINE"));
        }
        embed.setAuthor(new WebhookEmbed.EmbedAuthor(getBot().getSelfUser().getName(), getBot().getSelfUser().getAvatarUrl(), "https://Golden-Developer.de"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Gestartet als", Main.getDiscord().getBot().getSelfUser().getName()));
        embed.addField(new WebhookEmbed.EmbedField(false, "Server", Integer.toString(Main.getDiscord().getBot().getGuilds().size())));
        embed.addField(new WebhookEmbed.EmbedField(false, "Status", "\uD83D\uDFE2 Gestartet"));
        embed.addField(new WebhookEmbed.EmbedField(false, "Version", Main.getProjektVersion()));
        embed.setFooter(new WebhookEmbed.EmbedFooter("@Golden-Developer", getBot().getSelfUser().getAvatarUrl()));
        embed.setTimestamp(new Date().toInstant());
        new WebhookClientBuilder(Main.getConfig().getDiscordWebhook()).build().send(embed.build());
    }
}
