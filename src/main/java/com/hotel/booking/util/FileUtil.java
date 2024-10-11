package com.hotel.booking.util;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
public class FileUtil {
    public static byte[] base64ToBytes(String base64String) {
        return Base64.getDecoder().decode(base64String.getBytes(StandardCharsets.UTF_8));
    }
}
