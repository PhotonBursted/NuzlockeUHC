package st.photonbur.UHC.Nuzlocke.Entities;

public class Pokemon extends Player {
    private Type type;

    public Pokemon(String name, Role role) {
        super(name, role);
        type = Type.getRandom();
    }
}
