package cn.xdf.lubanplus;

import android.net.Uri;

import java.io.File;
import java.util.List;

/**
 * author:fumm
 * Date : 2022/ 11/ 02 11:18
 * Dec : Builder 配置方法
 **/
public interface IBuilder {

    // 加载压缩图片的方法
    IBuilder load(File file);
    IBuilder load(String path);
    IBuilder load(Uri uri);
    <T> IBuilder load(List<T> list);


    // 同步方法 压缩单个文件方法
    File get(String path);

    // 同步方法 压缩多个文件方法
    List<File> get();

    // 异步方法 压缩文件
    void launch();

    IBuilder setCompressListener(ICompressListener compressListener);

}
