package st.photonbur.UHC.Nuzlocke.Entities;

public enum Type {
    NORMAL, FIRE, WATER, ELECTRIC, GRASS,
    ICE, FIGHTING, POISON, GROUND, FLYING,
    PSYCHIC, BUG, ROCK, GHOST, DRAGON,
    DARK, STEEL, FAIRY;

    public static Type getRandom() {
        return values()[(int) (Math.random() * values().length)];
    }
}
