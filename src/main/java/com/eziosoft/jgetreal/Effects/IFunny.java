package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;
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
import java.util.Locale;

public class IFunny extends ImageEffect {

    /**
     * applies the ifunny watermark to provided image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * applies the ifunny watermark to an image
     * @param in image to apply watermark too
     * @return watermarked image; jpeg format
     * @throws IOException if something blows up
     */
    private static byte[] Raster(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // read both source image and watermark
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load the ifunny watermark and scale it by source's width
        streamin = IFunny.class.getResourceAsStream("/ifunny.png");
        BufferedImage ifunny = Scalr.resize(ImageIO.read(streamin), source.getWidth());
        streamin.close();
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
        return RasterUtils.ConvertToBytes(out);
    }

    /**
     * pads all bufferedimages in list to have extra space where the watermark would be
     * @param in list of buffered images to pad
     * @return list of padded images
     * @throws IOException if something blows up
     */
    private static List<BufferedImage> PadImages(List<BufferedImage> in) throws IOException{
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

    /**
     * applies ifunny watermark to animated gif
     * @param in animated gif to watermark
     * @return watermarked animated gif
     * @throws IOException if something blows up
     */
    private static byte[] Gif(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // split gif into container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // create list of finished frames
        List<GIFFrame> processed = new ArrayList<>();
        // create object for first frame
        GIFFrame frame1 = cont.getFrames().get(0);
        // remove frame1 from origin list
        cont.getFrames().remove(0);
        // process it and add it to the list
        ByteArrayInputStream tempin = new ByteArrayInputStream(Raster(RasterUtils.ConvertToBytes(frame1.getFrame())));
        processed.add(new GIFFrame(ImageIO.read(tempin), frame1.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        tempin.close();
        // get the padded frames
        List<BufferedImage> e = PadImages(cont.getRawFrames());
        // add all of the frames to the list
        for (int x = 0; x < e.size(); x++) {
            processed.add(new GIFFrame(e.get(x), cont.getFrames().get(x).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        // write gif to it
        return GifUtils.ConvertToBytes(processed);
    }

    /**
     * to be used by the effect gui
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something crashed during operation
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public IFunny(){
        this.name = "ifunny";
    }
}
