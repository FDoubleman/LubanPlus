package cn.xdf.lubanplus;

import android.text.TextUtils;

import java.io.File;
import java.util.Arrays;

/**
 * author:fumm
 * Date : 2022/ 10/ 28 14:46
 * Dec : 图片相关的检测
 **/
public class Checker {

    private static final String TAG = "LubanPlus";

    private static final String JPG = "jpg";
    private static final String PNG = "png";
    private static final String JPEG = "jpeg";
    private static final String DOT = ".";

    private static final String[] SUPPORT_IMAGE_FORMAT = new String[]{PNG, JPG, JPEG};

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
     *
     * @param file file
     * @return 文件后缀 jpg \ png
     */
    public static String getSuffix(File file) {
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
     *
     * @param ignoreCompressSize 目标压缩大小
     * @param path               路径
     * @return true:需要压缩
     */
    public static boolean needCompress(int ignoreCompressSize, String path,
                                       IFilterListener filterListener) {
        // 是否需要压缩的条件
        // 1、图片路径是否正确，图片文件是否存在
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File source = new File(path);
        if (!source.exists()) {
            return false;
        }

        // 2、压缩图片的格式是否支持 png 、jpeg、jpg
        String suffix = getSuffix(source);
        if (!Arrays.asList(SUPPORT_IMAGE_FORMAT).contains(suffix)) {
            return false;
        }

        // 3、指定压缩大小是否满足
        if (!(source.length() > ((long) ignoreCompressSize << 10))) {
            return false;
        }

        // 4、自定义的条件是否 满足
        Furniture furniture = new Furniture(source);
        if (filterListener != null && filterListener.isFilter(furniture)) {
            return false;
        }

        return true;
    }

    public static boolean needContinuePress(int ignoreCompressSize, String path) {
        // 1、图片路径是否正确，图片文件是否存在
        if (TextUtils.isEmpty(path)) {
            return false;
        }

        File source = new File(path);
        // 2、指定压缩大小是否满足
        if (!(source.length() > ((long) ignoreCompressSize << 10))) {
            return false;
        }
        return true;
    }

}
