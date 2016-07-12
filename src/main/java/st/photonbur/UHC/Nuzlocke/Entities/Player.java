package st.photonbur.UHC.Nuzlocke.Entities;

public class Player {
    private String name;
    private Role role;

    public Player(String name, Role role) {
        setName(name);
        setRole(role);
    }

    public String getName() {
        return name;
    }

    public Role getRole() {
        return role;
    }

    public Pokemon.Type getType() {
        return null;
    }

    private void setName(String newName) {
        name = newName;
    }

    public void setRole(Role newRole) {
        role = newRole;
    }

    void setType(Pokemon.Type newType) {}
}
