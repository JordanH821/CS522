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

    public long id;

    public String name;

    public Date timestamp;

    public Double longitude;

    public Double latitude;

    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("ID: " + id);
        sb.append(", Name: " + name);
        sb.append(", Time: " + timestamp);
        sb.append(", Long: " + longitude);
        sb.append(", Lat:" + longitude);
        sb.append("}");
        return sb.toString();
    }

    public Peer() {
    }

    public Peer(Cursor cursor) {
        id = PeerContract.getID(cursor);
        name = PeerContract.getName(cursor);
        timestamp = PeerContract.getTimestamp(cursor);
        latitude = PeerContract.getLatitude(cursor);
        longitude = PeerContract.getLongitude(cursor);
    }

    public Peer(Parcel in) {
        id = in.readLong();
        name = in.readString();
        timestamp = DateUtils.readDate(in);
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    @Override
    public void writeToProvider(ContentValues out) {
        out.put(PeerContract.ID, id);
        out.put(PeerContract.NAME, name);
        DateUtils.putDate(out, PeerContract.TIMESTAMP, timestamp);
        out.put(PeerContract.LATITUDE, latitude);
        out.put(PeerContract.LONGITUDE, longitude);
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
        out.writeDouble(latitude);
        out.writeDouble(longitude);
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