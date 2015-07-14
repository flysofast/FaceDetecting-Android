package com.forloexample.facedetect;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;

public class ImageTester {

    public interface ImageColor {
        void onImageColor(int r, int g, int b);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static void getMostCommonColour(final Bitmap image,
            final ImageColor heColor) {
        new Thread(new Runnable() {
            private int rgb;

            @Override
            public void run() {
                int height = image.getHeight();
                int width = image.getWidth();
                Map m = new HashMap();
                int boderWid = width / 4;
                int borderHeigh = height / 4;

                for (int i = boderWid; i < width - boderWid;) {
                    for (int j = borderHeigh; j < height - borderHeigh;) {
                        try {
                            rgb = image.getPixel(i, j);

                        } catch (Exception e) {
                            continue;
                        }finally{
                            i += 20;
                            j += 20;
                        }
                        int[] rgbArr = getRGBArr(rgb);
                        // Filter out grays....
                        if (!isGray(rgbArr)) {
                            Integer counter = (Integer) m.get(rgb);
                            if (counter == null)
                                counter = 0;
                            counter++;
                            m.put(rgb, counter);

                        }

                    }
                }
                List list = new LinkedList(m.entrySet());
                Collections.sort(list, new Comparator() {
                    public int compare(Object o1, Object o2) {
                        return ((Comparable) ((Map.Entry) (o1)).getValue())
                                .compareTo(((Map.Entry) (o2)).getValue());
                    }
                });
                Map.Entry me = (Map.Entry) list.get(list.size() - 1);
                int[] rgb = getRGBArr((Integer) me.getKey());
                heColor.onImageColor(rgb[0], rgb[1], rgb[2]);

            }
        }).start();
    }

    public static int[] getRGBArr(int pixel) {
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[] { red, green, blue };

    }

    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance)
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        return true;
    }
}
