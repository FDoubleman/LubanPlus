package cn.xdf.lubanplus;

import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_END;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_ERROR;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_START;
import static cn.xdf.lubanplus.LubanPlus.MSG_COMPRESS_SUCCESS;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.util.concurrent.CountDownLatch;

import cn.xdf.lubanplus.engine.SampleEngine;

/**
 * @Dec : 压缩任务类
 */
public class CompressTask implements Runnable {

    private Context mContext;
    private Handler mHandler;
    private Furniture mFurn;
    private CountDownLatch mCountDown;

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
            Log.d("run", "Thread name :" + Thread.currentThread().getName());
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_SUCCESS, furn));
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_ERROR, e));
        } finally {
            mCountDown.countDown();
            if (mCountDown.getCount() == 0) {
                mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_END));
            }
        }
    }

    public Furniture compress(Context context, Furniture before) {
        return getEngine(context).compress(before);
    }


    private SampleEngine getEngine(Context context) {
        return new SampleEngine(context);
    }
}
