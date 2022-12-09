package cn.xdf.lubanplus.listener;

import cn.xdf.lubanplus.Furniture;

/**
 * author:fumm
 * Date : 2022/ 11/ 03 14:28
 * Dec : 设置图片 过滤监听
 **/
public interface IFilterListener {

    /**
     * 设置 过滤自定义条件
     *
     * @param furn furn
     * @return 是否压缩，true: 过滤--> 不压缩 ，false:不过滤--> 压缩
     */
    boolean isFilter(Furniture furn);
}
