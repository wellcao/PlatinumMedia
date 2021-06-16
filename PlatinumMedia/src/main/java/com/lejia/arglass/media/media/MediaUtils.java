package com.lejia.arglass.media.media;

import android.content.Context;
import android.text.format.DateUtils;

import java.io.File;
import java.util.Formatter;
import java.util.Locale;

/**
 * Created by huzongyao on 2018/6/29.
 */

public class MediaUtils {

    public static final String EXTRA_MEDIA_INFO = "EXTRA_MEDIA_INFO";
    private static StringBuilder formatBuilder = new StringBuilder();
    private static Formatter formatter = new Formatter(formatBuilder, Locale.getDefault());


    /**
     * Init exo media
     *
     * @param context context
     */
    public static void configureExoMedia(Context context) {
        // Registers the media sources to use the OkHttp client instead of the standard Apache one
        // Note: the OkHttpDataSourceFactory can be found in the ExoPlayer extension library `extension-okhttp`
        /*ExoMedia.setDataSourceFactoryProvider(new ExoMedia.DataSourceFactoryProvider() {

            @NonNull
            @Override
            public DataSource.Factory provide(@NonNull String userAgent, @Nullable TransferListener listener) {
                if (instance == null) {
                    // Updates the network data source to use the OKHttp implementation
                    DataSource.Factory upstreamFactory = new OkHttpDataSourceFactory(new OkHttpClient(), userAgent, listener);

                    // Adds a cache around the upstreamFactory
                    Cache cache = new SimpleCache(new File(getCacheDirectory(context), "ExoMediaCache"),
                            new LeastRecentlyUsedCacheEvictor(50 * 1024 * 1024));
                    instance = new CacheDataSourceFactory(cache, upstreamFactory, CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR);
                }
                return instance;
            }

            @Nullable
            private CacheDataSourceFactory instance;

        });*/
    }

    private static File getCacheDirectory(Context context) {
        File cacheDir = context.getExternalCacheDir();
        if (cacheDir == null) {
            cacheDir = context.getCacheDir();
        }
        return cacheDir;
    }

    /**
     * Formats the specified milliseconds to a human readable format
     * in the form of (Hours : Minutes : Seconds).  If the specified
     * milliseconds is less than 0 the resulting format will be
     * "--:--" to represent an unknown time
     *
     * @param milliseconds The time in milliseconds to format
     * @return The human readable time
     */
    public static String formatMs(long milliseconds) {
        if (milliseconds < 0) {
            return "--:--";
        }

        long seconds = (milliseconds % DateUtils.MINUTE_IN_MILLIS) / DateUtils.SECOND_IN_MILLIS;
        long minutes = (milliseconds % DateUtils.HOUR_IN_MILLIS) / DateUtils.MINUTE_IN_MILLIS;
        long hours = (milliseconds % DateUtils.DAY_IN_MILLIS) / DateUtils.HOUR_IN_MILLIS;

        formatBuilder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        }

        return formatter.format("%02d:%02d", minutes, seconds).toString();
    }

    /*
     * 将时分秒转为秒数
     * */
    public static long formatTurnSecond(String time) {
        int index1 = time.indexOf(":");
        int index2 = time.indexOf(":", index1 + 1);
        int hh = Integer.parseInt(time.substring(0, index1));
        int mi = Integer.parseInt(time.substring(index1 + 1, index2));
        int ss = Integer.parseInt(time.substring(index2 + 1));
        return hh * 60 * 60 + mi * 60 + ss;
    }
}
