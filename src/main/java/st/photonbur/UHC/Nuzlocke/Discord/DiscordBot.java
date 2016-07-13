package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.JDA;
import net.dv8tion.jda.JDABuilder;
import net.dv8tion.jda.entities.Guild;
import net.dv8tion.jda.entities.TextChannel;
import net.dv8tion.jda.entities.User;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import javax.security.auth.login.LoginException;

public class DiscordBot {
    public enum Event {
        DEATH("Well, someone died."),
        START(StringLib.DiscordBot$AnnounceStart);

        private final String message;

        Event(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }
    }

    private JDA bot;
    Nuzlocke nuz;

    TextChannel general;
    Guild guild;

    public DiscordBot (Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public void announce(Event e) {
        announce(e, e.getMessage());
    }

    public void announce(Event e, String msg) {
        logMessage(general, e.name(), msg);
    }

    private User getUser(String name) {
        return (bot.getUsersByName(name) != null && !bot.getUsersByName(name).isEmpty()) ? bot.getUsersByName(name).get(0) : null;
    }

    public void logMessage(TextChannel tc, String purpose, String msg) {
        tc.sendMessage("**[" + purpose.toUpperCase() + "]** " + msg);
    }

    public void start() {
        try {
            bot = new JDABuilder().setBotToken("MjAyNjY1MjE0NzQxNTc3NzI4.CmdqMA.6FnFJgLRxoHxWoenLabblZxu5Tk").addListener(new DiscordListener(nuz)).buildBlocking();
        } catch (LoginException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
