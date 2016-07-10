package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Entities.Role;
import st.photonbur.UHC.Nuzlocke.Entities.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Trainer;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;
import java.util.Random;

public class PlayerManager {
    ArrayList<Player> players;
    ArrayList<Team> teams;
    Nuzlocke nuz;

    public PlayerManager(Nuzlocke nuz) {
        this.nuz = nuz;
        this.players = new ArrayList<>();
        this.teams = new ArrayList<>();
    }

    public void addPlayer(Player p) {
        players.add(p);
    }

    public void divideRoles() {
        int trainerAmount = (int) Math.ceil((players.size() + 1) / nuz.getSettings().getTeamSize());

        for(int i=0; i<trainerAmount; i++) {
            boolean found = false;
            Player target = null;
            while(!found) {
                target = players.get(new Random().nextInt(Bukkit.getOnlinePlayers().size()));
                if(target.getRole() == Role.PARTICIPANT && !(target instanceof Trainer)) {
                    found = true;
                }
            }
            removePlayer(target);
            addPlayer(new Trainer(target.getName(), target.getRole()));
        }

        ArrayList<Player> toAdd = new ArrayList<>();
        players.stream().filter(p -> !(p instanceof Trainer) && p.getRole() == Role.PARTICIPANT).forEach(toAdd::add);
        players.addAll(toAdd);
    }

    public Player getPlayer(org.bukkit.entity.Player q) {
        return getPlayer(q.getName());
    }

    public Player getPlayer(String name) {
        Player player = null;
        for(Player p: players) {
            if(p.getName().equals(name)) {
                player = p;
                break;
            }
        }
        return player;
    }

    public void registerPlayer(String name) {
        if(Bukkit.getPlayer(name) != null) {
            if(getPlayer(name) != null) {
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

    public void removePlayer(Player q) {
        players.stream().filter(p -> p.equals(q)).forEach(p -> players.remove(p));
    }

    public void unregisterPlayer(String name) {
        if(Bukkit.getPlayer(name) != null) {
            if(getPlayer(name) != null) {
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
