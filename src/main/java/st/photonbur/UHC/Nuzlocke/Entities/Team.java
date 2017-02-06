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

    public void addPlayer(Player player) {
        members.add(player);
    }

    public boolean contains(String p) {
        return members.stream().anyMatch(m -> m.getName().equals(p));
    }

    public int countStillAlive() {
        return (int) members.stream().filter(p -> nuz.getPlayerManager().getPlayer(p.getName()).getRole() == Role.PARTICIPANT).count();
    }

    public ArrayList<Player> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

    private int getTeamSize() {
        return members.size();
    }

    public String membersToString() {
        String res = "";
        for (int i = 0; i < members.size(); i++) {
            res += members.get(i).getName();
            if (i == members.size() - 2) {
                res += " & ";
            }
            if (i < members.size() - 2) {
                res += ", ";
            }
        }
        return res;
    }

    @Override
    public String toString() {
        return getName() + " (" + countStillAlive() + "/" + getTeamSize() + ")\n";
    }
}
