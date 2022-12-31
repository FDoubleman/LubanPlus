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

import cn.xdf.lubanplus.engine.CustomEngine;
import cn.xdf.lubanplus.engine.EngineType;
import cn.xdf.lubanplus.engine.FastEngine;
import cn.xdf.lubanplus.engine.IEngine;
import cn.xdf.lubanplus.engine.SampleEngine;

/**
 * @Dec : 压缩任务类
 */
public class CompressTask implements Runnable {

    private final Handler mHandler;
    private final Furniture mFurn;
    private final CountDownLatch mCountDown;

    public CompressTask( Furniture furn, Handler handler, CountDownLatch countDownLatch) {
        this.mHandler = handler;
        this.mFurn = furn;
        this.mCountDown = countDownLatch;
    }

    @Override
    public void run() {
        try {
            mHandler.sendMessage(mHandler.obtainMessage(MSG_COMPRESS_START, mFurn));
            Furniture furn = compress(mFurn);
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

    public Furniture compress(Furniture before) {
        int engineType = before.getConfig().getEngineType();
        return createEngine(engineType).compress(before);
    }


    /**
     * 根据EngineType 创建Engine
     *
     * @param type    type
     * @return IEngine
     */
    private IEngine createEngine(int type) {
        if (type == EngineType.CUSTOM_ENGINE) {
            return new CustomEngine();
        } else if (type == EngineType.FAST_ENGINE) {
            return new FastEngine();
        } else {
            return new SampleEngine();
        }
    }
}
