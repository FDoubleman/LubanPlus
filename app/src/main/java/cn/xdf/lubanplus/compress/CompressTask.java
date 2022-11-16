package cn.xdf.lubanplus.compress;



import static cn.xdf.lubanplus.compress.ImageCompress.MSG_COMPRESS_END;
import static cn.xdf.lubanplus.compress.ImageCompress.MSG_COMPRESS_ERROR;
import static cn.xdf.lubanplus.compress.ImageCompress.MSG_COMPRESS_FINISH;
import static cn.xdf.lubanplus.compress.ImageCompress.MSG_COMPRESS_START;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import top.zibin.luban.Luban;

public class CompressTask implements Runnable {

    private String mFilePath;
    private int mIgnoreSize;
    private boolean mFocusAlpha;
    private Handler mHandler;
    private CountDownLatch mLatch;
    private Context mContext;


    public CompressTask(Context context, String filePath, int ignoreSize,
                        boolean focusAlpha, CountDownLatch latch, Handler handler) {
        mContext = context;
        mFilePath = filePath;
        mIgnoreSize = ignoreSize;
        mFocusAlpha = focusAlpha;
        mLatch = latch;
        mHandler = handler;
    }

    @Override
    public void run() {
        try {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START, mFilePath));
            Log.d("Task","thread : "+ Thread.currentThread().getName());
            File file = realCompress(mContext, mFilePath, mIgnoreSize, mFocusAlpha);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_FINISH, file));
        } catch (Exception exception) {
            exception.printStackTrace();
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, mFilePath));
        } finally {
            mLatch.countDown();
            if(mLatch.getCount()==0){
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_END));
            }
        }

    }


    private static File realCompress(Context context, String filePath,
                                     int ignoreSize, boolean focusAlpha) {
        try {
            return Luban.with(context)
                    .ignoreBy(ignoreSize)
                    .setFocusAlpha(focusAlpha)
                    .get(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
