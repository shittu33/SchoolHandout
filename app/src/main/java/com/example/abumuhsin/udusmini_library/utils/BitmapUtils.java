/*
Copyright 2012 Aphid Mobile

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package com.example.abumuhsin.udusmini_library.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

public class BitmapUtils {
    private static final String TAG = "bitmapDebug";

    private BitmapUtils() {
    }

    public static Drawable getDrawableFromBitmap(Context context, Bitmap bitmap) {
        return new BitmapDrawable(context.getResources(), bitmap);
    }

    public static final byte[] EMPTY_BYTES = new byte[0];

    public static final Charset UTF_8 = Charset.forName("UTF-8");

    public static void close(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (IOException e) {

        }
    }

    public static byte[] readData(InputStream input) {
        if (input == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        int capacity = 0;
        try {
            capacity = input.available();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] buf = new byte[1024];
        int len = -1;

        try {
            while ((len = input.read(buf)) != -1) {
                output.write(buf, 0, len);
            }


            return output.toByteArray();
        } catch (IOException e) {
            Log.e(TAG, "Failed to readData");
            return null;
        } finally {
            close(input);
        }
    }

    public static String readString(InputStream input) {
        if (input == null) {
            return null;
        }
        return readString(new InputStreamReader(input, UTF_8));
    }

    public static String readString(Reader reader) {
        if (reader == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        char[] buf = new char[1024];
        int len = -1;

        try {
            while ((len = reader.read(buf)) != -1) {
                builder.append(buf, 0, len);
            }

            return builder.toString();
        } catch (IOException e) {
            Log.e(TAG, "Failed to readString");
            return null;
        } finally {
            close(reader);
        }
    }

    public static Bitmap readBitmap(InputStream input) {
        if (input == null) {
            return null;
        }

        try {
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e(TAG, "Failed to read bitmap");
            return null;
        } finally {
            close(input);
        }
    }

    public static Bitmap readBitmap(AssetManager manager, String name) {
        try {
            return readBitmap(manager.open(name));
        } catch (IOException e) {
            Log.e(TAG, "Failed to read bitmap '%s' from assets" + name);
            return null;
        }
    }
}
