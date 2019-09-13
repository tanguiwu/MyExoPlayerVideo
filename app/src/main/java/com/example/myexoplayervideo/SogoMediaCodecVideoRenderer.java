//package com.example.myexoplayervideo;
//
//import android.content.Context;
//import android.media.MediaFormat;
//import android.os.Handler;
//
//import androidx.annotation.Nullable;
//
//import com.google.android.exoplayer2.Format;
//import com.google.android.exoplayer2.drm.DrmSessionManager;
//import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
//import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
//import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
//import com.google.android.exoplayer2.video.VideoRendererEventListener;
//
//public class SogoMediaCodecVideoRenderer extends MediaCodecVideoRenderer {
//    public SogoMediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector) {
//        super(context, mediaCodecSelector);
//    }
//
//    public SogoMediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs) {
//        super(context, mediaCodecSelector, allowedJoiningTimeMs);
//    }
//
//    public SogoMediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFrameCountToNotify) {
//        super(context, mediaCodecSelector, allowedJoiningTimeMs, eventHandler, eventListener, maxDroppedFrameCountToNotify);
//    }
//
//    public SogoMediaCodecVideoRenderer(Context context, MediaCodecSelector mediaCodecSelector, long allowedJoiningTimeMs, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, boolean playClearSamplesWithoutKeys, @Nullable Handler eventHandler, @Nullable VideoRendererEventListener eventListener, int maxDroppedFramesToNotify) {
//        super(context, mediaCodecSelector, allowedJoiningTimeMs, drmSessionManager, playClearSamplesWithoutKeys, eventHandler, eventListener, maxDroppedFramesToNotify);
//    }
//
//
//    protected MediaFormat getMediaFormat(Format format, CodecMaxValues codecMaxValues, boolean deviceNeedsAutoFrcWorkaround, int tunnelingAudioSessionId) {
//        MediaFormat mediaFormat = super.getMediaFormat(format, "", codecMaxValues, 90f, deviceNeedsAutoFrcWorkaround, tunnelingAudioSessionId);
//        mediaFormat.setInteger("rotation-degrees", 90);
//        return mediaFormat;
//    }
//}