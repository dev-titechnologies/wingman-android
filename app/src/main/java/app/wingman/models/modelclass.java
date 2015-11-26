package app.wingman.models;

/**
 * Created by titech on 23/11/15.
 */
public class modelclass {

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCustomData() {
        return userCustomData;
    }

    public void setUserCustomData(String userCustomData) {
        this.userCustomData = userCustomData;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String userId;
    public String userName;
    public String userCustomData;
    public String userEmail;
    public String UserPhone;

    public String getGroupid() {
        return groupid;
    }

    public void setGroupid(String groupid) {
        this.groupid = groupid;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupUsers() {
        return groupUsers;
    }

    public void setGroupUsers(String groupUsers) {
        this.groupUsers = groupUsers;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getGroupTags() {
        return groupTags;
    }

    public void setGroupTags(String groupTags) {
        this.groupTags = groupTags;
    }

    public String groupid;
    public String groupName;
    public String groupUsers;
    public String adminId;
    public String groupTags;
}
