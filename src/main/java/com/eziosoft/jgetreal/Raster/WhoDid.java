package com.eziosoft.jgetreal.Raster;

import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class WhoDid {

    /**
     * applies the "WHO DID THIS" border to an image
     * @param in image to border
     * @return image with border, png format
     * @throws IOException if something blows up in the process
     */
    public static byte[] This(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // make an input stream for later
        InputStream streamin;
        // load in the source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load the 2 pieces of the watermark
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_top.png");
        BufferedImage top = Scalr.resize(ImageIO.read(streamin), source.getWidth());
        streamin.close();
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_bottom.png");
        BufferedImage bottom = Scalr.resize(ImageIO.read(streamin), source.getWidth());
        streamin.close();
        // create new buffered image to output the results too
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight() + top.getHeight() + bottom.getHeight(), source.getType());
        // create graphics context for this new image
        Graphics2D g = out.createGraphics();
        // first draw the top
        g.drawImage(top, null, 0, 0);
        // draw source image
        g.drawImage(source, null, 0, top.getHeight());
        // draw the bottom
        g.drawImage(bottom, null, 0, source.getHeight() + top.getHeight());
        // dispose
        g.dispose();
        // write and output result
        ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        ImageIO.write(out, "png", streamout);
        byte[] done = streamout.toByteArray();
        streamin.close();
        return done;
    }

    /**
     * pads image to have empty spaces where the caption would be
     * @param in list of bufferedimages to pad
     * @return padded images
     */
    public static List<BufferedImage> PadFrames(List<BufferedImage> in) throws IOException{
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // load both top and bottom for math reasons
        InputStream streamin;
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_top.png");
        BufferedImage top = Scalr.resize(ImageIO.read(streamin), in.get(0).getWidth());
        streamin.close();
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_bottom.png");
        BufferedImage bottom = Scalr.resize(ImageIO.read(streamin), in.get(0).getHeight());
        streamin.close();
        // create list for output images
        List<BufferedImage> newlist = new ArrayList<>();
        // process all the frames
        for (BufferedImage b : in){
            // create new buffered image and graphics context
            BufferedImage temp = new BufferedImage(b.getWidth(), b.getHeight() + top.getHeight() + bottom.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = temp.createGraphics();
            // draw source frame to it
            g.drawImage(b, null, 0, top.getHeight());
            // dispose
            g.dispose();
            // add to list
            newlist.add(temp);
        }
        // return list
        return newlist;
    }
}
