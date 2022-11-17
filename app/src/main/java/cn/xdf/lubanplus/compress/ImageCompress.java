package cn.xdf.lubanplus.compress;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author fmm
 * @time 2022/11/14
 * @desc 图片压缩工具类
 * 主要对图片压缩工具进行 封装 七功能如下：
 * 1、基于鲁班压缩功能 ，并能现实鲁班所有功能
 * 2、多线程并发压缩 ，并返回结果
 */
public class ImageCompress {

    private static final Executor sExecutors = Executors.newFixedThreadPool(3);
    public static final int MSG_COMPRESS_START = 0;
    public static final int MSG_COMPRESS_FINISH = 1;
    public static final int MSG_COMPRESS_END = 2;
    public static final int MSG_COMPRESS_ERROR = 3;


    public static File compress(Context context, String filePath,
                                int ignoreSize, boolean focusAlpha) {
        return CompressTask.realCompress(context, filePath, ignoreSize, focusAlpha);
    }

    public static void compress(Context context, List<String> filePaths,
                                IImageCompressListener listener) {
        compress(context, filePaths, 100, listener);
    }

    public static void compress(Context context, List<String> filePaths, int ignoreSize,
                                IImageCompressListener listener) {
        compress(context, filePaths, ignoreSize, false, listener);
    }

    public static void compress(Context context, List<String> filePaths, int ignoreSize,
                                boolean focusAlpha, IImageCompressListener listener) {
        if (filePaths == null) {
            listener.onError("", new NullPointerException());
            return;
        }
        Handler handler = new Handler(Looper.getMainLooper(), new HandlerCall(listener));

        CountDownLatch latch = new CountDownLatch(filePaths.size());

        Iterator<String> iterator = filePaths.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            sExecutors.execute(new CompressTask(context, new CompressInfo(path), ignoreSize, focusAlpha, latch, handler));
            iterator.remove();
        }
    }


    private static class HandlerCall implements Handler.Callback {

        private IImageCompressListener mListener;
        private HashMap<String,File> mFiles;

        public HandlerCall(IImageCompressListener listener) {
            this.mListener = listener;
            this.mFiles = new HashMap<>();
        }

        @Override
        public boolean handleMessage(@NonNull Message message) {
            switch (message.what) {
                case MSG_COMPRESS_START:
                    mListener.onStart((String) message.obj);
                    break;
                case MSG_COMPRESS_FINISH:
                    CompressInfo compressInfo = (CompressInfo) message.obj;
                    mListener.onFinish(compressInfo.getTarget());

                    mFiles.put(compressInfo.getSrcPath(),compressInfo.getTarget());
                    break;
                case MSG_COMPRESS_END:
                    mListener.onEnd(mFiles);
                    break;
                case MSG_COMPRESS_ERROR:
                    CompressInfo compress = (CompressInfo) message.obj;
                    mFiles.put(compress.getSrcPath(),null);
                    mListener.onError(compress.getSrcPath(), new Exception("compress image error !"));
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
