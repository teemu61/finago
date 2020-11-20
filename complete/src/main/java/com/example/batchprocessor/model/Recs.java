package com.example.batchprocessor.model;

import generated.ReceiverType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;



    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(
            name = "",
            propOrder = {"receiver"}
    )
    @XmlRootElement(
            name = "receivers"
    )
    public class Recs {
        protected List<ReceiverType> receiver;

        public Recs() {
        }

        public List<ReceiverType> getReceiver() {
            if (this.receiver == null) {
                this.receiver = new ArrayList();
            }

            return this.receiver;
        }

        public void setReceiver(List<ReceiverType> receiver) {
            this.receiver = receiver;
        }
    }

