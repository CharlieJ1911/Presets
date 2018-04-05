package io.chazza.presets.util;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ColorUtil {
    private static String translateAlternateColorCodes(String textToTranslate) {
        char[] b = textToTranslate.toCharArray();
        for (int i = 0; i < b.length - 1; i++) {
            if (b[i] == '&' && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i+1]) > -1) {
                b[i] = '§';
                b[i+1] = Character.toLowerCase(b[i+1]);
            }
        }
        return new String(b);
    }

    public static String translate(String string) {
        return translateAlternateColorCodes(string);
    }

    public static List<String> translate(List<String> string) {
        return string.stream().map(ColorUtil::translate).collect(Collectors.toCollection(ArrayList::new));
    }
}
