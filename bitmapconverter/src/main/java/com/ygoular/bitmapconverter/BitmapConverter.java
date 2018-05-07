package com.ygoular.bitmapconverter;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.nio.ByteBuffer;

/**
 * Project : bitmapconverter
 *
 *
 * Offer static methods to convert an Android Bitmap Object to a byte array specifically
 * formatted to represent a .bmp file either in 8-bit mod or 24-bit mod depending on the configuration
 *
 * Useful links to understand bmp files
 * https://www.javaworld.com/article/2077561/learn-java/java-tip-60--saving-bitmap-files-in-java.html
 * http://paulbourke.net/dataformats/bitmaps/
 * https://en.wikipedia.org/wiki/8-bit_color
 * https://en.wikipedia.org/wiki/BMP_file_format
 *
 */
public class BitmapConverter {
    
    private static final int BITMAP_WIDTH_MULTIPLE_OF_CONSTRAINT = 4; // Constraint of bmp format
    private static final int FILE_HEADER_SIZE = 0xE; // Fixed size due to bmp format
    private static final int INFO_HEADER_SIZE = 0x28; // Fixed size for BITMAPINFOHEADER header version (Windows NT, 3,1x or later)

    private static final BitmapFormat BITMAP_DEFAULT_FORMAT = BitmapFormat.BITMAP_24_BIT_COLOR;

    private static final int ASCII_VALUE_B_CC = 0x42;
    private static final int ASCII_VALUE_M_CC = 0x4D;
    
    // Buffer that store data to bmp file format
    private ByteBuffer buffer;

    // Different properties of bmp file format
    private int numberOfColors;
    private int imageDataOffset;
    private int bytePerPixel;
    private int width;
    private int height;
    private int rowWidthInBytes;
    private int imageDataSize;
    private int fileSize;
    private int[] pixels;
    private byte[] dummyBytesPerRow;
    private boolean needPadding;

    public BitmapConverter() { /* Empty constructor */ }

    /**
     * Convert Android Bitmap object into bmp file default format byte array
     * @param bitmap object to convert
     * @return array of byte to bmp file format
     */
    public byte[] convert(@NonNull final Bitmap bitmap) {
        return convert(bitmap, BITMAP_DEFAULT_FORMAT);
    }

    /**
     * Convert Android Bitmap object into bmp file specified format byte array
     * @param bitmap object to convert
     * @param format of the output array
     *
     * @see BitmapFormat
     * @return array of byte to bmp file format
     */
    public byte[] convert(@NonNull final Bitmap bitmap, @NonNull final BitmapFormat format) {
        
        calculateInfoHeaderDataFromFormat(format);
        
        // Image size
        width = bitmap.getWidth();
        height = bitmap.getHeight();

        // An array to receive the pixels from the source image
        pixels = new int[width * height];

        // Row width in bytes
        rowWidthInBytes = bytePerPixel * width; // Source image width * number of bytes to encode one pixel.

        calculatePadding();

        // The number of bytes used in the file to store raw image data (excluding file headers)
        imageDataSize = (rowWidthInBytes + (needPadding ?dummyBytesPerRow.length:0)) * height;

        // Final size of the file
        fileSize = imageDataSize + imageDataOffset;

        // Android Bitmap Image Data
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        // Buffer that will contain the data of the Bitmap
        buffer = ByteBuffer.allocate(fileSize);

        writeFileHeader(); /* BITMAP FILE HEADER */
        writeInfoHeader(format); /* BITMAP INFO HEADER */
        if(numberOfColors != 0) writeColorTable(); /* COLOR PALETTE */
        writeImageData(format); /* IMAGE DATA */

        return buffer.array();
    }

    /**
     * Set the variables depending on the BitmapFormat to convert on the Bitmap object
     * @param format of the bmp array output
     */
    private void calculateInfoHeaderDataFromFormat(@NonNull final BitmapFormat format) {
        // The use of a color palette is only needed for 8-bit color format or less
        numberOfColors = format.getValue() > 8 ? 0 : (int) Math.pow(2, format.getValue());
        int colorTableSize = numberOfColors * 4; // Contains BGR bytes + 0x0 as a separator
        // Offset before image data (contains all required and optional headers or color table)
        imageDataOffset = FILE_HEADER_SIZE + INFO_HEADER_SIZE + colorTableSize;
        bytePerPixel = format.getValue() / 0x8;
    }

    /**
     * The amount of bytes per image row must be a multiple of 4 (requirements of bmp format).
     * If image row width is not a multiple of 4 dummy pixels are created
     */
    private void calculatePadding() {
        if(rowWidthInBytes % BITMAP_WIDTH_MULTIPLE_OF_CONSTRAINT != 0){
            needPadding = true;
            // Dummy bytes that needs to added on each row
            dummyBytesPerRow = new byte[(BITMAP_WIDTH_MULTIPLE_OF_CONSTRAINT - (rowWidthInBytes % BITMAP_WIDTH_MULTIPLE_OF_CONSTRAINT))];
            // Just fill an array with the dummy bytes we need to append at the end of each row
            for(int i = 0; i < dummyBytesPerRow.length; i++){
                dummyBytesPerRow[i] = (byte)0xFF;
            }
        }
    }

    /**
     * Write File header into buffer
     * Represent 14 octets of data
     */
    private void writeFileHeader() {
        // Bitmap specific signature (BM in ASCII)
        buffer.put((byte) ASCII_VALUE_B_CC); // B
        buffer.put((byte) ASCII_VALUE_M_CC); // M

        // Size of the final file
        buffer.put(writeInt(fileSize));

        // Reserved bytes
        buffer.put(writeShort((short)0));
        buffer.put(writeShort((short)0));

        // Image data offset
        buffer.put(writeInt(imageDataOffset));
    }

    /**
     * Write Info header into buffer
     * Represent 40 octets of data
     */
    private void writeInfoHeader(@NonNull final BitmapFormat format) {
        // Size of Info Header
        buffer.put(writeInt(INFO_HEADER_SIZE));

        // width (row) and height (columns) of the image data
        buffer.put(writeInt(width+(needPadding ?(dummyBytesPerRow.length==3?1:0):0)));
        buffer.put(writeInt(height));

        // Color Planes --> must be 1
        buffer.put(writeShort((short)1));

        // Bit count (correspond to the different bmp file format)
        buffer.put(writeShort((short)format.getValue()));

        // Bit compression --> 0 means none
        buffer.put(writeInt(0));

        // Image data size
        buffer.put(writeInt(imageDataSize));

        // Horizontal resolution in pixels per meter
        buffer.put(writeInt(0x0B13));
        // Vertical resolution in pixels per meter
        buffer.put(writeInt(0x0B13));

        // Number of color used --> different of 0 only if a color palette is used (2^n in this case)
        // n corresponding to the number of bits per pixel
        buffer.put(writeInt(numberOfColors));
        // Number of color important --> 0 means all
        buffer.put(writeInt(0x0));

    }

    /**
     * Write Color palette into buffer
     * Represent 1024 octets of data
     */
    private void writeColorTable() {
        // GrayScaled Colors
        for (int i = 0 ; i < numberOfColors ; i++) {
            buffer.put((byte)i);// B
            buffer.put((byte)i);// G
            buffer.put((byte)i);// R
            buffer.put((byte)0x00); // Separator
        }
    }

    /**
     * Write Image data into buffer
     * All the bytes are written starting from the end of the image data
     */
    private void writeImageData(@NonNull final BitmapFormat format) {
        int row = height;
        int col = width;
        int startPosition = (row - 1) * col;
        int endPosition = row * col;
        while( row > 0 ){
            for(int i = startPosition; i < endPosition; i++ )
                writeImageData(pixels[i], format);
            if(needPadding)
                buffer.put(dummyBytesPerRow);

            row--;
            endPosition = startPosition;
            startPosition = startPosition - col;
        }
    }

    /**
     * Write gray scaled image data into a number of bytes depending on the current BitmapFormat
     * !! This will not work for less than 8-bit color formats !!
     * Pixels are just written x number of times
     * @param pixel data
     * @param format of the output byte array
     */
    private void writeImageData(final int pixel, @NonNull final BitmapFormat format) {
        for(int i = 0 ; i < (format.getValue()/8) ; i++)
            buffer.put((byte)(pixel));
    }

    /**
     * Write int (16 bits) in a byte array (little-endian order)
     * @param value to write in a byte array
     * @return the byte array containing the data
     */
    private byte[] writeShort(final short value) {
        byte[] b = new byte[2];

        b[0] = (byte)(value & 0x00FF);
        b[1] = (byte)((value & 0xFF00) >> 8);

        return b;
    }

    /**
     * Write int (32 bits) in a byte array (little-endian order)
     * @param value to write in a byte array
     * @return the byte array containing the data
     */
    private byte[] writeInt(final int value) {
        byte[] b = new byte[4];

        b[0] = (byte)(value & 0x000000FF);
        b[1] = (byte)((value & 0x0000FF00) >> 8);
        b[2] = (byte)((value & 0x00FF0000) >> 16);
        b[3] = (byte)((value & 0xFF000000) >> 24);

        return b;
    }

}
