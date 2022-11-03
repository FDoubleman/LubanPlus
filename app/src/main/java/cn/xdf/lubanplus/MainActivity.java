package cn.xdf.lubanplus;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

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

import cn.xdf.lubanplus.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
//                createTestImageFile();
                testLubanPlus();
//                getImageCacheFile("png");
//                File file = new File(getExternalFilesDir(null), "test_2.png");
                Log.d("fmm", "createTestImageFile");
            }
        });
        initPermission();
    }


    private void testLubanPlus() {
        File srcFile = new File(getExternalFilesDir(null), "test_1.png");
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
//                        Log.d("Luban", "onSuccess file path: " + furn.getTargetAbsolutePath());
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.d("Luban", "onStart e :" + e.toString());
//                    }
//                }).launch();


        // LubanPlus.with(this).load("").get()


        LubanPlus.with(this)
                .load(list)
                .setCompressListener(new ICompressListener() {
                    @Override
                    public void onStart() {
                        Log.d("Luban", "onStart");
                    }

                    @Override
                    public void onSuccess(Furniture furn) {
                        Log.d("Luban", "onSuccess file path: " + furn.getTargetAbsolutePath());
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d("Luban", "onStart e :" + e.toString());
                    }
                }).setFilterListener(new IFilterListener() {
                    @Override
                    public boolean isFilter(Furniture furn) {
                        Log.d("Luban", "isFilter file path: " + furn.getSrcAbsolutePath());
                        return false;
                    }
                }).setFocusAlpha(false)
                .setTargetDir(null)
                .setIgnoreBy(110)
                .launch();


    }

    public void createTestImageFile() {
        InputStream is = null;
        try {
            is = getResources().getAssets().open("test_1.png");
            File file = new File(getExternalFilesDir(null), "test_1.png");

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