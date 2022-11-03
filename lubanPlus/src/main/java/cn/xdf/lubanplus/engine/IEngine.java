package cn.xdf.lubanplus.engine;

import java.io.File;

import cn.xdf.lubanplus.Furniture;

/**
 * author:fumm
 * Date : 2022/ 10/ 31 15:13
 * Dec : 图片压缩引擎接口
 **/
public interface IEngine {

    Furniture compress(Furniture src);

}
