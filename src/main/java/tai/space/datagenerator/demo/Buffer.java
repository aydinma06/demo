package tai.space.datagenerator.demo;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.rest.RestStatus;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Buffer {
    private List<Event> collection ;
    private int bufferSize = 20;
    DateFormat dateFormat;
    public Buffer()
    {
        dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        collection = new CopyOnWriteArrayList<Event>();
    }

    public int getCollectionSize()
    {
        return collection.size();
    }

    public void addToBuffer(Event event) {

        collection.add(event);
        if(collection.size() >=20){
            List<Event> fullList = new ArrayList<>(collection);
            Thread t1 = new Thread(new Runnable() {
                @Override
                public void run()
                {
                    send(fullList);
                }
            });
            t1.start();
            //System.out.println(collection.size());
            collection = new CopyOnWriteArrayList<Event>();
        }


    }

    public void send(List<Event> collection) {
        //for(int i=0; i<bufferSize;i++)
        //{
        //    if(!collection.isEmpty()){
        //        GeneratedObject myObject = (GeneratedObject) collection.get(0);
        //        Map<String, Object> jsonMap = new HashMap<>();
        //        jsonMap.put("Temperature",myObject.getMyTemperature());
        //        jsonMap.put("Time", myObject.getMyDate());
        //        putSomethingForMe(jsonMap,myObject.getMyIndex(), myObject.getMyType(),String.valueOf(myObject.getMyID()));
        //        collection.remove(0);
        //    }
        //}

        for(Event event:collection)
        {
            GeneratedObject myObject = (GeneratedObject) event;
            Map<String, Object> jsonMap = new HashMap<>();
            Date date = new Date();
            String generatedDate = dateFormat.format(date);
            jsonMap.put("Temperature",myObject.getMyTemperature());
            jsonMap.put("Time", generatedDate); // Değiştirdim, önceden random ürettiği tarihi atıyordu, artık bastığı tarih
            //System.out.println(myObject.getMyDate());
            putSomethingForMe(jsonMap,myObject.getMyIndex(), myObject.getMyType(),String.valueOf(myObject.getMyID()));
        }
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
}
