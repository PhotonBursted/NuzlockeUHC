package st.photonbur.UHC.Nuzlocke;

import org.bukkit.ChatColor;

public interface StringLib {
    String ChatListener$SpectatorNotAllowed     = ChatColor.RED + "[!] Spectators aren't allowed to chat globally!"+ ChatColor.RESET;

    String DamageManager$Immune                 = ChatColor.GREEN + "[!] That did no damage at all...";
    String DamageManager$NotEffective           = ChatColor.GREEN + "[!] Hmm, that wasn't too effective.";
    String DamageManager$SuperEffective         = ChatColor.GREEN + "[!] Wow, you're super effective against that guy!";

    String Dark$Weakened                        = ChatColor.DARK_GREEN + "[!] The light... it's getting too much to handle!";
    String Dark$PokeEyesLanded                  = ChatColor.DARK_GREEN + "[!] Finger in their sockets, like it's supposed to be.";
    String Dark$PokeEyesVictim                  = ChatColor.DARK_GREEN + "[!] Oh no, someone poked your eyes out!";

    String DeathListener$DeathMove              = ChatColor.RED + "[!] You will be moved over to the spectator channel within %s seconds";
    String DeathListener$TeamWipe               = ChatColor.RED + "[!] Oh no! %s has been wiped! %s teams remain...";
    String DeathListener$TrainerWipe            = ChatColor.RED + "[!] Trainer %s has gone, meaning their team can't grow anymore! Also, all team caps have been raised by 1 to %s.";
    String DeathListener$Win                    = ChatColor.RED + "[!] THAT'S IT! Give a big hand to %s, consisting of %s, for reigning supreme in the %s!";

    String DiscordBot$AnnounceStart             = "@here Let the games begin!";
    String DiscordBot$GoodbyeMessage            = "Until next time, take it easy!";
    String DiscordBot$TeamWipe                  = "RIP that team! D:\nThere are now %s teams left";
    String DiscordBot$TrainerWipe               = "RIP that trainer... As they died preemptively, their team won't be growing anymore!";
    String DiscordBot$WelcomeMessage            = "Hey Vechs, guys here!";
    String DiscordBot$Win                       = "@everyone THAT'S IT! Give a big hand to %s, consisting of %s, for reigning supreme in the %s!";

    String Dragon$NotEnoughXP                   = ChatColor.DARK_GREEN + "[!] Oh no, you don't have enough levels to do that!";
    String Dragon$RedeemedElytra                = ChatColor.DARK_GREEN + "[!] Redeemed elytra!";

    String Electric$Paralysis                   = ChatColor.DARK_GREEN + "[!] You have been paralyzed!";

    String EMA$EternalDaylight                  = ChatColor.DARK_RED + "[!] From now on, the sun won't crash into the western part of the horizon again. It promises.";
    String EMA$EpisodeEnd                       = ChatColor.GOLD + "[!] There's that. Another episode down." + ChatColor.ITALIC + " (end of EP%s)";
    String EMA$GentlemenRuleEnd                 = ChatColor.DARK_RED + "[!] The pact has crumbled. Combat is now enabled." + ChatColor.RESET;
    String EMA$MarkerStart                      = ChatColor.GOLD + "[!] Markers have started!";
    String EMA$WbProgressReport                 = ChatColor.DARK_PURPLE + "[!] News flash from the front, borders are now at %s blocks' distance!";
    String EMA$WbShrinkEnd                      = ChatColor.DARK_PURPLE + "[!] The world border has been stopped by an unknown force. Playing room? %s blocks.";
    String EMA$WbShrinkStart                    = ChatColor.DARK_PURPLE + "[!] The world border has started closing in!";

    String GameManager$MatchStart                = ChatColor.GOLD + "[!] Please wait... Starting match. Teams are being generated!";

    String GCD$CountdownEnd                     = ChatColor.GOLD + "[!] Go, go, go!";
    String GCD$CountdownProgress                = ChatColor.GOLD + "[!] " + ChatColor.BOLD + "%s" + ChatColor.GOLD + "...";
    String GCD$CountdownStart                   = ChatColor.GOLD + "[!] Match starts in " + ChatColor.BOLD + "%s" + ChatColor.GOLD + "...";

    String GiveInfo$Bug                         = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Slowly heal when there's over 16 leaves surrounding you\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Leaves around you despawn faster than normal, even placed ones";
    String GiveInfo$Dark                        = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Long exposure to light leads to weakness\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " 40% chance of poking another player's eyes (causes blindness)";
    String GiveInfo$Dragon                      = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Being in cold biomes occasionally causes freezing in place\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Active:" + ChatColor.BLUE + " /redeem at 25+ XP levels to buy a set of elytra for 10 levels";
    String GiveInfo$Electric                    = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Attacks have a (5 + XP)% chance of landing a (5 + XP)/10 second paralysis. Also, getting attacked has a (5 + XP)% chance of paralysis.";
    String GiveInfo$Fighting                    = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Having nothing in hand gives Strength 1 and Haste 1";
    String GiveInfo$Fire                        = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Chopping wood/mining blocks has a 5% chance of burning/smelting them";
    String GiveInfo$Flying                      = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Every so often, 1-3 feathers spawn where you have just walked";
    String GiveInfo$Ghost                       = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Invisibility when above 9 haunches";
    String GiveInfo$Grass                       = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Invisibility when hiding in a plant\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Swimming in water gives slowness and absorption\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Holding a sword gives slight weakness";
    String GiveInfo$Ground                      = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Acrophobia; too high altitudes give slight nausea\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Shovel or pick in hand gives Haste 1";
    String GiveInfo$Ice                         = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Resistance against all slowness\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Permanent frost walker\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Weakness and slowness in hot, arid biomes";
    String GiveInfo$Normal                      = "- " + ChatColor.BLUE + ChatColor.ITALIC + "No special effects or redemptions. Just, normal.";
    String GiveInfo$Poison                      = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Poison resistance, replaced by unharmful effects\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Active:" + ChatColor.BLUE + " /redeem at 20+ XP levels to get 1 free splash potion of poison";
    String GiveInfo$Psychic                     = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Permanent weakness 1\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Active:" + ChatColor.BLUE + " /redeem [levels to spend] at 5+ XP levels to scan players within (5 + XP) blocks";
    String GiveInfo$Rock                        = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Permanent Fortune (on ores, but also rocks\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Carrying 6 stacks of (cobble)stone will increase strength and resistance, but reduce speed";
    String GiveInfo$Steel                       = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Permanent Resistance 1 and 1 extra heart. Tanky!\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Being in rain or water for too long inflicts 'rust'";
    String GiveInfo$Trainer                     = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Permanently get 5 extra hearts\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Active:" + ChatColor.BLUE + " /redeem at 2+ XP levels to get a Pokéball";
    String GiveInfo$Water                       = "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Being out of water or rain causes dehydration\n" + ChatColor.RESET +
                                                  "- " + ChatColor.BLUE + ChatColor.BOLD + "Passive:"+ ChatColor.BLUE + " Being in water gives speed, haste and respiration effects";

    String GiveInfo$NeedsArguments              = ChatColor.RED + "[!] As you haven't got a type appointed, try a type as argument!";

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

    String Poison$AlreadyRedeemed               = ChatColor.DARK_GREEN + "[!] You've already redeemed your free potion!";
    String Poison$NotEnoughXP                   = ChatColor.DARK_GREEN + "[!] Oh no, you don't have enough levels to do that!";
    String Poison$RedeemedPotion                = ChatColor.DARK_GREEN + "[!] Redeemed your free poison potion!";

    String PokeballDetector$CaughtVictim        = ChatColor.RED + "[!] You have been caught by %s!";
    String PokeballDetector$CaughtThrower       = ChatColor.RED + "[!] You just caught a wild %s!";
    String PokeballDetector$NotATrainer         = ChatColor.RED + "[!] Only trainers can catch Pokémon for the team!";
    String PokeballDetector$TargetOnTeamAlready = ChatColor.RED + "[!] The player you hit was already on a team!";
    String PokeballDetector$TeamAlreadyFull     = ChatColor.RED + "[!] Your team is already full!";

    String Pokemon$Type                         = ChatColor.RED + "[!] You've been selected to be %s" + ChatColor.RED + "! Do /info to give details when needed";

    String Psychic$NoPlayersInRange             = ChatColor.DARK_GREEN + "[!] There weren't any players close by!";
    String Psychic$NotEnoughXP                  = ChatColor.DARK_GREEN + "[!] Oh no, you don't have enough levels to do that!";
    String Psychic$RedeemedPerk                 = ChatColor.DARK_GREEN + "[!] Players within %s blocks of you:";

    String Redeem$InvalidArgLength              = ChatColor.RED + "[!] This redeem operation needs at least 1 argument!";
    String Redeem$InvalidInput                  = ChatColor.RED + "[!] You ned to put in at least 5 levels!";

    String StartUHC$GameUnderway                = ChatColor.DARK_RED + "[!] Game is already underway!";
    String StartUHC$NoParticipants              = ChatColor.DARK_RED + "[!] No volunteers to start the game with!";

    String Steel$Rusty                          = ChatColor.DARK_GREEN + "[!] You seem to be getting a little rusty!";

    String StopUHC$GameNotUnderway              = ChatColor.DARK_RED + "[!] There's no game running at the moment!";
    String StopUHC$Stopped                      = ChatColor.DARK_RED + "[!] The match was stopped!";

    String Trainer$NotEnoughXP                  = ChatColor.DARK_GREEN + "[!] You'll need a little more XP to do that!";

    String Water$Dehydrated                     = ChatColor.DARK_GREEN + "[!] Hmmm, it might be best to head for a little refreshment!";
}
