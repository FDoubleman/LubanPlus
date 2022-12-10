package cn.xdf.lubanplus;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import cn.xdf.lubanplus.listener.CompressListenerImp;
import cn.xdf.lubanplus.listener.ICompressListener;
import cn.xdf.lubanplus.listener.IFilterListener;

import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                createTestImageFile("test_1.png");
//                createTestImageFile("test_2.jpeg");
//                createTestImageFile("test_5.png");
//                createTestImageFile("test_7.jpg");
//                createTestImageFile("test_8.jpg");
                testLaunch();
            }
        });
        initPermission();
    }


    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 之后验证测试异步压缩
     */
    private void testLaunch() {
        File srcFile1 = new File(getExternalFilesDir(null), "test_1.png");
        File srcFile2 = new File(getExternalFilesDir(null), "test_2.jpeg");
        File srcFile5 = new File(getExternalFilesDir(null), "test_5.png");
        File srcFile7 = new File(getExternalFilesDir(null), "test_7.jpg");
        File srcFile8 = new File(getExternalFilesDir(null), "test_8.jpg");

        List<File> list = new ArrayList<>();
        list.add(new File(""));
        list.add(new File(""));
        list.add(srcFile1);
        list.add(srcFile2);
        list.add(srcFile5);
        list.add(srcFile7);
        list.add(srcFile8);
        String targetDir =getExternalCacheDir().getAbsolutePath();
        // android 11 及以后的版本 error ：open failed: EACCES (Permission denied)
        // 更多信息：https://stackoverflow.com/questions/8854359/exception-open-failed-eacces-permission-denied-on-android
        //         https://developer.android.com/training/data-storage#scoped-storage
        // String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
        LubanPlus.with(this)
                .load(list)
                .setFocusAlpha(true)
                .setTargetDir(targetDir)
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
    }

    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 同步方法 压缩单个图片  测试类
     */
    private void testGet() {
        File srcFile = new File(getExternalFilesDir(null), "test_8.jpg");
        String targetDir =getExternalCacheDir().getAbsolutePath();
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
        Log.d("LubanPlus", "testGet : " +targetFile);


    }

    /**
     * 首先通过 createTestImageFile
     * 按需创建测试图片下面要使用到的图片
     * 同步方法 压缩多个图片  测试类
     */
    private void testListGet(){
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
    }

    public void createTestImageFile(String assetsFileName) {
        InputStream is = null;
        try {
            is = getResources().getAssets().open(assetsFileName);
            // path : /storage/emulated/0/Android/data/cn.xdf.lubanplus/files/assetsFileName
            File file = new File(getExternalFilesDir(null),assetsFileName );
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
    }

    public File getImageCacheFile(String suffix) {
        String targetDir = Environment.getDataDirectory().getAbsolutePath() +
                File.separator + "fmm";

        String cacheBuilder = targetDir + "/" +
                System.currentTimeMillis() +
                (int) (Math.random() * 1000) +
                (TextUtils.isEmpty(suffix) ? ".jpg" : "." + suffix);
        return new File(cacheBuilder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
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