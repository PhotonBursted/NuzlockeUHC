package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.ChatColor;

public class Pokemon extends Player {
    public enum Type {
        NORMAL("[NRM]", ChatColor.DARK_GRAY),
        FIRE("[FIR]", ChatColor.GOLD),
        WATER("[WTR]", ChatColor.BLUE),
        ELECTRIC("[ELC]", ChatColor.YELLOW),
        GRASS("[GRS]", ChatColor.GREEN),
        ICE("[ICE]", ChatColor.AQUA),
        FIGHTING("[FGT]", ChatColor.DARK_RED),
        POISON("[PSN]", ChatColor.DARK_PURPLE),
        GROUND("[GRD]", ChatColor.GOLD),
        FLYING("[FLY]", ChatColor.WHITE),
        PSYCHIC("[PSY]", ChatColor.LIGHT_PURPLE),
        BUG("[BUG]", ChatColor.DARK_GREEN),
        ROCK("[RCK]", ChatColor.DARK_RED),
        GHOST("[GHO]", ChatColor.DARK_PURPLE),
        DRAGON("[DRG]", ChatColor.DARK_BLUE),
        DARK("[DRK]", ChatColor.BLACK),
        STEEL("[STL]", ChatColor.GRAY),
        FAIRY("[FAI]", ChatColor.LIGHT_PURPLE);

        private final ChatColor color;
        private final String name;

        Type(String name, ChatColor color) {
            this.name = name;
            this.color = color;
        }

        public ChatColor getColor() {
            return color;
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
