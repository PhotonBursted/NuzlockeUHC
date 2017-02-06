package st.photonbur.UHC.Nuzlocke.Discord;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

class DiscordEventListener implements EventListener {
    private final DiscordBot bot;

    DiscordEventListener(Nuzlocke nuz) {
        this.bot = nuz.getDiscordBot();
    }

    @Override
    public void onEvent(Event e) {
        if (e instanceof ReadyEvent) {
            bot.guild = e.getJDA().getGuilds().get(0);
            bot.general = bot.guild.getTextChannels().stream().filter(tc -> tc.getName().equals("bottest")).findFirst().get();
            bot.participants = bot.guild.getController()
                    .createCopyOfRole(bot.guild.getPublicRole())
                    .setName("Active Participants")
                    .setMentionable(true).complete();

            bot.announce(DiscordBot.Event.JOIN);
        } else if (e instanceof ShutdownEvent) {
            bot.announce(DiscordBot.Event.QUIT);
        }
    }
}
