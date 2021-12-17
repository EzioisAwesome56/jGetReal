package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.IFunny;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifIFunny {

    /**
     * applies ifunny watermark to animated gif
     * @param in animated gif to watermark
     * @return watermarked animated gif
     * @throws IOException if something blows up
     */
    public static byte[] WatermarkGif(byte[] in) throws IOException{
        // set imageio cache to off
        ImageIO.setUseCache(false);
        // split gif into container
        try {
            GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
            // create list of finished frames
            List<GIFFrame> processed = new ArrayList<>();
            // create object for first frame
            GIFFrame frame1 = cont.getFrames().get(0);
            // remove frame1 from origin list
            cont.getFrames().remove(0);
            // make new byte array stream for reuse later
            ByteArrayOutputStream temp = new ByteArrayOutputStream();
            // use imageio to write frame1 to it
            ImageIO.write(frame1.getFrame(), "png", temp);
            // process it and add it to the list
            ByteArrayInputStream tempin = new ByteArrayInputStream(IFunny.Watermark(temp.toByteArray()));
            processed.add(new GIFFrame(ImageIO.read(tempin), frame1.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
            tempin.close();
            // get the padded frames
            List<BufferedImage> e = IFunny.PadImages(cont.getRawFrames());
            // add all of the frames to the list
            for (int x = 0; x < e.size(); x++){
                processed.add(new GIFFrame(e.get(x), cont.getFrames().get(x).getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
            }
            // reset stream
            temp.reset();
            // write gif to it
            GIFTweaker.writeAnimatedGIF(processed.toArray(new GIFFrame[]{}), temp);
            // convert, close, return
            byte[] done = temp.toByteArray();
            temp.close();
            return done;
        } catch (Exception e){
            throw new IOException("Error occurred during GIF processing!", e);
        }
    }
}
