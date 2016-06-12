package softpatrol.drinkapp.api;

/**
 * MonsterMaskinen was here on 2016-06-10!
 * Copy this code for instant regret...
 */
public class Definitions {
    /*  IP   */
    public final static String IP = "drinkmeapp.eu-west-1.elasticbeanstalk.com";
    public final static String URL = "http://" + IP;

    /*  API  */
    public static final String CREATE_MOBILE = URL + "/account/create/mobile";
    public static final String GET_ACCOUNT_ME = URL + "/account/me";
    public static final String GET_STASH = URL + "/stash";
    public static final String POST_STASH = URL + "/stash";
    public static final String GET_INGREDIENTS = URL + "/ingredient";
    public static final String GET_RECIPES = URL + "/recipe";

}
