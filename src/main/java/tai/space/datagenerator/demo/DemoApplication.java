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
                        new HttpHost("localhost", 9201, "http")).setMaxRetryTimeoutMillis(1000000)); // Elastic Seach High Level API kullanmak için client initialize ediliyor

        //myBuffer = new Buffer(); //Verilerin üretilip, göndermek için tutulduğu bir buffer nesnesi
        //mapMyIndex();
        addGeneratedSamples();   //Sadece bunu açman yeterli
        //getSomethingForMe("mygeneratedvalues","generatedtemperature","2"); // İlgili index/type/id için elasticsearch'den veri çekme
        //putSomethingForMe(jsonMap,"fulltextsearch","mysampletexts","13"); // İlgili index/type/id için elasticsearch'e veri basma
        //deleteSomethingForMe("mygeneratedvalues"); // İndex silme

        //client.close();
    }


    public static double generateTemperature() { // Random sıcaklık değerleri üreten fonksiyon
        Random myRandomizer = new Random();
        double myGeneratedTemperatureValue = myRandomizer.nextGaussian();
        return myGeneratedTemperatureValue*10+20;
    }

    public static void getSomethingForMe(String myIndex,String myType,String myID) throws IOException {
        // İlgili index/type/id için elasticsearch'den veri çekme
        GetRequest myGetRequest = new GetRequest(myIndex,myType,myID);//"fulltextsearch","mysampletexts","2"
        GetResponse getResponse = client.get(myGetRequest, RequestOptions.DEFAULT); // Request atılıp dönen respone ilgili nesneye yazılıyor
        String message = getResponse.getSourceAsString(); // Dönen sonucun yazdırılabilmesi için string değer olarak alınıyor
        System.out.println(message);

    }

    public static void addGeneratedSamples() {
        myBuffer = new Buffer();//Verilerin üretilip, göndermek için tutulduğu bir buffer nesnesi
        //int normalIndex = 316000; // Normal değerlerin id'si
        //int outlierIndex = 316000; // Outlier değerlerin id'si
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss:SSS"); // Tarih için format
        //Date date = new Date();
        for(int i=0;i<100000;i++) // Eklenecek veri sayısı kadar dönüyor
        {
            Date date = new Date(); // O anın tarihi alınıyor
            //date.setTime(date.getTime()+300);

            double generatedTemperatureValue = generateTemperature(); // Tarih istenilen formata dönüştürülüyor
            String generatedDate = dateFormat.format(date);
            //System.out.println(generatedDate);
            if(generatedTemperatureValue < 5 || generatedTemperatureValue > 25) {  // İlgili değerler arasında değilse outlier indexine basılıyor
                myBuffer.addToBuffer(new GeneratedObject(generatedDate,generatedTemperatureValue));
                //outlierIndex++;
            }
            else { // Değilse normal index'e basılıyor
                myBuffer.addToBuffer(new GeneratedObject(generatedDate,generatedTemperatureValue));
                //normalIndex++;
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







