package yaman.hasan.hasandemo;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by hasanyaman on 29.08.2018.
 */

public class User implements Serializable{
    private String uid;
    private String username;
    private boolean isReady;

    public User(String uid, String username, boolean isReady) {
        this.uid = uid;
        this.username = username;
        this.isReady = isReady;
    }

    public boolean isReady() {
        return isReady;
    }

    public void setReady(boolean ready) {
        isReady = ready;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


}
