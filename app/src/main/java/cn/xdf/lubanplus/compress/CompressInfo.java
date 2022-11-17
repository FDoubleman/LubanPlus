package cn.xdf.lubanplus.compress;

import java.io.File;

/**
 *
 */
public class CompressInfo {

    /**
     * 源图片路径
     */
    private String srcPath;

    /**
     * 压缩后图片
     */
    private File target;

    public CompressInfo(String srcPath) {
        this.srcPath = srcPath;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public File getTarget() {
        return target;
    }

    public void setTarget(File target) {
        this.target = target;
    }
}
