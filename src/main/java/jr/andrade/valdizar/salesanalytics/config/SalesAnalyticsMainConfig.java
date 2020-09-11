package jr.andrade.valdizar.salesanalytics.config;

import jr.andrade.valdizar.salesanalytics.service.FileService;
import jr.andrade.valdizar.salesanalytics.service.FileServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SalesAnalyticsMainConfig {

    @Bean
    public FileService fileService() {
        return new FileServiceImpl();
    }

}
