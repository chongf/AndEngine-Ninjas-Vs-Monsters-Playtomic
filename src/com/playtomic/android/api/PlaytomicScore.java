//
// PlaytomicScore.java
// Playtomic
//
// This file is part of the official Playtomic API for Android games.
// Playtomic is a real time analytics platform for casual games
// and services that go in casual games. If you haven't used it
// before check it out:
// http://playtomic.com/
//
// Created by ben at the above domain on 10/19/11.
// Copyright 2011 Playtomic LLC. All rights reserved.
//
// Documentation is available at:
// http://playtomic.com/api/android
//
// PLEASE NOTE:
// You may modify this SDK if you wish but be kind to our servers. Be
// careful about modifying the analytics stuff as it may give you
// borked reports.
//
// If you make any awesome improvements feel free to let us know!
//
// -------------------------------------------------------------------------
// THIS SOFTWARE IS PROVIDED BY PLAYTOMIC, LLC "AS IS" AND ANY
// EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
// PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR
// CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
// EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
// PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
// PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
// LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
// NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
// SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

package com.playtomic.android.api;

import java.util.Date;
import java.util.LinkedHashMap;

public class PlaytomicScore {

    private String mName = "";
    private int mPoints = 0;
    private Date mDate = null;
    private String mRelativeDate = "";
    private LinkedHashMap<String, String> mCustomData = new LinkedHashMap<String, String>();
    private long mRank = 0;
    private String mFBUserId = "";

    public String getFBUserId() {
        return mFBUserId;
    }

    public void setFBUserId(String fBUserId) {
        this.mFBUserId = fBUserId;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getPoints() {
        return mPoints;
    }

    public void setPoints(int points) {
        this.mPoints = points;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        this.mDate = date;
    }

    public String getRelativeDate() {
        return mRelativeDate;
    }

    public void setRelativeDate(String relativeDate) {
        this.mRelativeDate = relativeDate;
    }

    public LinkedHashMap<String, String> getCustomData() {
        return mCustomData;
    }

    public void setCustomData(LinkedHashMap<String, String> customData) {
        this.mCustomData = customData;
    }

    public long getRank() {
        return mRank;
    }

    public void setRank(long rank) {
        this.mRank = rank;
    }

    public PlaytomicScore(String name, int points) {
        mName = name;
        mPoints = points;
    }

    public PlaytomicScore(
            String name, 
            int points, 
            Date date, 
            String relativedate, 
            LinkedHashMap<String, String> customdata, 
            long rank) {
        
        mName = name;
        mPoints = points;
        mDate = date;
        mRelativeDate = relativedate;
        mCustomData = customdata;
        mRank = rank;
    }

    public String getCustomValue(String key) {
        return mCustomData.get(key);
    }

    public void addCustomValue(String key, String value) {
        mCustomData.put(key, value);
    }    
}
