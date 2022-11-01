package cn.xdf.lubanplus;

import android.text.TextUtils;

import java.io.File;

/**
 * author:fumm
 * Date : 2022/ 10/ 28 14:46
 * Dec : 图片相关的检测
 **/
public class Checker {

    private static final String TAG = "LubanPlus";

    private static final String JPG = "jpg";
    private static final String DOT = ".";

    /**
     * 图片JPEG类型
     *
     * @param file file 文件
     * @return true:是JPEG类型
     */
    public static boolean isJPG(File file) {

        return JPG.equals(getSuffix(file));
    }

    /**
     * 获得文件后缀
     * @param file file
     * @return 文件后缀 jpg \ png
     */
    public static String getSuffix(File file){
        if (file == null) {
            return "";
        }
        String name = file.getName();
        if (TextUtils.isEmpty(name) ||
                !name.contains(DOT)) {
            return "";
        }
        String suffix = name.substring(name.lastIndexOf(DOT) + 1);
        return suffix;
    }
    /**
     * 是否需要压缩图片
     * @param targetCompressSize 目标压缩大小
     * @param path 路径
     * @return true:需要压缩
     */
    public static boolean needCompress(int targetCompressSize, String path) {
        if (targetCompressSize > 0) {
            File source = new File(path);
            return source.exists() && source.length() > ((long) targetCompressSize << 10);
        }
        return true;
    }


}
