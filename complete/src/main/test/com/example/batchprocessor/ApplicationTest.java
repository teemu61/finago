package com.example.batchprocessor;

import com.example.batchprocessor.model.Recs;
import com.example.batchprocessor.service.FileService;
import com.example.batchprocessor.service.NioService;
import com.example.batchprocessor.service.XmlService;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import generated.ReceiverType;
import generated.Receivers;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;


@ActiveProfiles("test")
@SpringBootTest
public class ApplicationTest {

    @Autowired
    NioService nioService;

    @Autowired
    XmlService xmlService;

    @Autowired
    FileService fileService;

    @Value("${data.in}")
    String basedir;

    @Test
    public void testBatchProcessing() throws IOException, JAXBException {
        fileService.processFiles();
    }

    @Ignore
    @Test
    void name2() throws IOException {
        String pdfFullPath = "/home/teemu/repo/java/interview_task-batch_processor/batch-processor/data/in/90072657.pdf";
        File f = new File(pdfFullPath);
        System.out.println(f.toString());
        HashCode hash = com.google.common.io.Files
                .hash(new File(pdfFullPath), Hashing.md5());
        String checksum = hash.toString().toLowerCase();
        String m5 = fileService.getM5(new File(pdfFullPath));

    }

    @Ignore
    @Test
    void name3()  {
        ClassLoader classLoader = getClass().getClassLoader();
        File f = new File("/home/teemu/repo/java/finago/complete/data/in/90072701.xml");
        File file2 = new File(classLoader.getResource("fileTest.txt").getFile());
        File file = new File(classLoader.getResource("/home/teemu/repo/java/finago/complete/data/in/90072701.xml").getFile());
        String basedir = "/home/teemu/repo/java/finago/complete/data/";
        File[] files = fileService.getResourceFolderFiles("/home/teemu/repo/java/finago/complete/data/in");
    }

}
