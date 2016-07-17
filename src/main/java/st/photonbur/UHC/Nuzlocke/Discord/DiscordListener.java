package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.events.Event;
import net.dv8tion.jda.events.ReadyEvent;
import net.dv8tion.jda.events.ShutdownEvent;
import net.dv8tion.jda.hooks.EventListener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

public class DiscordListener implements EventListener {
    DiscordBot bot;
    Nuzlocke nuz;

    public DiscordListener(Nuzlocke nuz) {
        this.bot = nuz.getDiscordBot();
        this.nuz = nuz;
    }

    @Override
    public void onEvent(Event e) {
        if(e instanceof ReadyEvent) {
            bot.guild = e.getJDA().getGuilds().get(0);
            bot.general = bot.guild.getTextChannels().stream().filter(tc -> tc.getName().equals("bottest")).findFirst().get();
            bot.participants = bot.guild.createCopyOfRole(bot.guild.getPublicRole());
            bot.participants.setName("Active Participants")
                    .setMentionable(true)
                    .update();

            bot.announce(DiscordBot.Event.JOIN);
        } else if(e instanceof ShutdownEvent) {
            bot.announce(DiscordBot.Event.QUIT);
        }
    }
}
