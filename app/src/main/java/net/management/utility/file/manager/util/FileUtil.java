package net.management.utility.file.manager.util;

import android.widget.Toast;

import java.io.File;

public class FileUtil {
    public static boolean deleteSingleFile(String filePath$Name) {
        File file = new File(filePath$Name);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                MyUtil.MyLog("--Method--"+ "Copy_Delete.deleteSingleFile: 删除单个文件" + filePath$Name + "成功！");
                return true;
            } else {
                MyUtil.MyLog("删除单个文件" + filePath$Name + "失败！");
                return false;
            }

        } else {
            MyUtil.MyLog( "删除单个文件失败：" + filePath$Name + "不存在！");
            return false;
        }
    }
}
