package id.starkey.pelanggan.Utilities;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import id.starkey.pelanggan.R;

/**
 * Created by Shin on 3/20/2017.
 */

public class ImageUtils {

    private static String TAG = "ImageUtils";

    public static Bitmap convert(String base64Str) throws IllegalArgumentException
    {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String convert(Bitmap bitmap)
    {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    public void LoadCategoryImage(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).resize(120,120).into(image);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri getImageUri(Context context, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    // General Image
    public void LoadRealImage(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).error(R.drawable.kunci).into(image);
    }

    public void LoadRealImage(Context context, String uri, final ImageView image, int thumb){

        Picasso.with(context).load(Uri.parse(uri)).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).error(context.getResources().getDrawable(thumb)).placeholder(context.getResources().getDrawable(thumb)).into(image);
    }

    public void LoadPromoImage(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).into(image);
    }

    public void LoadRealImageNoCache(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).into(image);
    }

    public void LoadRealImage(Context context, int uri, final ImageView image){

        Picasso.with(context).load(uri).into(image);

    }

    public void LoadProfileImage(Context context, String uri, final ImageView image){

        Picasso.with(context).load(Uri.parse(uri)).transform(new CircleTransform()).into(image);
    }

    public void LoadSquareImageHeaderSlider(Context context, String uri, final ImageView image, int size){

        Picasso.with(context).load(Uri.parse(uri)).networkPolicy(NetworkPolicy.NO_CACHE).resize(size, size).memoryPolicy(MemoryPolicy.NO_CACHE).into(image);
    }

    public void LoadCustomSizedImage(Context context, int uri, final ImageView image, int width, int height){

        Picasso.with(context).load(uri).networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE).resize(width, height).centerCrop().into(image);
    }

    public void LoadRealImageWithSmall(Context context, String uri, final ImageView image, Drawable resource, Transformation transformation){

        Picasso.with(context).load(Uri.parse(uri)).transform(transformation).error(resource).placeholder(resource).into(image);
    }

    public void LoadRealImageWithSmall(Context context, String uri, final ImageView image, Transformation transformation){

        Picasso.with(context).load(Uri.parse(uri)).transform(transformation).into(image);
    }

    public void LoadCustomSizedImage(Context context, String uri, final ImageView image, int width, int height){

        Picasso.with(context).load(uri).centerCrop().resize(width, height).into(image);
    }

    public void LoadAdvImage(Context context, String uri, final ImageView image){

        Glide.with(context).load(Uri.parse(uri)).override(360,74).into(image);
    }

    public static Bitmap decodeBitmap(Uri bitmapUri, ContentResolver resolver, int width, int height) throws IOException {
        InputStream is = resolver.openInputStream(bitmapUri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(is,null,options);
        is.close();

        int ratio = Math.min(options.outWidth/width, options.outHeight/height);
        int sampleSize = Integer.highestOneBit((int)Math.floor(ratio));
        if(sampleSize == 0){
            sampleSize = 1;
        }
        Log.d(TAG, "Image Size: " + sampleSize);

        options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;

        is = resolver.openInputStream(bitmapUri);
        Bitmap b = BitmapFactory.decodeStream(is,null,options);
        is.close();
        return b;
    }

}
