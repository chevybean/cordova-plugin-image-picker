package com.synconset;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONObject;
import org.json.JSONException;

public class Picture implements Parcelable {
    public String url;
    public int width;
    public int height;

    public Picture(String url, int width, int height) {
        this.url = url;
        this.width = width;
        this.height = height;
    }

    private Picture(Parcel in) {
        this.url = in.readString();
        this.width = in.readInt();
        this.height = in.readInt();
    }

    public JSONObject getJSONObject() throws JSONException{
        JSONObject picture = new JSONObject();
        picture.put("url", this.url);
        picture.put("width", this.width);
        picture.put("height", this.height);
        return picture;
    }

    public static final Parcelable.Creator<Picture> CREATOR = new Parcelable.Creator<Picture>() {
        public Picture createFromParcel(Parcel in) {
            return new Picture(in);
        }

        public Picture[] newArray(int size) {
            return new Picture[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeInt(width);
        dest.writeInt(height);
    }
}
