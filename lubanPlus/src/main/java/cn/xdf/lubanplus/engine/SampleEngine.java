package cn.xdf.lubanplus.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import cn.xdf.lubanplus.Checker;

/**
 * author:fumm
 * Date : 2022/ 10/ 31 15:43
 * Dec : 采样率 压缩
 **/
public class SampleEngine extends BaseEngine {


    private boolean mFocusAlpha;

    public SampleEngine(Context context,boolean focusAlpha) {
        super(context);
        mFocusAlpha = focusAlpha;
    }

    @Override
    public File realCompress(File src) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = computeSize(srcWidth(src), srcHeight(src));

        Bitmap tagBitmap = BitmapFactory.decodeFile(src.getAbsolutePath(), options);
        if (Checker.isJPG(src)) {
            // TODO:tagBitmap=rotatingImage();
        }
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tagBitmap.compress(mFocusAlpha ? Bitmap.CompressFormat.PNG : Bitmap.CompressFormat.JPEG,
                80, outputStream);

        // outputStream 转换为File
        File targetFile = getImageCacheFile(Checker.getSuffix(src));
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(targetFile);
            fos.write(outputStream.toByteArray());
            fos.flush();
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
        return targetFile;
    }
}
