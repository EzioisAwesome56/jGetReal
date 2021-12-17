package com.eziosoft.jgetreal.Utils;

import java.io.IOException;

public class ErrorUtils {

    public static IOException HandleiCafeError(Exception e){
        return new IOException("Error occurred during GIF write!", e);
    }
}
