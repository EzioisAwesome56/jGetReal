package com.eziosoft.jgetreal.Objects;

import java.io.IOException;

public abstract class ImageEffect {

    /**
     * effect name
     */
    private String name;
    /**
     * does it need a caption?
     */
    private boolean needscaption;
    /**
     * caption text (may be optional/not required
     */
    private String captionText;

    /**
     * runs your image effect, override this to add new effects to the easy list
     * @param input image to process
     * @return processed image as an effect result
     * @throws IOException if something goes kaboom
     */
    public abstract EffectResult runImageEffect(byte[] input) throws IOException;
}
