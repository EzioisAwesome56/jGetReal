package com.eziosoft.jgetreal.Utils;

import com.eziosoft.jgetreal.Effects.*;
import com.eziosoft.jgetreal.Objects.EffectResult;
import com.eziosoft.jgetreal.Objects.ImageEffect;
import com.eziosoft.jgetreal.jGetReal;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EffectGUI {

    /**
     * main jframe for the GUI window; this is called in different functions so it has to be global
     */
    private static final JFrame main = new JFrame("jGetReal GUI");

    /**
     * Starts the gui. That is all it does.
     */
    public static void StartGUI(){
        // code stolen from patchylauncher because i am very lazy please send help
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            try {
                UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
            } catch (Exception ee) {
                System.err.println("something has gone horribly wrong, how did you do this?");
                ee.printStackTrace();
                System.exit(-2);
            }
        }
        main.setLayout(new BorderLayout());
        main.setLocationRelativeTo(null);
        // create content pane
        JPanel panel = new JPanel();
        GridBagConstraints constraints = new GridBagConstraints();
        panel.setLayout(new GridBagLayout());
        // do other things
        main.setSize(500, 500);
        main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        main.setResizable(false);
        // build gui elements
        // first row starts here
        JPanel fuckoff = new JPanel();
        fuckoff.setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        fuckoff.add(new JLabel("Input file "), constraints);
        constraints.gridx = 1;
        JTextField input = new JTextField(40);
        fuckoff.add(input, constraints);
        constraints.gridx = 2;
        JButton inbrowse = new JButton("Browse");
        // browse funtion for input files
        inbrowse.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.setDialogTitle("Open input image...");
            int option = jfc.showOpenDialog(main);
            if (option == JFileChooser.APPROVE_OPTION){
                input.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });
        fuckoff.add(inbrowse, constraints);
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(fuckoff, constraints);
        // second row starts here
        JPanel dirpane = new JPanel();
        dirpane.setLayout(new GridBagLayout());
        dirpane.add(new JLabel("Output directory "), constraints);
        JTextField dir = new JTextField(40);
        constraints.gridx = 1;
        dirpane.add(dir, constraints);
        JButton outbrowse = new JButton("Browse");
        // file selection dialog for output directory
        outbrowse.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            jfc.setDialogTitle("Select output directory...");
            jfc.setDialogType(JFileChooser.OPEN_DIALOG);
            jfc.setApproveButtonText("Select");
            int option = jfc.showOpenDialog(main);
            if (option == JFileChooser.APPROVE_OPTION){
                dir.setText(jfc.getSelectedFile().getAbsolutePath());
            }
        });
        constraints.gridx = 2;
        dirpane.add(outbrowse, constraints);
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(dirpane, constraints);
        // third pane starts here
        constraints.gridx = 0;
        constraints.gridy = 0;
        JPanel namepanel = new JPanel();
        namepanel.setLayout(new GridBagLayout());
        namepanel.add(new JLabel("filename prefix "), constraints);
        JTextField name = new JTextField(10);
        constraints.gridx = 1;
        namepanel.add(name, constraints);
        constraints.gridx = 2;
        namepanel.add(new JLabel("  Caption text "), constraints);
        constraints.gridx = 3;
        JTextField caption = new JTextField(20);
        namepanel.add(caption, constraints);
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(namepanel, constraints);
        // third row row starts here
        JPanel fuckoff2 = new JPanel();
        fuckoff2.setLayout(new GridBagLayout());
        constraints.gridx = 0;
        constraints.gridy = 0;
        fuckoff2.add(new JLabel("Image effect "), constraints);
        constraints.gridx = 1;
        JComboBox<String> effects = new JComboBox<>(getEffectList());
        fuckoff2.add(effects, constraints);
        constraints.gridx = 2;
        JButton go = new JButton("Process image");
        fuckoff2.add(go, constraints);
        go.addActionListener(e -> {
            // first check to see if the input file exists; error if it doesnt
            File temp = new File(input.getText());
            if (!temp.exists()){
                DisplayErrorBox("Input file does not exist!");
                return;
            }
            // next, check to see if the output directory is valid
            temp = new File(dir.getText());
            if (!temp.exists()){
                DisplayErrorBox("Output directory does not exist!");
                return;
            }
            if (temp.exists() && !temp.isDirectory()){
                DisplayErrorBox("Output directory is actually a file!");
                return;
            }
            // check if the effect requires a caption, and ensure the caption is provided
            if (jGetReal.effects.get((String) effects.getSelectedItem()).needscaption){
                if (caption.getText().isEmpty()){
                    DisplayErrorBox("No Caption provided!", "this effect required a caption!");
                    return;
                }
            }
            // run the effect catching any errors
            try {
                ProcessImage(new File(input.getText()), temp, (String) effects.getSelectedItem(), name.getText(), caption.getText());
            } catch (IOException er){
                DisplayErrorBox("IO Error!", er.getMessage());
                return;
            }
            // if we survived, then we're done!
            DisplayPopup("Image processed successfully!");
        });
        constraints.gridx = 0;
        constraints.gridy = 3;
        panel.add(fuckoff2, constraints);
        // set visible
        main.add(panel);
        main.pack();
        main.setVisible(true);
    }

    /**
     * Returns list of all effects we can preform
     * @return list of strings; to be used by the jcombobox
     */
    private static String[] getEffectList(){
        List<String> effects = new ArrayList<>();
        for (Map.Entry<String, ImageEffect> ent : jGetReal.effects.entrySet()){
            effects.add(ent.getKey());
        }
        return effects.toArray(new String[]{});
    }

    /**
     * Error handler for the gui
     * @param errortxt error text
     */
    private static void DisplayErrorBox(String... errortxt){
        final JFrame error = new JFrame("jGetReal ERROR");
        error.setLayout(new GridBagLayout());
        GridBagConstraints fuck = new GridBagConstraints();
        error.add(new JLabel("Error while trying to execute image effect"), fuck);
        fuck.gridy = 1;
        for (String s : errortxt){
            error.add(new JLabel(s), fuck);
            fuck.gridy += 1;
        }
        JButton close = new JButton("OK");
        close.addActionListener(e -> {
            main.setFocusable(true);
            main.setFocusableWindowState(true);
            error.dispose();
        });
        error.add(close, fuck);
        error.pack();
        error.setResizable(false);
        error.setLocationRelativeTo(main);
        main.setFocusableWindowState(false);
        main.setFocusable(false);
        error.setVisible(true);
    }

    /**
     * pop-up notifactions
     * @param errortxt notification text text
     */
    private static void DisplayPopup(String... errortxt){
        final JFrame error = new JFrame("jGetReal Notification");
        error.setLayout(new GridBagLayout());
        GridBagConstraints fuck = new GridBagConstraints();
        fuck.gridy = 0;
        for (String s : errortxt){
            error.add(new JLabel(s), fuck);
            fuck.gridy += 1;
        }
        JButton close = new JButton("OK");
        close.addActionListener(e -> {
            main.setFocusable(true);
            main.setFocusableWindowState(true);
            error.dispose();
        });
        error.add(close, fuck);
        error.pack();
        error.setResizable(false);
        error.setLocationRelativeTo(main);
        main.setFocusableWindowState(false);
        main.setFocusable(false);
        error.setVisible(true);
    }

    /**
     * process image with the options provided by the gui
     * @param in input file
     * @param outdir output directory
     * @param effect name of image effect to apply
     * @param prefix filename prefix for output file
     * @throws IOException if something doesnt go to plan during the processing
     */
    private static void ProcessImage(File in, File outdir, String effect, String prefix, String caption) throws IOException {
        // step 1: get byte array input stream for the provided input file
        byte[] source = Files.readAllBytes(Paths.get(in.getAbsolutePath()));
        // step 2: switch case our way to find what effect they wanted
        EffectResult result;
        if (jGetReal.effects.containsKey(effect)){
            ImageEffect eff = jGetReal.effects.get(effect);
            if (eff.needscaption){
                result = eff.runImageEffect(source, caption);
            } else {
                result = eff.runImageEffect(source);
            }
        } else {
            throw new IOException("Invalid effect selected!");
        }
        // step 3; open output file for writing
        File out = new File(outdir, prefix + "." + result.getFiletype());
        // step 4: write
        Files.write(Paths.get(out.getAbsolutePath()), result.getImage());
    }
}
