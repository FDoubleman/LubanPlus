package cn.xdf.lubanplus.compress;

import android.util.Log;

import java.io.File;
import java.util.HashMap;

public class ImageCompressListenerImp implements IImageCompressListener {

    @Override
    public void onStart(String filePath) {
        Log.d("ImageCompress onStart", "filePath : " + filePath);
    }

    @Override
    public void onFinish(File file) {
        Log.d("ImageCompress onFinish", "filePath : " + file.getAbsolutePath());
    }

    @Override
    public void onError(String filePath, Exception exception) {
        Log.d("ImageCompress onError", "filePath : " + filePath);
    }

    @Override
    public void onEnd(HashMap<String, File> files) {
        Log.d("ImageCompress onEnd", "files size : " + files.size());
    }
}
