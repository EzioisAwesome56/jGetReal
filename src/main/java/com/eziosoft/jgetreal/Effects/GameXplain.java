package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import org.imgscalr.Scalr;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class GameXplain extends ImageEffect {

    /**
     * automatically detects and processes the provided image
     * @param in image to process
     * @return processed image
     * @throws IOException if something blows up
     */
    public static EffectResult Watermark(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? Gif(in) : Raster(in), type.contains("gif") ? "gif" : "png");
    }

    /**
     * did you know that circles are made from pi and r*r?
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up during the creation
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Watermark(input);
    }

    public GameXplain(){
        this.name = "gamexplain";
    }

    /**
     * applies gamexplain logo to gif
     * @param in gif to watermark
     * @return watermarked gif
     * @throws IOException if something blows up during processing
     */
    private static byte[] Gif(byte[] in) throws IOException{
        // turn imageio caching off
        ImageIO.setUseCache(false);
        // make list of processed frames
        List<GIFFrame> processed = new ArrayList<>();
        // get all frames of gif
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // we have to process frame 1 separately so we can abuse gif shit
        GIFFrame frame1 = cont.getFrames().get(0);
        cont.getFrames().remove(0);
        // add it to the list of processed frames
        processed.add(new GIFFrame(RasterUtils.ConvertToImage(Raster(RasterUtils.ConvertToBytes(frame1.getFrame()))), frame1.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        // obtain padded frames
        List<BufferedImage> padded = PadAndBorderFrames(cont.getRawFrames());
        // add all the frames to the array
        for (int x = 0; x < padded.size(); x++){
            processed.add(new GIFFrame(padded.get(x), cont.getFrames().get(x).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        // create new animated gif
        return GifUtils.ConvertToBytes(processed);
    }

    /**
     * Pads images to have empty part at the top
     * also applies the gamexplain border to images as well
     * @param frames frames to pad + border
     * @return processed frames, in order
     * @throws IOException if something blows up
     */
    private static List<BufferedImage> PadAndBorderFrames(List<BufferedImage> frames) throws IOException{
        // turn imageio cache off
        ImageIO.setUseCache(false);
        // make new list for processed frames
        List<BufferedImage> processed = new ArrayList<>();
        // load the game xplain border now
        BufferedImage border = RasterUtils.loadResource("/gxborder.png");
        // process each frame, one by one
        for (BufferedImage s : frames){
            // first, create new buffered image
            BufferedImage temp = new BufferedImage(1200, 675, BufferedImage.TYPE_4BYTE_ABGR);
            // create graphics context from that image
            Graphics2D g = temp.createGraphics();
            // scale source buffered image to the correct size
            BufferedImage sourcescale = Scalr.resize(s, Scalr.Mode.FIT_EXACT, 1200, 592);
            // draw scaled source image
            g.drawImage(sourcescale, null, 0, 83);
            // draw border ontop of that
            g.drawImage(border, null, 0, 83);
            // dipose of graphics2d
            g.dispose();
            // add to list
            processed.add(temp);
        }
        // return list
        return processed;
    }

    /**
     * applies gamexplain thumbnail template to an image
     * @param in image to watermark
     * @return watermarked image
     * @throws IOException if something blows up during the process
     */
    private static byte[] Raster(byte[] in) throws IOException{
        // turn imageio cache off
        ImageIO.setUseCache(false);
        // load source image
        BufferedImage source = RasterUtils.ConvertToImage(in);
        // resize source to match dimensions of border
        source = Scalr.resize(source, Scalr.Mode.FIT_EXACT, 1200, 592);
        // load border
        BufferedImage border = RasterUtils.loadResource("/gxborder.png");
        // load logo
        BufferedImage logo = RasterUtils.loadResource("/gxlogo.png");
        // create new buffered image of original gx template size
        BufferedImage product = new BufferedImage(1200, 675, BufferedImage.TYPE_4BYTE_ABGR);
        // create graphics context for it
        Graphics2D g = product.createGraphics();
        // first, draw logo
        g.drawImage(logo, null, 0, 0);
        // then draw source image
        g.drawImage(source, null,0, logo.getHeight());
        // finally, draw the border ontop of the source image
        g.drawImage(border, null, 0, logo.getHeight());
        // get rid of graphics 2d
        g.dispose();
        // convert to byte array and return it
        return RasterUtils.ConvertToBytes(product);
    }
}
