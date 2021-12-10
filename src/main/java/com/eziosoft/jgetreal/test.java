package com.eziosoft.jgetreal;

import java.io.File;
import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {

    public static void main(String[] args) throws Exception {
        File f = new File(args[0]);
        FileInputStream e = new FileInputStream(f);
        byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        byte[] test = GifCaptioner.CaptionGif(hec, args[1]);
        File out = new File("dank.gif");
        Files.write(Paths.get(out.getAbsolutePath()), test);
    }
}
