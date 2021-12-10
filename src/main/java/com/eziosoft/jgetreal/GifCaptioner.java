package com.eziosoft.jgetreal;

import com.eziosoft.jgetreal.objects.GifContainer;
import com.eziosoft.jgetreal.utils.GifUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;
import com.icafe4j.image.writer.GIFWriter;

import javax.imageio.ImageIO;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GifCaptioner {

    public static byte[] CaptionGif(byte[] in, String text) throws Exception {
        // split the gif into frames
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(new ByteArrayInputStream(in));
        // get first frame
        GIFFrame frame1g = cont.getFrames().get(0);
        cont.getFrames().remove(0);
        // convert frame 1 to byte array
        ImageIO.setUseCache(false);
        ByteArrayOutputStream temp = new ByteArrayOutputStream();
        ImageIO.write(frame1g.getFrame(), "png", temp);
        // close stream
        temp.close();
        // process the rest of the frames
        List<GIFFrame> list = new ArrayList<>();
        ByteArrayInputStream stream = new ByteArrayInputStream(Caption.captionImage(temp.toByteArray(), text));
        // we need to multiply the delay by 10 to account for gif being a bad format
        list.add(new GIFFrame(ImageIO.read(stream), frame1g.getDelay() * 10, frame1g.getDisposalMethod()));
        // put this here so i dont have to keep redefining it
        ByteArrayOutputStream helloneath;
        for (GIFFrame gf : cont.getFrames()){
            stream.close();
            helloneath = new ByteArrayOutputStream();
            ImageIO.write(gf.getFrame(), "png", helloneath);
            stream = new ByteArrayInputStream(Caption.padImage(helloneath.toByteArray()));
            helloneath.flush();
            helloneath.close();
            list.add(new GIFFrame(ImageIO.read(stream), gf.getDelay() * 10, gf.getDisposalMethod()));
        }
        stream.close();
        // make stream to write byte array
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GIFTweaker.writeAnimatedGIF(list.toArray(new GIFFrame[]{}), out);
        byte[] dank = out.toByteArray();
        out.flush();
        GIFTweaker.insertComments(new ByteArrayInputStream(dank), out, Collections.singletonList("This gif was produced using jGetReal!"));
        byte[] returnvar = out.toByteArray();
        out.close();
        return returnvar;
    }
}
