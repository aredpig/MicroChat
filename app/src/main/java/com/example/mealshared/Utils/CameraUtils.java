package com.example.mealshared.Utils;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * Camara, Album
 */
public class CameraUtils {

    /**
     * Camara Intent
     * @param context
     * @param outputImagePath
     * @return
     */
    public static Intent getTakePhotoIntent(Context context, File outputImagePath) {
        // activate camara
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //
        if (hasSdcard()) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                // create uri from album
                Uri uri = Uri.fromFile(outputImagePath);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            } else {
                //for android7.0
                ContentValues contentValues = new ContentValues(1);
                contentValues.put(MediaStore.Images.Media.DATA, outputImagePath.getAbsolutePath());
                Uri uri = context.getApplicationContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            }
        }
        return intent;
    }

    /**
     * Album Intent
     * @return
     */
    public static Intent getSelectPhotoIntent() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        return intent;
    }

    /**
     * sdcard
     */
    public static boolean hasSdcard() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    /**
     * 4.4 image processing
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getImageOnKitKatPath(Intent data, Context context) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("uri=intent.getData :", "" + uri);
        if (DocumentsContract.isDocumentUri(context, uri)) {
            //data
            String docId = DocumentsContract.getDocumentId(uri);
            Log.d("getDocumentId(uri) :", "" + docId);
            Log.d("uri.getAuthority() :", "" + uri.getAuthority());
            if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1];
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection, context);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null, context);
            }

        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            imagePath = getImagePath(uri, null, context);
        }
        return imagePath;
    }

    /**
     * through uri and selection get image from album path
     */
    public static String getImagePath(Uri uri, String selection, Context context) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    /**
     * change image proportion
     * @param filepath
     * @param orc_bitmap
     * @param iv
     */
    public static void ImgUpdateDirection(String filepath, Bitmap orc_bitmap, ImageView iv) {
        //image rotate
        int digree = 0;
        //through filepath get a ExifInterface object
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(filepath);
            if (exif != null) {

                // get position information from camara
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

                // calculating rotation proportion
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }

            }
            //
            if (digree != 0) {
                // Rotate image
                Matrix m = new Matrix();
                m.postRotate(digree);
                orc_bitmap = Bitmap.createBitmap(orc_bitmap, 0, 0, orc_bitmap.getWidth(),
                        orc_bitmap.getHeight(), m, true);
            }
            if (orc_bitmap != null) {
                iv.setImageBitmap(orc_bitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
            exif = null;
        }
    }

    /**
     * 4.4
     */
    public static String getImageBeforeKitKatPath(Intent data, Context context) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null, context);
        return imagePath;
    }

    /**
     * image compression
     * @param image
     * @return
     */
    public static Bitmap compression(Bitmap image) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        //
        if (outputStream.toByteArray().length / 1024 > 1024) {
            //
            outputStream.reset();
            //
            image.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        }
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        BitmapFactory.Options options = new BitmapFactory.Options();
        //options.inJustDecodeBounds
        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
        options.inJustDecodeBounds = false;
        int outWidth = options.outWidth;
        int outHeight = options.outHeight;

        float height = 800f;//
        float width = 480f;//


        int zoomRatio = 1;
        if (outWidth > outHeight && outWidth > width) {
            zoomRatio = (int) (options.outWidth / width);
        } else if (outWidth < outHeight && outHeight > height) {
            zoomRatio = (int) (options.outHeight / height);
        }
        if (zoomRatio <= 0) {
            zoomRatio = 1;
        }
        options.inSampleSize = zoomRatio;
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        bitmap = BitmapFactory.decodeStream(inputStream, null, options);

        return bitmap;
    }
}