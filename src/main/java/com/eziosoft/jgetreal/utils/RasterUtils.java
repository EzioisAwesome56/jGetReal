package com.eziosoft.jgetreal.utils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.Buffer;

public class RasterUtils {

    /**
     * converts image to jpg format
     * @param in byte array of image to convert
     * @return converted image in jpg format
     * @throws IOException if something blows up
     */
    public static byte[] ConvertToJpeg(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        BufferedImage tojpeg = ImageIO.read(new ByteArrayInputStream(in));
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

    /**
     * crapifies an image by outputing the lowest quality jpeg possible
     * @param in image to crapify
     * @return byte array of crapified image; jpeg format
     * @throws IOException if something blows up
     */
    public static byte[] CrapifyImage(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // prepare jpeg image writers and params
        ImageWriter jpeg = ImageIO.getImageWritersByFormatName("jpg").next();
        ImageWriteParam jpgparam = jpeg.getDefaultWriteParam();
        // setup compression
        jpgparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        jpgparam.setCompressionQuality(0.0f);
        // prepare the stream that will be written to
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageOutputStream imgout = ImageIO.createImageOutputStream(out);
        jpeg.setOutput(imgout);
        // next, use imageIO to produce the "jpeg", first by reading the input image
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(in));
        // create new bufferedimage that is in jpeg format
        BufferedImage jpegout = new BufferedImage(source.getWidth(), source.getHeight(),  BufferedImage.TYPE_INT_RGB);
        // create graphics2d, draw source to it, then dipose of it
        Graphics2D g = jpegout.createGraphics();
        g.drawImage(source, null, 0, 0);
        g.dispose();
        // create new IIOImage object
        IIOImage fuck = new IIOImage(jpegout, null, null);
        // use jpegwriter to write this new image
        jpeg.write(null, fuck, jpgparam);
        // dispose of jpeg writer
        jpeg.dispose();
        // convert to byte array
        byte[] done = out.toByteArray();
        // close streams & return
        out.close();
        imgout.close();
        return done;
    }
}
