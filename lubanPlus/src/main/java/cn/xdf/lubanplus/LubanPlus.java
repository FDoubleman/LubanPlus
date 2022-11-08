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

    private int mIgnoreCompressSize;
    private boolean mNeedLoopCompress =false;
    private ICompressListener mCompressListener;
    private IFilterListener mFilterListener;
    private BaseEngine mEngine;

    // TODO 待优化选择
    private Executor mExecutor = Executors.newFixedThreadPool(3);
    private Handler mHandler;

    private LubanPlus(Builder builder) {
        this.mFurnitureList = builder.mFurnitureList;
        this.mIgnoreCompressSize = builder.mIgnoreCompressSize;
        this.mNeedLoopCompress = builder.mNeedLoopCompress;
        this.mFilterListener = builder.mFilterListener;

        this.mEngine = builder.mEngine;
        this.mEngine.setFocusAlpha(builder.mFocusAlpha);
        this.mEngine.setTargetDir(builder.mTargetDir);
        this.mEngine.setQuality(builder.mQuality);

        this.mCompressListener = builder.mCompressListener;
        this.mFilterListener = builder.mFilterListener;
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
        // 是否需要压缩
        if (Checker.needCompress(mIgnoreCompressSize, path, mFilterListener)) {
            // 压缩图片
            Furniture beforeFurn = new Furniture(new File(path));
            // Furniture afterFurn = loopCompress(beforeFurn);

            return loopCompress(beforeFurn);
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

            if (Checker.needCompress(mIgnoreCompressSize,
                    beforeFur.getSrcAbsolutePath(),
                    mFilterListener)) {
                // 压缩图片
                Furniture afterFur = loopCompress(beforeFur);;
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
            // 压缩前检查
            if (!Checker.needCompress(mIgnoreCompressSize,
                    beforeFurn.getSrcAbsolutePath(),
                    mFilterListener)) {
                iterable.remove();
                continue;
            }
            realLaunch(beforeFurn);
            iterable.remove();
        }
    }

    private void realLaunch(Furniture beforeFurn) {
        AsyncTask.SERIAL_EXECUTOR.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START));
                    Furniture furn = loopCompress(beforeFurn);
                    Log.d("run", "Thread name :" + Thread.currentThread().getName());
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, furn));
                } catch (Exception e) {
                    mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
                }
            }
        });
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

    /**
     * 循环压缩图片到指定大小
     *
     * @param beforeFurn beforeFurn
     * @return Furniture
     */
    private Furniture loopCompress(Furniture beforeFurn) {
        Furniture furn = mEngine.compress(beforeFurn);
        if(!mNeedLoopCompress){
            return furn;
        }
        File src = furn.getSrcFile();
        Log.d("fumm", "loopCompress: " + furn.getTargetLenth());
        while (Checker.needContinuePress(mIgnoreCompressSize,
                furn.getTargetAbsolutePath())) {
            // 继续压缩
            Furniture furniture = new Furniture(furn.getTargetFile());
            furn = mEngine.compress(furniture);
            // 采样率压缩到极致 终止压缩
            if (furn.getTargetLenth() / 1024
                    == furn.getSrcLength() / 1024) {
                break;
            }
            Log.d("fumm", "loopCompress: " + furn.getTargetLenth());
        }
        // 设置源文件
        furn.setSrcFile(src);
        furn.reset();
        return furn;
    }

    /**
     * 如果想了解 更多的 注释和方法 请 阅读  IBuilder
     */
    public static final class Builder implements IBuilder {
        private Context mContext;
        private List<Furniture> mFurnitureList;

        private String mTargetDir;
        private int mIgnoreCompressSize = 100;
        private boolean mFocusAlpha = true;
        private int mQuality = 80;
        private boolean mNeedLoopCompress =false;

        private ICompressListener mCompressListener;
        private IFilterListener mFilterListener;
        private BaseEngine mEngine;

        private Builder(Context context) {
            mContext = context;
            mFurnitureList = new ArrayList<>();
            mEngine = new SampleEngine(context);
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
        public IBuilder setTargetDir(String targetDir) {
            mTargetDir = targetDir;
            return this;
        }

        @Override
        public IBuilder setIgnoreBy(int size) {
            mIgnoreCompressSize = size;
            return this;
        }

        @Override
        public IBuilder setFocusAlpha(boolean focusAlpha) {
            mFocusAlpha = focusAlpha;
            return this;
        }

        @Override
        public IBuilder setQuality(int quality) {
            if (quality <= 0 || quality > 100) {
                throw new IllegalArgumentException();
            }
            mQuality = quality;
            return this;
        }

        @Override
        public IBuilder setNeedLoopCompress(boolean need) {
            this.mNeedLoopCompress = need;
            return this;
        }

        @Override
        public IBuilder setCompressListener(ICompressListener compressListener) {
            this.mCompressListener = compressListener;
            return this;
        }

        @Override
        public IBuilder setFilterListener(IFilterListener filterListener) {
            this.mFilterListener = filterListener;
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
