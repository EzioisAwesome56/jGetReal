package com.eziosoft.jgetreal.Raster;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class IFunny {

    /**
     * applies the ifunny watermark to an image
     * @param in image to apply watermark too
     * @return watermarked image; jpeg format
     * @throws IOException if something blows up
     */
    public static byte[] Watermark(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // read both source image and watermark
        ByteArrayInputStream dankfuck = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(dankfuck);
        dankfuck.close();
        // load the ifunny watermark and scale it by source's width
        InputStream instream = IFunny.class.getResourceAsStream("/ifunny.png");
        BufferedImage ifunny = Scalr.resize(ImageIO.read(instream), source.getWidth());
        instream.close();
        // create new output buffered image
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight() + ifunny.getHeight(), source.getType());
        // create graphics 2d for buffered image
        Graphics2D g = out.createGraphics();
        // first draw the source image
        g.drawImage(source, null, 0, 0);
        // then draw the watermark
        g.drawImage(ifunny, null, 0, source.getHeight());
        // dispose of graphics
        g.dispose();
        // create output stream, write to it, dispose of it, return
        ByteArrayOutputStream dank = new ByteArrayOutputStream();
        ImageIO.write(out, "png", dank);
        byte[] done = dank.toByteArray();
        dank.close();
        return done;
    }

    /**
     * pads all bufferedimages in list to have extra space where the watermark would be
     * @param in list of buffered images to pad
     * @return list of padded images
     * @throws IOException if something blows up
     */
    public static List<BufferedImage> PadImages(List<BufferedImage> in) throws IOException{
        // turn imageio cache off
        ImageIO.setUseCache(false);
        // load the watermark, and scale it by the first image in the array
        InputStream instream = IFunny.class.getResourceAsStream("/ifunny.png");
        BufferedImage ifunny = Scalr.resize(ImageIO.read(instream), in.get(0).getWidth());
        instream.close();
        // make new list
        List<BufferedImage> processed = new ArrayList<>();
        // make new image object
        BufferedImage temp;
        // process the frames
        for (BufferedImage i : in){
            // create new image
            temp = new BufferedImage(i.getWidth(), i.getHeight() + ifunny.getHeight(), BufferedImage.TYPE_INT_ARGB);
            // create graphics context
            Graphics2D g = temp.createGraphics();
            // draw source to it
            g.drawImage(i, null, 0, 0);
            // dipose
            g.dispose();
            // add to list
            processed.add(temp);
        }
        // return list
        return processed;
    }
}
