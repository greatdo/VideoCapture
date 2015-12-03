package com.videocapture.datachange;

/**
 * Created by lhongqiang on 2015/10/30.
 */
public class NV21ToRgb {

    private static NV21ToRgb instance;

    public NV21ToRgb() {

    }

    public int getRGBarrayLen(int width, int height){
        return width*height*3;
    }
    /***
     *change NV21 data to rgb(888) byte array.
     * @param rgbArray:    rgb(888)
     * @param yuv420sp:     raw NV21(yuv420sp)
     * @param width:        pic width
     * @param height:       pic height
     * rgb byte array must be enough by caller. len = width*height*3
     */
    public void startChange(byte[]rgbArray,byte[]yuv420sp,int width, int height) {
        try {
// 解析YUV成RGB格式
            decodeYUV420SP(rgbArray, yuv420sp, width, height);
            //DataBuffer dataBuffer = new DataBufferByte(byteArray, numBands);
            //WritableRaster wr = Raster.createWritableRaster(sampleModel,dataBuffer, new Point(0, 0));
            //im = new BufferedImage(cm, wr, false, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void decodeYUV420SP(byte[] rgbBuf, byte[] yuv420sp,
                                       int width, int height) {
        final int frameSize = width * height;
        if (rgbBuf == null)
            throw new NullPointerException("buffer 'rgbBuf' is null");
        if (rgbBuf.length < frameSize * 3)
            throw new IllegalArgumentException("buffer 'rgbBuf' size "
                    + rgbBuf.length + " < minimum " + frameSize * 3);

        if (yuv420sp == null)
            throw new NullPointerException("buffer 'yuv420sp' is null");

        if (yuv420sp.length < frameSize * 3 / 2)
            throw new IllegalArgumentException("buffer 'yuv420sp' size "
                    + yuv420sp.length + " < minimum " + frameSize * 3 / 2);

        int i = 0, y = 0;
        int uvp = 0, u = 0, v = 0;
        int y1192 = 0, r = 0, g = 0, b = 0;

        for (int j = 0, yp = 0; j < height; j++) {
            uvp = frameSize + (j >> 1) * width;
            u = 0;
            v = 0;
            for (i = 0; i < width; i++, yp++) {
                y = (0xff & ((int) yuv420sp[yp])) - 16;
                if (y < 0)
                    y = 0;
                if ((i & 1) == 0) {
                    v = (0xff & yuv420sp[uvp++]) - 128;
                    u = (0xff & yuv420sp[uvp++]) - 128;
                }

                y1192 = 1192 * y;
                r = (y1192 + 1634 * v);
                g = (y1192 - 833 * v - 400 * u);
                b = (y1192 + 2066 * u);

                if (r < 0)
                    r = 0;
                else if (r > 262143)
                    r = 262143;
                if (g < 0)
                    g = 0;
                else if (g > 262143)
                    g = 262143;
                if (b < 0)
                    b = 0;
                else if (b > 262143)
                    b = 262143;

                rgbBuf[yp * 3] = (byte) (r >> 10);
                rgbBuf[yp * 3 + 1] = (byte) (g >> 10);
                rgbBuf[yp * 3 + 2] = (byte) (b >> 10);
            }
        }
    }
}
