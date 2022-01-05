package com.eziosoft.jgetreal.Effects;

import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.ErrorUtils;
import com.eziosoft.jgetreal.Utils.FormatUtils;
import com.eziosoft.jgetreal.Utils.GifUtils;
import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.GifContainer;
import com.eziosoft.jgetreal.Utils.RasterUtils;
import com.icafe4j.image.gif.GIFFrame;
import com.icafe4j.image.gif.GIFTweaker;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Locale;

public class Caption extends ImageEffect {

    /**
     * to be used by the gui; for running the effect out of an array
     * @param input image to process
     * @param caption caption text if required
     * @return processed image
     * @throws IOException if something dies during the process
     */
    @Override
    public EffectResult runImageEffect(byte[] input, String... caption) throws IOException {
        if (caption.length < 1){
            throw new IOException("No caption provided!");
        }
        return applyCaption(input, caption[0]);
    }

    public Caption(){
        this.name = "caption";
        this.needscaption = true;
    }

    /**
     * total number of lines for text until it starts going off the image
     */
    private static final int maxLines = 5;
    /**
     * holding variable for the custom font we need to load
     */
    private static Font captionfont;

    static {
        try {
            captionfont = Font.createFont(Font.TRUETYPE_FONT, Caption.class.getResourceAsStream("/caption.ttf"));
        } catch (FontFormatException | IOException e) {
            System.err.println("Error while loading font!");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * detects what format the image is and runs it thru the correct watermarker
     * @param in image to caption
     * @param fuck caption text
     * @return captioned image
     * @throws IOException if something blows up, usually an imageio failure or unsupported image format errors
     */
    public static EffectResult applyCaption(byte[] in, String fuck) throws IOException{
        // first get the image format type
        String type = FormatUtils.getFormatName(in).toLowerCase(Locale.ROOT);
        // then do shit based on format
        return new EffectResult(type.contains("gif") ? CaptionGif(in, fuck) : captionImage(in, fuck), type.contains("gif") ? "gif" : "png");
    }

    /**
     * returns an image with 300px of transparent padding above the main content
     * @param in byte array of image you wish to pad
     * @return byte array of padded image, png format;
     * @throws IOException usually if an io error occurs or if something else blows up
     */
    private static byte[] padImage(byte[] in) throws IOException {
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
        return RasterUtils.ConvertToBytes(out);
    }

    /**
     * captions an image with provided text
     * @param image byte array of image to caption
     * @param text string of text to caption image with
     * @return byte array of image with caption; png format
     * @throws IOException if imageio runs into a problem
     */
    private static byte[] captionImage(byte[] image, String text, Color... col) throws IOException {
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
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        ge.registerFont(captionfont);
        //Font font = new Font(Font.SERIF, Font.PLAIN, fontsize);
        // because im a lazy shit
        Font font = captionfont.deriveFont(Font.PLAIN, fontsize);
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
        return RasterUtils.ConvertToBytes(out);
    }

    /**
     * Captions an animated gif
     * @param in byte array of animated gif you wish to caption
     * @param text text of caption
     * @return byte array of captioned gif; obviously in gif format
     * @throws IOException if something goes wrong somewhere
     */
    private static byte[] CaptionGif(byte[] in, String text) throws IOException {
        ImageIO.setUseCache(false);
        // split the gif into frames
        GifContainer cont = GifUtils.splitAnimatedGifToContainer(in);
        // get first frame
        GIFFrame frame1g = cont.getFrames().get(0);
        cont.getFrames().remove(0);
        // convert frame 1 to byte array
        ByteArrayOutputStream helloneath = new ByteArrayOutputStream();
        ImageIO.write(frame1g.getFrame(), "png", helloneath);
        // add it to list
        List<GIFFrame> list = new ArrayList<>();
        ByteArrayInputStream stream = new ByteArrayInputStream(Caption.captionImage(helloneath.toByteArray(), text, Color.GRAY));
        // we need to multiply the delay by 10 to account for gif being a bad format
        list.add(new GIFFrame(ImageIO.read(stream), frame1g.getDelay() * 10, GIFFrame.DISPOSAL_LEAVE_AS_IS));
        for (GIFFrame gf : cont.getFrames()) {
            // reset all streams
            helloneath.reset();
            stream.close();
            // pad next frame
            ImageIO.write(gf.getFrame(), "png", helloneath);
            stream = new ByteArrayInputStream(padImage(helloneath.toByteArray()));
            // add it to list
            list.add(new GIFFrame(ImageIO.read(stream), gf.getDelay() * 10, GIFFrame.DISPOSAL_RESTORE_TO_PREVIOUS));
        }
        stream.close();
        // reset stream so you can write to it
        return GifUtils.ConvertToBytes(list);
    }
}
