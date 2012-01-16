//
// PlaytomicResponse.java
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
import java.util.LinkedHashMap;

public class PlaytomicResponse<T> {

    private Boolean mSuccess;
    private int mErrorCode = Playtomic.ERROR;
    private ArrayList<T> mData = null;
    private LinkedHashMap<String, T> mMap = null;
    private int mNumResults = 0;
    private String mErrorMessage = "";
    private T mObject;
    
    public Boolean getSuccess() {
        return mSuccess;
    }

    public int getErrorCode() {
        return mErrorCode;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }
    
    public ArrayList<T> getData() {
        return mData;
    }

    public LinkedHashMap<String, T> getMap() {
        return mMap;
    }

    public int getNumResults() {
        return mNumResults;
    }
    
    public T getObject() {
        return mObject;
    }
    
    public void setObject(T object) {
        mObject = object;
    }
    
    public PlaytomicResponse(Boolean success, int errorCode) {
        mSuccess = success;
        mErrorCode = errorCode;
    }
    
    public PlaytomicResponse(Boolean success, int errorCode, ArrayList<T> data, int numResults) {
        mSuccess = success;
        mErrorCode = errorCode;
        mData = data;
        mNumResults = numResults;
    }

    public PlaytomicResponse(Boolean success, int errorCode, LinkedHashMap<String, T> map) {
        mSuccess = success;
        mErrorCode = errorCode;
        mMap = map;
    }
    
    public PlaytomicResponse(Boolean success, int errorCode, T object) {
        mSuccess = success;
        mErrorCode = errorCode;
        mObject = object;
    }

    public PlaytomicResponse(int errorCode, String errorMessage) {
        mSuccess = false;
        mErrorCode = errorCode;
        mErrorMessage = errorMessage;
    }
}
