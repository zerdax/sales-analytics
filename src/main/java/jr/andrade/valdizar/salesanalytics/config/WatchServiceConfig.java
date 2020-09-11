package jr.andrade.valdizar.salesanalytics.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static jr.andrade.valdizar.salesanalytics.utils.PathUtils.PATH_DIR_IN;

@Configuration
public class WatchServiceConfig {

    @Bean
    public WatchService salesAnalyticsWatchService() {
        WatchService watchService = null;
        try {
            watchService = FileSystems.getDefault().newWatchService();
            PATH_DIR_IN.register(watchService, ENTRY_CREATE);
            return watchService;
        } catch(IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
