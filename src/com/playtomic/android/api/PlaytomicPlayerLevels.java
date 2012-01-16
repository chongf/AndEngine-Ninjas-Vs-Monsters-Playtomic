//
// PlaytomicPlayerLevels.java
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

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class PlaytomicPlayerLevels {

    private PlaytomicRequestListener<PlaytomicLevel> mRequestListener;

    private String mLevelId = "";
    
    public void setRequestListener(PlaytomicRequestListener<PlaytomicLevel> requestListener) {
        mRequestListener = requestListener;
    }
    
    private static String SECTION;
    private static String SAVE;
    private static String LIST;
    private static String LOAD;
    private static String RATE;
    
    static void Initialise(String apiKey) throws Exception {
        SECTION = PlaytomicEncrypt.md5("playerlevels-" + apiKey);
        RATE = PlaytomicEncrypt.md5("playerlevels-rate-" + apiKey);
        LIST = PlaytomicEncrypt.md5("playerlevels-list-" + apiKey);
        SAVE = PlaytomicEncrypt.md5("playerlevels-save-" + apiKey);
        LOAD = PlaytomicEncrypt.md5("playerlevels-load-" + apiKey);
    }
    
    public void load(String levelId) {
        if (Playtomic.isInternetActive()) {    
            
            mLevelId = levelId;

            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestLoadFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestLoadFailed();
                    }
                }
            });
            try {
                LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>(); 
                
                postData.put("levelid", levelId);
                
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, LOAD, postData);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                requestLoadFailed(ex.getMessage());
            }
        }
        else {
            requestLoadFailed();
        }        
    }

    private void requestLoadFinished(String response) {
        if (mRequestListener == null) {
            return;
        }
           
        try {        
            // we got a response of some kind
            //
            JSONObject json = new JSONObject(response);
            int status = json.getInt("Status");
            
            // failed on the server side
            if(status != 1) {
                int errorCode = json.getInt("ErrorCode");
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<PlaytomicLevel>(errorCode, "Connectivity error. Server side"));
                return;
            }
            
            JSONObject level = json.getJSONObject("Data");
            ArrayList<PlaytomicLevel> md = new ArrayList<PlaytomicLevel>();
            addLevelToArray(level, mLevelId, md);
            
            PlaytomicResponse<PlaytomicLevel> playtomicResponse = 
                    new PlaytomicResponse<PlaytomicLevel>(true, Playtomic.SUCCESS, md, 1);
            
            mRequestListener.onRequestFinished(playtomicResponse);
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, ex.getMessage()));
        }        
    }

    private void requestLoadFailed() {
        requestLoadFailed("");
    }
    
    private void requestLoadFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }

    public void rate(String levelId, int rating) {
        if(rating < 1 || rating > 10) {
            if (mRequestListener != null) {
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<PlaytomicLevel>(
                                Playtomic.ERROR_INVALID_RATING_VALUE, 
                                "Invalid rating value (must be 1 - 10)."));
                return;
            }
        }
        
        if (Playtomic.isInternetActive()) {    

            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestRateFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestRateFailed();
                    }
                }
            });
            try {
                LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>(); 
                
                postData.put("levelid", levelId);
                postData.put("rating", String.valueOf(rating));   
                
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, RATE, postData);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                requestRateFailed(ex.getMessage());
            }
        }
        else {
            requestRateFailed();
        }
    }

    private void requestRateFinished(String response) {
        if (mRequestListener == null) {
            return;
        }
        
        try {        
            // we got a response of some kind
            //
            JSONObject json = new JSONObject(response);
            int status = json.getInt("Status");
            
            int errorCode = json.getInt("ErrorCode");
            
            // failed on the server side
            if (status == 1) {
                mRequestListener.onRequestFinished(new PlaytomicResponse<PlaytomicLevel>(true, errorCode));
            }
            else {
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<PlaytomicLevel>(errorCode, "Connectivity error. Server side"));
            }
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, ex.getMessage()));
        }
    }

    private void requestRateFailed() {
        requestRateFailed("");
    }
    
    private void requestRateFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }

    public void list(
            String mode,
            int page,
            int perPage,
            Boolean includeData,
            Boolean includeThumbs,
            LinkedHashMap<String, String> customFilter)    {
        
        list(mode, page, perPage, includeData, includeThumbs, customFilter, null, null);
    }

    public void list(
            String mode,
            int page,
            int perPage,
            Boolean includeData,
            Boolean includeThumbs,
            LinkedHashMap<String, String> customFilter,
            Date dateMin,
            Date dateMax) {
        
        if (Playtomic.isInternetActive()) {    

            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestListFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestListFailed();
                    }
                }
            });
            
            try {
                LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>(); 

                String modeSafe = mode == null ? "popular" : (mode.equals("newest") || mode.equals("popular") ? mode : "popular");
                String dataSafe = includeData ? "y" : "n";
                String thumbSafe = includeThumbs ? "y" : "n";
                SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                String dateMinSafe = dateMin == null ? "" : formatter.format(dateMin);
                String dateMaxSafe = dateMax == null ? "" : formatter.format(dateMax);

                int numFilters = customFilter == null ? 0 : customFilter.size();

                postData.put("mode", modeSafe);
                postData.put("page", String.valueOf(page));
                postData.put("perpage", String.valueOf(perPage));
                postData.put("thumbs", thumbSafe);
                postData.put("data", dataSafe);
                postData.put("datemin", dateMinSafe);
                postData.put("datemax", dateMaxSafe);
                
                if (numFilters > 0) {
                    int fieldNumber = 0;
                    
                    for (Map.Entry<String, String> entry : customFilter.entrySet()) {
                        String ckey = "ckey" + fieldNumber;
                        String cdata = "cdata" + fieldNumber;
                        postData.put(ckey, entry.getKey());
                        postData.put(cdata, entry.getValue());
                        fieldNumber++;
                    }
                }
                
                postData.put("filters", String.valueOf(numFilters));
                
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, LIST, postData);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                    requestListFailed(ex.getMessage());
            }
        }
        else {
                requestListFailed();
        }        
    }

    private void requestListFinished(String response) {
        if (mRequestListener == null) {
            return;
        }
        
        try {        
            // we got a response of some kind
            //
            JSONObject json = new JSONObject(response);
            int status = json.getInt("Status");
            
            // failed on the server side
            if (status != 1) {
                int errorCode = json.getInt("ErrorCode");
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<PlaytomicLevel>(errorCode, "Connectivity error. Server side"));
                return;
            }
            
            JSONObject data = json.getJSONObject("Data");
            JSONArray levels = data.getJSONArray("Levels");
            int numLevels = data.getInt("NumLevels");
            ArrayList<PlaytomicLevel> md = new ArrayList<PlaytomicLevel>();
                        
            for (int i = 0; i < levels.length(); i++) {
                JSONObject level = levels.getJSONObject(i);
                String levelId = level.getString("LevelId");
                addLevelToArray(level, levelId, md);
            }
            
            PlaytomicResponse<PlaytomicLevel> playtomicResponse 
                    = new PlaytomicResponse<PlaytomicLevel>(true, Playtomic.SUCCESS, md, numLevels);
        
            mRequestListener.onRequestFinished(playtomicResponse);
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, ex.getMessage()));
        }
    }

    private void requestListFailed() {
        requestListFailed("");
    }
    
    private void requestListFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }

    public void save(PlaytomicLevel level) {
        if (Playtomic.isInternetActive()) {    

            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestSaveFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestSaveFailed();
                    }
                }
            });
            
            try {
                LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>(); 
            
                postData.put("data", level.getData());
                postData.put("playerid", level.getPlayerId());
                postData.put("playername", level.getPlayerName());
                postData.put("playersource", Playtomic.BaseUrl());
                postData.put("name", level.getName());
                postData.put("nothumb", "y");
    
                LinkedHashMap<String, String> map = level.getCustomData();
                postData.put("customfields", String.valueOf(map.size()));
                
                int fieldNumber = 0;
                
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    String ckey = "ckey" + fieldNumber;
                    String cdata = "cdata" + fieldNumber;
                    postData.put(ckey, entry.getKey());
                    postData.put(cdata, entry.getValue());
                    fieldNumber++;
                }                
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, SAVE, postData);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                    requestSaveFailed(ex.getMessage());
            }
        }
        else {
            requestSaveFailed();
        }        
    }

    private void requestSaveFailed() {
        requestSaveFailed("");
    }
    
    private void requestSaveFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }

    private void requestSaveFinished(String response) {
        if (mRequestListener == null) {
            return;
        }
        
        try {        
            // we got a response of some kind
            //
            JSONObject json = new JSONObject(response);
            int status = json.getInt("Status");
            
            // failed on the server side
            if (status != 1) {
                int errorCode = json.getInt("ErrorCode");
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<PlaytomicLevel>(errorCode, "Connectivity error. Server side"));
                return;
            }
            JSONObject data = json.getJSONObject("Data");
            String levelId = data.getString("LevelId");
            ArrayList<PlaytomicLevel> md = new ArrayList<PlaytomicLevel>();

            md.add(new PlaytomicLevel(levelId));
            
            PlaytomicResponse<PlaytomicLevel> playtomicResponse 
                    = new PlaytomicResponse<PlaytomicLevel>(true, Playtomic.SUCCESS, md, 1);
        
            mRequestListener.onRequestFinished(playtomicResponse);
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<PlaytomicLevel>(Playtomic.ERROR, ex.getMessage()));
        }        
    }

    public void startLevel(String levelId) {
        Playtomic.Log().playerLevelStart(levelId);
    }

    public void retryLevel(String levelId) {
        Playtomic.Log().playerLevelRetry(levelId);
    }

    public void winLevel(String levelId) {
        Playtomic.Log().playerLevelWin(levelId);
    }

    public void quitLevel(String levelId) {
        Playtomic.Log().playerLevelQuit(levelId);
    }

    public void flagLevel(String levelId) {
        Playtomic.Log().playerLevelFlag(levelId);
    }

    private void addLevelToArray(JSONObject level, String levelId, ArrayList<PlaytomicLevel> md) throws Exception {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");        
        String playerId = level.optString("PlayerId");
        String playerName = URLDecoder.decode(level.optString("PlayerName"), "UTF-8");
        String playerSource = level.optString("PlayerSource");
        String name = URLDecoder.decode(level.optString("Name"), "UTF-8");
        String data = URLDecoder.decode(level.optString("Data"), "UTF-8");
        String thumb = level.optString("Thumb");
        int plays = level.optInt("Plays");
        int votes = level.optInt("Votes");
        int score = level.optInt("Score");
        double rating = level.optDouble("Rating");
        String relativeDate = level.optString("RDate");
        Date date = formatter.parse(level.optString("SDate"));

        LinkedHashMap<String, String> customData = new LinkedHashMap<String, String>();
        JSONObject returnedCustomData = level.optJSONObject("CustomData");
        
        if (returnedCustomData != null) {
            Iterator<?> itr = returnedCustomData.keys();                
            while (itr.hasNext()) {
                String key = (String)itr.next();
                String cvalue = URLDecoder.decode(returnedCustomData.getString(key), "UTF-8");
                cvalue = cvalue.replace("+", " ");
                customData.put(key, cvalue);
            }
        }        
        md.add(new PlaytomicLevel(
                        name,
                        playerName,
                        playerId,
                        playerSource,
                        data,
                        thumb,
                        votes,
                        plays,
                        rating,
                        score,
                        date,
                        relativeDate,
                        customData,
                        levelId));        
    }
}
