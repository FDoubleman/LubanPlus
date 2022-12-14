package cn.xdf.lubanplus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
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
import android.widget.EditText;
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
        // ?????? ???
        lubanPlusLaunch();
        // ?????? ???
        // testGet();
        // ?????? ???
        // testListGet();
    }

    private void fastCompress() {
        LubanPlus.with(this)
                .load(mOriginalImages)
                .setEngineType(EngineType.FAST_ENGINE)
                .setIgnoreBy(100)
                .setFilterListener(new IFilterListener() {
                    @Override
                    public boolean isFilter(Furniture furn) {
                        // ?????????????????????true:???????????????
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
                        //  ?????????????????????????????????
                        Log.d("LubanPlus", "onStart:" + path);
                    }

                    @Override
                    public void onEnd(String srcPath, String compressPath) {
                        //  ????????????????????????????????????
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
                        //  ????????????????????????????????????
                        Log.d("LubanPlus", "onError srcPath :" + srcPath +
                                " -- exception : " + exception.toString());
                    }

                    @Override
                    public void onFinish(HashMap<String, String> resultMap) {
                        //  ??????????????????????????????
                        Log.d("LubanPlus", "onFinish : " + resultMap.size());
                        loadCompressImage(resultMap);
                    }
                })
                .launch();
    }

    private void customCompress() {
        String targetDir = getExternalCacheDir().getAbsolutePath();
        // android 11 ?????????????????? error ???open failed: EACCES (Permission denied)
        // ???????????????https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
        //         https://developer.android.com/training/data-storage#scoped-storage
        // String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
        int maxsize = getSizeByInput(binding.etMaxSize);
        int maxHeight = getSizeByInput(binding.etMaxHeight);
        int maxWidth =getSizeByInput(binding.etMaxWidth);

        LubanPlus.with(this)
                .load(mOriginalImages)
                .setEngineType(EngineType.CUSTOM_ENGINE)
                .setMaxHeight(maxHeight)
                .setMaxWidth(maxWidth)
                .setMaxSize(maxsize)
                .setIgnoreBy(100)
                .setCompressListener(new CompressListenerImp() {
                    @Override
                    public void onFinish(HashMap<String, String> resultMap) {
                        super.onFinish(resultMap);
                        //  ??????????????????????????????
                        Log.d("LubanPlus", "onFinish : " + resultMap.size());
                        loadCompressImage(resultMap);
                    }
                })
                .launch();
    }

    private void customSizeEnable(boolean enable) {
        binding.llCustomLayout.setVisibility(enable ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * ???????????? createTestImageFile
     * ???????????????????????????????????????????????????
     * ??????????????????????????????
     */
    private void lubanPlusLaunch() {
        String targetDir = getExternalCacheDir().getAbsolutePath();
        // android 11 ?????????????????? error ???open failed: EACCES (Permission denied)
        // ???????????????https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
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
                        // ?????????????????????true:???????????????
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
                        //  ?????????????????????????????????
                        Log.d("LubanPlus", "onStart:" + path);
                    }

                    @Override
                    public void onEnd(String srcPath, String compressPath) {
                        //  ????????????????????????????????????
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
                        //  ????????????????????????????????????
                        Log.d("LubanPlus", "onError srcPath :" + srcPath +
                                " -- exception : " + exception.toString());
                    }

                    @Override
                    public void onFinish(HashMap<String, String> resultMap) {
                        //  ??????????????????????????????
                        Log.d("LubanPlus", "onFinish : " + resultMap.size());
                        loadCompressImage(resultMap);
                    }
                })
                .launch();
    }

    /**
     * ???????????? createTestImageFile
     * ???????????????????????????????????????????????????
     * ???????????? ??????????????????  ?????????
     */
    private void lubanPlusGet() {
        File srcFile = mOriginalImages.get(0);
        String targetDir = getExternalCacheDir().getAbsolutePath();
        // String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
        // ????????????????????????
        String targetFile = LubanPlus.with(this)
                .setTargetDir(targetDir)
                .setQuality(80)
                .setIgnoreBy(100)
                .setFocusAlpha(true)
                .setFilterListener(new IFilterListener() {
                    @Override
                    public boolean isFilter(Furniture furn) {
                        // ?????????????????????true:???????????????
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
     * ???????????? createTestImageFile
     * ???????????????????????????????????????????????????
     * ???????????? ??????????????????  ?????????
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
                        // ?????????????????????true:???????????????
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
        int[] size = getImageSize(file.getAbsolutePath());
        return "?????????" + file.getAbsolutePath() + "\r\n" +
                "?????????" + (file.length()) / 1024 + "KB" +
                " -- ??????" + size[0] + " --??????" + size[1];
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

    public int[] getImageSize(String imagePath) {
        int[] res = new int[2];

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        options.inSampleSize = 1;
        BitmapFactory.decodeFile(imagePath, options);

        res[0] = options.outWidth;
        res[1] = options.outHeight;

        return res;
    }

    private int getSizeByInput(EditText editText) {
        String content = editText.getText().toString();
        int size = -1;
        try {
            size = Integer.parseInt(content);
        } catch (NumberFormatException formatException) {
            formatException.printStackTrace();
        }
        return size;
    }
}