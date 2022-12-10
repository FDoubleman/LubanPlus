package cn.xdf.lubanplus.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import cn.xdf.lubanplus.utils.Checker;
import cn.xdf.lubanplus.Furniture;

/**
 * author:fumm
 * Date : 2022/ 10/ 31 15:09
 * Dec : 图片压缩基础引擎 ,
 * 图片压缩可能使用到的通用 公共方法  ，位于此
 **/
public abstract class BaseEngine implements IEngine {
    protected Context context;

    public BaseEngine(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Furniture compress(Furniture src) {
        return realCompress(src);
    }

    public abstract Furniture realCompress(Furniture src);

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

    /**
     * bitmap 旋转 angle 度 图片
     *
     * @param bitmap bitmap
     * @param angle  angle
     * @return Bitmap
     */
    protected Bitmap rotatingImage(Bitmap bitmap, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                bitmap.getHeight(), matrix, true);
    }

    /**
     * 获取图片角度
     *
     * @param fileAbsolutePath file AbsolutePath
     * @return 角度
     */
    protected int getOrientation(String fileAbsolutePath) {
        try {
            ExifInterface exif = new ExifInterface(fileAbsolutePath);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    return 90;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    return 180;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    return 270;
                default:
                    return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 获取图片缓存文件
     *
     * @param furni 原文件路径
     * @return 图片缓存文件
     */
    public File getImageCacheFile(Furniture furni) {
        String suffix = Checker.getSuffix(furni.getSrcFile());
        String targetDir = furni.getConfig().getTargetDir(context);

        File file = new File(targetDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        String cacheBuilder = targetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : "." + suffix);
        return new File(cacheBuilder);
    }
}
