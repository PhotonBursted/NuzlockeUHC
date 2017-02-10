package st.photonbur.UHC.Nuzlocke.Managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Entities.Player;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TeamManager {
    class ColorCollection {
        private final ChatColor cc;
        private final Color c;

        ColorCollection(ChatColor cc, Color c) {
            this.cc = cc;
            this.c = c;
        }

        ChatColor getChatColor() {
            return cc;
        }

        Color getColor() {
            return c;
        }
    }

    private final ArrayList<st.photonbur.UHC.Nuzlocke.Entities.Team> teams = new ArrayList<>();
    private final Map<String, ColorCollection> colors = generateColorMap();
    private final Nuzlocke nuz;
    private final Random r = new Random();

    public TeamManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    private Map<String, ColorCollection> generateColorMap() {
        Map<String, ColorCollection> map = new HashMap<>();
        map.put("BLACK", new ColorCollection(ChatColor.BLACK, new Color(0, 0, 0)));
        map.put("DARK_BLUE", new ColorCollection(ChatColor.DARK_BLUE, new Color(0, 0, 170)));
        map.put("DARK_GREEN", new ColorCollection(ChatColor.DARK_GREEN, new Color(0, 170, 0)));
        map.put("DARK_AQUA", new ColorCollection(ChatColor.DARK_AQUA, new Color(0, 170, 170)));
        map.put("DARK_RED", new ColorCollection(ChatColor.DARK_RED, new Color(170, 0, 0)));
        map.put("DARK_PURPLE", new ColorCollection(ChatColor.DARK_PURPLE, new Color(170, 0, 170)));
        map.put("GOLD", new ColorCollection(ChatColor.GOLD, new Color(255, 170, 0)));
        map.put("GRAY", new ColorCollection(ChatColor.GRAY, new Color(170, 170, 170)));
        map.put("DARK_GRAY", new ColorCollection(ChatColor.DARK_GRAY, new Color(85, 85, 85)));
        map.put("BLUE", new ColorCollection(ChatColor.BLUE, new Color(85, 85, 255)));
        map.put("GREEN", new ColorCollection(ChatColor.GREEN, new Color(85, 255, 85)));
        map.put("AQUA", new ColorCollection(ChatColor.AQUA, new Color(85, 255, 255)));
        map.put("RED", new ColorCollection(ChatColor.RED, new Color(255, 85, 85)));
        map.put("LIGHT_PURPLE", new ColorCollection(ChatColor.LIGHT_PURPLE, new Color(255, 85, 255)));
        map.put("YELLOW", new ColorCollection(ChatColor.YELLOW, new Color(255, 255, 85)));

        return map;
    }

    public void addPlayer(String playerName, String teamName) {
        st.photonbur.UHC.Nuzlocke.Entities.Team target = getTeams().stream().filter(t -> t.contains(teamName)).findFirst().orElse(null);
        if (target != null) {
            target.addPlayer(nuz.getPlayerManager().getPlayer(playerName));
            nuz.getGameManager().getScoreboard().getTeams().stream()
                    .filter(t -> t.getEntries().contains(teamName))
                    .findFirst().get().addEntry(playerName);
            nuz.getDiscordBot().movePlayer(playerName, "Team " + teamName);
            nuz.getDiscordBot().addRole(playerName, "Team " + teamName);
        }
    }

    void createDiscordTeams() {
        teams.forEach(t -> nuz.getDiscordBot().addTeam(
                t.getMembers().get(0).getName(),
                colors.get(nuz.getGameManager().getScoreboard().getEntryTeam(t.getMembers().get(0).getName()).getName()).getColor()
        ));
    }

    void createTeam(String playerName) {
        String teamName;
        do {
            teamName = randomTeamColorName();
        } while (nuz.getGameManager().getScoreboard().getTeam(teamName) != null);

        Team team = nuz.getGameManager().getScoreboard().registerNewTeam(teamName);
        team.setPrefix(colors.get(teamName).getChatColor() + "");
        team.setSuffix(ChatColor.RESET + "");
        team.setDisplayName("Team " + playerName);
        team.addEntry(playerName);

        teams.add(new st.photonbur.UHC.Nuzlocke.Entities.Team(nuz, nuz.getPlayerManager().getPlayer(playerName), "Team " + playerName));

        synchronized (nuz.getPlayerManager().teamBuilderLock) {
            nuz.getPlayerManager().isBuildingTeam = false;
            nuz.getPlayerManager().teamBuilderLock.notifyAll();
        }
    }

    public ArrayList<st.photonbur.UHC.Nuzlocke.Entities.Team> getTeams() {
        return teams;
    }

    public int teamsAliveCount() {
        return (int) (getTeams().stream().filter(team -> team.countStillAlive() > 0).count()
                + Bukkit.getOnlinePlayers().stream().filter(
                p -> nuz.getGameManager().getScoreboard().getEntryTeam(p.getName()) == null && nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Player.Role.PARTICIPANT
        ).count());
    }

    private String randomTeamColorName() {
        return colors.keySet().toArray(new String[colors.keySet().size()])[r.nextInt(colors.keySet().size())];
    }
}
