package cn.xdf.lubanplus.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * author:fumm
 * Date : 2022/ 12/ 9 12:23
 * Dec : 文件相关工具类
 **/
public class FileUtils {

    /**
     * 通过 url 转换成File方法；
     * 注意考虑大文件 耗时问题！！
     *
     * @param uri     uri
     * @param context context
     * @return File
     */
    public static File uriToFileApiQ(Uri uri, Context context) {
        File file = null;
        if (uri == null) return file;
        //android10以上转换
        if (uri.getScheme().equals(ContentResolver.SCHEME_FILE)) {
            file = new File(uri.getPath());
        } else if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            // 把文件复制到沙盒目录
            Log.e("LuBanPlus_uriToFileApiQ", "nonsupport uri :" + uri);
//                ContentResolver contentResolver = context.getContentResolver();
//                String displayName = System.currentTimeMillis() + Math.round((Math.random() + 1) * 1000)
//                        + "." + MimeTypeMap.getSingleton().getExtensionFromMimeType(contentResolver.getType(uri));
//
//                try {
//                    InputStream is = contentResolver.openInputStream(uri);
//                    File cache = new File(context.getCacheDir().getAbsolutePath(), displayName);
//                    FileOutputStream fos = new FileOutputStream(cache);
//                    FileUtils.copy(is, fos);
//                    file = cache;
//                    fos.close();
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
        }
        return file;
    }
}
