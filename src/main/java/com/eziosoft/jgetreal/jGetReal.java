package com.eziosoft.jgetreal;

import com.eziosoft.jgetreal.Effects.*;
import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.Utils.EffectGUI;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class jGetReal {

    /**
     * only important for the built-in gui; used to hold all possible effects
     */
    public static HashMap<String,  ImageEffect> effects = new HashMap<>();

    /**
     * main class for the entire program
     * @param args command line arguments, pass any arguments to activate cli mode
     * @throws IOException if something explodes
     */
    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            EffectGUI.StartGUI();
        } else {
            // carry on as before
            File f = new File(args[1]);
            byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            EffectResult test = null;
            File out = null;
            switch (args[0]) {
                case "bandicam":
                    test = Bandicam.Watermark(hec);
                    break;
                case "caption":
                    test = Caption.applyCaption(hec, args[1]);
                    break;
                case "funky":
                    test = Funky.Watermark(hec);
                    break;
                case "hypercam2":
                    test = Hypercam2.Watermark(hec);
                    break;
                case "ifunny":
                    test = IFunny.Watermark(hec);
                    break;
                case "fuzz":
                    test = ImageFuzzer.Fuzz(hec);
                    break;
                case "invert":
                    test = Invert.Invert(hec);
                    break;
                case "shutterstock":
                    test = ShutterStock.Watermark(hec);
                    break;
                case "spin":
                    test = Spin.spinImage(hec);
                    break;
                case "touhoulook":
                    test = TouhouLook.Watermark(hec);
                    break;
                case "whodidthis":
                    test = WhoDid.Meme(hec);
                    break;
                case "jpeg":
                    test = Jpeg.Crapify(hec);
                    break;
                case "sort":
                    test = ImageColorSorter.Sort(hec);
                    break;
                default:
                    System.err.println("Error: please provide valid effect type!");
                    System.exit(-1);
            }
            out = new File("dank." + test.getFiletype());
            Files.write(Paths.get(out.getAbsolutePath()), test.getImage());
        }
    }
}
