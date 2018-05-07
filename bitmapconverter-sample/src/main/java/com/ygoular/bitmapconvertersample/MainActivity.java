package com.ygoular.bitmapconvertersample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.ygoular.bitmapconverter.BitmapConverter;
import com.ygoular.bitmapconverter.BitmapFormat;

import java.io.ByteArrayInputStream;
import java.io.IOException;


/**
 * Project : bitmapconverter
 *
 *
 * Sample example to convert a png file from asset to a byte array to bmp file format.
 *
 * Use case 1 : use default convert method to get a byte array to 24-bit color bmp file format
 * Use case 2 : specify the output format to convert method
 *
 */
public class MainActivity extends AppCompatActivity {

    private static final String FILE_NAME = "git_and_github.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView image8bitColor = findViewById(R.id.image_8bit_color);
        ImageView image24bitColor = findViewById(R.id.image_24bit_color);

        try {

            // Colored png file
            Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(FILE_NAME));

            // USE CASE 1
            byte [] bmp24bitColor = BitmapConverter.getInstance().convert(bitmap); // Default format is BITMAP_24_BIT_COLOR

            // USE CASE 2
            byte [] bmp8bitColor = BitmapConverter.getInstance().convert(bitmap, BitmapFormat.BITMAP_8_BIT_COLOR);

            // Gray scaled bmp files
            image8bitColor.setImageDrawable(Drawable.createFromStream(new ByteArrayInputStream(bmp8bitColor), "8-bit"));
            image24bitColor.setImageDrawable(Drawable.createFromStream(new ByteArrayInputStream(bmp24bitColor), "24-bit"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
