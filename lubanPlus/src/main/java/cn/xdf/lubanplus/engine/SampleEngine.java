package cn.xdf.lubanplus.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.xdf.lubanplus.utils.Checker;
import cn.xdf.lubanplus.Furniture;

/**
 * author:fumm
 * Date : 2022/ 10/ 31 15:43
 * Dec : 采样率 压缩
 **/
public class SampleEngine extends BaseEngine {


    public SampleEngine(Context context) {
        // TODO context 去除
        super(context);
    }

    @Override
    public Furniture realCompress(Furniture furni) {
        // 1、设置options
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(furni.getSrcWidth(), furni.getSrcHeight());
        Bitmap tagBitmap = BitmapFactory.decodeFile(furni.getSrcAbsolutePath(), options);
        // 2、针对jpg or jpeg 格式 需要旋转获取原图
        File srcFile = furni.getSrcFile();
        if (Checker.isJPG(srcFile)) {
            tagBitmap = rotatingImage(tagBitmap, getOrientation(srcFile.getAbsolutePath()));
        }
        // 3、压缩图片
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // format : png 格式 保留 Alpha 通道 ，jpeg：格式不支持 Alpha通道
        // quality : png 设置无效\
        boolean focusAlpha = furni.isFocusAlpha();
        int quality = furni.getQuality();

        tagBitmap.compress(focusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                quality, outputStream);
        tagBitmap.recycle();
        Log.d("fumm compress", "mQuality : " + quality);

        // 4、outputStream 转换为File
        File targetFile = getImageCacheFile(furni);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(outputStream.toByteArray());
            fos.flush();
            // IO 异常时不设置
            furni.setTargetFile(targetFile);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return furni;
    }
}
