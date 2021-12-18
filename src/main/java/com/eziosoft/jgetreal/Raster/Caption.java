package com.eziosoft.jgetreal.Raster;

import com.eziosoft.jgetreal.Utils.RasterUtils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Caption {

    private static final int maxLines = 5;

    /**
     * returns an image with 300px of transparent padding above the main content
     * @param in byte array of image you wish to pad
     * @return byte array of padded image, png format;
     * @throws IOException usually if an io error occurs or if something else blows up
     */
    public static byte[] padImage(byte[] in) throws IOException {
        ImageIO.setUseCache(false);
        ByteArrayInputStream streamin = new ByteArrayInputStream(in);
        BufferedImage source = ImageIO.read(streamin);
        streamin.close();
        // make graphics 2d
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight() + 300, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = out.createGraphics();
        // draw image
        g.drawImage(source, 0, 300, null);
        // throw graphics 2d in the trash
        g.dispose();
        // convert to byte array
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        ImageIO.write(out, "png", outstream);
        byte[] dank = outstream.toByteArray();
        outstream.close();
        return dank;
    }

    /**
     * captions an image, returns in jpeg format
     * @param image byte array of image to caption
     * @param text caption text
     * @return byte array of captioned image; jpeg format
     * @throws IOException if something blows up
     */
    public static byte[] captionAsJpeg(byte[] image, String text) throws IOException {
        return RasterUtils.ConvertToJpeg(captionImage(image, text));
    }

    /**
     * captions an image with provided text
     * @param image byte array of image to caption
     * @param text string of text to caption image with
     * @return byte array of image with caption; png format
     * @throws IOException if imageio runs into a problem
     */
    public static byte[] captionImage(byte[] image, String text, Color... col) throws IOException {
        ImageIO.setUseCache(false);
        // load image
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage buf = ImageIO.read(in);
        in.close();
        // get width and height of image
        int width = buf.getWidth();
        int height = buf.getHeight();
        int fontsize = 64;
        // make new buffered image thats slightly taller
        BufferedImage out = new BufferedImage(width, height + 300, BufferedImage.TYPE_INT_RGB);
        // draw our image to it, offset by 300 pixels
        Graphics2D g = out.createGraphics();
        g.drawImage(buf, 0, 300, null);
        // set color to white if no colors are set
        if (col.length == 0){
            g.setColor(Color.white);
        } else {
            g.setColor(col[0]);
        }
        // set stroke type
        g.fillRect(0, 0, width, 300);
        // set the font and font color
        Font font = new Font(Font.SERIF, Font.PLAIN, fontsize);
        g.setFont(font);
        g.setColor(Color.BLACK);
        // get font metrics and render context
        FontMetrics fm = g.getFontMetrics();
        FontRenderContext frc = g.getFontRenderContext();
        // draw string to image in white area
        // but we may have to do a lot of math to find out if its too long
        if (fm.stringWidth(text) > width){
            // does it have spaces?
            if (text.contains(" ")){
                // if it does, determin how many lines we need
                int total = (int) Math.round((double) fm.stringWidth(text) / (double) width);
                //System.err.println(total);
                if (total > maxLines) total = maxLines;
                List<StringBuilder> builders = new ArrayList<>();
                for (int x = 0; x < total; x++){
                    builders.add(new StringBuilder());
                }
                String[] split = text.split(" ");
                int count = 0;
                for (String s : split){
                    if (font.getStringBounds(builders.get(count).toString() + s + " ", frc).getWidth() > width){
                        if (total == maxLines) {
                            if (count != total - 1) {
                                count++;
                            }
                        } else {
                            builders.add(new StringBuilder());
                            count++;
                        }
                    }
                    builders.get(count).append(s).append(" ");
                }
                // extremely annoying math to draw the strings
                int start = (total < 2) ? 150 : 300 - (60 * total);
                for (StringBuilder b : builders){
                    g.drawString(b.toString(), (width - (int) font.getStringBounds(b.toString(), frc).getWidth()) / 2, start);
                    start += 60;
                }
            } else {
                List<StringBuilder> builders = new ArrayList<>();
                builders.add(new StringBuilder());
                int count = 0;
                for (int x = 0; x < text.length(); x++){
                    if (font.getStringBounds(builders.get(count).toString() + text.charAt(x), frc).getWidth() > width){
                        if (count + 1 < maxLines){
                            builders.add(new StringBuilder());
                            count++;
                        }
                    }
                    builders.get(count).append(text.charAt(x));
                }
                int start = 300 - (50 * builders.size());
                for (StringBuilder b : builders){
                    g.drawString(b.toString(), (width - (int) font.getStringBounds(b.toString(), frc).getWidth()) / 2, start);
                    start += 50;
                }
            }
        } else {
            g.drawString(text, (width - (int) font.getStringBounds(text, frc).getWidth()) / 2, 150);
        }
        // dispose of graphics 2d
        g.dispose();
        // convert to byte array
        ByteArrayOutputStream hell = new ByteArrayOutputStream();
        hell.flush();
        ImageIO.setUseCache(false);
        ImageIO.write(out, "png", hell);
        byte[] done = hell.toByteArray();
        hell.close();
        return done;
    }
}
