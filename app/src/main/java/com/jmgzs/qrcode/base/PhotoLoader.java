package com.jmgzs.qrcode.base;

import com.bumptech.glide.load.Options;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.ModelCache;
import com.bumptech.glide.load.model.ModelLoader;
import com.bumptech.glide.load.model.ModelLoaderFactory;
import com.bumptech.glide.load.model.MultiModelLoaderFactory;
import com.bumptech.glide.load.model.stream.BaseGlideUrlLoader;

import java.io.InputStream;

/**
 * Created by mac on 17/6/12.
 * Description:
 */

public class PhotoLoader extends BaseGlideUrlLoader<String> {

    public static class Factory implements ModelLoaderFactory<String, InputStream> {
        private final ModelCache<String, GlideUrl> modelCache = new ModelCache<>(500);

        @Override
        public ModelLoader<String, InputStream> build(MultiModelLoaderFactory multiFactory) {
            return new PhotoLoader(multiFactory.build(GlideUrl.class, InputStream.class), modelCache);
        }

        @Override
        public void teardown() {

        }
    }

    public PhotoLoader(ModelLoader<GlideUrl, InputStream> urlLoader, ModelCache<String, GlideUrl> cache) {
        super(urlLoader, cache);
    }

    @Override
    protected String getUrl(String photo, int width, int height, Options options) {
        return photo;
    }

    @Override
    public boolean handles(String photo) {
        return true;
    }
}
