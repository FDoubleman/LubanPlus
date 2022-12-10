package cn.xdf.lubanplus.listener;

import android.util.Log;

import java.util.Map;

/**
 * author:fumm
 * Date : 2022/ 12/ 09 19:07
 * Dec : 压缩监听器 具体实现类，
 **/
public class CompressListenerImp implements ICompressListener{
    private static final String Tag = CompressListenerImp.class.getSimpleName();

    @Override
    public void onReady() {
        Log.d(Tag,"onReady: do UI loading work ");
    }

    @Override
    public void onStart(String srcPath) {
        Log.d(Tag,"onStart:"+srcPath);
    }

    @Override
    public void onEnd(String srcPath, String compressPath) {
        Log.d(Tag,"onSuccess -> srcPath: " + srcPath +"  compressPath: "+ compressPath);
    }


    @Override
    public void onError(String srcPath, Exception exception) {
        Log.d(Tag,"onError -> srcPath: " + srcPath +"  exception:  "+exception.toString());
    }

    @Override
    public void onFinish(Map<String, String> resultMap) {
        Log.d(Tag," onEnd: resultMap size :"+ resultMap.size());
    }

}
