package com.eziosoft.jgetreal;

import com.eziosoft.jgetreal.AnimatedGif.*;
import com.eziosoft.jgetreal.Raster.*;
import com.eziosoft.jgetreal.Utils.GifUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class test {

    public static void main(String[] args) throws IOException {
        File f = new File(args[1]);
        byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
        byte[] test = new byte[0];
        File out = null;
        switch (args[0]) {
            case "static":
                test = Caption.captionImage(hec, args[2]);
                out = new File("dank.png");
                break;
            case "gif":
                test = GifCaptioner.CaptionGif(hec, args[2]);
                out = new File("dank.gif");
                break;
            case "fuzz":
                test = ImageFuzzer.FuzzImage(hec);
                out = new File("fuz.png");
                break;
            case "sort":
                test = ImageColorSorter.SortColorsOfImage(hec);
                out = new File("sort.png");
                break;
            case "giffuzz":
                test = GifFuzzer.FuzzGif(hec);
                out = new File("dank.gif");
                break;
            case "spin":
                test = Spin.spinRaster(hec);
                out = new File("dank.gif");
                break;
            case "gifspin":
                test = GifSpin.spinGif(hec);
                out = new File("dank.gif");
                break;
            case "hypercam":
                test = Hypercam2.Unregister(hec);
                out = new File("dank.png");
                break;
            case "gifhypercam":
                test = GifHypercam2.UnregisterGif(hec);
                out = new File("dank.gif");
                break;
            case "gifcrap":
                test = GifUtils.CrapifyGif(hec);
                out = new File("dank.gif");
                break;
            case "stock":
                test = ShutterStock.Stockify(hec);
                out = new File("dank.png");
                break;
            case "gifstock":
                test = GifShutterStock.StockifyGif(hec);
                out = new File("dank.gif");
                break;
            case "funky":
                test = Funky.Funk(hec);
                out = new File("dank.png");
                break;
            case "giffunky":
                test = GifFunky.FunkGif(hec);
                out = new File("dank.gif");
                break;
            case "invert":
                test = Invert.InvertColors(hec);
                out = new File("dank.png");
                break;
            case "gifinvert":
                test = GIfInverter.InvertGif(hec);
                out = new File("dank.gif");
                break;
            case "ifunny":
                test = IFunny.Watermark(hec);
                out = new File("dank.png");
                break;
            case "gififunny":
                test = GifIFunny.WatermarkGif(hec);
                out = new File("dank.gif");
                break;
            case "bandicam":
                test = Bandicam.Watermark(hec);
                out = new File("dank.png");
                break;
            case "whodid":
                test = WhoDid.This(hec);
                out = new File("dank.png");
                break;
            case "gifwhodid":
                test = GifWhoDid.GifThis(hec);
                out = new File("dank.gif");
                break;
            case "gifbandicam":
                test = GifBandicam.Watermark(hec);
                out = new File("dank.gif");
                break;
            default:
                System.err.println("Error: first argument must either be static or gif");
                System.exit(-1);
        }
        Files.write(Paths.get(out.getAbsolutePath()), test);
    }
}
