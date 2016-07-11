package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Pokemon;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;

public class PlayerManager {
    ArrayList<Player> players;
    Nuzlocke nuz;

    public PlayerManager(Nuzlocke nuz) {
        this.nuz = nuz;
        this.players = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void divideRoles() {
        ArrayList<Player> toAdd = new ArrayList<>();
        ArrayList<Player> toRemove = new ArrayList<>();
        int trainerAmount = (int) Math.max(Math.ceil((players.size() + 1) / nuz.getSettings().getTeamSize()), 1);

        for(int i=0; i<trainerAmount; i++) {
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

    Player findNewParticipant() {
        Player target = players.stream().findAny().get();
        return target.getRole() == Role.PARTICIPANT && !(target instanceof Trainer) ? target : findNewParticipant();
    }

    public Player getPlayer(org.bukkit.entity.Player q) {
        return getPlayer(q.getName());
    }

    public Player getPlayer(String name) {
        return players.stream().filter(p -> p.getName().equals(name)).findFirst().get();
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void registerPlayer(String name) {
        if(Bukkit.getPlayer(name) != null) {
            if(players.stream().anyMatch(p -> p.getName().equals(name))) {
                removePlayer(name);
            }
            addPlayer(new Player(name, Role.PARTICIPANT));
        }
    }

    public void registerPlayer(String name, CommandSender sender) {
        if(Bukkit.getPlayer(name) != null) {
            if(getPlayer(name) == null) {
                registerPlayer(name);
                sender.sendMessage(ChatColor.RED + "[!] You just registered " + ChatColor.BOLD + name + ChatColor.RED + " into playing this game.");
                Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + sender.getName() + ChatColor.RED + " just registered you for the game. GLHF!");
            } else if(getPlayer(name).getRole() == Role.SPECTATOR) {
                registerPlayer(name);
                if(sender.getName().equals(name)) {
                    sender.sendMessage(ChatColor.RED + "[!] You just registered yourself from playing this game.");
                } else {
                    sender.sendMessage(ChatColor.RED + "[!] You just registered " + ChatColor.BOLD + name + ChatColor.RED + " into playing this game.");
                    Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + sender.getName() + ChatColor.RED + " just registered you for the game. GLHF!");
                }
            } else {
                if(sender.getName().equals(name)) {
                    sender.sendMessage(ChatColor.RED + "[!] You were already registered.");
                } else {
                    sender.sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + name + ChatColor.RED + " was already registered.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "[!] You failed to register " + ChatColor.BOLD + name + ChatColor.RED + " as they couldn't be found.");
        }
    }

    public void removePlayer(String name) {
        ArrayList<Player> toRemove = new ArrayList<>();
        players.stream().filter(p -> p.getName().equals(name)).forEach(toRemove::add);
        players.removeAll(toRemove);
    }

    public void unregisterPlayer(String name) {
        if(Bukkit.getPlayer(name) != null) {
            if(players.stream().anyMatch(p -> p.getName().equals(name))) {
                removePlayer(name);
            }
            addPlayer(new Player(name, Role.SPECTATOR));
        }
    }

    public void unregisterPlayer(String name, CommandSender sender) {
        if(Bukkit.getPlayer(name) != null) {
            if(getPlayer(name) == null) {
                unregisterPlayer(name);
                sender.sendMessage(ChatColor.RED + "[!] You just unregistered " + ChatColor.BOLD + name + ChatColor.RED + " from playing this game.");
                Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + sender.getName() + ChatColor.RED + " just unregistered you from the game.");
            } else if(getPlayer(name).getRole() == Role.PARTICIPANT) {
                unregisterPlayer(name);
                if(sender.getName().equals(name)) {
                    sender.sendMessage(ChatColor.RED + "[!] You just unregistered yourself from playing this game.");
                } else {
                    sender.sendMessage(ChatColor.RED + "[!] You just unregistered " + ChatColor.BOLD + name + ChatColor.RED + " from playing this game.");
                    Bukkit.getPlayer(name).sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + sender.getName() + ChatColor.RED + " just unregistered you from the game.");
                }
            } else {
                if(sender.getName().equals(name)) {
                    sender.sendMessage(ChatColor.RED + "[!] You weren't registered to begin with.");
                } else {
                    sender.sendMessage(ChatColor.RED + "[!] " + ChatColor.BOLD + name + ChatColor.RED + " wasn't registered to begin with.");
                }
            }
        } else {
            sender.sendMessage(ChatColor.RED + "[!] You failed to unregister " + ChatColor.BOLD + name + ChatColor.RED + " as they couldn't be found.");
        }
    }
}
