package com.example.batchprocessor.service;

import com.example.batchprocessor.model.Recs;
import generated.ReceiverType;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class XmlService {


    public void marshalInXml(String basedir, ReceiverType item, String pdfName, String path) throws JAXBException {
        marshalXml(basedir, item, pdfName, path, "in");
    }


    public void marshalErrorXml(String basedir, ReceiverType item, String pdfName, String path) throws JAXBException {
        marshalXml(basedir, item, pdfName, path, "error");
    }

    public void marshalXml(String basedir, ReceiverType item, String pdfName, String path, String subDir) throws JAXBException {

        String xmlNewName = getXmlNewName(pdfName);

        Recs recs = createRecs(item);

        JAXBContext jaxbContext = JAXBContext.newInstance(Recs.class);
        Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        String xmlNameFull = path + "/" + xmlNewName;
        String pdfNameFull = basedir + subDir + "/" + pdfName;
        File outFile = new File(xmlNameFull);
        jaxbMarshaller.marshal(recs, outFile);
    }

    private Recs createRecs(ReceiverType item) {
        Recs recs = new Recs();
        List<ReceiverType> recTypes = new ArrayList<>();
        recTypes.add(item);
        recs.setReceiver(recTypes);
        return recs;
    }

    private String getXmlNewName(String pdfName) {
        return pdfName.substring(0, pdfName.length() - 3) + "xml";
    }

}
