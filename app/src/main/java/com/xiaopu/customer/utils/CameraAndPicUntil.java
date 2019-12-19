package com.xiaopu.customer.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Administrator on 2017/10/13.
 */

public class CameraAndPicUntil {
    private Activity mContext;

    private static final String IMAGE_PATH = Environment.getExternalStorageDirectory() + "/XiaopuHealth/images";

    private String image_file_name;

    private int output_X = 1000;

    private int output_Y = 1000;

    private int proportion_X = 1;

    private int proportion_Y = 1;

    public CameraAndPicUntil(Activity mContext) {
        this.mContext = mContext;
    }

    public void selectToPicture(int code) {
        Intent mIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//
        mContext.startActivityForResult(mIntent, code);
    }

    public File selectToCamera(int code, String str_image_name) {
        if (str_image_name != null) {
            image_file_name = str_image_name;
        } else {
            image_file_name = System.currentTimeMillis() + ".jpg";
        }
        File cameraFile = new File(IMAGE_PATH, image_file_name);
        if (!cameraFile.getParentFile().exists()) cameraFile.getParentFile().mkdirs();
        Uri imageUri = FileProvider.getUriForFile(mContext, "com.xiaopu.customer.fileprovider", cameraFile);//通过FileProvider创建一个content类型的Uri
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);//设置Action为拍照
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);//将拍取的照片保存到指定URI
        mContext.startActivityForResult(intent, code);
        return cameraFile;
    }

    public File cropRawPhoto(Uri uri, int code) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra("crop", "true");
        if (android.os.Build.MANUFACTURER.contains("HUAWEI")) {// 华为特殊处理 不然会显示圆
            intent.putExtra("aspectX", 9999);
            intent.putExtra("aspectY", 9998);
        } else {
            // aspectX , aspectY :宽高的比例
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
        }
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);

        File cropFile = new File(IMAGE_PATH, System.currentTimeMillis() + ".jpg");
        if (!cropFile.getParentFile().exists()) cropFile.getParentFile().mkdirs();
        Uri imageUri = Uri.fromFile(cropFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true);
        intent.putExtra("return-data", false);
        mContext.startActivityForResult(intent, code);
        return cropFile;
    }

    public String getRealFilePath(final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = mContext.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }


    public void setOutput_X(int output_X) {
        this.output_X = output_X;
    }

    public void setOutput_Y(int output_Y) {
        this.output_Y = output_Y;
    }

    public void setImage_file_name(String image_file_name) {
        this.image_file_name = image_file_name;
    }

    public void setProportion_X(int proportion_X) {
        this.proportion_X = proportion_X;
    }

    public void setProportion_Y(int proportion_Y) {
        this.proportion_Y = proportion_Y;
    }
}
