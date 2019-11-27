package edu.stevens.cs522.chat.rest;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.IOException;
import java.net.HttpURLConnection;

import edu.stevens.cs522.base.EnumUtils;

/**
 * Created by dduggan.
 */

public abstract class Response implements Parcelable {

    private final static String TAG = Response.class.getCanonicalName();

    public static enum ResponseType {
        ERROR,
        REGISTER,
        POSTMESSAGE,
        UNREGISTER
    }

    public final static String RESPONSE_MESSAGE_HEADER = "X-Response-Message";

	/*
	 * These fields are obtained from the response metadata (response headers and status line).
	 * The fields in the subclass responses are obtained from the JSON body of the response entity.
	 */

    // Human-readable response message (optional)
    public String responseMessage = "";

    // HTTP status code.
    public int httpResponseCode = 0;

    // HTTP status line message.
    public String httpResponseMessage = "";

    public abstract boolean isValid();

    @Override
    public String toString(){
        return "ResponseMessage:\t" + responseMessage + "\n"
                + "HTTP Status Code:\t" + httpResponseCode + "\n"
                + "HTTP Status:\t" + httpResponseMessage;
    }

    public Response(HttpURLConnection connection) throws IOException {

        String message = connection.getHeaderField(RESPONSE_MESSAGE_HEADER);
        if (message != null) {
            responseMessage = message;
        }

        httpResponseCode = connection.getResponseCode();

        httpResponseMessage = connection.getResponseMessage();

    }

    public Response(String responseMessage, int httpResponseCode, String httpResponseMessage) {
        this.responseMessage = responseMessage;
        this.httpResponseCode = httpResponseCode;
        this.httpResponseMessage = httpResponseMessage;
    }

    public Response(Parcel in) {
        if (in.readByte() == 1) {
            responseMessage = in.readString();
        }
        httpResponseCode = in.readInt();
        if (in.readByte() == 1) {
            httpResponseMessage = in.readString();
        }
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        if (responseMessage != null) {
            out.writeByte((byte)1);
            out.writeString(responseMessage);
        } else {
            out.writeByte((byte)0);
        }
        out.writeInt(httpResponseCode);
        if (httpResponseMessage != null) {
            out.writeByte((byte)1);
            out.writeString(httpResponseMessage);
        } else {
            out.writeByte((byte)0);
        }
        out.writeString(httpResponseMessage);}

    public int describeContents() {
        return 0;
    }

    public static Response createResponse(Parcel in) {
        ResponseType requestType = EnumUtils.readEnum(ResponseType.class, in);
        switch (requestType) {
            case ERROR:
                return new ErrorResponse(in);
            case REGISTER:
                return new RegisterResponse(in);
            case POSTMESSAGE:
                return new PostMessageResponse(in);
            default:
                break;
        }
        throw new IllegalArgumentException("Unknown request type: "+requestType.name());
    }

    public static final Parcelable.Creator<Response> CREATOR = new Parcelable.Creator<Response>() {
        public Response createFromParcel(Parcel in) {
            return createResponse(in);
        }

        public Response[] newArray(int size) {
            return new Response[size];
        }
    };

}
