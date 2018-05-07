package com.ygoular.bitmapconverter;

/**
 * Project : bitmapconverter
 *
 *
 * Different bmp file format to be converted on from Bitmap object
 *
 */
public enum BitmapFormat {
    BITMAP_8_BIT_COLOR(8),
    // TODO : BITMAP_16_BIT_COLOR(16),
    BITMAP_24_BIT_COLOR(24);
    // TODO : BITMAP_32_BIT_COLOR(32);

    private int value;

    BitmapFormat(int i) { this.value = i; }

    public int getValue() { return value; }
}
