package com.example.batchprocessor.service;

import com.example.batchprocessor.model.Recs;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import generated.ReceiverType;
import org.springframework.batch.item.util.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;


@Service
public class FileService {

    NioService nioService;

    XmlService xmlService;

    @Value("${data}")
    String basedir;

    @Value("${data.in}")
    String inputDir;


    public FileService(NioService nioService, XmlService xmlService) {
        this.nioService = nioService;
        this.xmlService = xmlService;
    }

    public void processFiles() throws IOException, JAXBException {
        List<File> xmlFileList = getXmlFileList();
        for (File file : xmlFileList) {
            processXmlFile(file);
        }
    }

    private void processXmlFile(File file) throws JAXBException, IOException {

        Boolean xmlIsValid = true;

        Recs recs = null;
        try {
            recs = getRecs(file);
        } catch (JAXBException e) {
            xmlIsValid = false;
        }

        if (!xmlIsValid) {
            nioService.copyErrorXml(file.getName());
            file.delete();
            return;
        }

        List<ReceiverType> list = recs.getReceiver();

        for (ReceiverType item : list) {
            String md5 = item.getFileMd5();
            String pdfName = item.getFile();
            String pdfFullPath = inputDir + pdfName;
            String subDir = getSubDirName(item);

            File pdfFile = new File(pdfFullPath);
            boolean pdfExists = pdfFile.exists();

            if (pdfExists) {

                String checksum = getM5(pdfFile);

                if (checksum.equals(md5)) {
                    String path = nioService.createOutSubFolder(basedir, subDir);
                    nioService.copyInPdf(pdfName, item, getSubDirName(item));
                    xmlService.marshalInXml(basedir, item, pdfName, path);
                } else {
                    System.out.println("md5 was incorrect!");
                    File faultyPdf = new File(pdfFullPath);
                    faultyPdf.delete();
                    String path = nioService.createErrorSubFolder(basedir, subDir);
                    nioService.copyErrorPdf(pdfName, item, getSubDirName(item));
                    xmlService.marshalErrorXml(basedir, item, pdfName, path);
                }
            }
            nioService.copyXml(file);
            File pdf = new File(pdfFullPath);
            pdf.delete();
        }
        file.delete();
    }

    private String getSubDirName(ReceiverType item) {
        int receiverId = item.getReceiverId().intValue();
        return Integer.toString(receiverId % 100);
    }

    private List<File> getXmlFileList() throws IOException {
        List<File> fileList = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(inputDir))) {
            paths.filter(Files::isRegularFile)
                    .filter(i -> i.toString().endsWith("xml"))
                    .forEach(i -> fileList.add(i.toFile()));
        }
        return fileList;
    }


    public Recs getRecs(File file) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(Recs.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        return (Recs) jaxbUnmarshaller.unmarshal(file);
    }

    public static File[] getResourceFolderFiles(String folder) {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(folder);
        String path = url.getPath();
        return new File(path).listFiles();
    }

    public String getM5(File file) throws IOException {
        HashCode hash = com.google.common.io.Files
                .hash(file, Hashing.md5());
        return hash.toString().toLowerCase();
    }
}
