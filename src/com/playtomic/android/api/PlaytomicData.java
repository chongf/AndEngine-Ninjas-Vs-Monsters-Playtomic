//
// PlaytomicData.java
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

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.json.JSONObject;

public class PlaytomicData {

    private PlaytomicRequestListener<String> mRequestListener;

    public void setRequestListener(PlaytomicRequestListener<String> requestListener) {
        mRequestListener = requestListener;
    }
    
    private static String SECTION;
    private static String VIEWS;
    private static String PLAYS;
    private static String PLAYTIME;
    private static String CUSTOMMETRIC;
    private static String LEVELCOUNTERMETRIC;
    private static String LEVELAVERAGEMETRIC;
    private static String LEVELRANGEDMETRIC;
    
    static void Initialise(String apikey) throws Exception {
        SECTION = PlaytomicEncrypt.md5("data-" + apikey);
        VIEWS = PlaytomicEncrypt.md5("data-views-" + apikey);
        PLAYS = PlaytomicEncrypt.md5("data-plays-" + apikey);
        PLAYTIME = PlaytomicEncrypt.md5("data-playtime-" + apikey);
        CUSTOMMETRIC = PlaytomicEncrypt.md5("data-custommetric-" + apikey);
        LEVELCOUNTERMETRIC = PlaytomicEncrypt.md5("data-levelcountermetric-" + apikey);
        LEVELAVERAGEMETRIC = PlaytomicEncrypt.md5("data-levelaveragemetric-" + apikey);
        LEVELRANGEDMETRIC = PlaytomicEncrypt.md5("data-levelrangedmetric-" + apikey);
    }
    
    public void views() {
        general(VIEWS, "Views", 0, 0, 0);
    }

    public void views(int month, int year) {
        general(VIEWS, "Views", 0, month, year);
    }

    public void views(int day, int month, int year) {
        general(VIEWS, "Views", day, month, year);
    }

    public void plays()    {
        general(PLAYS, "Plays", 0, 0, 0);
    }

    public void plays(int month, int year) {
        general(PLAYS, "Plays", 0, month, year);
    }

    public void plays(int day , int month , int year) {
        general(PLAYS, "Plays", day, month, year);
    }

    public void playtime() {
        general(PLAYTIME, "PlayTime", 0, 0, 0);
    }

    public void playtime(int month, int year) {
        general(PLAYTIME, "PlayTime", 0, month, year);
    }

    public void playtime(int day, int month, int year) {
        general(PLAYTIME, "PlayTime", day, month, year);
    }

    public void general(String action, String type, int day, int month, int year) {
        
        LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
        
        postData.put("type", type);
        postData.put("day", String.valueOf(day));
        postData.put("month", String.valueOf(month));
        postData.put("year", String.valueOf(year));
        
        getData(action, postData);
    }

    // custom metrics
    public void customMetric(String name) {
        customMetric(name, 0, 0, 0);
    }

    public void customMetric(String name, int month, int year) {
        customMetric(name, 0, month, year);
    }

    public void customMetric(String name, int day, int month, int year) {
        
        LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
        
        postData.put("day", String.valueOf(day));
        postData.put("month", String.valueOf(month));
        postData.put("year", String.valueOf(year));
        postData.put("metric", clean(name));
        
        getData(CUSTOMMETRIC, postData);
    }

    // level counters
    public void levelCounterMetric(String name, String level) {
        levelMetric(LEVELCOUNTERMETRIC, name, level, 0, 0, 0);
    }

    public void levelCounterMetric(String name, String level, int month, int year) {
        levelMetric(LEVELCOUNTERMETRIC, name, level, 0, month, year);
    }

    public void levelCounterMetric(String name, String level, int day, int month, int year) {
        levelMetric(LEVELCOUNTERMETRIC, name, level, day, month, year);
    }

    public void levelCounterMetric(String name, int level) {
        levelMetric(LEVELCOUNTERMETRIC, name, String.valueOf(level), 0, 0, 0);
    }

    public void levelCounterMetric(String name, int level, int month, int year) {
        levelMetric(LEVELCOUNTERMETRIC, name, String.valueOf(level), 0, month, year);
    }

    public void levelCounterMetric(String name, int level, int day, int month, int year) {
        levelMetric(LEVELCOUNTERMETRIC, name, String.valueOf(level), day, month, year);
    }

    // level averages
    public void levelAverageMetric(String name, String level) {
        levelMetric(LEVELAVERAGEMETRIC, name, level, 0, 0, 0);
    }

    public void levelAverageMetric(String name, String level, int month, int year) {
        levelMetric(LEVELAVERAGEMETRIC, name, level, 0, month, year);
    }

    public void levelAverageMetric(String name, String level, int day, int month, int year) {
        levelMetric(LEVELAVERAGEMETRIC, name, level, day, month, year);
    }

    public void levelAverageMetric(String name, int level) {
        levelMetric(LEVELAVERAGEMETRIC, name, String.valueOf(level), 0, 0, 0);
    }

    public void levelAverageMetric(String name, int level, int month, int year) {
        levelMetric(LEVELAVERAGEMETRIC, name, String.valueOf(level), 0, month, year);
    }

    public void levelAverageMetric(String name, int level, int day, int month, int year) {
        levelMetric(LEVELAVERAGEMETRIC, name, String.valueOf(level), day, month, year);
    }

    // level ranged
    public void levelRangedMetric(String name, String level) {
        levelMetric(LEVELRANGEDMETRIC, name, level, 0, 0, 0);
    }

    public void levelRangedMetric(String name, String level, int month, int year) {
        levelMetric(LEVELRANGEDMETRIC, name, level, 0, month, year);
    }

    public void levelRangedMetric(String name, String level, int day, int month, int year) {
        levelMetric(LEVELRANGEDMETRIC, name, level, day, month, year);
    }

    public void levelRangedMetric(String name, int level) {
        levelMetric(LEVELRANGEDMETRIC, name, String.valueOf(level), 0, 0, 0);
    }

    public void levelRangedMetric(String name, int level, int month, int year) {
        levelMetric(LEVELRANGEDMETRIC, name, String.valueOf(level), 0, month, year);
    }

    public void levelRangedMetric(String name, int level, int day, int month, int year) {
        levelMetric(LEVELRANGEDMETRIC, name, String.valueOf(level), day, month, year);
    }

    public void levelMetric(String action, String name, String level, int day, int month, int year) {
        
        LinkedHashMap<String, String> postData = new LinkedHashMap<String, String>();
        
        postData.put("metric", name);
        postData.put("level", level);
        postData.put("day", String.valueOf(day));
        postData.put("month", String.valueOf(month));
        postData.put("year", String.valueOf(year));
        
        getData(action, postData);
    }

    // get data
    public void getData(String action, LinkedHashMap<String, String> postData) {
        if (Playtomic.isInternetActive()) {    
            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestGetDataFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestGetDataFailed();
                    }
                }
            });
            try {
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, action, postData);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                requestGetDataFailed(ex.getMessage());
            }
        }
        else  {
            requestGetDataFailed();
        }
    }

    private void requestGetDataFinished(String response) {
        if (mRequestListener == null) {
            return;
        }
        
        try {        
            // we got a response of some kind
            //
            JSONObject json = new JSONObject(response);
            int status = json.getInt("Status");
            
            // failed on the server side
            if(status != 1)
            {
                int errorCode = json.getInt("ErrorCode");
                mRequestListener.onRequestFinished(
                        new PlaytomicResponse<String>(errorCode, "Connectivity error. Server side"));
                return;
            }
            
            JSONObject data = json.getJSONObject("Data");
            LinkedHashMap<String, String> md = new LinkedHashMap<String, String>();    
            Iterator<?> itr = data.keys();
            while (itr.hasNext()) {
                String key = (String)itr.next();
                md.put(key, data.getString(key));
            }
            
            PlaytomicResponse<String> playtomicResponse = new PlaytomicResponse<String>(true, Playtomic.SUCCESS, md);
            
            mRequestListener.onRequestFinished(playtomicResponse);
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<String>(Playtomic.ERROR, ex.getMessage()));
        }
    }

    private void requestGetDataFailed() {
        requestGetDataFailed("");
    }
    
    private void requestGetDataFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<String>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }

    private String clean(String string) {    
        string = string.replace("/","\\");
        string = string.replace("~","-");
        string = URLUTF8Encoder.encode(string);
        return string;
    }    
}
