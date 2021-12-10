package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.Caption;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GifCaptioner {

    /**
     * Captions an animated gif
     * @param in byte array of animated gif you wish to caption
     * @param text text of caption
     * @return byte array of captioned gif; obviously in gif format
     * @throws Exception if something goes wrong somewhere
     */
    public static byte[] CaptionGif(byte[] in, String text) throws Exception {
        ImageIO.setUseCache(false);
        // split the gif into frames
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // get first frame
        GIFFrame frame1g = cont.getFrames().get(0);
        cont.getFrames().remove(0);
        // convert frame 1 to byte array
        ByteArrayOutputStream helloneath = new ByteArrayOutputStream();
        ImageIO.write(frame1g.getFrame(), "png", helloneath);
        // add it to list
        List<GIFFrame> list = new ArrayList<>();
        ByteArrayInputStream stream = new ByteArrayInputStream(Caption.captionImage(helloneath.toByteArray(), text));
        // we need to multiply the delay by 10 to account for gif being a bad format
        list.add(new GIFFrame(ImageIO.read(stream), frame1g.getDelay() * 10, frame1g.getDisposalMethod()));
        for (GIFFrame gf : cont.getFrames()){
            // reset all streams
            helloneath.reset();
            stream.close();
            // pad next frame
            ImageIO.write(gf.getFrame(), "png", helloneath);
            stream = new ByteArrayInputStream(Caption.padImage(helloneath.toByteArray()));
            // add it to list
            list.add(new GIFFrame(ImageIO.read(stream), gf.getDelay() * 10, gf.getDisposalMethod()));
        }
        stream.close();
        helloneath.close();
        // make stream to write byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GIFTweaker.writeAnimatedGIF(list.toArray(new GIFFrame[]{}), out);
        byte[] dank = out.toByteArray();
        out.reset();
        GIFTweaker.insertComments(new ByteArrayInputStream(dank), out, Collections.singletonList("This gif was produced using jGetReal!"));
        byte[] returnvar = out.toByteArray();
        out.close();
        return returnvar;
    }
}
