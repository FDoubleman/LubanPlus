package cn.xdf.lubanplus;

import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_END;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_ERROR;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_FINISH;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_START;


import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import cn.xdf.lubanplus.engine.SampleEngine;

/**
 * @Dec : 压缩任务类
 */
public class CompressTask implements Runnable {

    private final Context mContext;
    private final Handler mHandler;
    private final Furniture mFurn;
    private final CountDownLatch mCountDown;

    public CompressTask(Context context, Furniture furn, Handler handler, CountDownLatch countDownLatch) {
        this.mHandler = handler;
        this.mFurn = furn;
        this.mContext = context;
        this.mCountDown = countDownLatch;
    }

    @Override
    public void run() {
        try {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START, mFurn));
            Furniture furn = compress(mContext, mFurn);
            if (furn.getTargetFile() == null) {
                mFurn.setException(new IOException("LuBanPlus compress Image IO Exception!"));
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, mFurn));
            } else {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_END, furn));
            }
        } catch (Exception e) {
            e.printStackTrace();
            mFurn.setException(e);
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, mFurn));
        } finally {
            mCountDown.countDown();
            if (mCountDown.getCount() == 0) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_FINISH));
            }
        }
    }

    public Furniture compress(Context context, Furniture before) {
        return getEngine(context).compress(before);
    }


    private SampleEngine getEngine(Context context) {
        // TODO 使用ThreadLocal 处理
        return new SampleEngine(context);
    }
}
