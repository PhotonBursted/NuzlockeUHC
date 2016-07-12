package st.photonbur.UHC.Nuzlocke.Entities;

import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.ArrayList;

public class Team {
    ArrayList<Player> members = new ArrayList<>();
    Nuzlocke nuz;
    String name;

    public Team(Nuzlocke nuz, Player player, String name) {
        addPlayer(player);
        this.name = name;
        this.nuz = nuz;
    }

    public void addPlayer(Player player) {
        members.add(player);
    }

    public int countStillAlive() {
        return (int) members.stream().filter(p -> nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT).count();
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        final String[] message = {getName() + " (" + countStillAlive() + "/" + getTeamSize() + ")\n"};
        return message[0];
    }

    public int getTeamSize() {
        return members.size();
    }
}
