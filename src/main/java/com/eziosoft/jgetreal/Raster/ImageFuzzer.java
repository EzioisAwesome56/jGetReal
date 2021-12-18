package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ImageFuzzer {

    /**
     * fuzzes the pixels of an image
     * @param image image you want to fuzz
     * @return fuzzed image; png format;
     * @throws IOException if imagio has a stroke
     */
    public static byte[] FuzzImage(byte[] image) throws IOException {
        ImageIO.setUseCache(false);
        // first convert array to buffered image
        InputStream streamin = new ByteArrayInputStream(image);
        BufferedImage buf = ImageIO.read(streamin);
        streamin.close();
        // make list to hold all rows
        List<List<Integer>> rows = new ArrayList<>();
        // fill list with empty lists
        for (int x = 0; x < buf.getHeight(); x++){
            rows.add(new ArrayList<Integer>());
        }
        // next, get each pixel and hold it in the list
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                rows.get(y).add(buf.getRGB(x, y));
            }
        }
        // fuzz each list of pixels
        for (List<Integer> list : rows){
            Collections.shuffle(list);
        }
        // also fuzz the rows themselves
        Collections.shuffle(rows);
        // create new bufferedimage for output
        BufferedImage out = new BufferedImage(buf.getWidth(), buf.getHeight(), buf.getType());
        // output fuzzed data to buffered image
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                out.setRGB(x, y, rows.get(y).get(x));
            }
        }
        // create stream to write too
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        // write to it
        ImageIO.write(out, "png", stream);
        // convert to byte array
        byte[] result = stream.toByteArray();
        // close stream
        stream.flush();
        stream.close();
        // return data
        return result;
    }

    /**
     * fuzzed an image and returns it in jpeg format
     * @param image byte array of image to fuzz
     * @return byte array of fuzzed image; jpg format
     * @throws IOException if imageio or something else explodes
     */
    public static byte[] FuzzToJpeg(byte[] image) throws IOException{
        return RasterUtils.ConvertToJpeg(FuzzImage(image));
    }
}
