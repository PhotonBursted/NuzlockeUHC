package st.photonbur.UHC.Nuzlocke.Entities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import st.photonbur.UHC.Nuzlocke.StringLib;

public class Pokemon extends Player {
    public enum Type {
        TRAINER("[Trainer]", ChatColor.WHITE, 0, StringLib.GiveInfo$Trainer),
        NORMAL("[NRM]", ChatColor.DARK_GRAY, 1, StringLib.GiveInfo$Normal),
        FIRE("[FIR]", ChatColor.GOLD, 2, StringLib.GiveInfo$Fire),
        WATER("[WTR]", ChatColor.BLUE, 3, StringLib.GiveInfo$Water),
        ELECTRIC("[ELC]", ChatColor.YELLOW, 4, StringLib.GiveInfo$Electric),
        GRASS("[GRA]", ChatColor.GREEN, 5, StringLib.GiveInfo$Grass),
        ICE("[ICE]", ChatColor.AQUA, 6, StringLib.GiveInfo$Ice),
        FIGHTING("[FIG]", ChatColor.DARK_RED, 7, StringLib.GiveInfo$Fighting),
        POISON("[PSN]", ChatColor.DARK_PURPLE, 8, StringLib.GiveInfo$Poison),
        GROUND("[GRD]", ChatColor.GOLD, 9, StringLib.GiveInfo$Ground),
        FLYING("[FLY]", ChatColor.WHITE, 10, StringLib.GiveInfo$Flying),
        PSYCHIC("[PSY]", ChatColor.LIGHT_PURPLE, 11, StringLib.GiveInfo$Psychic),
        BUG("[BUG]", ChatColor.DARK_GREEN, 12, StringLib.GiveInfo$Bug),
        //ROCK("[RCK]", ChatColor.DARK_RED, 13, StringLib.GiveInfo$Rock),
        GHOST("[GHO]", ChatColor.DARK_PURPLE, 14, StringLib.GiveInfo$Ghost),
        DRAGON("[DRG]", ChatColor.DARK_BLUE, 15, StringLib.GiveInfo$Dragon),
        DARK("[DRK]", ChatColor.BLACK, 16, StringLib.GiveInfo$Dark),
        STEEL("[STL]", ChatColor.GRAY, 17, StringLib.GiveInfo$Steel);
        //FAIRY("[FAI]", ChatColor.LIGHT_PURPLE, 18, StringLib.GiveInfo$Fairy);

        private final ChatColor color;
        private final String name;
        private final int id;
        private final String info;

        Type(String name, ChatColor color, int id, String info) {
            this.name = name;
            this.color = color;
            this.id = id;
            this.info = info;
        }

        public ChatColor getColor() {
            return color;
        }
        public int getID() {
            return id;
        }
        public String getInfo() {
            String message = ""+ ChatColor.BLUE + ChatColor.BOLD + "Type: " + color + ChatColor.ITALIC + name + ChatColor.RESET + "\n";
            message += info;
            return message;
        }
        public String getName() {
            return name;
        }
        public static Type getRandom() {
            Type t;
            return (t = values()[(int) (Math.random() * values().length)]) == TRAINER ? getRandom() : t;
        }
    }

    public Pokemon(String name, Role role) {
        super(name, role);
        setType(Type.getRandom());
        Bukkit.getPlayer(name).sendMessage(String.format(StringLib.Pokemon$Type, type.getColor() +""+ ChatColor.BOLD + type.name()));
    }

    public Pokemon(String name, Role role, Type type) {
        super(name, role);
        if(type == null) {
            setType(Type.getRandom());
        } else {
            setType(type);
        }
    }
}
