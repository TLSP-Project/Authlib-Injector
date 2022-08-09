package org.tlsp.mc.utils;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;

public class ResourceUtils {
    public static byte[] getResource(String path) throws IOException {
        InputStream in = ResourceUtils.class.getClassLoader().getResourceAsStream(path);
        return IOUtils.toByteArray(in);
    }
}
