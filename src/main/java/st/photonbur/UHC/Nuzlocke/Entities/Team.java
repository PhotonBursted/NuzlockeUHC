package st.photonbur.UHC.Nuzlocke.Entities;

import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;

public class Team {
    private final ArrayList<Player> members = new ArrayList<>();
    private final Nuzlocke nuz;
    private final String name;

    public Team(Nuzlocke nuz, Player player, String name) {
        addPlayer(player);
        this.name = name;
        this.nuz = nuz;
    }

    private void addPlayer(Player player) {
        members.add(player);
    }

    public int countStillAlive() {
        return (int) members.stream().filter(p -> nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT).count();
    }

    private String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName() + " (" + countStillAlive() + "/" + getTeamSize() + ")\n";
    }

    private int getTeamSize() {
        return members.size();
    }
}
