package cn.xdf.lubanplus;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import java.io.File;

/**
 * author:fumm
 * Date : 2022/ 11/ 03 10:52
 * Dec : 图片压缩数据操作流转类
 **/
public class Furniture {
    private static final int SRC_WIDTH_DEFAULT = -1 << 3;
    private static final int SRC_HEIGHT_DEFAULT = -1 << 4;
    /**
     * 源文件
     */
    private File srcFile;

    /**
     * 压缩后的文件
     */
    private File targetFile;

    /**
     * 目标文件夹
     */
    private String targetDir;

    /**
     * 设置是否保留图片的Alpha通道
     */
    private boolean focusAlpha;

    /**
     * 设置 图片压缩质量
     */
    private int quality;

    private int srcWidth = SRC_WIDTH_DEFAULT;

    private int srcHeight = SRC_HEIGHT_DEFAULT;

    /**
     * 压缩异常时回调
     */
    private Exception exception;

    public Furniture(File srcFile){
        this.srcFile = srcFile;
    }

    public Furniture(File srcFile, String targetDir, boolean focusAlpha, int quality) {
        this.srcFile = srcFile;
        this.targetDir = targetDir;
        this.focusAlpha = focusAlpha;
        this.quality = quality;
    }

    /**
     * 获取源图片的宽
     *
     * @return 源图片的宽
     */
    public int getSrcWidth() {
        if (srcWidth == SRC_WIDTH_DEFAULT) {
            calculateSrcSize();
        }
        return srcWidth;
    }

    /**
     * 获取源图片的高
     *
     * @return 源图片的高
     */
    public int getSrcHeight() {
        if (srcHeight == SRC_HEIGHT_DEFAULT) {
            calculateSrcSize();
        }
        return srcHeight;
    }

    public String getSrcAbsolutePath() {
        return srcFile.getAbsolutePath();
    }

    public String getTargetAbsolutePath() {
        if (targetFile == null) {
            return "";
        }
        return targetFile.getAbsolutePath();
    }

    public long getTargetLength() {
        if (targetFile == null) {
            return 0;
        }
        return targetFile.length();
    }

    public long getSrcLength(){
        if(srcFile == null){
            return 0;
        }
        return srcFile.length();
    }

    public File getSrcFile() {
        return srcFile;
    }

    public void setSrcFile(File srcFile) {
        this.srcFile = srcFile;
    }

    public File getTargetFile() {
        return targetFile;
    }

    public void setTargetFile(File targetFile) {
        this.targetFile = targetFile;
    }

    public String getTargetDir(Context context) {
        if (TextUtils.isEmpty(targetDir)) {
            targetDir = context.getCacheDir().getAbsolutePath();
        }
        return targetDir;
    }

    public void setTargetDir(String targetDir) {
        this.targetDir = targetDir;
    }

    public boolean isFocusAlpha() {
        return focusAlpha;
    }

    public void setFocusAlpha(boolean focusAlpha) {
        this.focusAlpha = focusAlpha;
    }

    public int getQuality() {
        return quality;
    }

    public void setQuality(int quality) {
        this.quality = quality;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(Exception exception) {
        this.exception = exception;
    }


    /**
     * 计算图片文件的宽高
     */
    private void calculateSrcSize() {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(getSrcAbsolutePath(), options);
        srcWidth = options.outWidth;
        srcHeight = options.outHeight;
    }
}
