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
import cn.xdf.lubanplus.compress.ImageCompressListenerImp;
import cn.xdf.lubanplus.databinding.ActivityMainBinding;

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
//                testCompressImage();
//                createTestImageFile();
                testQulity();
//                getImageCacheFile("png");
//                File file = new File(getExternalFilesDir(null), "test_2.png");
//                Log.d("fmm", "createTestImageFile");
            }
        });
        initPermission();
    }


    public void testCompressImage() {
        File srcFile1 = new File(getExternalFilesDir(null), "test_1.png");
        File srcFile2 = new File(getExternalFilesDir(null), "test_2.jpeg");
//        File srcFile3 = new File(getExternalFilesDir(null), "test_4.jpg");
        List<String> paths = new ArrayList<>();
        paths.add(srcFile1.getAbsolutePath());
        paths.add(srcFile2.getAbsolutePath());

        paths.add(srcFile1.getAbsolutePath());
        paths.add(srcFile2.getAbsolutePath());
        paths.add(srcFile1.getAbsolutePath());
        paths.add(srcFile2.getAbsolutePath());

//        paths.add(srcFile3.getAbsolutePath());


        ImageCompress.compress(getApplication(),
                paths, 1000, true, new IImageCompressListener() {
                    @Override
                    public void onStart(String filePath) {
                        Log.d("fmm", "onstar : path --> " + filePath);
                    }

                    @Override
                    public void onFinish(File file) {
                        Log.d("fmm", "onFinish : path --> " + file.getAbsolutePath());
                    }

                    @Override
                    public void onError(String filePath, Exception exception) {
                        Log.d("fmm", "onError : path --> " + filePath);
                    }

                    @Override
                    public void onEnd(HashMap<String, File> files) {
                        Log.d("fmm", "onEnd : size --> " + files.size());
                    }
                });
//        ImageCompress.compress(getApplication(),paths,1000,true,
//                new ImageCompressListenerImp(){
//            @Override
//            public void onEnd(HashMap<String, File> files) {
//                super.onEnd(files);
//            }
//        });
    }

    public void testCountDownLatch() {
        Log.d("testCountDownLatch", "start !!!");
        new Thread(new Runnable() {
            @Override
            public void run() {
                CountDownLatchUtils.testExecutor();
            }
        }).start();
        Log.d("testCountDownLatch", "end !!!");
    }

    private void testQulity() {
        File srcFile1 = new File(getExternalFilesDir(null), "test_1.png");
        File srcFile2 = new File(getExternalFilesDir(null), "test_2.jpeg");
        File srcFile5 = new File(getExternalFilesDir(null), "test_5.png");
        File srcFile7 = new File(getExternalFilesDir(null), "test_7.jpg");
        File srcFile8 = new File(getExternalFilesDir(null), "test_8.jpg");
//        File srcFile3 = new File(getExternalFilesDir(null), "test_4.jpg");
        List<File> list = new ArrayList<>();
        list.add(srcFile1);
        list.add(srcFile2);
        list.add(srcFile5);
        list.add(srcFile7);
        list.add(srcFile8);

        //  验证  Alpha 透明度通道 与保持图片格式的 关系
        //  Alpha : true      false

//        Furniture furniture = LubanPlus.with(this)
//                .setQuality(80)
//                .setNeedLoopCompress(true)
//                .setFocusAlpha(false)
//                .get(srcFile.getAbsolutePath());
//        Log.d("fumm", "target path: " + furniture.getTargetAbsolutePath() +
//                "   src file size :"+ furniture.getSrcLength()+
//                "   target file size : "+ furniture.getTargetLenth() );

//        List<Furniture> listFurn = LubanPlus.with(this)
//                .load(list)
//                .setNeedLoopCompress(true)
//                .get();
        LubanPlus.with(this)
                .load(list)
                .setNeedLoopCompress(true)
                .setFocusAlpha(false)
                .setCompressListener(new ICompressListener() {

                    @Override
                    public void onStart(String path) {
                        Log.d("Luban", "onStart:" + path);
                    }

                    @Override
                    public void onSuccess(String srcPath, String compressPath) {
                        Log.d("fumm", "srcPath path: " + srcPath +
                                "   compressPath file size :" + compressPath);
                    }

                    @Override
                    public void onError(String srcPath, Exception exception) {
                        Log.d("Luban", "onError srcPath :" + srcPath +
                                " -- exception : "+exception.toString());
                    }

                    @Override
                    public void onEnd(Map<String, String> resultMap) {
                        Log.d("fumm", "onEnd : " + resultMap.size());
                    }
                })
                .launch();

//        for (Furniture furniture : listFurn) {
//            Log.d("fumm", "target path: " + furniture.getTargetAbsolutePath() +
//                    "   src file size :" + furniture.getSrcLength() +
//                    "   target file size : " + furniture.getTargetLenth());
//        }

//        for (int i = 0; i < listFurn.size(); i++) {
//            Furniture furniture = LubanPlus.with(this)
//                    .setQuality(100 - (10 * i))
//                    .setFocusAlpha(false)
//                    .get(list.get(i).getAbsolutePath());
//            furniture.getTargetAbsolutePath();
//            Log.d("fumm", "target path: " + furniture.getTargetAbsolutePath() +
//                    "   src file size :"+ furniture.getSrcLength()+
//                    "   target file size : "+ furniture.getTargetLenth() );
//        }

    }


    private void testLubanPlus() {
//        File srcFile = new File(getExternalFilesDir(null), "test_1.png");
//        File srcFile = new File(getExternalFilesDir(null), "test_3.jpeg");
        File srcFile = new File(getExternalFilesDir(null), "test_4.jpg");
        List<File> list = new ArrayList<>();
        list.add(srcFile);
        list.add(srcFile);
        list.add(srcFile);
        list.add(srcFile);
//        List<File> files = LubanPlus.with(this)
//                .load(srcFile)
//                .get();

//        File file1 = LubanPlus.with(this).get(srcFile.getAbsolutePath());


//        LubanPlus.with(this).load(list)
//                .setCompressListener(new ICompressListener() {
//                    @Override
//                    public void onStart() {
//                        Log.d("Luban", "onStart");
//                    }
//
//                    @Override
//                    public void onSuccess(Furniture furn) {
//                        Log.d("Luban", "onSuccess file targetDir: " + furn.getTargetAbsolutePath());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("Luban", "onStart e :" + e.toString());
//                    }
//                }).launch();


        // LubanPlus.with(this).load("").get()

//         String targetDir = this.getCacheDir().getAbsolutePath()+File.separator + "LuBanPlus";
        String targetDir = Environment.getDownloadCacheDirectory().getAbsolutePath();
        LubanPlus.with(this)
                .load(list)
                .setCompressListener(new ICompressListener() {
                    @Override
                    public void onStart(String path) {
                        Log.d("Luban", "onStart:" + path);
                    }

                    @Override
                    public void onSuccess(String srcPath, String compressPath) {
                        Log.d("fumm", "srcPath path: " + srcPath +
                                "   compressPath file size :" + compressPath);
                    }

                    @Override
                    public void onError(String srcPath, Exception exception) {
                        Log.d("Luban", "onError srcPath :" + srcPath +
                                " -- exception : "+exception.toString());
                    }

                    @Override
                    public void onEnd(Map<String, String> resultMap) {
                        Log.d("fumm", "onEnd : " + resultMap.size());
                    }
                }).setFilterListener(new IFilterListener() {
                    @Override
                    public boolean isFilter(Furniture furn) {
                        Log.d("Luban", "isFilter file targetDir: " + furn.getSrcAbsolutePath());
                        return false;
                    }
                }).setFocusAlpha(false)
                .setTargetDir(targetDir)
                .setIgnoreBy(110)
                .launch();


    }

    public void createTestImageFile() {
        InputStream is = null;
        try {
            is = getResources().getAssets().open("test_2.jpeg");
            File file = new File(getExternalFilesDir(null), "test_2.jpeg");
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