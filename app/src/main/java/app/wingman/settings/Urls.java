package app.wingman.settings;

/**

 * Created by titech on 20/11/15.
 * CLASS FOR STORING SERVER URLS
 */

public class Urls {

    public static String PARENT_URL="http://192.168.1.64:1338/user/"; // DEMO SERVER URL


    public static String USER_SIGNUP=PARENT_URL+"signup";
    public static String USER_email_check=PARENT_URL+"emailExists";
    public static String USER_UPDATE=PARENT_URL+"updateUser";

    public static String GET_TAGS="http://192.168.1.64:1338/tag/list";
    public static String ADDUSERGRP="http://192.168.1.64:1338/group/inviteUser";
    public static String CREATEGROUP="http://192.168.1.64:1338/group/create";
    public static String GETYOURGROUPS="http://192.168.1.64:1338/group/groupList";
    public static String GETNAMES="http://192.168.1.64:1338/user/getUserNames";
}
