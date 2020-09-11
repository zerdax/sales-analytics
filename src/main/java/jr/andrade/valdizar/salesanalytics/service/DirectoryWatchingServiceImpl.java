package jr.andrade.valdizar.salesanalytics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Objects;

@Service
public class DirectoryWatchingServiceImpl implements  DirectoryWatchingService {

    @Autowired
    private WatchService watchService;

    @Autowired
    private FileService fileService;

    @Async
    @PostConstruct
    @Override
    public void startWatch() {
        WatchKey key;
        try {
            while(Objects.nonNull(key = watchService.take())) {
                String dirPath = key.watchable().toString();
                key.pollEvents().stream()
                        .filter(watchEvent -> watchEvent.kind().equals(StandardWatchEventKinds.ENTRY_CREATE))
                        .forEach(watchEvent -> {
                            fileService.inputFileProcess(Paths.get(String.format("%s%s%s", dirPath,
                                    File.separator, watchEvent.context().toString())));
                        });
                key.reset();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
