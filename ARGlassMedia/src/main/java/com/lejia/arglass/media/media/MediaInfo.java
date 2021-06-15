package com.lejia.arglass.media.media;

import android.os.Parcel;
import android.os.Parcelable;

import com.lejia.arglass.media.utils.MediaType;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class MediaInfo implements Parcelable {

    public MediaType mediaType;
    public String title;
    public String url;
    public String albumArtURI;
    public String event;

    public MediaInfo() {
    }

    protected MediaInfo(Parcel in) {
        title = in.readString();
        url = in.readString();
        albumArtURI = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(albumArtURI);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MediaInfo> CREATOR = new Creator<MediaInfo>() {
        @Override
        public MediaInfo createFromParcel(Parcel in) {
            return new MediaInfo(in);
        }

        @Override
        public MediaInfo[] newArray(int size) {
            return new MediaInfo[size];
        }
    };
}
