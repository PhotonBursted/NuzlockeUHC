package st.photonbur.UHC.Nuzlocke.Managers;

import net.dv8tion.jda.core.entities.User;
import st.photonbur.UHC.Nuzlocke.Nuzlocke;

import java.util.HashMap;

public class ServerLinkManager {
    @SuppressWarnings("WeakerAccess")
    public class ServerLink extends HashMap<String, User> {

    }

    private final Nuzlocke nuz;
    private final ServerLink links;

    public ServerLinkManager(Nuzlocke nuz) {
        this.nuz = nuz;
        this.links = new ServerLink();
    }

    public void link(String ign, User discusr) {
        if (links.containsKey(ign)) {
            links.replace(ign, discusr);
        } else {
            links.put(ign, discusr);
        }

        nuz.getJSONManager().writeLinks();
    }

    public void unlink(User discusr) {
        ServerLink entries = new ServerLink();

        links.entrySet().stream()
                .filter(link -> link.getValue().equals(discusr))
                .forEach(link -> entries.put(link.getKey(), link.getValue()));
        entries.entrySet().forEach(link -> links.remove(link.getKey(), link.getValue()));

        nuz.getJSONManager().writeLinks();
    }

    public User getLinkedUser(String ign) {
        return links.get(ign);
    }

    ServerLink getLinks() {
        return links;
    }
}
