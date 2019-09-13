package com.example.myexoplayervideo;

import android.content.Context;
import android.os.Handler;

import androidx.annotation.Nullable;

import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.Renderer;
import com.google.android.exoplayer2.drm.DrmSessionManager;
import com.google.android.exoplayer2.drm.FrameworkMediaCrypto;
import com.google.android.exoplayer2.mediacodec.MediaCodecSelector;
import com.google.android.exoplayer2.video.MediaCodecVideoRenderer;
import com.google.android.exoplayer2.video.VideoRendererEventListener;

import java.util.ArrayList;

//public class SogoDefaultRenderersFactory extends DefaultRenderersFactory {
//
//    public SogoDefaultRenderersFactory(Context context) {
//        super(context);
//    }
//
//    public SogoDefaultRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager) {
//        super(context, drmSessionManager);
//    }
//
//    public SogoDefaultRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode) {
//        super(context, drmSessionManager, extensionRendererMode);
//    }
//
//    public SogoDefaultRenderersFactory(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, int extensionRendererMode, long allowedVideoJoiningTimeMs) {
//        super(context, drmSessionManager, extensionRendererMode, allowedVideoJoiningTimeMs);
//    }
//
//
//    @Override
//    protected void buildVideoRenderers(Context context, @Nullable DrmSessionManager<FrameworkMediaCrypto> drmSessionManager, long allowedVideoJoiningTimeMs, Handler eventHandler, VideoRendererEventListener eventListener, int extensionRendererMode, ArrayList<Renderer> out) {
//        super.buildVideoRenderers(context,1, drmSessionManager, allowedVideoJoiningTimeMs, eventHandler, eventListener, extensionRendererMode, out);
//        for (int i = out.size() - 1; i >= 0; i--) {
//            Renderer renderer = out.get(i);
//            if (renderer instanceof MediaCodecVideoRenderer) {
//                out.remove(renderer);
//                out.add(i, new SogoMediaCodecVideoRenderer(context, MediaCodecSelector.DEFAULT,
//                        allowedVideoJoiningTimeMs, drmSessionManager, false, eventHandler, eventListener,
//                        MAX_DROPPED_VIDEO_FRAME_COUNT_TO_NOTIFY));
//            }
//        }
//    }
//}