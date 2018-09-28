package yaman.hasan.hasandemo;

import com.google.firebase.auth.FirebaseUser;

import java.util.List;

/**
 * Created by hasanyaman on 28.08.2018.
 */

public class Group {
    private String id;
    private String groupName;
    private String ownerUserID;
    private List<User> users;
    private boolean isActive;
    private String videoID;
    private String videoTitle;


    public Group(String id, String groupName, String ownerUserID, List<User> users, boolean isActive,
                 String videoID, String videoTitle) {
        this.id = id;
        this.groupName = groupName;
        this.ownerUserID = ownerUserID;
        this.users = users;
        this.isActive = isActive;
        this.videoID = videoID;
        this.videoTitle = videoTitle;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public String getVideoID() {
        return videoID;
    }

    public void setVideoID(String videoID) {
        this.videoID = videoID;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getOwnerUserID() {
        return ownerUserID;
    }

    public void setOwnerUserID(String ownerUserID) {
        this.ownerUserID = ownerUserID;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
