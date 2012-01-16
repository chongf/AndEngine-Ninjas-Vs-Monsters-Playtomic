//
// PlaytomicLog.java
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Handler;

public class PlaytomicLog {

    private static final long ONE_MINUTE = 60000;
    private static final long THIRTY_SECONDS = 30000;
    
    private String mTrackUrl;
    private String mSourceUrl;
    private TimerTask mPingTask;
    private final Handler mHandler = new Handler();
    private Timer mT;
    private int mPings;
    private int mViews;
    private int mPlays;
    private Boolean mFrozen;
    private List<String> mQueue;
    private List<String> mCustomMetrics;
    private List<String> mLevelCounters;
    private List<String> mLevelAverages;
    private List<String> mLevelRangeds;
    private Date mLastEventOccurence;

    public PlaytomicLog(int gameId, String gameGuid) {
        mLastEventOccurence = new Date();
        mSourceUrl = Playtomic.SourceUrl();
        mTrackUrl = "http://g" 
                    + gameGuid
                    + ".api.playtomic.com/tracker/q.aspx?swfid="
                    + gameId
                    + "&url=" 
                    + mSourceUrl 
                    + "&q=";
        mViews = 0;
        mPlays = 0;
        mPings = 0;
        mFrozen = false;
        mQueue = new ArrayList<String>();
        mCustomMetrics = new ArrayList<String>();
        mLevelCounters = new ArrayList<String>();
        mLevelAverages = new ArrayList<String>();
        mLevelRangeds = new ArrayList<String>();

        // first ping 1 minute, then 30 seconds
        //
        startTimer(ONE_MINUTE, THIRTY_SECONDS);
    }

    public void view() {
        sendEvent("v/" + (mViews + 1), true);
    }

    public void play() {
        sendEvent("p/" + (mPlays + 1), true);
    }

    private void pingServer() {
        mPings++;
        sendEvent("t/" + (mPings == 1 ? "y" : "n") + "/" + mPings, true);
    }

    // custom metrics
    public void customMetric(String name, String group, Boolean unique) {
        if(group == null)
            group = "";
        
        if(unique == true) {
            if(mCustomMetrics.contains(name)) {
                return;
            }            
            mCustomMetrics.add(name);
        }        
        sendEvent("c/" + clean(name) + "/" + clean(group), false);
    }
    
    // links
    public void link(String url, String name, String group, int unique, int total, int fail) { 
        sendEvent("l/" + clean(name) 
                    + "/" + clean(group) 
                    + "/" + clean(url) 
                    + "/" + unique
                    + "/" + total
                    + "/" + fail, 
                false);
    }

    public void heatmap(String name, String group, int x, int y) {
        sendEvent("h/" + clean(name) 
                    + "/" + clean(group) 
                    + "/" + x 
                    + "/" + y,
                false);
    }

    // level metrics
    public void levelCounterMetric(String name, int levelnumber, Boolean unique) {
        levelCounterMetric(name, String.valueOf(levelnumber), unique);
    }
    
    public void levelCounterMetric(String name, String level, Boolean unique) {
        String nameclean = clean(name);
        String levelclean = clean(level);
        
        if(unique) {
            String key = nameclean + "." + levelclean;
               
            if(mLevelCounters.contains(key)) {
                return;
            }        
            mLevelCounters.add(key);
        }        
        sendEvent("lc/" + nameclean + "/" + levelclean, false); 
    }

    public void levelRangedMetric(String name, int levelnumber, long trackvalue, Boolean unique) {
        levelRangedMetric(name, String.valueOf(levelnumber), trackvalue, unique);
    }
    
    public void levelRangedMetric(String name, String level, long trackvalue, Boolean unique) {
        String nameclean = clean(name);
        String levelclean = clean(level);

        if(unique) {
            String key = nameclean + "." + levelclean + "." + trackvalue;
        
            if(mLevelRangeds.contains(key)) {
                return;
            }        
            mLevelRangeds.add(key);
        }        
        sendEvent("lr/" + nameclean + "/" + levelclean + "/" + trackvalue, false); 
    }    
    
    public void levelAverageMetric(String name, int levelnumber, long value, Boolean unique) {
        levelAverageMetric(name, String.valueOf(levelnumber), value, unique);
    }

    public void levelAverageMetric(String name, String level, long value, Boolean unique) {
        String nameclean = clean(name);
        String levelclean = clean(level);

        if(unique) {
            String key = nameclean + "." + levelclean;

            if(mLevelAverages.contains(key)) {
                return;
            }
            mLevelAverages.add(key);
        }
        sendEvent("la/" + nameclean + "/" + levelclean + "/" + value, false); 
    }

    //player levels
    public void playerLevelStart(String levelid) {
        sendEvent("pls/" + clean(levelid), false);
    }

    public void playerLevelWin(String levelid) {
        sendEvent("plw/" + clean(levelid), false); 
    }

    public void playerLevelRetry(String levelid) {
        sendEvent("plr/" + clean(levelid), false); 
    }

    public void playerLevelQuit(String levelid) {
        sendEvent("plq/" + clean(levelid), false);
    }

    public void playerLevelFlag(String levelid) {
        sendEvent("plf/" + clean(levelid), false);
    }
    
    // misc
    public void freeze() {
        if (!mFrozen) {
            mFrozen = true;
            mPingTask.cancel();
            mT.cancel();
            mT.purge();
        }
    }

    public void unfreeze() {
        if (mFrozen) {
            mFrozen = false;
            
            // if 20 minutes have past
            //
            if (getSecondsElapsed() < 1200) {
                view();
            }
            else {
                forceSend();
            }
            if (mPings == 0) {
                startTimer(ONE_MINUTE, THIRTY_SECONDS);
            }
            else {
                startTimer(THIRTY_SECONDS, THIRTY_SECONDS);
            }
        }
    }

    public void forceSend() {
        if(mQueue.size() > 0) {
            massQueue();
        }
    }
         
    public void sendEvent(String event, Boolean commit)    {
        mLastEventOccurence = new Date();
        mQueue.add(event);
        
        if(mFrozen || !commit) {
            return;
        }
        massQueue();
    }

    public void increaseViews()    {
        mViews++;    
    }

    public void increasePlays()    {
        mPlays++;
    }
    
    private void startTimer(long delay, long period) {
        // timer
        //
        mPingTask = new TimerTask() {
            public void run() {
                mHandler.post(new Runnable() {
                    public void run() {
                        pingServer();
                    }
                });
            }
        };
        mT = new Timer();
        mT.schedule(mPingTask, delay, period);
    }
    
    private long getSecondsElapsed() {
        Calendar earlier = Calendar.getInstance();
        Calendar later = Calendar.getInstance();

        earlier.setTime(mLastEventOccurence);
        later.setTime(new Date());

        return (earlier.getTimeInMillis() - later.getTimeInMillis()) / 1000;        
    }
    
    private void massQueue() {
        PlaytomicLogRequest request = new PlaytomicLogRequest(mTrackUrl);
        request.setRequestListener(mRequestListener);
        request.massQueue(mQueue);
        mQueue.removeAll(mQueue);        
    }

    private String clean(String string) {    
        string = string.replace("/","\\");
        string = string.replace("~","-");
        string = URLUTF8Encoder.encode(string);
        return string;
    }

    private PlaytomicRequestListener<String> mRequestListener;

    public void setRequestListener(PlaytomicRequestListener<String> requestListener) {
        mRequestListener = requestListener;
    }    
}
