package com.example.batchprocessor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Profile("!test")
@Component
public class WatchFileService implements ApplicationRunner {

    private static Boolean shutdown;

    private WatchService watcher;

    private FileService fileService;

    public WatchFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Value("${data.in}")
    private String fileLocation;

    @Override
    public void run(ApplicationArguments args) throws IOException, InterruptedException {

        System.out.println("ApplicationRunner called");

        Path myDir = Paths.get(fileLocation);

        try {
            watcher = myDir.getFileSystem().newWatchService();
            myDir.register(watcher, StandardWatchEventKinds.ENTRY_CREATE);
            while (true) {
                WatchKey watchKey = watcher.take();
                List<WatchEvent<?>> events = watchKey.pollEvents();

                System.out.println("data/in folder was modified");
                fileService.processFiles();

                for (WatchEvent event : events) {
                    if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE) {
                        System.out.println("event: "+event.kind().name().toString());
                    }
                }
                watchKey.reset();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}