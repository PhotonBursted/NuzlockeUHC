package st.photonbur.UHC.Nuzlocke;

import org.bukkit.ChatColor;

public interface StringLib {
    String ChatListener$SpectatorNotAllowed     = ChatColor.RED +"[!] Spectators aren't allowed to chat globally!"+ ChatColor.RESET;

    String DeathListener$DeathMove              = "" + ChatColor.RED + "[!] You will be moved over to the spectator channel within %s seconds";

    String DiscordBot$AnnounceStart             = "@here Let the games begin!";
    String DiscordBot$GoodbyeMessage            = "**[QUIT]** Until next time, take it easy!";
    String DiscordBot$WelcomeMessage            = "**[JOIN]** Hey Vechs, guys here!";

    String EMA$EpisodeEnd                       = ChatColor.GOLD + "[!] There's that. Another episode down." + ChatColor.ITALIC + " (end of EP%s)";
    String EMA$GentlemenRuleEnd                 = ChatColor.DARK_RED + "[!] The pact has crumbled. Combat is now enabled." + ChatColor.RESET;
    String EMA$MarkerStart                      = ChatColor.GOLD + "[!] Markers have started!";

    String GCD$CountdownEnd                     = ChatColor.GOLD + "[!] Go, go, go!";
    String GCD$CountdownProgress                = ChatColor.GOLD + "[!] " + ChatColor.BOLD + "%s" + ChatColor.GOLD + "...";
    String GCD$CountdownStart                   = ChatColor.GOLD + "[!] Match starts in " + ChatColor.BOLD + "%s" + ChatColor.GOLD + "...";

    String ListPlayers$NoPlayersFound           = "- " + ChatColor.BLUE + ChatColor.ITALIC + "No players found";
    String ListPlayers$Participants             = "" + ChatColor.BLUE + ChatColor.BOLD + "Participants:";
    String ListPlayers$Spectators               = "" + ChatColor.BLUE + ChatColor.BOLD + "Spectators:";

    String PlayerManager$DeregisteredAlready    = ChatColor.RED + "[!] " + ChatColor.BOLD + "%s" + ChatColor.RED + " wasn't registered anyway.";
    String PlayerManager$DeregisteredAlreadySelf= ChatColor.RED + "[!] You weren't registered to begin with.";
    String PlayerManager$DeregisteredBy         = ChatColor.RED + "[!] " + ChatColor.BOLD + "%s" + ChatColor.RED + " just deregistered you.";
    String PlayerManager$DeregisteredOther      = ChatColor.RED + "[!] You just deregistered " + ChatColor.BOLD + "%s" + ChatColor.RED + " into playing this game.";
    String PlayerManager$DeregisteredSelf       = ChatColor.RED + "[!] You just deregistered yourself out of this game.";
    String PlayerManager$DeregisterFail         = ChatColor.RED + "[!] You failed to deregister " + ChatColor.BOLD + "%s" + ChatColor.RED + " as they couldn't be found.";

    String PlayerManager$MatchStarted           = ChatColor.RED + "[!] Register failed, the target wasn't registered before the start of the match.";

    String PlayerManager$RegisteredAlready      = ChatColor.RED + "[!] " + ChatColor.BOLD + "%s" + ChatColor.RED + " was already registered.";
    String PlayerManager$RegisteredAlreadySelf  = ChatColor.RED + "[!] You were already registered.";
    String PlayerManager$RegisteredBy           = ChatColor.RED + "[!] " + ChatColor.BOLD + "%s" + ChatColor.RED + " just registered you for the game. GLHF!";
    String PlayerManager$RegisteredOther        = ChatColor.RED + "[!] You just registered " + ChatColor.BOLD + "%s" + ChatColor.RED + " into playing this game.";
    String PlayerManager$RegisteredSelf         = ChatColor.RED + "[!] You just registered yourself for the upcoming match.";
    String PlayerManager$RegisterFail           = ChatColor.RED + "[!] You failed to register " + ChatColor.BOLD + "%s" + ChatColor.RED + " as they couldn't be found.";

    String StartUHC$GameUnderway                = ChatColor.DARK_RED + "[!] Game is already underway!";

    String StopUHC$GameNotUnderway              = ChatColor.DARK_RED + "[!] There's no game running at the moment!";
    String StopUHC$Stopped                      = ChatColor.DARK_RED + "[!] The match was stopped!";
}
