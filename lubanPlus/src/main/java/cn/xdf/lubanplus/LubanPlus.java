package cn.xdf.lubanplus;

import android.content.ContentResolver;
import android.content.Context;
import android.net.LinkAddress;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
        this.mContext = builder.mContext;
        this.mFiles = builder.mFiles;
        this.mTargetDir = builder.mTargetDir;
        this.mEngine = builder.mEngine;
    }


    public static Builder with(Context context) {
        return new Builder(context);
    }

    /**
     * 同步获取 单个压缩图片的方法
     *
     * @param path 图片路径
     * @return 压缩后的图片
     */
    public File get(String path) {
        if (Checker.needCompress(100, path)) {
            // 压缩图片
            File beforeFile = new File(path);
            File afterFile = mEngine.compress(beforeFile);
            return afterFile;
        }
        Log.d("LuBanPlus", "get path is error! path:" + path);
        return null;
    }

    /**
     * 同步获取 多个压缩图片的方法
     * @return
     */
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


    public static final class Builder implements IBuilder {

        private List<File> mFiles;
        private String mTargetDir;
        private IEngine mEngine;
        private Context mContext;

        private Builder(Context context) {
            mContext = context;
            mFiles = new ArrayList<>();
            mEngine = new SampleEngine(context, false);
        }

        public LubanPlus build() {
            return new LubanPlus(this);
        }


        @Override
        public IBuilder load(File file) {
            addFile(file);
            return this;
        }

        @Override
        public IBuilder load(String path) {
            File file = new File(path);
            addFile(file);
            return this;
        }

        @Override
        public IBuilder load(Uri uri) {
            File file = uriToFileApiQ(uri, mContext);
            if (file == null) {
                Log.e("LuBanPlus", "load uri is null or nonsupport！");
            } else {
                addFile(file);
            }
            return this;
        }

        @Override
        public <T> IBuilder load(List<T> list) {
            for (T t : list) {
                if (t instanceof File) {
                    load((File) t);
                } else if (t instanceof String) {
                    load((String) t);
                } else if (t instanceof Uri) {
                    load((Uri) t);
                } else {
                    Log.d("LuBan load ", "list Item error! about t : " + t.toString());
                }
            }
            return this;
        }

        @Override
        public File get(String path) {
            return build().get(path);
        }

        @Override
        public List<File> get() {
            return build().get();
        }

        @Override
        public void launch() {

        }

        //------------------------------------------------------------

        /**
         * 添加待压缩的图片
         *
         * @param file 图片
         */
        private void addFile(File file) {
            mFiles.add(file);
        }


        /**
         * 通过 url 转换成File方法；
         * 注意考虑大文件 耗时问题！！
         *
         * @param uri     uri
         * @param context context
         * @return File
         */
        private static File uriToFileApiQ(Uri uri, Context context) {
            File file = null;
            if (uri == null) return file;
            //android10以上转换
            if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
                file = new File(uri.getPath());
            } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
                // 把文件复制到沙盒目录
                Log.e("LuBanPlus_uriToFileApiQ", "nonsupport uri :" + uri);
//                ContentResolver contentResolver = context.getContentResolver();
//                String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)
//                        + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
//
//                try {
//                    InputStream is = contentResolver.openInputStream(uri);
//                    File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
//                    FileOutputStream fos = new FileOutputStream(cache);
//                    FileUtils.copy(is, fos);
//                    file = cache;
//                    fos.close();
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
            }
            return file;
        }
    }
}
