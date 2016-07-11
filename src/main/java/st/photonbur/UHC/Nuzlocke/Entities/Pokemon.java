package st.photonbur.UHC.Nuzlocke.Entities;

public class Pokemon extends Player {
    private Type type;

    public Pokemon(String name, Role role) {
        super(name, role);
        type = Type.getRandom();
    }

    public enum Type {
        NORMAL, FIRE, WATER, ELECTRIC, GRASS,
        ICE, FIGHTING, POISON, GROUND, FLYING,
        PSYCHIC, BUG, ROCK, GHOST, DRAGON,
        DARK, STEEL, FAIRY;

        public static Type getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

}
