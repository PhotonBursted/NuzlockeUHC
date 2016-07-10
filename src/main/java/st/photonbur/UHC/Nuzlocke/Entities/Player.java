package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.Color;

public class Player {
    Color teamColor;
    String name;
    Role role;

    public Player(String name, Role role) {
        setName(name);
        setRole(role);
        setTeam(null);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public Color getTeam() {
        return teamColor;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setRole(Role newRole) {
        role = newRole;
    }

    public void setTeam(Color newTeam) {
        teamColor = newTeam;
    }
}
