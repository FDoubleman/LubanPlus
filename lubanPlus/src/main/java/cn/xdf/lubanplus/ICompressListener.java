package cn.xdf.lubanplus;

import java.io.File;
import java.util.Map;

/**
 * author:fumm
 * Date : 2022/ 11/ 02 17:48
 * Dec : 压缩监听器
 **/
public interface ICompressListener {

    /**
     * Fired when the compression is started, override to handle in your own code
     */
    void onStart(String path);

    /**
     * Fired when a compression returns successfully, override to handle in your own code
     */
    void onSuccess(Furniture furn);

    /**
     * Fired when a compression fails to complete, override to handle in your own code
     */
    void onError(Throwable e);

    void onEnd(Map<String,String> resultMap);
}
