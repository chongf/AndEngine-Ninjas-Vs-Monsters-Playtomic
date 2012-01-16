//
// PlaytomicLogRequest.java
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

import java.util.LinkedHashMap;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;

public class PlaytomicLogRequest {

    private static final String PLAYTOMIC_QUEUE_SIZE = "playtomic.queue.size";
    private static final String PLAYTOMIC_QUEUE_BYTES = "playtomic.queue.bytes";
    private static final String PLAYTOMIC_QUEUE_ITEM = "playtomic.queue.item_";
    private static final String PLAYTOMIC_QUEUE_READY = "playtomic.queue.ready";

    private String mData = "";
    private String mTrackUrl = "";
    private String mFullUrl = "";
    
    public String getData() {
        return mData;
    }
    
    public void setData(String data) {
        this.mData = data;
    }
    
    public String getTrackUrl() {
        return mTrackUrl;
    }
    
    public void setTrackUrl(String trackUrl) {
        this.mTrackUrl = trackUrl;
    }

    public PlaytomicLogRequest(String url) {
        mTrackUrl = url;
        mData = "";
    }

    public void queueEvent(String event) {
        if(mData.length() == 0) {
            mData = event;
        }
        else {
            mData += "~";
            mData += event;
        }
    }

    public void massQueue(List<String>eventqueue) {
        while(eventqueue.size() > 0) {
            String event = eventqueue.get(0);
            eventqueue.remove(0);
            
            if(mData.length() == 0) {
                mData = event;
            } 
            else {
                mData += "~";
                mData += event;
                
                if(mData.length() > 300) {
                    send();                
                    PlaytomicLogRequest request = new PlaytomicLogRequest(mTrackUrl);
                    request.massQueue(eventqueue);
                    return;
                }
            } 
        }        
        send();
    }

    public void send() {
        if (Playtomic.isInternetActive()) {    
            mFullUrl = mTrackUrl += mData;
            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestFinished();
                    }            
                    else {
                        requestFailed();
                    }
                }
            });
            request.execute(mFullUrl);
        }
        else  {
            requestFailed();
        }
    }

    private void requestFinished() {
        // try to send data we have failed to send in a previous call to send
        //    
        Context ctx = Playtomic.getContext();
        SharedPreferences settings = ctx.getSharedPreferences(Playtomic.PLAYTOMIC_CACHE, 0);

        int queueSize = settings.getInt(PLAYTOMIC_QUEUE_SIZE, 0);
        
        if (queueSize > 0) {
            // we send only one message by call
            //
            Boolean ready = settings.getBoolean(PLAYTOMIC_QUEUE_READY, false);
            if (ready) {
                SharedPreferences.Editor editor = settings.edit();        
                editor.putBoolean(PLAYTOMIC_QUEUE_READY, false);                
                // this is managed as FILO list
                //
                String key = PLAYTOMIC_QUEUE_ITEM + queueSize;
                String savedData = settings.getString(key, "");
                queueSize--;                
                editor.putInt(PLAYTOMIC_QUEUE_SIZE, queueSize);   
                editor.remove(key);
                
                PlaytomicLogRequest request = new PlaytomicLogRequest(mTrackUrl);
                request.queueEvent(savedData);
                request.send();
                editor.commit();
            }
            else {
                SharedPreferences.Editor editor = settings.edit();        
                editor.putBoolean(PLAYTOMIC_QUEUE_READY, true);     
                editor.commit();
            }
        }
        
        if (mRequestListener != null) {
            LinkedHashMap<String, String> log = new LinkedHashMap<String, String>();
            log.put("Url", mFullUrl);
            log.put("Result", "true");
            log.put("Queue size", String.valueOf(queueSize));
            mRequestListener.onRequestFinished(new PlaytomicResponse<String>(true, Playtomic.SUCCESS, log));
        }
    }

    private void requestFailed() {
        // save data to send later
        //
        // this is managed as FILO list
        //    
        Context ctx = Playtomic.getContext();
        SharedPreferences settings = ctx.getSharedPreferences(Playtomic.PLAYTOMIC_CACHE, 0);

        int queueSize = settings.getInt(PLAYTOMIC_QUEUE_SIZE, 0);
        int queueBytes = settings.getInt(PLAYTOMIC_QUEUE_BYTES, 0);
        
        if (queueBytes < Playtomic.OfflineQueueMaxSize()) {        
            queueSize++;
            SharedPreferences.Editor editor = settings.edit();        
            editor.putInt(PLAYTOMIC_QUEUE_SIZE, queueSize);
            String key = PLAYTOMIC_QUEUE_ITEM + queueSize;
            String dataToSave = mData;
            if (!dataToSave.contains("&date=")) {
                long seconds = System.currentTimeMillis() / 1000; // seconds since 1-1-1970 UTC
                dataToSave += "&date=" + seconds;
            }
            editor.putString(key, dataToSave);
            queueBytes += dataToSave.length() * 2;
            editor.putInt(PLAYTOMIC_QUEUE_BYTES, queueBytes);
            editor.putBoolean(PLAYTOMIC_QUEUE_READY, true);
            editor.commit();
        }
        
        if (mRequestListener != null) {
            LinkedHashMap<String, String> log = new LinkedHashMap<String, String>();
            log.put("Url", mFullUrl);
            log.put("Result", "false");
            log.put("Queue size", String.valueOf(queueSize));
            log.put("Queue bytes", String.valueOf(queueBytes));
            mRequestListener.onRequestFinished(new PlaytomicResponse<String>(false, Playtomic.ERROR, log));
        }
    }

    private PlaytomicRequestListener<String> mRequestListener;

    public void setRequestListener(PlaytomicRequestListener<String> requestListener) {
        mRequestListener = requestListener;
    }
}
