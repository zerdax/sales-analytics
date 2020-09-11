package jr.andrade.valdizar.salesanalytics.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchService;

@Configuration
public class WatchServiceConfig {

    @Value("${homepath.dir.in}")
    private String directoryIn;



    @Bean
    public WatchService salesAnalyticsWatchService() {
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            Path path = Paths.get(directoryIn);
            if(!Files.isDirectory(path)) {
                throw new RuntimeException(String.format("Directory informed is not correct: %s", directoryIn));
            }
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE);
            return watchService;
        } catch(IOException e) {
            e.printStackTrace();
            return watchService;
        }
    }
}
