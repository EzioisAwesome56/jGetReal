package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ImageColorSorter extends ImageEffect {

    /**
     * sorts all the colors of an image
     * @param in image to sort
     * @return sorted image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Sort(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : SortColorsOfImage(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * sorts colors of an image, then returns a image with the colors sorted painted on it
     * @param image image with colors to sort
     * @return byte array of final image; png format;
     * @throws IOException if something blows uo
     */
    private static byte[] SortColorsOfImage(byte[] image) throws IOException {
        // disable imageio cache because thats cringe
        ImageIO.setUseCache(false);
        // get buffered image
        InputStream streamin = new ByteArrayInputStream(image);
        BufferedImage buf = ImageIO.read(streamin);
        streamin.close();
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

    /**
     * sorts colors of an animated gif
     * @param in gif to sort
     * @return sorted gif
     * @throws IOException if something stops working like it should
     */
    private static byte[] Gif(byte[] in) throws IOException{
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // get gif frames as container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create new list for processes frames
        List<GIFFrame> imgs = new ArrayList<>();
        // create reusable stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            // reset stream
            temp.reset();
            // write frame to stream
            ImageIO.write(f.getFrame(), "png", temp);
            // add frame once processed
            ByteArrayInputStream streamin = new ByteArrayInputStream(SortColorsOfImage(temp.toByteArray()));
            imgs.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, f.getDisposalMethod()));
            streamin.close();
        }
        // reset stream
        temp.reset();
        // output gif to the stream
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to array, close, return
        byte[] done = temp.toByteArray();
        temp.close();
        return done;
    }

    /**
     * to be used by effect gui and/or cli and whatever else needs it i guess
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something crashes during the operation
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Sort(input);
    }

    public ImageColorSorter(){
        this.name = "sort";
    }
}
