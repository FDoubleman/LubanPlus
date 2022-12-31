package cn.xdf.lubanplus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Environment;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import cn.xdf.lubanplus.compress.IImageCompressListener;
import cn.xdf.lubanplus.compress.ImageCompress;
import cn.xdf.lubanplus.databinding.ActivityMainBinding;
import cn.xdf.lubanplus.engine.EngineType;
import cn.xdf.lubanplus.listener.CompressListenerImp;
import cn.xdf.lubanplus.listener.ICompressListener;
import cn.xdf.lubanplus.listener.IFilterListener;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private int compress_model = COMPRESS_MODEL_LUBAN;
    private static final int COMPRESS_MODEL_LUBAN = 100;
    private static final int COMPRESS_MODEL_FAST = 101;
    private static final int COMPRESS_MODEL_CUSTOM = 102;
    private List<File> mOriginalImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initRadioModel();
        initCreateImage();
        initCompress();
        initPermission();
    }


    private void initRadioModel() {
        binding.rgModel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                if (id == R.id.rb_luban) {
                    customSizeEnable(false);
                    compress_model = COMPRESS_MODEL_LUBAN;
                } else if (id == R.id.rb_fast) {
                    customSizeEnable(false);
                    compress_model = COMPRESS_MODEL_FAST;
                } else if (id == R.id.rb_custom) {
                    customSizeEnable(true);
                    compress_model = COMPRESS_MODEL_CUSTOM;
                } else {
                    Log.d("LubanPlus", "id not support");
                }
            }
        });
    }


    private void initCreateImage() {
        binding.btnCreateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                create();
            }
        });
    }

    private void create() {
        File imageFile1 = createTestImageFile("test_1.png");
        File imageFile2 = createTestImageFile("test_2.jpeg");
        File imageFile3 = createTestImageFile("test_5.png");
//        createTestImageFile("test_7.jpg");
//        createTestImageFile("test_8.jpg");

        if (imageFile1 != null) {
            binding.ivOriginal1.setImageURI(Uri.fromFile(imageFile1));
            binding.tvPathOriginal1.setText(getFileText(imageFile1));
            mOriginalImages.add(imageFile1);
        }
        if (imageFile2 != null) {
            binding.ivOriginal2.setImageURI(Uri.fromFile(imageFile2));
            binding.tvPathOriginal2.setText(getFileText(imageFile2));
            mOriginalImages.add(imageFile2);
        }
        if (imageFile3 != null) {
            binding.ivOriginal3.setImageURI(Uri.fromFile(imageFile3));
            binding.tvPathOriginal3.setText(getFileText(imageFile3));
            mOriginalImages.add(imageFile3);
        }
    }

    private void initCompress() {
        binding.btnCompress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                compress();
            }
        });
    }

    private void loadCompressImage(HashMap<String, String> compressResult) {
        String originalImage1 = mOriginalImages.get(0).getAbsolutePath();
        String originalImage2 = mOriginalImages.get(1).getAbsolutePath();
        String originalImage3 = mOriginalImages.get(2).getAbsolutePath();

        String compressImage1 = compressResult.get(originalImage1);
        String compressImage2 = compressResult.get(originalImage2);
        String compressImage3 = compressResult.get(originalImage3);

        File compressFile1 = new File(compressImage1);
        File compressFile2 = new File(compressImage2);
        File compressFile3 = new File(compressImage3);


        binding.ivCompress1.setImageURI(Uri.fromFile(compressFile1));
        binding.tvPathCompress1.setText(getFileText(compressFile1));

        binding.ivCompress2.setImageURI(Uri.fromFile(compressFile2));
        binding.tvPathCompress2.setText(getFileText(compressFile2));

        binding.ivCompress3.setImageURI(Uri.fromFile(compressFile3));
        binding.tvPathCompress3.setText(getFileText(compressFile3));
    }

    private void compress() {
        if (compress_model == COMPRESS_MODEL_LUBAN) {
            lubanCompress();
        } else if (compress_model == COMPRESS_MODEL_FAST) {
            fastCompress();
        } else if (compress_model == COMPRESS_MODEL_CUSTOM) {
            customCompress();
        }
    }


    private void lubanCompress() {
        // 方式 一
        lubanPlusLaunch();
        // 方式 二
        // testGet();
        // 方式 三
        // testListGet();
    }

    private void fastCompress() {

    }

    private void customCompress() {

    }

    private void customSizeEnable(boolean enable) {
        binding.llCustomLayout.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 之后验证测试异步压缩
     */
    private void lubanPlusLaunch() {
        String targetDir = getExternalCacheDir().getAbsolutePath();
        // android 11 及以后的版本 error ：open failed: EACCES (Permission denied)
        // 更多信息：https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
        //         https://developer.android.com/training/data-storage#scoped-storage
        // String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
        LubanPlus.with(this)
                .load(mOriginalImages)
                .setFocusAlpha(true)
                .setEngineType(EngineType.LUBAN_ENGINE)
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
                        //  每个文件开始压缩前调用
                        Log.d("LubanPlus", "onStart:" + path);
                    }

                    @Override
                    public void onEnd(String srcPath, String compressPath) {
                        //  每个文件开始压缩成功调用
                        File file;
                        if (!TextUtils.isEmpty(compressPath)) {
                            file = new File(compressPath);
                        } else {
                            file = new File("");
                        }
                        Log.d("LubanPlus", "onEnd :srcPath path: " + srcPath +
                                " --> target path:" + compressPath +
                                "   compressPath file size :" + file.length() / 1024.0 + "K");
                    }

                    @Override
                    public void onError(String srcPath, Exception exception) {
                        //  每个文件开始压缩失败调用
                        Log.d("LubanPlus", "onError srcPath :" + srcPath +
                                " -- exception : " + exception.toString());
                    }

                    @Override
                    public void onFinish(HashMap<String, String> resultMap) {
                        //  所有文件开始压缩完成
                        Log.d("LubanPlus", "onFinish : " + resultMap.size());
                        loadCompressImage(resultMap);
                    }
                })
                .launch();
    }

    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 同步方法 压缩单个图片  测试类
     */
    private void lubanPlusGet() {
        File srcFile = mOriginalImages.get(0);
        String targetDir = getExternalCacheDir().getAbsolutePath();
        // String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
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
        Log.d("LubanPlus", "testGet : " + targetFile);
        HashMap<String, String> resultMap = new HashMap<>();
        resultMap.put(srcFile.getAbsolutePath(), targetFile);

        loadCompressImage(resultMap);

    }

    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 同步方法 压缩多个图片  测试类
     */
    private void lubanPlusList() {
        String targetDir = getExternalCacheDir().getAbsolutePath();

        HashMap<String, String> result = (HashMap<String, String>) LubanPlus.with(this).setTargetDir(targetDir)
                .load(mOriginalImages)
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

        loadCompressImage(result);
    }

    public File createTestImageFile(String assetsFileName) {
        InputStream is = null;
        File file = null;
        try {
            is = getResources().getAssets().open(assetsFileName);
            // path : /storage/emulated/0/Android/data/cn.xdf.lubanplus/files/assetsFileName
            file = new File(getExternalFilesDir(null), assetsFileName);
            Log.d("createTestImageFile", "file path: "
                    + file.getAbsolutePath());
            FileOutputStream fos = new FileOutputStream(file);

            byte[] buffer = new byte[2048];
            int len = is.read(buffer);
            while (len > 0) {
                fos.write(buffer, 0, len);
                len = is.read(buffer);
            }
            fos.close();
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private String getFileText(File file) {
        return "路径：" + file.getAbsolutePath() + "\r\n" + "尺寸：" + (file.length()) / 1024 + "KB";
    }

    private void initPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0x0001);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                startActivity(new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION));
            }
        }
    }
}