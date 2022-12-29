package cn.xdf.lubanplus.engine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import cn.xdf.lubanplus.Furniture;

public class CustomEngine extends BaseEngine {
    private ByteArrayOutputStream mByteArrayOutputStream;

    public CustomEngine(Context context) {
        super(context);
    }

    @Override
    public Furniture realCompress(Furniture src) {
        try {
            return customCompress(src);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return src;
    }

    /**
     * 自定义压缩模式，可限制宽高、图片最大尺寸
     *
     * @param src
     * @return
     * @throws IOException
     */
    private Furniture customCompress(Furniture src) throws IOException {
        String thumbFilePath = getImageCacheFile(src).getAbsolutePath();
        String filePath = src.getSrcAbsolutePath();

        int angle = getOrientation(filePath);
        Furniture.CompressConfig config = src.getConfig();
        int maxSize = config.getMaxSize();
        long srcFileLength = src.getSrcFile().length();

        long fileSize = maxSize > 0 && maxSize < srcFileLength / 1024 ? maxSize
                : srcFileLength / 1024;

        int[] size = getImageSize(filePath);
        int width = size[0];
        int height = size[1];

        if (maxSize > 0 && maxSize < srcFileLength / 1024f) {
            // find a suitable size
            float scale = (float) Math.sqrt(srcFileLength / 1024f / maxSize);
            width = (int) (width / scale);
            height = (int) (height / scale);
        }

        // check the width&height
        int maxHeight = config.getMaxHeight();
        int maxWidth = config.getMaxWidth();
        if (maxWidth > 0) {
            width = Math.min(width, maxWidth);
        }
        if (maxHeight > 0) {
            height = Math.min(height, maxHeight);
        }
        float scale = Math.min((float) width / size[0], (float) height / size[1]);
        width = (int) (size[0] * scale);
        height = (int) (size[1] * scale);

        // 不压缩
        if (maxSize > srcFileLength / 1024f && scale == 1) {
            src.setTargetFile(src.getSrcFile());
            return src;
        }
        File targetFile = compress(filePath, thumbFilePath, width, height, angle, fileSize);
        src.setTargetFile(targetFile);
        return src;
    }


    /**
     * 指定参数压缩图片
     * create the thumbnail with the true rotate angle
     *
     * @param largeImagePath the big image path
     * @param thumbFilePath  the thumbnail path
     * @param width          width of thumbnail
     * @param height         height of thumbnail
     * @param angle          rotation angle of thumbnail
     * @param size           the file size of image
     */
    private File compress(String largeImagePath, String thumbFilePath, int width, int height,
                          int angle, long size) throws IOException {
        Bitmap thbBitmap = compress(largeImagePath, width, height);

        thbBitmap = rotatingImage(thbBitmap, angle);

        return saveImage(thumbFilePath, thbBitmap, size);
    }

    /**
     * obtain the thumbnail that specify the size
     *
     * @param imagePath the target image path
     * @param width     the width of thumbnail
     * @param height    the height of thumbnail
     * @return {@link Bitmap}
     */
    private Bitmap compress(String imagePath, int width, int height) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);

        int outH = options.outHeight;
        int outW = options.outWidth;
        int inSampleSize = 1;

        while (outH / inSampleSize > height || outW / inSampleSize > width) {
            inSampleSize *= 2;
        }

        options.inSampleSize = inSampleSize;
        options.inJustDecodeBounds = false;

        return BitmapFactory.decodeFile(imagePath, options);
    }

    /**
     * 保存图片到指定路径
     * Save image with specified size
     *
     * @param filePath the image file save path 储存路径
     * @param bitmap   the image what be save   目标图片
     * @param size     the file size of image   期望大小
     */
    private File saveImage(String filePath, Bitmap bitmap, long size) throws IOException {

        File result = new File(filePath.substring(0, filePath.lastIndexOf("/")));

        if (!result.exists() && !result.mkdirs()) {
            return null;
        }

        if (mByteArrayOutputStream == null) {
            mByteArrayOutputStream = new ByteArrayOutputStream(
                    bitmap.getWidth() * bitmap.getHeight());
        } else {
            mByteArrayOutputStream.reset();
        }

        int options = 100;
        bitmap.compress(Bitmap.CompressFormat.JPEG, options, mByteArrayOutputStream);

        while (mByteArrayOutputStream.size() / 1024 > size && options > 6) {
            mByteArrayOutputStream.reset();
            options -= 6;
            bitmap.compress(Bitmap.CompressFormat.JPEG, options, mByteArrayOutputStream);
        }
        bitmap.recycle();

        FileOutputStream fos = new FileOutputStream(filePath);
        mByteArrayOutputStream.writeTo(fos);
        fos.close();

        return new File(filePath);
    }

}
