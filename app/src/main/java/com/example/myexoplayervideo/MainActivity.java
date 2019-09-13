package com.example.myexoplayervideo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultControlDispatcher;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.Format;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.database.DatabaseProvider;
import com.google.android.exoplayer2.database.ExoDatabaseProvider;
import com.google.android.exoplayer2.offline.ActionFileUpgradeUtil;
import com.google.android.exoplayer2.offline.DefaultDownloadIndex;
import com.google.android.exoplayer2.offline.DefaultDownloaderFactory;
import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloadRequest;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.source.ClippingMediaSource;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.LoopingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.MergingMediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.SingleSampleMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.MimeTypes;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private Button btDown;
    private ExoDatabaseProvider databaseProvider;
    private SimpleCache downloadCache;
    private DefaultHttpDataSourceFactory httpDataSourceFactory;
    private DownloadManager downloadManager;
    //    private DownloadTracker downloadTracker;
    private static final String TAG = "DemoApplication";
    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //1.默认的播放View
    private PlayerView playerView;
    private ImageButton imageButton;

    //2.您可以ExoPlayer使用创建实例ExoPlayerFactory。工厂提供了一系列方法来创建ExoPlayer具有不同
    //级别自定义的实例。对于绝大多数用例，ExoPlayerFactory.newSimpleInstance应该使用其中一种 方法。
    //这些方法返回SimpleExoPlayer
    private SimpleExoPlayer player = null;

    //3.控制音频
    private DefaultControlDispatcher defaultControlDispatcher = null;
    private MediaSource videoSource = null;//播放资源
    private DataSource.Factory dataSourceFactory = null;//生成用于创建加载媒体数据的数据源实例

    //4.播放资源：
    private String videoPath = null;
    private Uri videoUri = null;


    //5.视频轨道
    private TrackSelection.Factory videoTrackSelectionFactory = null;
    private DefaultTrackSelector trackSelector = null;
    private DefaultBandwidthMeter.Builder bandwidthMeter = null; //new 一个实例作用为在播放期间测量带宽。 如果不需要，可以为null


    //4.有三个参数分为播放速度speed ：播放速率  pitch：声调的变化（音频音高缩放因子） skipSilence：是否跳过音频流中的静音
    private PlaybackParameters playbackParameters = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置为无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置为全屏模式
        setContentView(R.layout.activity_main);
        verifyStoragePermissions(MainActivity.this);
        //将背景图与状态栏融合
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        btDown = findViewById(R.id.bt_download);


        playerView = findViewById(R.id.exo_play_view);
        imageButton = findViewById(R.id.exo_screen);
        //DefaultTrackSelector用来选择轨道，
        // 我们把AdaptiveTrackSelection.Factory传入DefaultTrackSelector的构造函数，
        // 这样DefaultTrackSelector就可以选择自适应的轨道了。
        bandwidthMeter = new DefaultBandwidthMeter.Builder(MainActivity.this);

        //创建轨道选择工厂
        videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
        // 创建轨道选择实例
        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        //可以这样用ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector())
        //在下面一行面的代码中，我们传入了默认的渲染工厂（DefaultRenderersFactory），
        // 默认的轨道选择器（DefaultTrackSelector）和默认的加载控制器（DefaultLoadControl），
        // 然后把返回的播放器实例赋值给成员变量player。
        trackSelector.setParameters(
                trackSelector
                        .buildUponParameters()
                        .setMaxVideoSizeSd()
                        .setPreferredAudioLanguage("deu"));

        player = ExoPlayerFactory.newSimpleInstance(MainActivity.this, trackSelector, new DefaultLoadControl());
        //默认公共静态最终PlaybackParameters默认值
        //默认回放参数:实时回放，不修改音高，不跳音。
        //playbackParameters = PlaybackParameters.DEFAULT;
        //自定义
        playbackParameters = new PlaybackParameters(2.0f, 1.0f);

        player.setPlaybackParameters(playbackParameters);

        //将播放器绑定到视图非常简单：
        //如果您需要在播放器控制细粒度的控制和Surface 在其上渲染视频，
        // 可以设置玩家的目标SurfaceView， TextureView，SurfaceHolder或
        // Surface直接使用SimpleExoPlayer的 setVideoSurfaceView，setVideoTextureView，
        // setVideoSurfaceHolder和 setVideoSurface分别的方法。
        // 您还可以将其PlayerControlView用作独立组件，或实现自己的播放控件，
        // 直接与播放器进行交互。SimpleExoPlayer的addTextOutput和 addMetadataOutput方法
        // 可用于在回放期间接收字幕和ID3元数据。
        playerView.setShowBuffering(PlayerView.SHOW_BUFFERING_ALWAYS);
        playerView.setPlayer(player);

        //获取播放资源
        videoPath = "http://transcoding.ugc.snslearn.com/61b5a9693b73fcd17fd03520fd30d5fb.mp4";
        videoUri = Uri.parse(videoPath);

        //用于加载媒体数据的数据源实例。注意这里还可以放第三个参数：DefaultBandwidthMeter的
        dataSourceFactory = new DefaultDataSourceFactory(MainActivity.this,
                new DefaultHttpDataSourceFactory(Util.getUserAgent(MainActivity.this, "MyExoPlayerVideo")));


        // 这是代表要播放的媒体的媒体资源.
        videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory, new Mp4ExtractorsFactory()).setTag("tgw")
                .createMediaSource(videoUri);

        //循环播放，播放视频两次。
        //注意:要无限循环MediaSource，通常最好使用Player.setRepeatMode(int)而不是这个类。
        LoopingMediaSource loopingSource = new LoopingMediaSource(videoSource, 2);
        //player.prepare(loopingSource);//应用
        // player.setRepeatMode(Player.REPEAT_MODE_ONE);
//侧加载副标题文件
//给定一个视频文件和一个单独的副标题文件，
// 可以使用MergingMediaSource将它们合并到一个源文件中进行播放。

//        Format subtitleFormat = Format.createTextSampleFormat(
//                "id55", // An identifier for the track. May be null.
//                MimeTypes.APPLICATION_SUBRIP, // The mime type. Must be set correctly.
//               C.SELECTION_FLAG_DEFAULT , // Selection flags for the track.
//                "UTF-8"); // The subtitle language. May be null.
//        MediaSource subtitleSource =
//                new SingleSampleMediaSource.Factory(dataSourceFactory)
//        .createMediaSource(videoUri, subtitleFormat, C.TIME_UNSET);
// //播放带有副标题的视频。
//        MergingMediaSource mergedSource =
//                new MergingMediaSource(videoSource, subtitleSource);
//        player.prepare(mergedSource);


        // 准备播放控制播放器
        //一旦准备好了播放器，就可以通过调用播放器上的方法来控制播放。
        // 例如，setPlayWhenReady开始和暂停播放，各种seekTo方法在媒体内寻找，
        // setRepeatMode控制媒体是否以及如何循环，setShuffleModeEnabled控制播放列表改组
        // ，以及 setPlaybackParameters调整播放速度和音高。
        //注意  defaultControlDispatcher = new DefaultControlDispatcher();
        //setPlayWhenReady相当于 defaultControlDispatcher.dispatchSetPlayWhenReady(player, true);
        //释放播放器
        //在不再需要播放器时释放播放器非常重要，这样可以释放有限的资源，例如视频解码器，供其他应用程序使用。这可以通过调用来完成ExoPlayer.release。


//        //先播放第一个视频，再播放第二个视频
        ConcatenatingMediaSource concatenatedSource =
                new ConcatenatingMediaSource(videoSource, videoSource);


        //剪切视频 从 第5秒 到第10秒
        ClippingMediaSource clippingSource =
                new ClippingMediaSource(
                        videoSource,
                        /* startPositionUs= */ 5_000_000,
                        /* endPositionUs= */ 10_000_000);

        player.prepare(videoSource);

        player.seekTo(0);

        player.setPlayWhenReady(true);


        Log.d("tgw1", "onCreate: " + player.getCurrentTag());
        // 注册播放器监听
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {
                String content = "当时间轴和/或清单被刷新时调用。\n" +
                        "注意，如果时间轴发生了变化，那么位置不连续也可能发生。例如，由于从时间轴中添加或删除了句点，当前句点索引可能已经更改。这将不会通过对onPositionDiscontinuity(int)的单独调用报告。\n" +
                        "参数:时间轴-最新的时间轴。永远不要为空，但可能是空的。\n" +
                        "舱单-最新的舱单。可能是null。\n" +
                        "原因-玩家。TimelineChangeReason负责此时间线更改。";
                Log.d("tgw", "onTimelineChanged: i=" + reason + "发生情况" + content);
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                String content = "默认的void onTracksChanged(TrackGroupArray trackGroups，\n" +
                        "TrackSelectionArray trackSelections)\n" +
                        "当可用轨道或选定轨道发生更改时调用。\n" +
                        "参数:trackGroups—可用的曲目。决不为空，但长度可能为零。\n" +
                        "trackselection——每个渲染器的轨迹选择。决不为空，且长度始终为Player.getRendererCount()，但可能包含空元素。";
                Log.d("tgw", "onTracksChanged: " + content);
            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

                String content = "当播放器开始或停止加载源文件时调用。\n" +
                        "参数:isLoading—当前是否正在加载源文件。";
                Log.d("tgw", "onLoadingChanged: isLoading" + isLoading + "---" + content);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                //Player.STATE_IDLE：这是初始状态，播放器停止时的状态以及播放失败时的状态。
                //Player.STATE_BUFFERING：玩家无法立即从当前位置进行游戏。这主要是因为需要加载更多数据。
                // Player.STATE_READY：玩家可以立即从当前位置进行游戏。
                //Player.STATE_ENDED：播放器播放完所有媒体。
                String content = "当从Player.getPlayWhenReady()或Player.getPlaybackState()返回的值发生更改时调用。\n" +
                        "参数:播放时准备-是否播放将继续时准备。\n" +
                        "状态常量之一。";
                Log.d("tgw", "onPlayerStateChanged: playWhenReady" + playWhenReady + "---playbackState:" + playbackState + "---" + content);
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                String content = "当Player.getRepeatMode()的值发生更改时调用。\n" +
                        "参数:重复模式-播放器。用于回放的重复模式。";
                Log.d("tgw", "onRepeatModeChanged: repeatMode" + repeatMode + "---" + content);

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {
                String content = "当Player.getShuffleModeEnabled()的值发生更改时调用。\n" +
                        "参数:shuffleModeEnabled -是否启用窗口的改组。";
                Log.d("tgw", "onShuffleModeEnabledChanged: shuffleModeEnabled" + shuffleModeEnabled + "---" + content);

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                String content = "默认空onPlayerError(外部playbackexception错误)\n" +
                        "发生错误时调用。播放状态将转换到播放器。调用此方法后立即执行STATE_IDLE。player实例仍然可以使用，如果不再需要player .release()，则仍然必须调用它。\n" +
                        "参数:错误-错误。";
                Log.d("tgw", "onPlayerError: error" + error + "---" + content);

            }

            @Override
            public void onPositionDiscontinuity(int reason) {
                String content = "当位置不连续而不更改时间轴时调用。一段位置不连续发生在当前窗口或指数变化(由于回放过渡时间表从一个时期到下一个),或者当回放位置跳跃在正在播放(作为寻求执行的结果,或当源引入了内部不连续)。\n" +
                        "位置不连续是由于位置的变化而发生的";
                Log.d("tgw", "onPositionDiscontinuity: reason" + reason + "---" + content + player.getCurrentTag());


            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                String content = "当当前回放参数更改时调用。回放参数可能会由于调用player . setplaybackparameters (PlaybackParameters)而更改，或者播放器本身也可能更改这些参数(例如，如果音频回放切换到直通模式，则不再可能进行速度调整)。\n" +
                        "参数:播放参数-播放参数。";
                Log.d("tgw", "onPlaybackParametersChanged: playbackParameters" + playbackParameters + "---" + content);

            }

            @Override
            public void onSeekProcessed() {
                String content = "当玩家处理完所有挂起的请求后调用。在向onPlayerStateChanged(boolean, int)报告了对播放器状态的任何必要更改之后，保证会发生这种情况。";
                Log.d("tgw", "onSeekProcessed: " + "---" + content);

            }
        });


        //下载媒体
        btDown.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Note: This should be a singleton in your app.
                downloadVideo();
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("TGW横竖屏", "被点击了");
                //判断当前屏幕方向
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                    //切换竖屏
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                } else {
                    //切换横屏
                    MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                }
            }
        });

    }

    private void downloadVideo() {
        getDownloadCache();


        // Create a factory for reading the data from the network.
        httpDataSourceFactory = new DefaultHttpDataSourceFactory(Util.getUserAgent(MainActivity.this, "MyExoPlayerVideo"));


// Create the download manager.
        downloadManager = new DownloadManager(
                MainActivity.this,
                getDatabaseProvider(),
                getDownloadCache(),
                dataSourceFactory);

//// Optionally, setters can be called to configure the download manager.
//        downloadManager.setRequirements(requirements);
//        downloadManager.setMaxParallelDownloads(3);
//
//        DownloadRequest downloadRequest = new DownloadRequest(
//                contentId,
//                DownloadRequest.TYPE_PROGRESSIVE,
//                videoUri,
//                /* streamKeys= */ Collections.emptyList(),
//                /* customCacheKey= */ null,
//                appData);
    }


    protected synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            //下载缓存不应该驱逐媒体，所以应该使用NoopCacheEvictor。
            File downloadFileDir = new File(createDownloadDir(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(
                    downloadFileDir,
                    new NoOpCacheEvictor(),
                    getDatabaseProvider());
        }
        return downloadCache;
    }

    private DatabaseProvider getDatabaseProvider() {
        if (databaseProvider == null) {
            databaseProvider = new ExoDatabaseProvider(this);
        }
        return databaseProvider;
    }


    //创建文件存储位置
    private File createDownloadDir() {
        File outDir = Environment.getExternalStorageDirectory();
        File downloadPath = new File(outDir, "myDownLoad");
        if (!downloadPath.exists()) {
            downloadPath.mkdirs();
        }
        Log.d("tgw", "createDownloadDir: 下载地址" + downloadPath.getAbsolutePath());
        return downloadPath;
    }

    protected static CacheDataSourceFactory buildReadOnlyCacheDataSource(
            DataSource.Factory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(
                cache,
                upstreamFactory,
                new FileDataSourceFactory(),
                /* cacheWriteDataSinkFactory= */ null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR,
                /* eventListener= */ null);
    }

//    public DownloadManager getDownloadManager() {
//        initDownloadManager();
//        return downloadManager;
//    }

//    private synchronized void initDownloadManager() {
//        if (downloadManager == null) {
//            DefaultDownloadIndex downloadIndex = new DefaultDownloadIndex(getDatabaseProvider());
//            upgradeActionFile(
//                    DOWNLOAD_ACTION_FILE, downloadIndex, /* addNewDownloadsAsCompleted= */ false);
//            upgradeActionFile(
//                    DOWNLOAD_TRACKER_ACTION_FILE, downloadIndex, /* addNewDownloadsAsCompleted= */ true);
//            DownloaderConstructorHelper downloaderConstructorHelper =
//                    new DownloaderConstructorHelper(getDownloadCache(), new DefaultHttpDataSourceFactory(Util.getUserAgent(MainActivity.this, "MyExoPlayerVideo")));
//
//            downloadManager =
//                    new DownloadManager(
//                            this, downloadIndex, new DefaultDownloaderFactory(downloaderConstructorHelper));
//            downloadTracker =
//                    new DownloadTracker(/* context= */ this, buildReadOnlyCacheDataSource(dataSourceFactory, getDownloadCache()), downloadManager);
//        }
//    }


    private void upgradeActionFile(
            String fileName, DefaultDownloadIndex downloadIndex, boolean addNewDownloadsAsCompleted) {
        try {
            ActionFileUpgradeUtil.upgradeAndDelete(
                    new File(createDownloadDir(), fileName),
                    /* downloadIdProvider= */ null,
                    downloadIndex,
                    /* deleteOnFailure= */ true,
                    addNewDownloadsAsCompleted);
        } catch (IOException e) {
            Log.e(TAG, "Failed to upgrade action file: " + fileName, e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void onBackPressed() {
        if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            //切换竖屏
            MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }else {
            super.onBackPressed();
        }

    }
}
