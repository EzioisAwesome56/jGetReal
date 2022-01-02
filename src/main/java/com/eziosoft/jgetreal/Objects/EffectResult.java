package com.eziosoft.jgetreal.Objects;

public class EffectResult {

    private byte[] image;
    private String filetype;

    public EffectResult(byte[] in, String type){
        this.filetype = type;
        this.image = in;
    }

    public byte[] getImage() {
        return image;
    }

    public String getFiletype() {
        return filetype;
    }
}
