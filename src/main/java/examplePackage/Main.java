package examplePackage;

import com.fasterxml.jackson.databind.ObjectMapper;
import examplePackage.ExportData.MyKafkaProducer;
import examplePackage.data.Rum;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import org.apache.spark.SparkConf;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaInputDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka010.ConsumerStrategies;
import org.apache.spark.streaming.kafka010.KafkaUtils;
import org.apache.spark.streaming.kafka010.LocationStrategies;
import scala.Tuple2;

import java.util.*;


public class Main {
    public static void main(String[] args) throws Exception  {
        //        Initialize spark and the streaming context
        SparkConf conf = new SparkConf().setMaster("local[2]").setAppName("AppName");
        //        the duration is the interval of calling new data
        JavaStreamingContext jssc = new JavaStreamingContext(conf, Durations.minutes(15));

        //        kafka parameters

        Map<String, Object> kafkaParams = new HashMap<>();
        kafkaParams.put("bootstrap.servers", "kafkac1n1.dev.bo1.csnzoo.com:9092");
        kafkaParams.put("key.deserializer", StringDeserializer.class);
        kafkaParams.put("value.deserializer", StringDeserializer.class);
        kafkaParams.put("group.id", "exampleGroup");
        kafkaParams.put("auto.offset.reset", "latest");
        kafkaParams.put("enable.auto.commit", false);

        Collection<String> topics = Arrays.asList("META_UI_RUM_AGG_RAW");

        JavaInputDStream<ConsumerRecord<String, String>> stream =
                KafkaUtils.createDirectStream(
                        jssc,
                        LocationStrategies.PreferBrokers(),
                        ConsumerStrategies.Subscribe(topics, kafkaParams)
                );


//        Number of User
        JavaPairDStream<String, Integer> numberOfClickPerUser = stream.mapToPair(record -> {
            ObjectMapper mapper = new ObjectMapper();
            Rum rum = mapper.readValue(record.value(), Rum.class);
            return new Tuple2<>(rum.getUser(), rum);
        }).mapValues(m -> 1).reduceByKey((c1, c2) -> c1 + c2).mapValues(m->1).reduceByKey((c1, c2) -> c1 + c2);



//       number of searched KeyWords
        JavaPairDStream<String, Integer> numberOfSearchedKeyWords = stream.mapToPair(record -> {
            ObjectMapper mapper = new ObjectMapper();
            Rum rum = mapper.readValue(record.value(), Rum.class);
            return new Tuple2<>(rum.getEvent(), rum.getKeyword());
        }).filter(m-> m._1.equals("submit")).mapValues(m -> 1).reduceByKey((c1, c2) -> c1 + c2);



//        Send number of searched keyword to kafka topic
        numberOfSearchedKeyWords.foreachRDD(rdd -> {
                rdd.foreachPartition(partitionOfRecords -> {
                    Producer<Integer, String> producer = MyKafkaProducer.getProducer();
                    while (partitionOfRecords.hasNext()){
                        producer.send(new ProducerRecord<>("test", 1, partitionOfRecords.next().toString()), new Callback() {
                            public void onCompletion(RecordMetadata metadata, Exception e) {
                                if(e != null) {
                                    e.printStackTrace();
                                } else {
                                    System.out.println("The offset of the record we just sent is: " + metadata.offset());
                                }
                            }
                        });
                    }
                });
           });


//        send Number of Click per user to kafka topic
        numberOfClickPerUser.foreachRDD(rdd -> {
            rdd.foreachPartition(partitionOfRecords -> {
                Producer<Integer, String> producer = MyKafkaProducer.getProducer();
                while (partitionOfRecords.hasNext()){
                    producer.send(new ProducerRecord<>("test", 1, partitionOfRecords.next().toString()), new Callback() {
                        public void onCompletion(RecordMetadata metadata, Exception e) {
                            if(e != null) {
                                e.printStackTrace();
                            } else {
                                System.out.println("The offset of the record we just sent is: " + metadata.offset());
                            }
                        }
                    });
                }
            });
        });


        jssc.start();
        jssc.awaitTermination();
    }
}

