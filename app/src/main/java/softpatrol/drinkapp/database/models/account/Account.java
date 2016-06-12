package softpatrol.drinkapp.database.models.account;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import softpatrol.drinkapp.database.models.stash.Stash;

/**
 * David was here on 2016-06-08!
 * Copy this code for instant regret...
 */
public class Account {

    public static final String TOKEN = "accToken";
    public static final String ALIAS = "username";

    private long id;
    private String token;
    private String alias;
    private ArrayList<Stash> stashes = new ArrayList<>();

    public Account(JSONObject jsonObject) {
        try {
            token = jsonObject.getString(TOKEN);
            alias = jsonObject.getString(ALIAS);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Account() {}

    @Override
    public String toString() {
        String ret = "---Account " + id + "---\n";
        ret += "ID: " + id + '\n';
        ret += "Token: " + token + '\n';
        ret += "Alias: " + alias + '\n';
        return ret;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public ArrayList<Stash> getStashes() {
        return stashes;
    }

    public void setStashes(ArrayList<Stash> stashes) {
        this.stashes = stashes;
    }
}
