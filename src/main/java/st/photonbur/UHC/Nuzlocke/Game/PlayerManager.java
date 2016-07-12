package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;
import st.photonbur.UHC.Nuzlocke.StringLib;

import java.util.ArrayList;
import java.util.Random;

public class PlayerManager {
    private final ArrayList<Player> players;
    private final Nuzlocke nuz;
    private final Random r;

    public PlayerManager(Nuzlocke nuz) {
        this.nuz = nuz;
        this.players = new ArrayList<>();
        this.r = new Random();
    }

    private void addPlayer(Player p) {
        players.add(p);
    }

    public void deregisterPlayer(String targetName) {
        substitutePlayer(targetName, Role.SPECTATOR);
    }

    public void deregisterPlayer(String targetName, CommandSender sender) {
        if (Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(targetName))) {
            if (getPlayer(targetName) == null) {
                deregisterPlayer(targetName);
                sender.sendMessage(String.format(StringLib.PlayerManager$DeregisteredOther, targetName));
                Bukkit.getPlayer(targetName).sendMessage(String.format(StringLib.PlayerManager$DeregisteredBy, sender.getName()));
            } else if (getPlayer(targetName).getRole() == Role.PARTICIPANT) {
                deregisterPlayer(targetName);
                if (sender.getName().equals(targetName)) {
                    sender.sendMessage(StringLib.PlayerManager$DeregisteredSelf);
                } else {
                    sender.sendMessage(String.format(StringLib.PlayerManager$DeregisteredOther, targetName));
                    Bukkit.getPlayer(targetName).sendMessage(String.format(StringLib.PlayerManager$DeregisteredBy, sender.getName()));
                }
            } else {
                if (sender.getName().equals(targetName)) {
                    sender.sendMessage(StringLib.PlayerManager$DeregisteredAlreadySelf);
                } else {
                    sender.sendMessage(String.format(StringLib.PlayerManager$DeregisteredAlready, targetName));
                }
            }
        } else {
            sender.sendMessage(String.format(StringLib.PlayerManager$DeregisterFail, targetName));
        }
    }

    public void divideRoles() {
        ArrayList<Player> toAdd = new ArrayList<>();
        ArrayList<Player> toRemove = new ArrayList<>();
        int trainerAmount = (int) Math.max(Math.ceil((players.size() + 1) / nuz.getSettings().getTeamSize()), 1);

        for (int i = 0; i < trainerAmount; i++) {
            Player target = findNewParticipant();
            nuz.getTeamManager().createTeam(target.getName());
            toAdd.add(new Trainer(target.getName(), Role.PARTICIPANT));
            toRemove.add(target);
        }

        players.stream()
                .filter(p -> !(p instanceof Trainer)
                        && toAdd.stream().noneMatch(player -> player.getName().equals(p.getName()))
                        && p.getRole() == Role.PARTICIPANT)
                .forEach(
                        p -> {
                            toAdd.add(new Pokemon(p.getName(), Role.PARTICIPANT));
                            toRemove.add(p);
                        }
                );
        players.addAll(toAdd);
        players.removeAll(toRemove);
    }

    private Player findNewParticipant() {
        Player target = players.get(r.nextInt(players.size()));
        return target.getRole() == Role.PARTICIPANT && !(target instanceof Trainer) ? target : findNewParticipant();
    }

    public Player getPlayer(org.bukkit.entity.Player q) {
        return getPlayer(q.getName());
    }

    public Player getPlayer(String name) {
        return players.stream().filter(p -> p.getName().equals(name)).findFirst().orElse(null);
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void registerPlayer(String targetName) {
        substitutePlayer(targetName, Role.PARTICIPANT);
    }

    public void registerPlayer(String targetName, CommandSender sender) {
        if (Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(targetName))) {
            if (getPlayer(targetName) == null) {
                registerPlayer(targetName);
                sender.sendMessage(String.format(StringLib.PlayerManager$RegisteredOther, targetName));
                Bukkit.getPlayer(targetName).sendMessage(String.format(StringLib.PlayerManager$RegisteredBy, sender.getName()));
            } else if (getPlayer(targetName).getRole() == Role.SPECTATOR) {
                registerPlayer(targetName);
                if (sender.getName().equals(targetName)) {
                    sender.sendMessage(StringLib.PlayerManager$RegisteredSelf);
                } else {
                    sender.sendMessage(String.format(StringLib.PlayerManager$RegisteredOther, targetName));
                    Bukkit.getPlayer(targetName).sendMessage(String.format(StringLib.PlayerManager$RegisteredBy, sender.getName()));
                }
            } else {
                if (sender.getName().equals(targetName)) {
                    sender.sendMessage(StringLib.PlayerManager$RegisteredAlreadySelf);
                } else {
                    sender.sendMessage(String.format(StringLib.PlayerManager$RegisteredAlready, targetName));
                }
            }
        } else {
            sender.sendMessage(String.format(StringLib.PlayerManager$RegisterFail, targetName));
        }
    }

    private void removePlayer(String name) {
        ArrayList<Player> toRemove = new ArrayList<>();
        players.stream().filter(p -> p.getName().equals(name)).forEach(toRemove::add);
        players.removeAll(toRemove);
    }

    private void substitutePlayer(String name, Role newRole) {
        if (Bukkit.getOnlinePlayers().stream().anyMatch(p -> p.getName().equals(name))) {
            String pc = (getPlayer(name) == null ? "Player" : getPlayer(name).getClass().getSimpleName());
            Pokemon.Type pt = (pc.equals("Pokemon") ? getPlayer(name).getType() : null);

            if (players.stream().anyMatch(p -> p.getName().equals(name))) {
                removePlayer(name);
            }
            switch (pc) {
                case "Pokemon":
                    addPlayer(new Pokemon(name, newRole, pt));
                    break;
                case "Trainer":
                    addPlayer(new Trainer(name, newRole));
                    break;
                default:
                    addPlayer(new Player(name, newRole));
            }
        }
    }
}
