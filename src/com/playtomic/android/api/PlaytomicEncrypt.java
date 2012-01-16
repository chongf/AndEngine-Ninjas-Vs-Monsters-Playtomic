//
// PlaytomicEncrypt.java
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

import java.security.MessageDigest;
import org.apache.commons.codec.binary.Base64;
//import android.util.Base64;

public class PlaytomicEncrypt {

   public static String md5(String value) throws Exception {
       MessageDigest algorithm = MessageDigest.getInstance("MD5");
       algorithm.reset();
       algorithm.update(value.getBytes());
       byte messageDigest[] = algorithm.digest();

       StringBuffer hexString = new StringBuffer();

       for (int i = 0; i < messageDigest.length; i++) {
           hexString.append(Integer.toString((messageDigest[i] & 0xff) + 0x100, 16).substring(1));
       }

       return hexString.toString();
   }

   /**
    * Encodes the string 'in' using 'flags'.  Asserts that decoding
    * gives the same string.  Returns the encoded string.
    */
   public static String encodeToString(String in) throws Exception {
       /*int[] flagses = { Base64.DEFAULT,
               Base64.NO_PADDING,
               Base64.NO_WRAP,
               Base64.NO_PADDING | Base64.NO_WRAP,
               Base64.CRLF,
               Base64.URL_SAFE };*/
       //String b64 = Base64.encodeToString(in.getBytes(), Base64.NO_WRAP);
       String b64 = new String(Base64.encodeBase64(in.getBytes()));
       return b64;
   }
}