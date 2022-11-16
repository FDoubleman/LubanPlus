package cn.xdf.lubanplus.compress;

import java.io.File;
import java.util.List;

public interface IImageCompressListener {

    /**
     * 压缩开始一个时
     *
     * @param filePath 准备开始压缩图片路径
     */
    void onStart(String filePath);

    /**
     * 压缩完成一个时
     *
     * @param file 压缩后 文件
     */
    void onFinish(File file);

    /**
     * 压缩失败一个时
     *
     * @param filePath 源文件路径
     */
    void onError(String filePath, Exception exception);

    /**
     * 压缩全部完成时
     *
     * @param files 压缩后 文件集合
     */
    void onEnd(List<File> files);
}
