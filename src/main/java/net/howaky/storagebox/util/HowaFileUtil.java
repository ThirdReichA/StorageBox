package net.howaky.storagebox.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HowaFileUtil {
    public static List<File> dumpFile(File file) {
        List<File> list = new ArrayList<>();
        File[] files = file.listFiles();
        if (files == null) return list;
        for (File tmpFile : files) {
            if (!tmpFile.getName().equals(".sync")) {
                if (tmpFile.isDirectory()) {
                    list.addAll(dumpFile(tmpFile));
                } else {
                    list.add(tmpFile);
                }
            }
        }
        return list;
    }
}
