package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Locale;

public class ImageFuzzer extends ImageEffect {

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
        // make a list to store each and every pixel
        List<Integer> all = new ArrayList<>();
        // next, get each pixel and hold it in the list
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                all.add(buf.getRGB(x, y));
            }
        }
        // fuzz all the pixels
        Collections.shuffle(all);
        // put the pixels into each row
        int count = 0;
        for (int y = 0; y < buf.getHeight(); y++){
            for (int x = 0; x < buf.getWidth(); x++){
                rows.get(y).add(all.get(count));
                count++;
            }
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
        return RasterUtils.ConvertToBytes(out);
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
        for (GIFFrame f : cont.getFrames()){
            // put new frame into the list
            streamin = new ByteArrayInputStream(FuzzImage(RasterUtils.ConvertToBytes(f.getFrame())));
            processed.add(new GIFFrame(ImageIO.read(streamin), f.getDelay() * 10, f.getDisposalMethod()));
            streamin.close();
        }
        // create the animated gif
        return GifUtils.ConvertToBytes(processed);
    }

    /**
     * to be used for gui or whatever else idfk youve seen this a million times already
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Fuzz(input);
    }

    public ImageFuzzer(){
        this.name = "fuzz";
    }
}
