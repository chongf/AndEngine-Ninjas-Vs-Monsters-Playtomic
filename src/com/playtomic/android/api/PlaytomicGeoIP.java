//
// PlaytomicGeoIP.java
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

import org.json.JSONObject;

public class PlaytomicGeoIP {

    private PlaytomicRequestListener<String> mRequestListener;

    public void setRequestListener(PlaytomicRequestListener<String> requestListener) {
        mRequestListener = requestListener;
    }

    private static String SECTION;
    private static String LOOKUP;
    
    static void Initialise(String apiKey) throws Exception {
        SECTION = PlaytomicEncrypt.md5("geoip-" + apiKey);
        LOOKUP = PlaytomicEncrypt.md5("geoip-lookup-" + apiKey);
    }
    
    public void load() {
        if (Playtomic.isInternetActive()) {    
            
            PlaytomicHttpRequest request = new PlaytomicHttpRequest();
            request.setHttpRequestListener(new PlaytomicHttpRequestListener() {
                public void onRequestFinished(PlaytomicHttpResponse playtomicHttpResponse) {
                    if (playtomicHttpResponse.getSuccess()) {
                        requestFinished(playtomicHttpResponse.getData());
                    }            
                    else {
                        requestFailed();
                    }
                }
            });
            try {
                PlaytomicHttpRequestUrl url = PlaytomicHttpRequest.prepare(SECTION, LOOKUP, null);
                request.addPostData("data", url.getData());
                request.execute(url.getUrl());
            }
            catch (Exception ex) {
                requestFailed(ex.getMessage());
            }
        }
        else  {
            requestFailed();
        }
    }        
    
    private void requestFinished(String response) {
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
            
            md.put("Code", data.getString("Code"));
            md.put("Name", data.getString("Name"));
            
            PlaytomicResponse<String> playtomicResponse = new PlaytomicResponse<String>(true, Playtomic.SUCCESS, md);
            
            mRequestListener.onRequestFinished(playtomicResponse);
        }
        catch (Exception ex) {
            mRequestListener.onRequestFinished(new PlaytomicResponse<String>(Playtomic.ERROR, ex.getMessage()));
        }
    }

    private void requestFailed() {
        requestFailed("");
    }
    
    private void requestFailed(String message) {
        // failed on the client / connectivity side
        //
        if (mRequestListener == null) {
            return;
        }
        mRequestListener.onRequestFinished(
                new PlaytomicResponse<String>(Playtomic.ERROR, "Connectivity error. Client side. " + message));
    }    
}
