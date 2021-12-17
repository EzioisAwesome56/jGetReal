package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ImageColorSorter {

    /**
     * sorts colors of an image, then returns a image with the colors sorted painted on it
     * @param image image with colors to sort
     * @return byte array of final image; png format;
     * @throws IOException if something blows uo
     */
    public static byte[] SortColorsOfImage(byte[] image) throws IOException {
        // disable imageio cache because thats cringe
        ImageIO.setUseCache(false);
        // get buffered image
        BufferedImage buf = ImageIO.read(new ByteArrayInputStream(image));
        // get each pixel, store it in a list
        List<Integer> pixels = new ArrayList<>();
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                pixels.add(buf.getRGB(x, y));
            }
        }
        // sort list
        Collections.sort(pixels);
        // make new buffered image to redraw the pixels too
        BufferedImage out = new BufferedImage(buf.getWidth(), buf.getHeight(), buf.getType());
        // then actually draw the pixels
        int count = 0;
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                out.setRGB(x, y, pixels.get(count));
                count++;
            }
        }
        // write image to output stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(out, "png", stream);
        // convert stream to byte array
        byte[] finish = stream.toByteArray();
        // get rid of the stream
        stream.flush();
        stream.close();
        // return it
        return finish;
    }

    public static byte[] SortColorsOfImageToJpeg(byte[] image) throws IOException{
        return RasterUtils.ConvertToJpeg(SortColorsOfImage(image));
    }
}
