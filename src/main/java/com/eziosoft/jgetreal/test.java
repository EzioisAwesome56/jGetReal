package com.eziosoft.jgetreal;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {

    public static void main(String[] args) throws IOException {
        File f = new File(args[0]);
        byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        byte[] test = Caption.captionImage(hec, args[1]);
        File out = new File("dank.png");
        Files.write(Paths.get(out.getAbsolutePath()), test);
    }
}
