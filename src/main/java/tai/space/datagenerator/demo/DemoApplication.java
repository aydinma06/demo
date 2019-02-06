package tai.space.datagenerator.demo;

import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


@SpringBootApplication
public class DemoApplication {
    static RestHighLevelClient client;
    static Buffer myBuffer;
    public static void main(String[] args) throws IOException {

        SpringApplication.run(DemoApplication.class, args);
        //System.out.println("Hello World !!!");

        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")).setMaxRetryTimeoutMillis(100000));

        myBuffer = new Buffer();
        //mapMyIndex();
        addGeneratedSamples();   //Sadece bunu açman yeterli
        //getSomethingForMe("mygeneratedvalues","generatedtemperature","2");
        //putSomethingForMe(jsonMap,"fulltextsearch","mysampletexts","13");
        //deleteSomethingForMe("mygeneratedvalues");

        //System.out.println(myBuffer.getCollectionSize());
        //System.out.println(myBuffer.getCollectionSize());
        //client.close();
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

    public static void addGeneratedSamples()
    {
        myBuffer = new Buffer();
        int normalIndex = 0;
        int outlierIndex = 0;
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS");
        //Date date = new Date();
        for(int i=0;i<8000;i++)
        {
            Date date = new Date();
            //date.setTime(date.getTime()+300);

            double generatedTemperatureValue = generateTemperature();
            String generatedDate = dateFormat.format(date);
            //System.out.println(generatedDate);
            if(generatedTemperatureValue < 5 || generatedTemperatureValue > 25)
            {
                myBuffer.addToBuffer(new GeneratedObject(outlierIndex,generatedDate,generatedTemperatureValue,"myoutlierindex","generatedtemperature"));
                outlierIndex++;
            }
            else{
                myBuffer.addToBuffer(new GeneratedObject(normalIndex,generatedDate,generatedTemperatureValue,"mynormalindex","generatedtemperature"));
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







