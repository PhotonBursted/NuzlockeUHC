package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.Arrays;

class DiscordChatListener extends ListenerAdapter {
    private final Nuzlocke nuz;
    private final DiscordBot bot;
    private String msg;
    private int level;

    DiscordChatListener(Nuzlocke nuz) {
        this.nuz = nuz;
        this.bot = nuz.getDiscordBot();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        msg = e.getMessage().getStrippedContent();
        level = 0;

        if (messageIsCommand("help")) {
            bot.showHelp(e.getChannel());
        } else if (messageIsCommand("link")) {
            nuz.getServerLinkManager().link(msg, e.getAuthor());
        } else if (messageIsCommand("unlink")) {
            nuz.getServerLinkManager().unlink(e.getAuthor());
        } else if (messageIsCommand("settings")) {
            if (messageIsCommand("token")) {
                bot.setCommandPrefix(msg);
            }
        }
    }

    private boolean messageIsCommand(String command) {
        boolean success = msg.startsWith((level == 0 ? bot.getCommandPrefix() : "") + command);
        if (success) {
            msg = String.join(" ", Arrays.copyOfRange(msg.split(" "), 1, msg.split(" ").length));
            level++;
        }

        return success;
    }
}
