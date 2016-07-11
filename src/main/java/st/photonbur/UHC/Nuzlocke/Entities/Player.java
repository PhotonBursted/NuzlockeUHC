package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.ChatColor;

public class Player {
    ChatColor teamColor;
    String name;
    Role role;

    public Player(String name, Role role) {
        setName(name);
        setRole(role);
        setTeamColor(null);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public ChatColor getTeamColor() {
        return teamColor;
    }

    public void setName(String newName) {
        name = newName;
    }

    public void setRole(Role newRole) {
        role = newRole;
    }

    public void setTeamColor(ChatColor newTeamColor) {
        this.teamColor = newTeamColor;
    }
}
