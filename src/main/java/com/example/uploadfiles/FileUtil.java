package com.example.uploadfiles;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

public class FileUtil {

    // this constructor to avoid class instantiation
    private FileUtil() {}

    public static final String folderPath = "incoming-files/";
    public static final Path  filePath = Paths.get(folderPath);

    public static File targetDirectory(String dirName) {
        return Optional.ofNullable(dirName)
                .map(name -> new File(targetDirectoryName(name)))
                .orElse(new File(folderPath))
                ;
    }

    public static String targetDirectoryName(String dirName) {
        return dirName.startsWith(folderPath) ? dirName : folderPath + File.separatorChar + dirName;
    }
}
