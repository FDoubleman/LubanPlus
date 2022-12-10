package cn.xdf.lubanplus.listener;

import java.io.File;
import java.util.Map;

/**
 * author:fumm
 * Date : 2022/ 11/ 02 17:48
 * Dec : 压缩监听器
 **/
public interface ICompressListener {

    /**
     * 图片压缩准备
     */
    void onReady();

    /**
     * 图片压缩开始时回调
     * @param srcPath 原始图片路径
     */
    void onStart(String srcPath);

    /**
     * 图片压缩成功后的回调
     * @param srcPath 原始图片路径
     * @param compressPath 压缩图片后的路径
     */
    void onEnd(String srcPath,String compressPath);

    /**
     * 图片压缩时 发生错误的回调
     * @param srcPath srcPath
     * @param exception exception
     */
    void onError(String srcPath,Exception exception);

    /**
     * 图片list全部压缩完成之后的回调
     * @param resultMap 压缩结果
     */
    void onFinish(Map<String,String> resultMap);
}
