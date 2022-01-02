package com.eziosoft.jgetreal.Utils;

import org.apache.commons.collections4.IteratorUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

public class FormatUtils {

    /**
     * code lifted slightly from https://stackoverflow.com/questions/11447035/java-get-image-extension-type-using-bufferedimage-from-url
     * find out the format of the input byte array
     * @param in image to get type of
     * @return file type as string
     * @throws IOException if something blows up
     */
    public static String getFormatName(byte[] in) throws IOException {
        // oh right FUCK IMAGEIO
        ImageIO.setUseCache(false);
        // first we need a bytearray input stream
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        // convert to image input stream
        ImageInputStream imgin = ImageIO.createImageInputStream(streamin);
        // make an iterator ig
        Iterator<ImageReader> imagereaders = ImageIO.getImageReaders(imgin);
        int total = IteratorUtils.size(imagereaders);
        System.err.println(total);
        if (total < 1){
            throw new IOException("Unsupported image format!");
        }
        imagereaders = ImageIO.getImageReaders(imgin);
        ImageReader reader = (ImageReader) imagereaders.next();
        imgin.flush();
        imgin.close();
        streamin.close();
        return reader.getFormatName();
    }
}
