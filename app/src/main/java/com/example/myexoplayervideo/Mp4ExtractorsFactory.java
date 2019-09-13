package com.example.myexoplayervideo;

import com.google.android.exoplayer2.extractor.Extractor;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.extractor.mp4.Mp4Extractor;

public class Mp4ExtractorsFactory implements ExtractorsFactory {
    @Override
    public Extractor[] createExtractors() {
        return new Extractor[]{new Mp4Extractor()};
    }
}
