package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.Color;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;

public class Team {
    ArrayList<Player> players;
    Color color;
    Nuzlocke nuz;
    String name;

    public Team(Nuzlocke nuz, String name, Color color) {
        new Team(nuz, name, color, new ArrayList<>(0));
    }

    public Team(Nuzlocke nuz, String name, Color color, ArrayList<Player> players) {
        setColor(color);
        setName(name);
        this.nuz = nuz;
        this.players = players;
    }

    public void addPlayer(String name) {
        Player newTeamMember = nuz.getPlayerManager().getPlayer(name);
        if(newTeamMember != null) {
            players.add(newTeamMember);
            newTeamMember.setTeam(color);
        }
    }

    public void removePlayer(String name) {
        Player teamMember = nuz.getPlayerManager().getPlayer(name);
        if(teamMember != null) {
            players.remove(teamMember);
            teamMember.setTeam(null);
        }
    }

    public Color getColor() {
        return color;
    }

    public String getName() {
        return name;
    }

    public void setColor(Color newColor) {
        color = newColor;
    }

    public void setName(String newName) {
        name = newName;
    }
}
