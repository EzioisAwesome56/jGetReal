package com.eziosoft.jgetreal.Objects;

import java.io.IOException;

public abstract class ImageEffect {

    /**
     * effect name
     */
    public String name;
    /**
     * does it need a caption?
     */
    public boolean needscaption = false;

    /**
     * runs your image effect, override this to add new effects to the easy list
     * @param input image to process
     * @param caption caption text if required
     * @return processed image as an effect result
     * @throws IOException if something goes kaboom
     */
    public abstract EffectResult runImageEffect(byte[] input, String... caption) throws IOException;
}
