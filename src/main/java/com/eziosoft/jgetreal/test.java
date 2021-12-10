package com.eziosoft.jgetreal;

import com.eziosoft.jgetreal.AnimatedGif.GifCaptioner;
import com.eziosoft.jgetreal.AnimatedGif.GifFuzzer;
import com.eziosoft.jgetreal.Raster.Caption;
import com.eziosoft.jgetreal.Raster.ImageColorSorter;
import com.eziosoft.jgetreal.Raster.ImageFuzzer;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {

    public static void main(String[] args) throws Exception {
        File f = new File(args[1]);
        byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        byte[] test = new byte[0];
        File out = null;
        if (args[0].equals("static")){
            test = Caption.captionImage(hec, args[2]);
            out = new File("dank.png");
        } else if (args[0].equals("gif")){
            test = GifCaptioner.CaptionGif(hec, args[2]);
            out = new File("dank.gif");
        } else if(args[0].equals("fuzz")){
            test = ImageFuzzer.FuzzImage(hec);
            out = new File("fuz.png");
        } else if (args[0].equals("sort")){
            test = ImageColorSorter.SortColorsOfImage(hec);
            out = new File("sort.png");
        } else if (args[0].equals("giffuzz")){
            test = GifFuzzer.FuzzGif(hec);
            out = new File("dank.gif");
        } else {
            System.err.println("Error: first argument must either be static or gif");
            System.exit(-1);
        }
        Files.write(Paths.get(out.getAbsolutePath()), test);
    }
}
