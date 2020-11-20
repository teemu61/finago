package com.example.batchprocessor.service;

import generated.ReceiverType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class NioService {


    @Value("${data.in}")
    String inDir;

    @Value("${data.out}")
    String outDir;

    @Value("${data.error}")
    String errorDir;

    @Value("${data.archive}")
    String archiveDir;



    public Boolean copyInPdf(String pdfName, ReceiverType item, String subDir) throws IOException {
        return copyPdf(pdfName, item, subDir, outDir);
    }

    public Boolean copyErrorPdf(String pdfName, ReceiverType item, String subDir) throws IOException {
        return copyPdf(pdfName, item, subDir, errorDir);
    }

    public Boolean copyPdf(String pdfName, ReceiverType item, String subDir, String dir) throws IOException {

        File original = getSourceFile(pdfName);
        boolean fileExists = original.exists();
        if (fileExists) {
            String target = dir + subDir + "/" + pdfName;
            Path targetPath = Paths.get(target);
            Path sourcePath = original.toPath();
            Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }
        return fileExists;
    }

    private File getSourceFile(String pdfName) {
        String source = inDir + pdfName;
        return new File(source);
    }

    public void copyErrorXml(String xmlName) throws IOException {

        File source = new File(inDir + xmlName);
        Path targetPath = Paths.get(errorDir + xmlName);
        Path sourcePath = source.toPath();
        Files.copy(sourcePath, targetPath, StandardCopyOption.REPLACE_EXISTING);
    }

    public void copyXml(File file) throws IOException {

        String xmlName = file.getName();
        File original = new File(inDir + xmlName);
        Path copied = Paths.get(archiveDir + xmlName);
        Path originalPath = original.toPath();
        Files.copy(originalPath, copied, StandardCopyOption.REPLACE_EXISTING);
    }

    public String createOutSubFolder(String basedir, String subDir) throws IOException {
        String path = basedir + "out/" + subDir;
        Files.createDirectories(Paths.get(path));
        return path;
    }

    public String createErrorSubFolder(String basedir, String subDir) throws IOException {
        String path = basedir + "error/" + subDir;
        Files.createDirectories(Paths.get(path));
        return path;
    }

}
