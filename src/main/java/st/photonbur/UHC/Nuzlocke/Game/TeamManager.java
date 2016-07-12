package st.photonbur.UHC.Nuzlocke.Game;

import org.bukkit.ChatColor;
import org.bukkit.scoreboard.Team;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class TeamManager {
    Map<String, ChatColor> colors;
    Nuzlocke nuz;
    Random r;

    public TeamManager(Nuzlocke nuz) {
        this.nuz = nuz;
        r = new Random();
        colors = generateColorMap();
    }

    private Map<String, ChatColor> generateColorMap() {
        Map<String, ChatColor> map = new HashMap<>();
        map.put("BLACK", ChatColor.BLACK);
        map.put("DARK_BLUE", ChatColor.DARK_BLUE);
        map.put("DARK_GREEN", ChatColor.DARK_GREEN);
        map.put("DARK_AQUA", ChatColor.DARK_AQUA);
        map.put("DARK_RED", ChatColor.DARK_RED);
        map.put("DARK_PURPLE", ChatColor.DARK_PURPLE);
        map.put("GOLD", ChatColor.GOLD);
        map.put("GRAY", ChatColor.GRAY);
        map.put("DARK_GRAY", ChatColor.DARK_GRAY);
        map.put("BLUE", ChatColor.BLUE);
        map.put("GREEN", ChatColor.GREEN);
        map.put("AQUA", ChatColor.AQUA);
        map.put("RED", ChatColor.RED);
        map.put("LIGHT_PURPLE", ChatColor.LIGHT_PURPLE);
        map.put("YELLOW", ChatColor.YELLOW);

        return map;
    }

    public void createTeam(String playerName) {
        String teamName;
        do {
            teamName = randomTeamColorName();
        } while (nuz.getGameManager().getScoreboard().getTeam(teamName) != null);

        Team team = nuz.getGameManager().getScoreboard().registerNewTeam(teamName);
        team.setPrefix(colors.get(teamName) + "");
        team.setSuffix(ChatColor.RESET + "");
        team.setDisplayName("Team " + playerName);
        team.addEntry(playerName);
    }

    private String randomTeamColorName() {
        return colors.keySet().toArray(new String[colors.keySet().size()])[r.nextInt(colors.keySet().size())];
    }
}

