package tai.space.datagenerator.demo;

import org.apache.http.HttpHost;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@SpringBootApplication
public class DemoApplication {
    static RestHighLevelClient client;
    public static void main(String[] args) throws IOException {

        //SpringApplication.run(DemoApplication.class, args);
        //System.out.println("Hello World !!!");

        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));

        //mapMyIndex();
        addGeneratedSamples();
        //getSomethingForMe("mygeneratedvalues","generatedtemperature","2");
        //putSomethingForMe(jsonMap,"fulltextsearch","mysampletexts","13");
        //deleteSomethingForMe("mygeneratedvalues");


        client.close();
    }

    public static double generateTemperature()
    {
        Random myRandomizer = new Random();
        double myGeneratedTemperatureValue = myRandomizer.nextGaussian();
        return myGeneratedTemperatureValue*10+20;
    }

    public static void getSomethingForMe(String myIndex,String myType,String myID) throws IOException {

        GetRequest myGetRequest = new GetRequest(myIndex,myType,myID);//"fulltextsearch","mysampletexts","2"
        GetResponse getResponse = client.get(myGetRequest, RequestOptions.DEFAULT);
        String message = getResponse.getSourceAsString();
        System.out.println(message);

    }

    public static void putSomethingForMe (Map<String,Object> myJSONMap,String myIndex,String myType,String myID)
    {

        IndexRequest myPutRequest = new IndexRequest(myIndex,myType).id(myID).source(myJSONMap);
        IndexResponse response = null;
        try {
            response = client.index(myPutRequest, RequestOptions.DEFAULT);
        } catch(ElasticsearchException e) {
            if (e.status() == RestStatus.CONFLICT) {
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(response.getResult());
    }

    public static void addGeneratedSamples()
    {
        int normalIndex = 0;
        int outlierIndex = 0;
        for(int i=0;i<1000;i++)
        {

            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
            Date date = new Date();
            //System.out.println(dateFormat.format(date));
            Map<String, Object> jsonMap = new HashMap<>();
            double generatedTemperatureValue = generateTemperature();
            jsonMap.put("Temperature",generatedTemperatureValue);
            jsonMap.put("Time", dateFormat.format(date));
            if(generatedTemperatureValue < 5 || generatedTemperatureValue > 25)
            {
                putSomethingForMe(jsonMap,"myoutlierindex","generatedtemperature",String.valueOf(outlierIndex));
                outlierIndex++;
            }
            else{
                putSomethingForMe(jsonMap,"mynormalindex","generatedtemperature",String.valueOf(normalIndex));
                normalIndex++;
            }
        }
    }

    public static void deleteSomethingForMe(String myIndex) throws IOException {
        DeleteIndexRequest myDeleteRequest = new DeleteIndexRequest(myIndex);
        AcknowledgedResponse deleteIndexResponse = client.indices().delete(myDeleteRequest, RequestOptions.DEFAULT);
    }

    public static void mapMyIndex() throws IOException {
        PutMappingRequest request = new PutMappingRequest("mygeneratedvalues");
        request.source("{\n" +
                "  \"mappings\" : {\n" +
                "    \"generatedtemperature\" : {\n" +
                "      \"properties\" : {\n" +
                "        \"Temperature\" : { \"type\" : \"integer\" },\n" +
                "        \"Time\" : { \"type\" : \"date\", \"format\": \"yyyy/MM/dd HH:mm:ss:SSS\" }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}", XContentType.JSON);

        AcknowledgedResponse putMappingResponse = client.indices().putMapping(request, RequestOptions.DEFAULT);
    }

}







