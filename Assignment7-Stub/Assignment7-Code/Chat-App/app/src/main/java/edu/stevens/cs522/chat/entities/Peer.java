package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable, Persistable {

    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    public Double longitude;

    public Double latitude;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        // TODO
    }

    public Peer(Parcel in) {
        // TODO
    }

    @Override
    public void writeToProvider(ContentValues out) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        // TODO
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            // TODO
            return null;
        }

        @Override
        public Peer[] newArray(int size) {
            // TODO
            return null;
        }

    };
}
