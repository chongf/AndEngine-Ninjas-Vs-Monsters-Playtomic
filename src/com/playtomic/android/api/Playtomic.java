//
// Playtomic.java
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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Playtomic {

    public static final int SUCCESS = 0;
    public static final int ERROR = 1;
    public static final int ERROR_INVALID_RATING_VALUE = 401;
    
    public static final String PLAYTOMIC_CACHE = "playtomic.cache";

    // actually the max size is 1048576.
    // the 1048577 value allow us to do
    // if (queueSize < PLAYTOMIC_QUEUE_MAX_SIZE) {...}
    //
    public static final int PLAYTOMIC_QUEUE_MAX_BYTES = 1048577;     
    
    private static Playtomic sInstance;

    private int mGameId = 0;
    private String mGameGuid = "";
    private String mSourceUrl = "";
    private String mBaseUrl = "";
    private PlaytomicLog mLog = null;
    private Context mCtx;
    private int mOfflineQueueMaxSize;
    
    public static PlaytomicLog Log;
    public static String GameGuid;
    public static int GameId;
    public static String ApiKey;

    public static Playtomic getInstance(int gameId, String gameGuid, String apiKey, Context context) throws Exception {
        if (sInstance == null) {
            sInstance = new Playtomic(gameId, gameGuid, apiKey, context);
        }
        return sInstance;
    }
    
    protected Playtomic(int gameId, String gameGuid, String apiKey, Context context) throws Exception {

        sInstance = this;
        
        mCtx = context;
        
        String model = android.os.Build.MODEL;
        String system = getSystemName();
        // from 1.6 Donut (API Level 4)
        String version = String.valueOf(android.os.Build.VERSION.SDK_INT);
        // for 1.5 or previous version 
        //String version = android.os.Build.VERSION.SDK;

        model = model.replace(" ", "+");
        system = system.replace(" ", "+");
        version = version.replace(" ", "+");

        mGameId = gameId;
        mGameGuid = gameGuid;
        mSourceUrl = "http://android.com/" + model + "/" + system + "/"    + version;
        mBaseUrl = "android.com";
        
        PlaytomicHttpRequest.Initialise();
        PlaytomicData.Initialise(apiKey);        
        PlaytomicLeaderboards.Initialise(apiKey);
        PlaytomicGameVars.Initialise(apiKey);
        PlaytomicGeoIP.Initialise(apiKey);
        PlaytomicPlayerLevels.Initialise(apiKey);

        mLog = new PlaytomicLog(gameId, gameGuid);
        
        GameGuid = gameGuid;
        GameId = gameId;
        ApiKey = apiKey;
        Log = mLog;
    }

    protected Context getCtx() {
        return mCtx;
    }

    protected int getGameId() {
        return mGameId;
    }
    
    protected String getGameGuid() {
        return mGameGuid;
    }
    
    protected int getOfflineQueueMaxSize() {
        return mOfflineQueueMaxSize;
    }
    
    protected void setOfflineQueueMaximumSize(int size) {
        mOfflineQueueMaxSize = size;
    }
    
    protected String getBaseUrl() {
        return mBaseUrl;
    }

    protected String getSourceUrl() {
        return mSourceUrl;
    }

    protected PlaytomicLog getLog() {
        return mLog;
    }
    
    protected Boolean getInternetActive() {
        ConnectivityManager cm = (ConnectivityManager) mCtx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }
    
    public static Context getContext() {
        return sInstance.getCtx();
    }

    public static int GameId() {
        return sInstance.getGameId();
    } 
    
    public static String GameGuid() {
        return sInstance.getGameGuid();
    }
    
    public static int OfflineQueueMaxSize() {
        return sInstance.getOfflineQueueMaxSize();
    }

    public static void setOfflineQueueMaxSize(int kbSize) {
        // we save the size in bytes
        //
        if (kbSize < 0) {
            kbSize = 0;
        }
        kbSize = kbSize * 1024;
        
        if (kbSize > PLAYTOMIC_QUEUE_MAX_BYTES) {
            sInstance.setOfflineQueueMaximumSize(PLAYTOMIC_QUEUE_MAX_BYTES);
        }
        else {
            sInstance.setOfflineQueueMaximumSize(kbSize);
        }
    }
    
    public static String BaseUrl() {
        return sInstance.getBaseUrl();
    }

    public static String SourceUrl() {
        return sInstance.getSourceUrl();
    }
    
    public static PlaytomicLog Log() {   
        return sInstance.getLog();
    }

    public static Boolean isInternetActive() {
        return sInstance.getInternetActive();
    }
    
    private String getSystemName() {
        String systemName = "";

        switch (android.os.Build.VERSION.SDK_INT) {
        case android.os.Build.VERSION_CODES.BASE:
            systemName = "BASE";
            break;
        case android.os.Build.VERSION_CODES.BASE_1_1:
            systemName = "BASE_1_1";
            break;
        case android.os.Build.VERSION_CODES.CUPCAKE:
            systemName = "CUPCAKE";
            break;
        case android.os.Build.VERSION_CODES.CUR_DEVELOPMENT:
            systemName = "CUR_DEVELOPMENT";
            break;
        case android.os.Build.VERSION_CODES.DONUT:
            systemName = "DONUT";
            break;
           /* 
        case android.os.Build.VERSION_CODES.ECLAIR:
            systemName = "ECLAIR";
            break;
        case android.os.Build.VERSION_CODES.ECLAIR_0_1:
            systemName = "ECLAIR_0_1";
            break;
        case android.os.Build.VERSION_CODES.ECLAIR_MR1:
            systemName = "ECLAIR_MR1";
            break;
        case android.os.Build.VERSION_CODES.FROYO:
            systemName = "FROYO";
            break;
            */
        }
        
        return systemName;
    }
}
