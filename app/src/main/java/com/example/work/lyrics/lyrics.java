package com.example.work.lyrics;

/**
 * Created by Work on 18.05.2018.
 */


import android.os.Parcel;
import android.os.Parcelable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;




public class   lyrics implements Serializable, Parcelable{

    private String mTitle;
    private  String urlimage;
    private String mArtist;
    private String mOriginalTitle;
    private String mOriginalArtist;
    private String mSourceUrl;
    private String mCoverURL;
    private String mLyrics;
    private String mSource;
    private boolean mLRC = false;
    private final int mFlag;
    public static final int NO_RESULT = -2;
    public static final int NEGATIVE_RESULT = -1;
    public static final int POSITIVE_RESULT = 1;
    public static final int ERROR = -3;
    public static final int SEARCH_ITEM = 2;

    public interface Callback {
        void onLyricsDownloaded(lyrics lyrics);
    }

    public lyrics(int flag) {
        this.mFlag = flag;
    }

    protected lyrics(Parcel in) {
        mTitle = in.readString();
        mArtist = in.readString();
        mOriginalTitle = in.readString();
        mOriginalArtist = in.readString();
        mSourceUrl = in.readString();
        mCoverURL = in.readString();
        mLyrics = in.readString();
        mSource = in.readString();
        mLRC = in.readByte() != 0;
        mFlag = in.readInt();
        urlimage  = in.readString();
    }
    public static final Creator<lyrics> CREATOR = new Creator<lyrics>() {
        @Override
        public lyrics createFromParcel(Parcel in) {
            return new lyrics(in);
        }

        @Override
        public lyrics[] newArray(int size) {
            return new lyrics[size];
        }
    };

    public String getTrack() {
        return mTitle;
    }

    public String getURLImage() {
        return urlimage;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getOriginalTrack() {
        if (mOriginalTitle != null)
            return mOriginalTitle;
        else
            return mTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.mOriginalTitle = originalTitle;
    }
    public void setURLImage(String urlimage) {
        this.urlimage = urlimage;
    }
    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        this.mArtist = artist;
    }

    public String getOriginalArtist() {
        if (mOriginalArtist != null)
            return mOriginalArtist;
        else
            return mArtist;
    }

    public void setOriginalArtist(String originalArtist) {
        this.mOriginalArtist = originalArtist;
    }

    public String getURL() {
        return mSourceUrl;
    }

    public void setURL(String uRL) {
        this.mSourceUrl = uRL;
    }

    public String getCoverURL() {
        return mCoverURL;
    }

    public void setCoverURL(String coverURL) {
        this.mCoverURL = coverURL;
    }

    public String getText() {
        return mLyrics;
    }

    public void setText(String lyrics) {
        this.mLyrics = lyrics;
    }

    public String getSource() {
        return mSource;
    }

    public int getFlag() {
        return mFlag;
    }

    public void setSource(String mSource) {
        this.mSource = mSource;
    }

    public void setLRC(boolean LRC) {
        this.mLRC = LRC;
    }

    public boolean isLRC() {
        return this.mLRC;
    }

    public byte[] toBytes() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutput out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.close();
        } finally {
            bos.close();
        }
        return bos.toByteArray();
    }

    public static lyrics fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        if (data == null)
            return null;
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        ObjectInputStream is = new ObjectInputStream(in);
        return (lyrics) is.readObject();
    }

    @Override
    public boolean equals(Object object) {
        boolean isLyrics = object instanceof lyrics;
        if (isLyrics && (this.getURL() != null) && ((lyrics) object).getURL() != null)
            return this.getURL().equals(((lyrics) object).getURL());
        else if (isLyrics) {
            lyrics other = (lyrics) object;
            boolean result = this.getText().equals(other.getText());
            result &= this.getFlag() == other.getFlag();
            result &= this.getSource().equals(other.getSource());
            result &= this.getArtist().equals(other.getArtist());
            result &= this.getTrack().equals(other.getTrack());
            return result;
        }
        else
            return false;
    }

    @Override
    public int hashCode() {
        // Potential issue with the Birthday Paradox when we hash over 50k lyrics
        return this.getURL() != null ? this.getURL().hashCode() :
                (""+this.getOriginalArtist()+this.getOriginalTrack()+this.getSource()).hashCode();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mTitle);
        parcel.writeString(mArtist);
        parcel.writeString(mOriginalTitle);
        parcel.writeString(mOriginalArtist);
        parcel.writeString(mSourceUrl);
        parcel.writeString(mCoverURL);
        parcel.writeString(mLyrics);
        parcel.writeString(mSource);
        parcel.writeByte((byte) (mLRC ? 1 : 0));
        parcel.writeInt(mFlag);
    }
}
