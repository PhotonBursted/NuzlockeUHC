package st.photonbur.UHC.Nuzlocke.Entities;

public class Player {
    Pokemon.Type type;
    private String name;
    private Role role;

    public Player(String name, Role role) {
        setName(name);
        setRole(role);
        setType(null);
    }

    public String getName() {
        return name;
    }

    private void setName(String newName) {
        name = newName;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role newRole) {
        role = newRole;
    }

    public Pokemon.Type getType() {
        return type;
    }

    void setType(Pokemon.Type newType) {
        type = newType;
    }
}
