package com.jmgzs.qrcode.base;


import android.content.Context;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.module.AppGlideModule;

import java.io.InputStream;

/**
 * Created by mac on 17/6/12.
 * Description: 如果有多个GlideModules,应使用LibraryGlideModule而非AppGlideModule,否则会导致冲突
 */
@GlideModule
public class MyAppGlideModule extends AppGlideModule {

    @Override
    public boolean isManifestParsingEnabled() {
        //避免检查元数据的性能开销,设为false来禁用清单解析
        return false ;
    }

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        builder.setMemoryCache(new LruResourceCache(15*1024*1024));
//        builder.setDiskCache();
    }

    @Override
    public void registerComponents(Context context, Registry registry) {
        registry.append(String.class, InputStream.class,new PhotoLoader.Factory());
    }
}
