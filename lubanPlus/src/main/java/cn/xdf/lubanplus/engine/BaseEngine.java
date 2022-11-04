package cn.xdf.lubanplus.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import cn.xdf.lubanplus.Furniture;

/**
 * author:fumm
 * Date : 2022/ 10/ 31 15:09
 * Dec : 图片压缩引擎 ,
 * 图片压缩可能使用到的通用 公共方法  ，位于此
 **/
public abstract class BaseEngine implements IEngine {
    private static final String sCacheFileDirName = "LuBanPlus";
    protected Context context;
    protected String mTargetDir;
    protected boolean mFocusAlpha = true;


    private BaseEngine() {
    }

    public BaseEngine(Context context) {
        this.context = context;
    }

    @Override
    public Furniture compress(Furniture src) {
        return realCompress(src);
    }

    public abstract Furniture realCompress(Furniture src);

    public void setTargetDir(String targetDir){
        this.mTargetDir = targetDir;
    }

    public void setFocusAlpha(boolean focusAlpha){
        this.mFocusAlpha = focusAlpha;
    }


    protected int computeSize(int srcWidth, int srcHeight) {
        srcWidth = srcWidth % 2 == 1 ? srcWidth + 1 : srcWidth;
        srcHeight = srcHeight % 2 == 1 ? srcHeight + 1 : srcHeight;

        int longSide = Math.max(srcWidth, srcHeight);
        int shortSide = Math.min(srcWidth, srcHeight);

        float scale = ((float) shortSide / longSide);
        if (scale <= 1 && scale > 0.5625) {
            if (longSide < 1664) {
                return 1;
            } else if (longSide < 4990) {
                return 2;
            } else if (longSide > 4990 && longSide < 10240) {
                return 4;
            } else {
                return longSide / 1280 == 0 ? 1 : longSide / 1280;
            }
        } else if (scale <= 0.5625 && scale > 0.5) {
            return longSide / 1280 == 0 ? 1 : longSide / 1280;
        } else {
            return (int) Math.ceil(longSide / (1280.0 / scale));
        }
    }

    protected Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();

        matrix.postRotate(angle);

        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }


    /**
     * 获取图片缓存文件
     * @param suffix 原文件路径
     * @return 图片缓存文件
     */
    public File getImageCacheFile(String suffix) {
        String targetDir;
        if(TextUtils.isEmpty(mTargetDir)){
            targetDir = context.getCacheDir().getAbsolutePath();
        }else{
            targetDir = mTargetDir;
        }

//        String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
//        String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath() +
//                File.separator + sCacheFileDirName;
        // 文件夹不存在 创建
//        File dirFile = new File(targetDir);
//        if(!dirFile.exists()){
//            dirFile.mkdirs();
//        }
        File file = new File(targetDir);
        if(!file.exists()){
            file.mkdirs();
        }

        String cacheBuilder = targetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : "."+suffix);

//        File file = new File(cacheBuilder);
//        if(!file.exists()){
//            file.mkdirs();
//        }
//        try {
//            file.createNewFile();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        return new File(cacheBuilder);
    }

}
