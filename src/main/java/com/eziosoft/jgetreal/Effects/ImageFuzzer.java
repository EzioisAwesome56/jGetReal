package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

public class ImageFuzzer {

    /**
     * detects image format and fuzzes it
     * @param in image to fuzz
     * @return fuzzed image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Fuzz(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? FuzzGif(in) : FuzzImage(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * fuzzes the pixels of an image
     * @param image image you want to fuzz
     * @return fuzzed image; png format;
     * @throws IOException if imagio has a stroke
     */
    private static byte[] FuzzImage(byte[] image) throws IOException {
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
     * fuzzes every frame of an animated gif
     * @param in byte array of gif you wish to fuz
     * @return byte array of fuzzed gif
     * @throws IOException if something blows up in the process
     */
    private static byte[] FuzzGif(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        // first we need to get all the frames of the gif
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(streamin);
        streamin.close();
        // make new list of gifFrames
        List<GIFFrame> processed = new ArrayList<>();
        // process each frame
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        for (GIFFrame f : cont.getFrames()){
            // flush old streams
            temp.reset();
            // write to output stream
            ImageIO.write(f.getFrame(), "png", temp);
            // put new frame into the list
            streamin = new ByteArrayInputStream(ImageFuzzer.FuzzImage(temp.toByteArray()));
            processed.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, f.getDisposalMethod()));
            streamin.close();
        }
        // flush old data out of temp
        temp.reset();
        // create the animated gif
        try {
            GIFTweaker.writeAnimatedGIF(processed.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert to byte array
        byte[] finish = temp.toByteArray();
        // close stream
        temp.close();
        // return
        return finish;
    }
}
