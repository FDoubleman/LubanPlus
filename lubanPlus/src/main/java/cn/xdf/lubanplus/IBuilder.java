package cn.xdf.lubanplus;

import android.net.Uri;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * author:fumm
 * Date : 2022/ 11/ 02 11:18
 * Dec : Builder 配置方法
 **/
public interface IBuilder {

    /**
     * 加载压缩图片的方法
     *
     * @param file file
     * @return IBuilder
     */
    IBuilder load(File file);

    IBuilder load(String path);

    IBuilder load(Uri uri);

    <T> IBuilder load(List<T> list);

    /**
     * 设置压缩后文件目录
     *
     * @param targetDir targetDir
     * @return IBuilder
     */
    IBuilder setTargetDir(String targetDir);

    /**
     * 设置不压缩的最大size 默认100K 单位KB
     *
     * @param size size
     * @return IBuilder
     */
    IBuilder setIgnoreBy(int size);

    /**
     * 设置是否保留图片的Alpha通道
     *
     * @param focusAlpha true:保留  ，默认保留
     * @return IBuilder
     */
    IBuilder setFocusAlpha(boolean focusAlpha);

    /**
     * 设置 图片压缩质量
     * @param quality setQuality 范围 0--100
     * @return IBuilder
     */
    IBuilder setQuality(int quality);

    /**
     * 设置是否需要 循环极致压缩到 最小值
     * @param need true：需要
     * @return IBuilder
     */
    // IBuilder setNeedLoopCompress(boolean need);
    /**
     * 同步方法 压缩单个文件方法
     *
     * @param path 单个文件路径
     * @return Furniture
     */
    String get(String path);

    /**
     * 同步方法 压缩多个文件方法
     *
     * @return List<Furniture>
     */
    Map<String,String> get();

    /**
     * 异步方法 压缩文件
     */
    void launch();

    /**
     * 设置压缩监听
     *
     * @param compressListener compressListener
     * @return IBuilder
     */
    IBuilder setCompressListener(ICompressListener compressListener);

    /**
     * 设置 自定义压缩过滤监听
     *
     * @param filterListener 监听
     * @return IBuilder
     */
    IBuilder setFilterListener(IFilterListener filterListener);

}
