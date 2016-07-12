package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.ChatColor;

public class Pokemon extends Player {
    public enum Type {
        NORMAL("Normal", ChatColor.DARK_GRAY),
        FIRE("Fire", ChatColor.GOLD),
        WATER("Water", ChatColor.BLUE),
        ELECTRIC("Electric", ChatColor.YELLOW),
        GRASS("Grass", ChatColor.GREEN),
        ICE("Ice", ChatColor.AQUA),
        FIGHTING("Fighting", ChatColor.DARK_RED),
        POISON("Poison", ChatColor.DARK_PURPLE),
        GROUND("Ground", ChatColor.GOLD),
        FLYING("Flying", ChatColor.WHITE),
        PSYCHIC("Psychic", ChatColor.LIGHT_PURPLE),
        BUG("Bug", ChatColor.DARK_GREEN),
        ROCK("Rock", ChatColor.DARK_RED),
        GHOST("Ghost", ChatColor.DARK_PURPLE),
        DRAGON("Dragon", ChatColor.DARK_BLUE),
        DARK("Dark", ChatColor.BLACK),
        STEEL("Steel", ChatColor.GRAY),
        FAIRY("Fairy", ChatColor.LIGHT_PURPLE);

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
