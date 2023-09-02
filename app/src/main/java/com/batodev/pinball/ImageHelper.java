package com.batodev.pinball;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;

import androidx.annotation.NonNull;

import com.dozingcatsoftware.bouncy.BouncyActivity;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ImageHelper {

    public static final String PRIZE_IMAGES_10_K = "prize-images-10k";
    public static final String PRIZE_IMAGES_100_K = "prize-images-100k";
    public static final String NAME = "name";
    public static final String BITMAP = "bitmap";

    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, (float) pixels, (float) pixels, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    static List<String> IMG_PATHS = List.of(PRIZE_IMAGES_10_K, PRIZE_IMAGES_100_K);

    public static Map<String, Object> random10kBitmap(BouncyActivity bouncyActivity) {
        return pickImage(bouncyActivity, PRIZE_IMAGES_10_K);
    }

    private static Map<String, Object> pickImage(BouncyActivity bouncyActivity, String imagePath) {
        List<String> uncoveredPics = new ArrayList<>(new SettingsHelper(bouncyActivity).getPreferences().getUncoveredPics());
        List<String> allPrizeImg;
        try {
            allPrizeImg = Arrays.stream(Objects.requireNonNull(bouncyActivity.getAssets().list(imagePath))).collect(Collectors.toList());
            allPrizeImg.removeAll(uncoveredPics);
            String prizeImage = allPrizeImg.isEmpty() ? uncoveredPics.get(new Random().nextInt(uncoveredPics.size())) : allPrizeImg.get(new Random().nextInt(allPrizeImg.size()));
            Log.d(ImageHelper.class.getSimpleName(), "prizeImage: " + prizeImage);
            String imageFolder = findPathForImage(bouncyActivity.getAssets(), prizeImage);
            Log.d(ImageHelper.class.getSimpleName(), "imageFolder: " + imageFolder);
            InputStream stream = bouncyActivity.getAssets().open(imageFolder + File.separator + prizeImage);
            Bitmap bitmap = BitmapFactory.decodeStream(stream);
            stream.close();
            return Map.of(BITMAP,
                    getRoundedCornerBitmap(bitmap, 100),
                    NAME, prizeImage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Object> random100kBitmap(BouncyActivity bouncyActivity) {
        return pickImage(bouncyActivity, PRIZE_IMAGES_100_K);
    }

    @NotNull
    public static Bitmap findBitmap(@NotNull String imgName, @NotNull GalleryActivity galleryActivity) {
        String imageFolder = findPathForImage(galleryActivity.getAssets(), imgName);
        try {
            return BitmapFactory.decodeStream(galleryActivity.getAssets().open(imageFolder + File.separator + imgName));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String findPathForImage(AssetManager galleryActivity, @NonNull String imgName) {
        String imageFolder = IMG_PATHS.stream().filter(imgPath -> {
            try {
                String[] list = galleryActivity.list(imgPath);
                List<String> fileList = Arrays.asList(Objects.requireNonNull(list));
                Log.d(ImageHelper.class.getSimpleName(), "fileList: " + fileList);
                return fileList.contains(imgName);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).findAny().orElseThrow(() -> new IllegalStateException("Uncovered image " + imgName + " is nowhere to found?"));
        return imageFolder;
    }
}