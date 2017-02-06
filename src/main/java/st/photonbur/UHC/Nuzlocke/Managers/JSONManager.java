package st.photonbur.UHC.Nuzlocke.Managers;

//import com.google.gson.Gson;
//import com.google.gson.GsonBuilder;

import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.dv8tion.jda.core.entities.User;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.io.*;
import java.util.Map;

public class JSONManager {
    //    private Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
    private final Nuzlocke nuz;
    private JsonReader jr;
    private JsonWriter jw;

    public JSONManager(Nuzlocke nuz) {
        this.nuz = nuz;
    }

    public void readAllConfigs() {
        readDiscordConfig();
        readLinks();
    }

    public void writeAllConfigs() {
        writeDiscordConfig();
        writeLinks();
    }

    private void readDiscordConfig() {
        nuz.getLogger().info("Reading config...");

        try {
            jr = new JsonReader(new FileReader(nuz.getConfigFilename()));
            jr.beginObject();

            while (jr.hasNext()) {
                String name = jr.nextName();

                switch (name) {
                    case "token":
                        nuz.getDiscordBot().setToken(jr.nextString());
                        if (!nuz.getDiscordBot().isRunning()) {
                            nuz.getDiscordBot().start();
                        }
                        break;
                    case "commandPrefix":
                        nuz.getDiscordBot().setCommandPrefix(jr.nextString(), false);
                        break;
                }
            }

            jr.endObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private void readLinks() {
        nuz.getLogger().info("Reading user links...");

        try {
            jr = new JsonReader(new FileReader(nuz.getLinkFilename()));
            jr.beginObject();

            while (jr.hasNext()) {
                nuz.getServerLinkManager().link(jr.nextName(), nuz.getDiscordBot().getJDA().getUserById(jr.nextString()));
            }

            jr.endObject();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void writeDiscordConfig() {
        try {
            jw = setupWriter(nuz.getConfigFilename());

            jw.beginObject();
            jw.name("commandPrefix");
            jw.value(nuz.getDiscordBot().getCommandPrefix());
            jw.name("token");
            jw.value(nuz.getDiscordBot().getToken());
            jw.endObject();
            jw.close();
            jw = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void writeLinks() {
        try {
            jw = setupWriter(nuz.getLinkFilename());

            jw.beginObject();

            for (Map.Entry<String, User> link : nuz.getServerLinkManager().getLinks().entrySet()) {
                nuz.getLogger().info(link.getKey() + ", " + link.getValue().getId());

                try {
                    jw.name(link.getKey());
                    jw.value(link.getValue().getId());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            jw.endObject();
            jw.close();
            jw = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private JsonWriter setupWriter(String filename) throws FileNotFoundException, UnsupportedEncodingException {
        JsonWriter jw;

        jw = new JsonWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        jw.setIndent("  ");
        return jw;
    }
}
