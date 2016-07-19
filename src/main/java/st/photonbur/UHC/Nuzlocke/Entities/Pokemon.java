package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.ChatColor;

public class Pokemon extends Player {
    public enum Type {
        //NORMAL("[NRM]", ChatColor.DARK_GRAY, 1),
        FIRE("[FIR]", ChatColor.GOLD, 2),
        //WATER("[WTR]", ChatColor.BLUE, 3),
        ELECTRIC("[ELC]", ChatColor.YELLOW, 4),
        //GRASS("[GRA]", ChatColor.GREEN, 5),
        //ICE("[ICE]", ChatColor.AQUA, 6),
        FIGHTING("[FIG]", ChatColor.DARK_RED, 7),
        //POISON("[PSN]", ChatColor.DARK_PURPLE, 8),
        //GROUND("[GRD]", ChatColor.GOLD, 9),
        FLYING("[FLY]", ChatColor.WHITE, 10),
        //PSYCHIC("[PSY]", ChatColor.LIGHT_PURPLE, 11),
        BUG("[BUG]", ChatColor.DARK_GREEN, 12),
        //ROCK("[RCK]", ChatColor.DARK_RED, 13),
        //GHOST("[GHO]", ChatColor.DARK_PURPLE, 14),
        DRAGON("[DRG]", ChatColor.DARK_BLUE, 15),
        DARK("[DRK]", ChatColor.BLACK, 16);
        //STEEL("[STL]", ChatColor.GRAY, 17),
        //FAIRY("[FAI]", ChatColor.LIGHT_PURPLE, 18);

        private final ChatColor color;
        private final String name;
        private final int id;

        Type(String name, ChatColor color, int id) {
            this.name = name;
            this.color = color;
            this.id = id;
        }

        public ChatColor getColor() {
            return color;
        }
        public int getID() {
            return id;
        }
        public String getName() {
            return name;
        }
        public static Type getRandom() {
            return values()[(int) (Math.random() * values().length)];
        }
    }

    private Type type;

    public Pokemon(String name, Role role) {
        super(name, role);
        setType(Type.getRandom());
    }

    public Pokemon(String name, Role role, Type type) {
        super(name, role);
        if(type == null) {
            setType(Type.getRandom());
        } else {
            setType(type);
        }
    }

    public Type getType() {
        return type;
    }

    void setType(Type t) {
        this.type = t;
    }
}
