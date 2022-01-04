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
import java.util.Map;

public class jGetReal {

    /**
     * only important for the built-in gui and cli; used to hold all possible effects
     */
    public static HashMap<String,  ImageEffect> effects = new HashMap<>();

    /**
     * main class for the entire program
     * @param args command line arguments, pass any arguments to activate cli mode
     * @throws IOException if something explodes
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Initializing, please wait...");
        AddEffect(new Bandicam());
        AddEffect(new Caption());
        AddEffect(new Funky());
        AddEffect(new Hypercam2());
        AddEffect(new IFunny());
        AddEffect(new ImageColorSorter());
        AddEffect(new ImageFuzzer());
        AddEffect(new Invert());
        AddEffect(new Jpeg());
        AddEffect(new ShutterStock());
        AddEffect(new Spin());
        AddEffect(new TouhouLook());
        AddEffect(new WhoDid());
        if (args.length < 1) {
            EffectGUI.StartGUI();
        } else {
            // carry on as before
            File f = new File(args[1]);
            byte[] hec = Files.readAllBytes(Paths.get(f.getAbsolutePath()));
            EffectResult test = null;
            File out = null;
            if (effects.containsKey(args[0])){
                ImageEffect eff = effects.get(args[0]);
                if (eff.needscaption){
                    if (args.length < 3) {
                        System.err.println("Error: you need to provide a caption as the third argument for this effect!");
                        System.exit(-1);
                    }
                    test = eff.runImageEffect(hec, args[3]);
                } else {
                    test = eff.runImageEffect(hec);
                }
            } else {
                System.err.println("Error: the effect '" + args[0] + "' is not valid");
                StringBuilder b = new StringBuilder();
                for (Map.Entry<String, ImageEffect> fuck : effects.entrySet()){
                    b.append(fuck.getKey()).append(", ");
                }
                System.err.println("Valid effects are: " + b.toString());
                System.exit(-1);
            }
            out = new File("dank." + test.getFiletype());
            Files.write(Paths.get(out.getAbsolutePath()), test.getImage());
        }
    }

    /**
     * add new effect to the image effect map
     * @param effect effect to add
     * @throws IOException if the effect already exists
     */
    public static void AddEffect(ImageEffect effect) throws IOException{
        if (effects.containsKey(effect.name)){
            throw new IOException("Effect with the same name already exists!");
        }
        effects.put(effect.name, effect);
        System.out.println("Registered effect " + effect.name);
    }
}
