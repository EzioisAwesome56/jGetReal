package com.eziosoft.jgetreal.objects;

import com.icafe4j.image.gif.GIFFrame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GifContainer {
    private List<GIFFrame> frames = new ArrayList<>();

    public void addFrame(GIFFrame in){
        this.frames.add(in);
    }


    public List<GIFFrame> getFrames() {
        return frames;
    }

    public List<BufferedImage> getRawFrames(){
        List<BufferedImage> temp = new ArrayList<>();
        for (GIFFrame f : this.frames){
            temp.add(f.getFrame());
        }
        return temp;
    }
}
