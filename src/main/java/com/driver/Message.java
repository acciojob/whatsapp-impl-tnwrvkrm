package com.driver;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {
    private int id;
    private String content;
    private Date timestamp;

    WhatsappRepository whatsappRepository = new WhatsappRepository();

    public Message(int id, String content, Date timestamp) {
        this.id = id;
        this.content = content;
        this.timestamp = timestamp;
    }

    public Message(String content) {
        whatsappRepository.setMessageId(whatsappRepository.getMessageId()+1);

        this.id = whatsappRepository.getMessageId();

        this.content = content;

        DateFormat dFor = new SimpleDateFormat("dd/MM/yy");
        this.timestamp = new Date();
        dFor.format(timestamp);
    }
}
