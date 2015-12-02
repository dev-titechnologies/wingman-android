package app.wingman.settings;

/**

 * Created by titech on 20/11/15.
 * CLASS FOR STORING SERVER URLS
 */

public class Urls {

    public static String PARENT_URL="http://192.168.1.64:1338/"; // DEMO SERVER URL


    public static String USER_SIGNUP=PARENT_URL+"user/signup";
    public static String USER_email_check=PARENT_URL+"user/emailExists";
    public static String USER_UPDATE=PARENT_URL+"user/updateUser";
    public static String GET_USERS=PARENT_URL+"user/userList";
    public static String GET_GROUPS=PARENT_URL+"group/groupDetails";
    public static String GET_CONNECTIONS=PARENT_URL+"user/getConnections";
    public static String GET_REQUESTS=PARENT_URL+"group/getInvitation";
    public static String SETREADSTATUS=PARENT_URL+"user/readConnection";
    public static String SETREADSTATUSINVITATION=PARENT_URL+"group/readInvitation";
    public static String RESPOND_TO_INVITATION=PARENT_URL+"group/manageInvitation";

    public static String GET_TAGS=PARENT_URL+"tag/list";

}
