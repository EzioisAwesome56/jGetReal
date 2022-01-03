package com.eziosoft.jgetreal.Utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RasterUtils {

    /**
     * converts image to jpg format
     * @param in byte array of image to convert
     * @return converted image in jpg format
     * @throws IOException if something blows up
     */
    public static byte[] ConvertToJpeg(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage tojpeg = ImageIO.read(streamin);
        streamin.close();
        // create output buffered image
        BufferedImage jpeg = new BufferedImage(tojpeg.getWidth(), tojpeg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // make graphics 2d
        Graphics2D g = jpeg.createGraphics();
        // draw image
        g.drawImage(tojpeg, 0, 0, null);
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

    /**
     * converts input raster to webp
     * @param in raster to convert
     * @return webp
     * @throws IOException if something blows up
     */
    public static byte[] ConvertToWebp(byte[] in) throws IOException{
        ImageIO.setUseCache(false);
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage tojpeg = ImageIO.read(streamin);
        streamin.close();
        // create output buffered image
        BufferedImage jpeg = new BufferedImage(tojpeg.getWidth(), tojpeg.getHeight(), BufferedImage.TYPE_INT_RGB);
        // make graphics 2d
        Graphics2D g = jpeg.createGraphics();
        // draw image
        g.drawImage(tojpeg, 0, 0, null);
        // dispose of graphics 2d
        g.dispose();
        // write to output stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ImageIO.write(jpeg, "webp", stream);
        // convert to byte array
        byte[] finish = stream.toByteArray();
        // close stream
        stream.flush();
        stream.close();
        // return
        return finish;
    }

    /**
     * Mirros an image
     * @param in bufferedimage to mirror
     * @return mirrored buffered image
     */
    public static BufferedImage MirrorImage(BufferedImage in){
        // setup transform settings
        AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
        tx.translate(-in.getWidth(), 0);
        // setup operation
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(in, null);
    }

    /**
     * flips image verically
     * @param in image to flip
     * @return flipped image
     */
    public static BufferedImage VerticalFlip(BufferedImage in){
        // setup transform settings
        AffineTransform tx = AffineTransform.getScaleInstance(1, -1);
        tx.translate(0, -in.getHeight());
        // setup operation
        AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return op.filter(in, null);
    }
}
