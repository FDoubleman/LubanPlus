package cn.xdf.lubanplus;

import android.content.Context;
import android.net.LinkAddress;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import cn.xdf.lubanplus.engine.BaseEngine;
import cn.xdf.lubanplus.engine.IEngine;
import cn.xdf.lubanplus.engine.SampleEngine;

/**
 * author:fumm
 * Date : 2022/ 10/ 28 14:19
 * Dec : 鲁班plus
 **/
public class LubanPlus {
    private static final String sCacheFileDirName = "LuBanPlus";
    private List<File> mFiles;
    private String mTargetDir;
    private IEngine mEngine;
    private Context mContext;

    private LubanPlus(Builder builder) {
        this.mContext =builder.mContext;
        this.mFiles = builder.mFiles;
        this.mTargetDir = builder.mTargetDir;
        this.mEngine = builder.mEngine;
    }


    public static Builder with(Context context) {
        return new Builder(context);
    }

    public List<File> get() {
        List<File> files = new ArrayList<>();
        for (File beforeFile : mFiles) {
            // 检测图片
            if (Checker.needCompress(100, beforeFile.getAbsolutePath())) {
                // 压缩图片
                File afterFile = mEngine.compress(beforeFile);
                files.add(afterFile);
            } else {
                files.add(beforeFile);
            }
        }
        // 返回图片
        return files;
    }


    public static final class Builder {

        private List<File> mFiles;
        private String mTargetDir;
        private IEngine mEngine;
        private Context mContext;

        private Builder(Context context) {
            mContext =context;
            mFiles = new ArrayList<>();
            mEngine= new SampleEngine(context,false);
        }

        public LubanPlus build() {
            return new LubanPlus(this);
        }

        /**
         * 加载图片文件
         *
         * @param file file
         * @return builder
         */
        public Builder load(File file) {
            mFiles.add(file);
            return this;
        }


        /**
         * 图片压缩后的存放的路径目录
         *
         * @param targetDir targetDir
         * @return Builder
         */
        public Builder targetDir(String targetDir) {
            mTargetDir = targetDir;
            return this;
        }

        /**
         * 同步方法获得压缩图片
         *
         * @return 压缩后的图片
         */
        public List<File> get() {
            return build().get();
        }



        /**
         * 获取图片缓存文件
         * @param suffix 原文件路径
         * @return 图片缓存文件
         */
//        public File getImageCacheFile(String suffix) {
//            String targetDir = Environment.getDataDirectory().getAbsolutePath() +
//                    File.separator + sCacheFileDirName;
//
//            String cacheBuilder = targetDir + "/" +
//                    System.currentTimeMillis() +
//                    (int) (Math.random() * 1000) +
//                    (TextUtils.isEmpty(suffix) ? ".jpg" : suffix);
//            return new File(cacheBuilder);
//        }
    }
}
