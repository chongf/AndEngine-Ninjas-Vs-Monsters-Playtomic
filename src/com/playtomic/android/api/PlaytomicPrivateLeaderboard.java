package com.playtomic.android.api;

public class PlaytomicPrivateLeaderboard {
    private String mTableId;
    private String mName;
    private String mBitly;
    private String mPermalink;
    private Boolean mHighest;
    private String mRealName;
    
    public PlaytomicPrivateLeaderboard(String tableid, String name, String bitly, String permalink, Boolean highest, String realname) {
        mTableId = tableid;
        mName = name;
        mBitly = bitly;
        mPermalink = permalink;
        mHighest = highest;
        mRealName = realname;
    }
    
    public String toString() {
        return "Playtomic.PrivateLeaderboard:" + 
                "\nTableId: "+ mTableId + 
                "\nName: " + mName + 
                "\nBitly: " + mBitly + 
                "\nPermalink: " + mPermalink + 
                "\nHighest: " + mHighest + 
                "\nRealName: " + mRealName;
    }

    public String getTableId() {
        return mTableId;
    }

    public void setTableId(String tableId) {
        this.mTableId = tableId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getBitly() {
        return mBitly;
    }

    public void setBitly(String bitly) {
        this.mBitly = bitly;
    }

    public String getPermalink() {
        return mPermalink;
    }

    public void setPermalink(String permalink) {
        this.mPermalink = permalink;
    }

    public Boolean getHighest() {
        return mHighest;
    }

    public void setHighest(Boolean highest) {
        this.mHighest = highest;
    }

    public String getRealName() {
        return mRealName;
    }

    public void setRealName(String realName) {
        this.mRealName = realName;
    }
}
