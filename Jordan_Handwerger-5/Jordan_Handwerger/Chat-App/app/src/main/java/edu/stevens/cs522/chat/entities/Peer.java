package edu.stevens.cs522.chat.entities;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.net.InetAddress;
import java.util.Date;

import edu.stevens.cs522.base.DateUtils;
import edu.stevens.cs522.base.InetAddressUtils;
import edu.stevens.cs522.chat.contracts.PeerContract;

/**
 * Created by dduggan.
 */

public class Peer implements Parcelable, Persistable {

    // Will be database key
    public long id;

    public String name;

    // Last time we heard from this peer.
    public Date timestamp;

    // Where we heard from them
    public InetAddress address;

    public Peer() {
    }

    public Peer(Cursor cursor) {
        id = PeerContract.getID(cursor);
        name = PeerContract.getName(cursor);
        timestamp = PeerContract.getTimestamp(cursor);
        address = PeerContract.getAddress(cursor);
    }

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        address = InetAddressUtils.readAddress(in);
    }

    @Override
    public void writeToProvider(ContentValues out) {
        out.put(PeerContract.ID, id);
        out.put(PeerContract.NAME, name);
        DateUtils.putDate(out, PeerContract.TIMESTAMP, timestamp);
        InetAddressUtils.putAddress(out, PeerContract.ADDRESS, address);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(id);
        out.writeString(name);
        DateUtils.writeDate(out, timestamp);
        InetAddressUtils.writeAddress(out, address);
    }

    public static final Creator<Peer> CREATOR = new Creator<Peer>() {

        @Override
        public Peer createFromParcel(Parcel source) {
            return new Peer(source);
        }

        @Override
        public Peer[] newArray(int size) {
            return new Peer[size];
        }

    };
}