package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Transparify extends ImageEffect {


    /**
     * detects provided image format and runs it thru correct function
     * @param in image to process
     * @return processed image
     * @throws IOException if something blows up
     */
    public static EffectResult Transparent(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * makes a gif 50% transparent.
     * @param in gif to transparify
     * @return transparent gif
     * @throws IOException if something goes kaboom
     */
    @Deprecated
    private static byte[] Gif(byte[] in) throws IOException{
        /*  FIXME: this doesnt work with gifs like at all, either find out why or
                   remove it entirely! */
        throw new IOException("GIF is not supported by this effect!");
        /*// turn imageio caching off
        ImageIO.setUseCache(false);
        // get all frames of the gif
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // make list of processed frames
        List<GIFFrame> processed = new ArrayList<>();
        // process every frame
        for (GIFFrame f : cont.getFrames()){
            InputStream instream = new ByteArrayInputStream(Raster(RasterUtils.ConvertToBytes(f.getFrame())));
            processed.add(new GIFFrame(ImageIO.read(instream), f.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
            instream.close();
        }
        // return the gif as bytes
        return GifUtils.ConvertToBytes(processed);*/
    }

    /**
     * makes an image 50% transparent...in theory
     * @param in image to transparify
     * @return transparent image
     * @throws IOException if something goes kaboom blam bang oof
     */
    private static byte[] Raster(byte[] in) throws IOException {
        // turn off imageio caching
        ImageIO.setUseCache(false);
        // load buffered image
        InputStream instream = new ByteArrayInputStream(in);
        // use imageio to read it
        BufferedImage source = ImageIO.read(instream);
        instream.close();
        // create new buffered image with transparency enabled
        BufferedImage product = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        // loop thru every pixel, get its color value, set transparency to half, paint to output image
        for (int y = 0; y < source.getHeight(); y++){
            for (int x = 0; x < source.getWidth(); x++){
                // for this pixel; get its color
                Color temp = new Color(source.getRGB(x, y), true);
                // then, create a new color, but set its transparency to 50%
                Color paintcol = new Color(temp.getRed(), temp.getGreen(), temp.getBlue(), temp.getAlpha() / 2);
                // paint it to output buffered image
                product.setRGB(x, y, paintcol.getRGB());
            }
        }
        return RasterUtils.ConvertToBytes(product, "png");
    }

    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Transparent(input);
    }

    public Transparify(){
        this.name = "transparent";
    }
}
