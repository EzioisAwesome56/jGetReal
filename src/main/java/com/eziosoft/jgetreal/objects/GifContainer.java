package com.eziosoft.jgetreal.objects;

import com.icafe4j.image.gif.GIFFrame;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class GifContainer {
    private List<BufferedImage> frames = new ArrayList<>();
    private int loopcount;
    private List<Integer> delays = new ArrayList<>();

    public void addFrame(BufferedImage in){
        this.frames.add(in);
    }

    public void addDelay(int in){
        this.delays.add(in);
    }
}
