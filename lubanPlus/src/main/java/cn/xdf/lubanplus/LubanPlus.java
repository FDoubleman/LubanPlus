package cn.xdf.lubanplus;

import android.content.ContentResolver;
import android.content.Context;
import android.net.LinkAddress;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.FileUtils;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.xdf.lubanplus.engine.BaseEngine;
import cn.xdf.lubanplus.engine.IEngine;
import cn.xdf.lubanplus.engine.SampleEngine;

/**
 * author:fumm
 * Date : 2022/ 10/ 28 14:19
 * Dec : 鲁班plus
 **/
public class LubanPlus implements Handler.Callback {
    private static final String sCacheFileDirName = "LuBanPlus";
    private static final int MSG_COMPRESS_SUCCESS = 0;
    private static final int MSG_COMPRESS_START = 1;
    private static final int MSG_COMPRESS_ERROR = 2;

    private List<Furniture> mFurnitureList;
    private String mTargetDir;
    private IEngine mEngine;
    private ICompressListener mCompressListener;

    // TODO 待优化选择
    private Executor mExecutor = Executors.newFixedThreadPool(3);
    private Handler mHandler;

    private LubanPlus(Builder builder) {
        this.mFurnitureList = builder.mFurnitureList;
        this.mTargetDir = builder.mTargetDir;
        this.mEngine = builder.mEngine;
        this.mCompressListener = builder.mCompressListener;
        this.mHandler = new Handler(Looper.getMainLooper(), this);
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
    private Furniture get(String path) {
        if (Checker.needCompress(100, path)) {
            // 压缩图片
            File beforeFile = new File(path);

            Furniture afterFile = mEngine.compress(new Furniture(beforeFile));
            return afterFile;
        }
        Log.d("LuBanPlus", "get path is error! path:" + path);
        return null;
    }

    /**
     * 同步获取 多个压缩图片的方法
     *
     * @return
     */
    private List<Furniture> get() {
        List<Furniture> furnitureList = new ArrayList<>();

        Iterator<Furniture> iterable = mFurnitureList.iterator();
        while (iterable.hasNext()) {
            Furniture beforeFur = iterable.next();
            if (Checker.needCompress(100, beforeFur.getSrcAbsolutePath())) {
                // 压缩图片
                Furniture afterFur = mEngine.compress(beforeFur);
                furnitureList.add(afterFur);
            } else {
                furnitureList.add(beforeFur);
            }
            iterable.remove();
        }
        // 返回图片
        return furnitureList;
    }

    private void launch() {
        Iterator<Furniture> iterable = mFurnitureList.iterator();
        while (iterable.hasNext()) {
            Furniture beforeFurn = iterable.next();

//            mExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//                    try {
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
//                        File file = mEngine.compress(beforeFile);
//                        Log.d("run","Thread name :"+Thread.currentThread().getName());
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, file));
//                    } catch (Exception e) {
//                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
//                    }
//                }
//            });

            AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                        Furniture furn = mEngine.compress(beforeFurn);
                        Log.d("run", "Thread name :" + Thread.currentThread().getName());
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, furn));
                    } catch (Exception e) {
                        mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                    }
                }
            });
            iterable.remove();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (mCompressListener == null) return false;
        switch (msg.what) {
            case MSG_COMPRESS_START:
                mCompressListener.onStart();
                break;
            case MSG_COMPRESS_SUCCESS:
                mCompressListener.onSuccess((Furniture) msg.obj);
                break;
            case MSG_COMPRESS_ERROR:
                mCompressListener.onError((Throwable) msg.obj);
                break;
        }
        return false;
    }

    public static final class Builder implements IBuilder {

        private List<Furniture> mFurnitureList;
        private String mTargetDir;
        private IEngine mEngine;
        private Context mContext;
        private ICompressListener mCompressListener;

        private Builder(Context context) {
            mContext = context;
            mFurnitureList = new ArrayList<>();
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

        /**
         * 设置压缩监听
         *
         * @param compressListener compressListener
         */
        @Override
        public IBuilder setCompressListener(ICompressListener compressListener) {
            this.mCompressListener = compressListener;
            return this;
        }

        @Override
        public Furniture get(String path) {
            return build().get(path);
        }

        @Override
        public List<Furniture> get() {
            return build().get();
        }

        @Override
        public void launch() {
            build().launch();
        }


        //------------------------------------------------------------

        /**
         * 添加待压缩的图片
         *
         * @param file 图片
         */
        private void addFile(File file) {
            Furniture furn = new Furniture(file);
            // TODO 路径 、重命名 。。。
            mFurnitureList.add(furn);
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
