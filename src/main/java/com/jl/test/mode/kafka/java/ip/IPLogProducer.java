package com.jl.test.mode.kafka.java.ip;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.Future;

/**
 * Author: shudj
 * Time: 2018/12/5 11:54
 * Description:
 */
public class IPLogProducer extends TimerTask {
    static String path = "";
    public BufferedReader readFile() {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("./IP_LOG.log")));

        return bufferedReader;
    }

    public static void main(String[] args) {
        Timer timer = new Timer();
        timer.schedule(new IPLogProducer(), 3000, 3000);
    }

    private String getNewRecordWithRandomIP(String line) {
        Random rand = new Random();
        String ip = rand.nextInt(256) + "." + rand.nextInt(256) + "." +
                rand.nextInt(256) + "." + rand.nextInt(256);
        String[] colums = line.split(" ");
        colums[0] = ip;
        return Arrays.toString(colums);
    }

    @Override
    public void run() {
        PropertyReader propertyReader = new PropertyReader();
        Properties props = new Properties();
        props.put("bootstrap.servers", propertyReader.getPropertyValue(PropertyReader.BROKER_LIST));
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("auto.create.topics.enable", "true");

        KafkaProducer<String, String> producer = new KafkaProducer<>(props);
        BufferedReader reader = readFile();
        String oldLine = "";
        try {

            while ((oldLine = reader.readLine()) != null) {
                String line = getNewRecordWithRandomIP(oldLine).replace("[", "").replace("]", "");
                ProducerRecord<String, String> ipData = new ProducerRecord<>(propertyReader.getPropertyValue(PropertyReader.TOPIC), line);
                Future<RecordMetadata> metadataFuture = producer.send(ipData);
            }
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            producer.close();
        }
    }
}
