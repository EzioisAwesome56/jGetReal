package com.eziosoft.jgetreal.AnimatedGif;

import com.eziosoft.jgetreal.Raster.WhoDid;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.objects.GifContainer;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class GifWhoDid {

    /**
     * adds the "WHO DID THIS" border to a gif
     * @param in gif to apply effect to
     * @return gif with effect applied
     * @throws IOException if something blows up
     */
    public static byte[] GifThis(byte[] in) throws IOException {
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
        tempin = new ByteArrayInputStream(WhoDid.This(temp.toByteArray()));
        BufferedImage why = ImageIO.read(tempin);
        tempin.close();
        // add it to the new array for frames
        imgs.add(new GIFFrame(why, frameone.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        // get list of padded buffered images
        List<BufferedImage> padded = WhoDid.PadFrames(cont.getRawFrames());
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
}
