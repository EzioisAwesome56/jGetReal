package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;
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
        // next, we need to convert the frame data to a byte array
        ByteArrayOutputStream tempout = new ByteArrayOutputStream();
        ImageIO.write(frame1.getFrame(), "png", tempout);
        // then we need an input stream to process the first frame
        InputStream instream = new ByteArrayInputStream(Raster(tempout.toByteArray()));
        tempout.reset();
        // add it to the list of processed frames
        processed.add(new GIFFrame(ImageIO.read(instream), frame1.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        instream.close();
        // obtain padded frames
        List<BufferedImage> padded = PadAndBorderFrames(cont.getRawFrames());
        // add all the frames to the array
        for (int x = 0; x < padded.size(); x++){
            processed.add(new GIFFrame(padded.get(x), cont.getFrames().get(x).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        // create new animated gif
        try {
            GIFTweaker.writeAnimatedGIF(processed.toArray(new GIFFrame[]{}), tempout);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert, close, return
        byte[] done = tempout.toByteArray();
        tempout.close();
        return done;
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
        InputStream in = GameXplain.class.getResourceAsStream("/gxborder.png");
        BufferedImage border = ImageIO.read(in);
        in.close();
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
        InputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // resize source to match dimensions of border
        source = Scalr.resize(source, Scalr.Mode.FIT_EXACT, 1200, 592);
        // load border
        streamin = GameXplain.class.getResourceAsStream("/gxborder.png");
        BufferedImage border = ImageIO.read(streamin);
        streamin.close();
        // load logo
        streamin = GameXplain.class.getResourceAsStream("/gxlogo.png");
        BufferedImage logo = ImageIO.read(streamin);
        streamin.close();
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
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(product, "png", out);
        byte[] done = out.toByteArray();
        out.close();
        return done;
    }
}
