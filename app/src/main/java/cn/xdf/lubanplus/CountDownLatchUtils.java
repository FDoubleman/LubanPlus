package cn.xdf.lubanplus;

import android.util.Log;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatch 测试类
 * 主要验证 Count的功能
 */
public class CountDownLatchUtils {

//    private  CountDownLatch sLatch = new CountDownLatch(3);

    private static Executor sExecutor = Executors.newCachedThreadPool();

    public static void testExecutor(){
        CountDownLatch sLatch = new CountDownLatch(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            sExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    Log.d("CountDownLatchUtils ", " start thread 1 name : " +
                            Thread.currentThread().getName());
                    try {
                        Thread.sleep(1000 * finalI);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }finally {
                        sLatch.countDown();
                    }
                }
            });
        }
        try {
            sLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("CountDownLatchUtils ", " start thread run name : " +
                Thread.currentThread().getName());
        Log.d("CountDownLatchUtils", "do next");
    }

    public static void test() {
        CountDownLatch sLatch = new CountDownLatch(3);

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("CountDownLatchUtils ", " start thread 1 name : " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sLatch.countDown();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d("CountDownLatchUtils ", " start thread2 name : " +
                        Thread.currentThread().getName());
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                sLatch.countDown();
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d("CountDownLatchUtils ", " start thread3 name : " +
                        Thread.currentThread().getName());
                sLatch.countDown();
            }
        }).start();

        try {
            sLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d("CountDownLatchUtils ", " start thread run name : " +
                Thread.currentThread().getName());
        Log.d("CountDownLatchUtils", "do next");
    }

}
