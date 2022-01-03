package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Jpeg {

    /**
     * reduces the quality of the image as much as possible via jpeg compression
     * @param in image to crapify
     * @return crapified image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Crapify(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? CrapifyGif(in) : CrapifyImage(in), type.contains("gif") ? "gif" : "jpg");
    }

    /**
     * crapifies an image by outputing the lowest quality jpeg possible
     * @param in image to crapify
     * @return byte array of crapified image; jpeg format
     * @throws IOException if something blows up
     */
    private static byte[] CrapifyImage(byte[] in) throws IOException {
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
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
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

    /**
     * crapifies a gif
     * @param in gif to crapify
     * @return crapifyed gif
     * @throws IOException if something blows up in the process
     */
    private static byte[] CrapifyGif(byte[] in) throws IOException {
        // set imageio caching to false
        ImageIO.setUseCache(false);
        // get gif container
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // make list of processed frames
        List<GIFFrame> imgs = new ArrayList<>();
        // make new bytearray output stream for temp space
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            // reset stream
            temp.reset();
            // use imageIO to write to the stream
            ImageIO.write(f.getFrame(), "png", temp);
            // create new gifframe and add it to the list
            streamin = new ByteArrayInputStream(CrapifyImage(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() *  10, f.getDisposalMethod()));
            streamin.close();
        }
        // reset stream
        temp.reset();
        // write our animated gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw new IOException("Error occurred during GIF writing!", e);
        }
        // convert to byte array
        byte[] done = temp.toByteArray();
        // close the stream
        temp.close();
        // return
        return done;
    }
}
