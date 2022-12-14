# LubanPlus
基于Luban改进而来的图片压缩库，功能更加丰富、迭代更迅速。持续完善开发中

`LubanPlus`（鲁班+） —— `Android`图片压缩工具，仿微信朋友圈压缩策略。

## 写在前面
从事`App`开发离不开图片压缩功能，而 [Luban](https://github.com/Curzibn/Luban) 是比较优秀的图片压缩库。

随着时间的推移`Luban`库很久没有更新迭代了，已知的一些`Bug`也无法被修改，某些新的业务需求单靠`Luban`自身的`Api`无法实现。

遂结合工作需求、`Luban`库框架`Api`冒昧重构了此`LubanPlus`项目。


## 项目和功能描述
`LubanPlus`的目标就是:在`Luban`的基础上,更加完善和丰富其功能，主要功能如下：

| 功能 | Luban | LubanPlus | 备注 |
| --- | --- | --- | --- |
| 图片同步压缩 |支持  |支持  |  |
| 图片异步压缩 |支持  |支持  |  |
| 忽略压缩大小 |支持  |支持  |  |
| 自定义过滤条件压缩 |支持  |支持  |  |
| 是否保留透明通道 |支持  |支持  | bug:Luban同步方法设置无效 |
| 缓存压缩图片路径 |支持  |支持  |  |
| 异步压缩回调 |支持  |支持  |  |
| 压缩前重命名接口 |支持  |不支持  |待确认使用常见后添加  |
|压缩方式选择or自定义  | 不支持 |支持  |  |
| 多线程并发压缩 |不支持  |支持  |核心线程数为3，并行压缩  |
| 循环极致压缩 |不支持  |支持  | 参考快速压缩模式 |
| Issues和功能处理 |不支持  |支持  |  |



## 效果对比

由于基于`Luban`封装 效果相同：
内容 | 原图 | `Luban` | `Wechat`
---- | ---- | ------ | ------
截屏 720P |720*1280,390k|720*1280,87k|720*1280,56k
截屏 1080P|1080*1920,2.21M|1080*1920,104k|1080*1920,112k
拍照 13M(4:3)|3096*4128,3.12M|1548*2064,141k|1548*2064,147k
拍照 9.6M(16:9)|4128*2322,4.64M|1032*581,97k|1032*581,74k
滚动截屏|1080*6433,1.56M|1080*6433,351k|1080*6433,482k

## 导入
```sh
implementation 'io.github.FDoubleman:LubanPlus:1.0.2'
```
## 使用

### 方法列表

| 方法 | 描述 |备注 |
| --- | --- |--- |
| load | 传入图片path、图片File、图片Uri  | |
|  get()|同步多图片压缩，配合load(list)  | |
|  get(String path)|同步单图片压缩  | |
|  launch()|异步压缩  | |
| setTargetDir | 缓存压缩图片路径 | |
|  setIgnoreBy| 不压缩的阈值，单位为K | |
|  setFocusAlpha|设置是否保留透明通道  |仅`LUBAN_ENGINE`模式支持 |
|  setQuality| 压缩的质量,0-100 默认60 |仅`LUBAN_ENGINE`模式支持 |
|  setCompressListener| 异步压缩回调 | |
|  setFilterListener| 压缩过滤监听 | |
|  ==setEngineType==| 设置压缩Engine类型 |`FAST_ENGINE`：极速模式     `LUBAN_ENGINE`：默认鲁班模式 `CUSTOM_ENGINE`：自定义模式    |
|  ==setMaxSize==| 设置压缩后的最大size |`CUSTOM_ENGINE` 模式下使用|
|  ==setMaxWidth==| 压缩后最大宽度 |`CUSTOM_ENGINE` 模式下使用 |
|  ==setMaxHeight==| 压缩后最大高度 |`CUSTOM_ENGINE` 模式下使用 |
### 参数效果图

` 注意：png图片格式压缩设置 setFocusAlpha(false) ,会导致的透明背景变成黑色，使用时注意！ `

![image](https://github.com/FDoubleman/LubanPlus/blob/master/app/src/main/assets/parameter_effect.png)

### 异步调用
`LubanPlus`内存采用固定3个线程的线程池并行压缩，外部可以按需创建`ICompressListener`的默认实现类，重写其对应的方法。

或者直接创建 `ICompressListene`匿名内部类；

```java
LubanPlus.with(this)
    .load(list)
    .setFocusAlpha(true)
    // .setTargetDir(targetDir)
    .setQuality(80)
    .setIgnoreBy(100)
    .setFilterListener(new IFilterListener() {
        @Override
        public boolean isFilter(Furniture furn) {
            // 是否压缩过滤，true:过滤不压缩
            String srcPath = furn.getSrcAbsolutePath();
            return (TextUtils.isEmpty(srcPath) || srcPath.toLowerCase().endsWith(".gif"));
        }
    })
    // .setCompressListener(new CompressListenerImp())
    .setCompressListener(new ICompressListener() {

        @Override
        public void onReady() {
            Log.d("LubanPlus", "onReady");
        }

        @Override
        public void onStart(String path) {
            // TODO 每个文件开始压缩前调用
            Log.d("LubanPlus", "onStart:" + path);
        }

        @Override
        public void onEnd(String srcPath, String compressPath) {
            // TODO 每个文件开始压缩成功调用
            File file;
            if(!TextUtils.isEmpty(compressPath)){
                file = new File(compressPath);
            }else{
                file = new File("");
            }
            Log.d("LubanPlus", "onEnd :srcPath path: " + srcPath +
                    " --> target path:"+compressPath+
                    "   compressPath file size :" + file.length()/1024.0 +"K");
        }

        @Override
        public void onError(String srcPath, Exception exception) {
            // TODO 每个文件开始压缩失败调用
            Log.d("LubanPlus", "onError srcPath :" + srcPath +
                    " -- exception : " + exception.toString());
        }

        @Override
        public void onFinish(Map<String, String> resultMap) {
            // TODO 所有文件开始压缩完成
            Log.d("LubanPlus", "onFinish : " + resultMap.size());
        }
    })
    .launch();
```

### 同步调用
同步方法请尽量避免在主线程调用以免阻塞主线程！！！ **通常配合线程池、Rxjava、Handler等异步框架使用。**

#### 同步多文件压缩
```java
 String targetDir =getExternalCacheDir().getAbsolutePath();
File srcFile1 = new File(getExternalFilesDir(null), "test_1.png");
File srcFile2 = new File(getExternalFilesDir(null), "test_2.jpeg");
File srcFile5 = new File(getExternalFilesDir(null), "test_5.png");

// 多个图片同步压缩
List<File> list = new ArrayList<>();
list.add(srcFile1);
list.add(srcFile2);
list.add(new File(""));
list.add(srcFile5);
LubanPlus.with(this).setTargetDir(targetDir)
        .load(list)
        .setQuality(80)
        .setIgnoreBy(100)
        .setFocusAlpha(true)
        .setFilterListener(new IFilterListener() {
            @Override
            public boolean isFilter(Furniture furn) {
                // 是否压缩过滤，true:过滤不压缩
                String srcPath = furn.getSrcAbsolutePath();
                return (TextUtils.isEmpty(srcPath) || srcPath.toLowerCase().endsWith(".gif"));
            }
        }).get();
for (File file : list) {
    Log.d("LubanPlus", "testGet : path---> "
            + file.getAbsolutePath() +"  size:" + file.length());
}

```

#### 同步单文件压缩

```java
// 单个图片同步压缩
String targetFile = LubanPlus.with(this)
    .setTargetDir(targetDir)
    .setQuality(80)
    .setIgnoreBy(100)
    .setFocusAlpha(true)
    .setFilterListener(new IFilterListener() {
        @Override
        public boolean isFilter(Furniture furn) {
            // 是否压缩过滤，true:过滤不压缩
            String srcPath = furn.getSrcAbsolutePath();
            return (TextUtils.isEmpty(srcPath) || srcPath.toLowerCase().endsWith(".gif"));
        }
    })
    .get(srcFile.getAbsolutePath());
Log.d("LubanPlus", "testGet : " +targetFile);
```
## 版本日志
#### 1.0.2版本
- 参考 [issues/2](https://github.com/FDoubleman/LubanPlus/issues/2) 建议，添加设置`压缩后的文件最大值`功能
- 参考 [AdvancedLuban](https://github.com/jackyHuangH/AdvancedLuban) 设计`三种压缩Engine`,提供选择
- 代码测试样例 `MainActivity` 页面UI重构，测试更加方便
- 代码减少`Context`使用深度
- 新增`4个API`,请参见标注`黄色Api`
- 相关代码优化等

#### 1.0.1版本
- 最基础版本，在Luban基础上完善开发封装
- 提供压缩框架Api
- 实现最基础压缩功能
- 实现异步并发3线程压缩
- 部分功能等...


## About issues

`关于issuse请参考如下模板：`

- `1、问题类型`：如：反馈bug or 添加新功能
- `2、运行环境`：如：android 10(Q)
- `3、使用版本`：如：1.0.1
- `4、问题详细日志`：Log
- `5、新增功能使用场景描述`：某XXXX



本人尽量在最短时间内回复。

谢谢！



## License

    Copyright 2022 Fu ManMan
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
        http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.