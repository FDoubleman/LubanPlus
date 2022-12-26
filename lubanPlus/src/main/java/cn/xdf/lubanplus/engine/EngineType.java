package cn.xdf.lubanplus.engine;

import androidx.annotation.IntDef;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@IntDef({EngineType.FAST_ENGINE, EngineType.LUBAN_ENGINE, EngineType.CUSTOM_ENGINE})
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.SOURCE)
@Documented
@Inherited
public @interface EngineType {
    /**
     * 极速模式，图片体积小，质量差
     */
    int FAST_ENGINE = 1;
    /**
     * 默认鲁班模式
     */
    int LUBAN_ENGINE = 3;
    /**
     * 自定义模式，可以限制图片宽度、高度、最大尺寸
     */
    int CUSTOM_ENGINE = 4;
}
