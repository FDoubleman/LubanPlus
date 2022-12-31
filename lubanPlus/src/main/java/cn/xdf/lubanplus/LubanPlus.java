package cn.xdf.lubanplus;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import cn.xdf.lubanplus.engine.CustomEngine;
import cn.xdf.lubanplus.engine.EngineType;
import cn.xdf.lubanplus.engine.FastEngine;
import cn.xdf.lubanplus.engine.IEngine;
import cn.xdf.lubanplus.engine.SampleEngine;
import cn.xdf.lubanplus.listener.ICompressListener;
import cn.xdf.lubanplus.listener.IFilterListener;
import cn.xdf.lubanplus.utils.Checker;
import cn.xdf.lubanplus.utils.FileUtils;

/**
 * author:fumm
 * Date : 2022/ 10/ 28 14:19
 * Dec : 鲁班plus
 **/
public class LubanPlus {
    public static final int MSG_COMPRESS_READY = 0;
    public static final int MSG_COMPRESS_START = 1;
    public static final int MSG_COMPRESS_END = 2;
    public static final int MSG_COMPRESS_ERROR = 3;
    public static final int MSG_COMPRESS_FINISH = 4;
    // 线程池 线程的数量
    private static final int sExecutor_Thread_Count = 3;
    private final List<Furniture> mFurnitureList;
    private final int mIgnoreCompressSize;
    private final ICompressListener mCompressListener;
    private final IFilterListener mFilterListener;
    private final Builder mBuilder;
    private final Executor mExecutor = Executors.newFixedThreadPool(sExecutor_Thread_Count);


    private LubanPlus(Builder builder) {
        this.mFurnitureList = builder.mFurnitureList;
        this.mIgnoreCompressSize = builder.mIgnoreCompressSize;
        this.mBuilder = builder;
        this.mCompressListener = builder.mCompressListener;
        this.mFilterListener = builder.mFilterListener;
    }


    public static Builder with(Context context) {
        Context app = context.getApplicationContext();
        return new Builder(app);
    }

    /**
     * 同步获取 单个压缩图片的方法
     *
     * @param path 图片路径
     * @return 压缩后的图片
     */
    private String get(String path) {
        // 是否需要压缩
        if (Checker.needCompress(mIgnoreCompressSize, path, mFilterListener)) {
            // 压缩图片
            Furniture beforeFur = new Furniture(new File(path));
            beforeFur.setConfig(mBuilder.mConfig);

            return createEngine(mBuilder.mContext,mBuilder.mConfig.getEngineType()).compress(beforeFur).getTargetAbsolutePath();
        }
        Log.d("LuBanPlus", "get path is error! path:" + path);
        return path;
    }

    /**
     * 同步获取 多个压缩图片的方法
     *
     * @return 图片结果集合 ，对于多个图片压缩
     */
    private Map<String, String> get() {
        Map<String, String> resultMap = new HashMap<>();

        Iterator<Furniture> iterable = mFurnitureList.iterator();
        while (iterable.hasNext()) {
            Furniture beforeFur = iterable.next();
            beforeFur.setConfig(mBuilder.mConfig);
            if (Checker.needCompress(mIgnoreCompressSize,
                    beforeFur.getSrcAbsolutePath(), mFilterListener)) {
                // 压缩图片
                Furniture.CompressConfig config = mBuilder.mConfig;
                Furniture afterFur = createEngine(mBuilder.mContext,config.getEngineType()).compress(beforeFur);
                resultMap.put(afterFur.getSrcAbsolutePath(), afterFur.getTargetAbsolutePath());
            } else {
                resultMap.put(beforeFur.getSrcAbsolutePath(), beforeFur.getSrcAbsolutePath());
            }
            iterable.remove();
        }
        // 返回图片
        return resultMap;
    }

    private void launch() {
        if (mFurnitureList.isEmpty()) {
            Log.d("LuBanPlus", "launch image size is zero!");
        }
        Handler handler = new Handler(Looper.getMainLooper(), new HandlerCall(mCompressListener));
        handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_READY));

        Iterator<Furniture> iterable = mFurnitureList.iterator();
        int furnSize = mFurnitureList.size();
        // 1、检查过滤
        while (iterable.hasNext()) {
            Furniture beforeFurn = iterable.next();
            // 压缩前检查
            if (!Checker.needCompress(mIgnoreCompressSize,
                    beforeFurn.getSrcAbsolutePath(),
                    mFilterListener)) {
                // 检查，不需要压缩后 直接返回原来地址
                handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_START, beforeFurn));

                beforeFurn.setTargetFile(beforeFurn.getSrcFile());
                handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_END, beforeFurn));

                iterable.remove();
                furnSize--;
            }
        }
        if (furnSize <= 0) {
            handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_FINISH));
            return;
        }

        CountDownLatch countDownLatch = new CountDownLatch(furnSize);
        for (Furniture beforeFurn : mFurnitureList) {
            beforeFurn.setConfig(mBuilder.mConfig);
            realLaunch(beforeFurn, handler, countDownLatch);
        }
        mFurnitureList.clear();
    }

    // TODO 替换
    private void obtain(Handler handler) {
        Iterator<Furniture> iterable = mFurnitureList.iterator();
        int furnSize = mFurnitureList.size();
        // 1、检查过滤
        while (iterable.hasNext()) {
            Furniture beforeFurn = iterable.next();
            // 压缩前检查
            if (!Checker.needCompress(mIgnoreCompressSize,
                    beforeFurn.getSrcAbsolutePath(),
                    mFilterListener)) {
                // 检查，不需要压缩后 直接返回原来地址
                handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_START, beforeFurn));

                beforeFurn.setTargetFile(beforeFurn.getSrcFile());
                handler.sendMessage(handler.obtainMessage(MSG_COMPRESS_END, beforeFurn));

                iterable.remove();
                furnSize--;
            }
        }
    }

    private void realLaunch(Furniture beforeFurn, Handler handler, CountDownLatch countDownLatch) {
        mExecutor.execute(new CompressTask(mBuilder.mContext, beforeFurn,
                handler, countDownLatch));
    }

    /**
     * 根据EngineType 创建Engine
     * @param context context
     * @param type type
     * @return IEngine
     */
    private IEngine createEngine(Context context,@EngineType int type){
        if(type == EngineType.CUSTOM_ENGINE){
            return new CustomEngine(context);
        }else if(type == EngineType.FAST_ENGINE){
            return new FastEngine(context);
        }else {
            return new SampleEngine(context);
        }
    }


    public static class HandlerCall implements Handler.Callback {
        private final ICompressListener mCompressListener;
        private final HashMap<String, String> mResult;

        public HandlerCall(ICompressListener compressListener) {
            this.mCompressListener = compressListener;
            mResult = new HashMap<>();
        }

        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (mCompressListener == null) return false;
            switch (message.what) {
                case MSG_COMPRESS_READY:
                    mCompressListener.onReady();
                    break;
                case MSG_COMPRESS_START:
                    Furniture furniture = (Furniture) message.obj;
                    mCompressListener.onStart(furniture.getSrcAbsolutePath());
                    break;
                case MSG_COMPRESS_END:
                    Furniture succ = (Furniture) message.obj;
                    String srcPath = succ.getSrcAbsolutePath();
                    String targetPath = succ.getTargetAbsolutePath();
                    // TODO 可能为空情况
                    mCompressListener.onEnd(srcPath, targetPath);
                    mResult.put(srcPath, targetPath);
                    break;
                case MSG_COMPRESS_FINISH:
                    mCompressListener.onFinish(mResult);
                    break;
                case MSG_COMPRESS_ERROR:
                    Furniture err = (Furniture) message.obj;
                    mCompressListener.onError(err.getSrcAbsolutePath(), err.getException());
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    /**
     * 如果想了解 更多的 注释和方法 请 阅读  IBuilder
     */
    public static final class Builder implements IBuilder {
        private Context mContext;
        private List<Furniture> mFurnitureList;
        private int mIgnoreCompressSize = 100;

        private ICompressListener mCompressListener;
        private IFilterListener mFilterListener;
        private Furniture.CompressConfig mConfig;

        private Builder(Context context) {
            mContext = context;
            mFurnitureList = new ArrayList<>();
            mConfig = new Furniture.CompressConfig();
        }

        private LubanPlus build() {
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
            File file = FileUtils.uriToFileApiQ(uri, mContext);
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
            mConfig.setTargetDir(targetDir);
            return this;
        }

        @Override
        public IBuilder setIgnoreBy(int size) {
            mIgnoreCompressSize = size;
            return this;
        }

        @Override
        public IBuilder setFocusAlpha(boolean focusAlpha) {
            mConfig.setFocusAlpha(focusAlpha);
            return this;
        }

        @Override
        public IBuilder setMaxSize(int maxSize) {
            mConfig.setMaxSize(maxSize);
            return this;
        }

        @Override
        public IBuilder setMaxWidth(int maxWidth) {
            mConfig.setMaxWidth(maxWidth);
            return this;
        }

        @Override
        public IBuilder setMaxHeight(int maxHeight) {
            mConfig.setMaxHeight(maxHeight);
            return this;
        }

        @Override
        public IBuilder setQuality(int quality) {
            if (quality <= 0 || quality > 100) {
                throw new IllegalArgumentException();
            }
            mConfig.setQuality(quality);
            return this;
        }



        @Override
        public IBuilder setEngineType(@EngineType int type) {
            mConfig.setEngineType(type);
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
        public String get(String path) {
            return build().get(path);
        }

        @Override
        public Map<String, String> get() {
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
    }
}
