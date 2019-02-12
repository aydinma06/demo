package tai.space.datagenerator.demo;


import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

public class Buffer {
    private List<Event> collection ;
    private int bufferSize = 20;
    DateFormat dateFormat;
    private int  corePoolSize  =    10;
    private int  maxPoolSize   =   20; //  Eğer queue doluysa ve corePoolSize<maxPoolSize ise yeni thread oluşturur.
    private long keepAliveTime = 20000; //Thread terminating time
    ThreadPoolExecutor executor;
    public Buffer()
    {
        executor =new ThreadPoolExecutor(corePoolSize,maxPoolSize,keepAliveTime
                        ,TimeUnit.MILLISECONDS,new LinkedBlockingQueue<>());
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        collection = new CopyOnWriteArrayList<Event>(); // Gelen objelerin eklendiği ana buffer. Thread safe olması için CopyOnWriteArrayList tipinde
    }

    public int getCollectionSize()
    {
        return collection.size();
    }

    public void addToBuffer(Event event) {

        collection.add(event); // Gelen obje buffer'a ekleniyor
        if(collection.size() >=100){ // Buffer'ın size'ının istenilen boyuta ulaşıp ulaşmadığı kontrol ediliyor
            List<Event> fullList = new ArrayList<>(collection); // Buffer istenilen boyuna ulaştığı zaman, bir arraylist'e kopyalanır.

            executor.submit(() -> {
                send(fullList);//Kopyalanan array'in içindeki objeler ilgili indexe basılır.
                return null;
            });


            //System.out.println(collection.size());
            collection = new CopyOnWriteArrayList<Event>(); // Gönderilme işleminden sonra buffer boşaltılır.
        }


    }

    public void send(List<Event> collection) {
        BulkRequest bulkRequest = new BulkRequest();
        for(Event event:collection)
        {

            GeneratedObject myObject = (GeneratedObject) event;
            Map<String, Object> jsonMap = new HashMap<>();
            Date date = new Date();
            String generatedDate = dateFormat.format(date);
            jsonMap.put("Temperature",myObject.getMyTemperature());
            jsonMap.put("Time", generatedDate); // Değiştirdim, önceden random ürettiği tarihi atıyordu, artık bastığı tarih
            //putSomethingForMe(jsonMap,myObject.getMyIndex(), myObject.getMyType(),String.valueOf(myObject.getMyID()));
            bulkRequest.add(new IndexRequest(myObject.getMyIndex(),myObject.getMyType()).source(jsonMap));
        }
        BulkResponse response = null;
        try {
            response = DemoApplication.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.status());


    }

    public static void putSomethingForMe (Map<String,Object> myJSONMap, String myIndex, String myType, String myID) {
        IndexRequest myPutRequest = new IndexRequest(myIndex,myType).id(myID).source(myJSONMap);
        IndexResponse response = null;
        try {
            response = DemoApplication.client.index(myPutRequest, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.getResult());
    }

    public static void putSomethingBulkForMe (List<Map<String, Object>> maps) {
        BulkRequest bulkRequest = new BulkRequest();
        for(Map<String, Object> map:maps)
        {
            bulkRequest.add(new IndexRequest("mynormalindex","generatedtemperature").source(map));
        }
        BulkResponse response = null;
        try {
            response = DemoApplication.client.bulk(bulkRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.status());

    }
}
