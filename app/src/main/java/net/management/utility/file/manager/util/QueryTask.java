package net.management.utility.file.manager.util;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import net.management.utility.file.manager.bean.ImageBean;
import net.management.utility.file.manager.bean.VideoBean;
import net.management.utility.file.manager.bean.ZipBean;

import java.io.File;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class QueryTask extends AsyncTask<Void, Void, Void> {
    private Context context;
    private QueryListener listener;
    private ArrayList<ArrayList> list;
    private ArrayList<ImageBean> imageBeans;
    private ArrayList<VideoBean> videoBeans;
    private ArrayList<ZipBean> zipBeans;

    public QueryTask(Context context,QueryListener listener){
        this.context = context;
        this.listener = listener;
        list = new ArrayList<>();
        imageBeans = new ArrayList<>();
        videoBeans = new ArrayList<>();
        zipBeans = new ArrayList<>();
    }

    @Override
    protected Void doInBackground(Void... params) {
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);

        Cursor c = context.getContentResolver().query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
        if (c != null) {
            while (c.moveToNext()) {
                int column_index = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
                String path = c.getString(column_index);

                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    videoBeans.add(new VideoBean(path));
                }
            }
            c.close();
        }
        if (cursor != null) {
            while (cursor.moveToNext()) {
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                String path = cursor.getString(column_index);

                File file = new File(path);
                if (file.exists() && file.isFile()) {
                    imageBeans.add(new ImageBean(path));
                }
            }
            cursor.close();
        }
        findZipFiles(context);
        list.add(imageBeans);
        list.add(videoBeans);
        list.add(zipBeans);
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // 查询完成后触发回调，可以在这里处理查询结果或通知其他组件
        // 例如，可以调用一个回调接口的方法来通知查询完成
        if (listener != null) {
            listener.onQueryComplete(list);
        }
    }

    public void findZipFiles(Context context) {
        String[] projection = {MediaStore.Files.FileColumns.DATA};
        String selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?";
        String[] selectionArgs = {"application/zip"};
        ContentResolver contentResolver = context.getContentResolver();
        Uri uri = MediaStore.Files.getContentUri("external");
        Cursor cursor = contentResolver.query(
                uri,
                projection,
                selection,
                selectionArgs,
                null
        );
        if (cursor != null) {
          //  zip_text.setText(cursor.getCount()+" items");
            while (cursor.moveToNext()) {
                String filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA));
                File file = new File(filePath);
                if (file.exists() && file.isFile()) {
                    String name = file.getName();
                    long size = file.length();  // 获取压缩包的大小，单位为字节
                    double sizeMB = (double) size / (1024 * 1024);
                    String formattedSize;

                    if (sizeMB < 1) {
                        // 如果大小小于1MB，则显示为KB
                        double sizeKB = (double) size / 1024;
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        formattedSize = decimalFormat.format(sizeKB) + "KB";
                    } else {
                        // 如果大小大于等于1MB，则显示为MB
                        DecimalFormat decimalFormat = new DecimalFormat("#0.00");
                        formattedSize = decimalFormat.format(sizeMB) + "MB";
                    }

                    long lastModified = file.lastModified();  // 获取压缩包的最后修改时间，单位为毫秒

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String formattedTime = dateFormat.format(new Date(lastModified));

                    zipBeans.add(new ZipBean(filePath,formattedSize,formattedTime,name));
                }

            }
            cursor.close();
        }
    }

    public interface QueryListener {
        void onQueryComplete(ArrayList<ArrayList> list);
    }
}