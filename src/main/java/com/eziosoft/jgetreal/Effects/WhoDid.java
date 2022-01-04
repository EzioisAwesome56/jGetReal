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

public class WhoDid extends ImageEffect {

    /**
     * detects image format and applies who did this meme to it
     * @param in image to meme
     * @return meme'd image
     * @throws IOException if something blows up anywhere during this mess
     */
    public static EffectResult Meme(byte[] in) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? GifThis(in) : This(in), "gif");
    }

    /**
     * applies the "WHO DID THIS" border to an image
     * @param in image to border
     * @return image with border, png format
     * @throws IOException if something blows up in the process
     */
    private static byte[] This(byte[] in) throws IOException {
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // make an input stream for later
        InputStream streamin;
        // load in the source image
        streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // load the 2 pieces of the watermark
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_top.png");
        BufferedImage top = Scalr.resize(ImageIO.read(streamin), source.getWidth());
        streamin.close();
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_bottom.png");
        BufferedImage bottom = Scalr.resize(ImageIO.read(streamin), source.getWidth());
        streamin.close();
        // create new buffered image to output the results too
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight() + top.getHeight() + bottom.getHeight(), source.getType());
        // create graphics context for this new image
        Graphics2D g = out.createGraphics();
        // first draw the top
        g.drawImage(top, null, 0, 0);
        // draw source image
        g.drawImage(source, null, 0, top.getHeight());
        // draw the bottom
        g.drawImage(bottom, null, 0, source.getHeight() + top.getHeight());
        // dispose
        g.dispose();
        // write and output result
        ByteArrayOutputStream streamout = new ByteArrayOutputStream();
        ImageIO.write(out, "png", streamout);
        byte[] done = streamout.toByteArray();
        streamin.close();
        return done;
    }

    /**
     * pads image to have empty spaces where the caption would be
     * @param in list of bufferedimages to pad
     * @return padded images
     * @throws IOException if something blows up during the process
     */
    private static List<BufferedImage> PadFrames(List<BufferedImage> in) throws IOException{
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // load both top and bottom for math reasons
        InputStream streamin;
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_top.png");
        BufferedImage top = Scalr.resize(ImageIO.read(streamin), in.get(0).getWidth());
        streamin.close();
        streamin = WhoDid.class.getResourceAsStream("/whodidthis_bottom.png");
        BufferedImage bottom = Scalr.resize(ImageIO.read(streamin), in.get(0).getHeight());
        streamin.close();
        // create list for output images
        List<BufferedImage> newlist = new ArrayList<>();
        // process all the frames
        for (BufferedImage b : in){
            // create new buffered image and graphics context
            BufferedImage temp = new BufferedImage(b.getWidth(), b.getHeight() + top.getHeight() + bottom.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = temp.createGraphics();
            // draw source frame to it
            g.drawImage(b, null, 0, top.getHeight());
            // dispose
            g.dispose();
            // add to list
            newlist.add(temp);
        }
        // return list
        return newlist;
    }

    /**
     * adds the "WHO DID THIS" border to a gif
     * @param in gif to apply effect to
     * @return gif with effect applied
     * @throws IOException if something blows up
     */
    private static byte[] GifThis(byte[] in) throws IOException {
        // set imageio cache to false
        ImageIO.setUseCache(false);
        // split gif to container
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // get the first frame, and remove it from the list
        GIFFrame frameone = cont.getFrames().get(0);
        cont.getFrames().remove(0);
        // for later: make list of new frames
        List<GIFFrame> imgs = new ArrayList<>();
        // use imageIO to write the first frame's data to a stream
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        InputStream tempin;
        ImageIO.write(frameone.getFrame(), "png", temp);
        // convert back to buffered image
        tempin = new ByteArrayInputStream(This(temp.toByteArray()));
        BufferedImage why = ImageIO.read(tempin);
        tempin.close();
        // add it to the new array for frames
        imgs.add(new GIFFrame(why, frameone.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        // get list of padded buffered images
        List<BufferedImage> padded = PadFrames(cont.getRawFrames());
        // process them all
        for (int x = 0; x < padded.size(); x++){
            imgs.add(new GIFFrame(padded.get(x), cont.getFrames().get(x).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        // reset the stream from eariler
        temp.reset();
        // write gif to it
        try {
            GIFTweaker.writeAnimatedGIF(imgs.toArray(new GIFFrame[]{}), temp);
        } catch (Exception e){
            throw ErrorUtils.HandleiCafeError(e);
        }
        // convert, close, return, etc
        byte[] done = temp.toByteArray();
        temp.close();
        return done;
    }

    /**
     * did you know: despacito
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something blows up in the process
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        return Meme(input);
    }

    public WhoDid(){
        this.name = "whodidthis";
    }
}
