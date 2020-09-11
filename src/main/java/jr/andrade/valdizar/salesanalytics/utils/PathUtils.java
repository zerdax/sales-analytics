package jr.andrade.valdizar.salesanalytics.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static java.util.Objects.isNull;

public class PathUtils {

    public static final Path PATH_DIR_IN;
    public static final Path PATH_DIR_OUT;

    private static final String HOME_PATH = "/tmp";
    private static final String DATA_DIR_IN = "data/in";
    private static final String DATA_DIR_OUT = "data/out";

    static {
        String pathPrefix;
        if(isNull(pathPrefix = System.getenv("HOME"))) {
            if(isNull(pathPrefix = System.getenv("HOMEPATH"))) {
                pathPrefix = HOME_PATH;
            }
        }
        PATH_DIR_IN = Paths.get(String.format("%s%s%s", pathPrefix, File.separator, DATA_DIR_IN));
        PATH_DIR_OUT = Paths.get(String.format("%s%s%s", pathPrefix, File.separator, DATA_DIR_OUT));
        try {
            if(!Files.exists(PATH_DIR_IN)) {
                Files.createDirectories(PATH_DIR_IN);
            } else if(!Files.isDirectory(PATH_DIR_IN)) {
                throw new RuntimeException(String.format("Invalid path for input files: %s", PATH_DIR_IN));
            }
            if(!Files.exists(PATH_DIR_OUT)) {
                Files.createDirectories(PATH_DIR_OUT);
            } else if(!Files.isDirectory(PATH_DIR_OUT)) {
                throw new RuntimeException(String.format("Invalid path for input files: %s", PATH_DIR_OUT));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
