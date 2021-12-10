package com.eziosoft.jgetreal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.ByteArrayOutputStream;
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
        BufferedImage buf = ImageIO.read(new ByteArrayInputStream(image));
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

    public static byte[] FuzzToJpeg(byte[] image) throws IOException{
        ImageIO.setUseCache(false);
        BufferedImage tojpeg = ImageIO.read(new ByteArrayInputStream(image));
        // create output buffered image
        BufferedImage jpeg = new BufferedImage(tojpeg.getWidth(), tojpeg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // make graphics 2d
        Graphics2D g = jpeg.createGraphics();
        // draw image
        g.drawImage(tojpeg, 0, 0, Color.white, null);
        // dispose of graphics 2d
        g.dispose();
        // write to output stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(jpeg, "jpg", stream);
        // convert to byte array
        byte[] finish = stream.toByteArray();
        // close stream
        stream.flush();
        stream.close();
        // return
        return finish;
    }
}
