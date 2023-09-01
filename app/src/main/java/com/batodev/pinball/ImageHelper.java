package com.batodev.pinball;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.dozingcatsoftware.bouncy.BouncyActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public class ImageHelper {

    public static final String PRIZE_IMAGES_10_K = "prize-images-10k";

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

    public static Bitmap random10kBitmap(BouncyActivity bouncyActivity) {
        List<String> uncoveredPics = new SettingsHelper(bouncyActivity).getPreferences().getUncoveredPics();
        List<String> allPrizeImg;
        try {
            allPrizeImg = Arrays.stream(Objects.requireNonNull(bouncyActivity.getAssets().list(PRIZE_IMAGES_10_K))).collect(Collectors.toList());
            allPrizeImg.removeAll(uncoveredPics);
            String prizeImage = allPrizeImg.isEmpty() ? uncoveredPics.get(new Random().nextInt(uncoveredPics.size())) : allPrizeImg.get(new Random().nextInt(allPrizeImg.size()));
            return getRoundedCornerBitmap(BitmapFactory.decodeStream(bouncyActivity.getAssets().open(PRIZE_IMAGES_10_K + File.separator + prizeImage)), 100);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}