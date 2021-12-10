package com.eziosoft.jgetreal;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;

public class Caption {

    private static final int maxLines = 5;

    public static byte[] padImage(byte[] in) throws Exception{
        ImageIO.setUseCache(false);
        BufferedImage source = ImageIO.read(new ByteArrayInputStream(in));
        // make graphics 2d
        BufferedImage out = new BufferedImage(source.getWidth(), source.getHeight() + 300, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = out.createGraphics();
        // draw image
        g.drawImage(source, 0, 300, new Color(1f,1f,1f,1f), null);
        // throw graphics 2d in the trash
        g.dispose();
        // convert to byte array
        ByteArrayOutputStream outstream = new ByteArrayOutputStream();
        ImageIO.write(out, "png", outstream);
        byte[] dank = outstream.toByteArray();
        outstream.close();
        return dank;
    }

    public static byte[] captionImage(byte[] image, String text) throws IOException {
        // load image
        InputStream in = new ByteArrayInputStream(image);
        BufferedImage buf = ImageIO.read(in);
        in.close();
        // get width and height of image
        int width = buf.getWidth();
        int height = buf.getHeight();
        int fontsize = 64;
        // make new buffered image thats slightly taller
        BufferedImage out = new BufferedImage(width, height + 300, BufferedImage.TYPE_4BYTE_ABGR);
        // draw our image to it, offset by 300 pixels
        Graphics2D g = out.createGraphics();
        g.drawImage(buf, 0, 300, new Color(1f,1f,1f,1f), null);
        // set color to white
        g.setColor(Color.WHITE);
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
                // todo: deal with really long things without spaces
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
        return hell.toByteArray();
    }
}
