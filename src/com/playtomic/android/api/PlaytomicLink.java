//
// PlaytomicLink.java
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

public class PlaytomicLink {

    private LinkedHashMap<String, String> mClicks = new LinkedHashMap<String, String>();

    public void trac(String url, String name, String group) {
        int unique = 0;
        int bunique = 0;
        int total = 0;
        int btotal = 0;

        String key = url + "." + name;
        String baseurl = url.replace("http://", "");
        baseurl = baseurl.replace("https://", "");

        int i = baseurl.indexOf("/");

        if (i >= 0) {
            baseurl = baseurl.substring(i);
        }

        String baseurlname = baseurl;
        int www = baseurlname.indexOf("www.");

        if (www == 0) {
            baseurlname = baseurlname.substring(4);
        }

        if (mClicks.containsKey(key)) {
            total = 1;
        } else {
            total = 1;
            unique = 1;
            mClicks.put(key, key);
        }

        if (mClicks.containsKey(baseurlname)) {
            btotal = 1;
        } else {
            btotal = 1;
            bunique = 1;
            mClicks.put(baseurlname, baseurlname);
        }

        Playtomic.Log().link(baseurl, baseurlname, "DomainTotals", bunique, btotal, 0);

        Playtomic.Log().link(url, name, group, unique, total, 0);

        Playtomic.Log().forceSend();
    }
}
