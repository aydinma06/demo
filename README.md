# Elasticsearch
[Elasticsearch](https://www.elastic.co/), Apache Lucene tabanlı, Java programlama dili ile yazılmış açık kaynak kodlu bir arama ve analiz motorudur.

## Kurulumu
Elastisearch kurulumu için ;


* [Elasticsearch](https://www.elastic.co/downloads/elasticsearch ) indirilip, dosyalar çıkartılır.
* Dosyalar çıkartıldıktan sonra *".\elasticsearch-6.5.2\bin"* içindeki "elasticsearch.bat" çalıştırıldığında kurulum gerçekleştirilmiş ve Elasticsearch çalışmaya olacaktır.
* Daha sonraki çalıştırmalar için yine *"elasticseach.bat"* kullanılır.
* Elasticsearch'ün çalışıp çalışmadığını anlamak için browser üzerinden *"http://localhost:9200/"* adresi kontrol edilebilir (Default port değiştirilmedi ise). Aşağıdakine benzer bir yazı çıkması elastic search'ün ayakta olduğunu göstermektedir.
```
{
  "name" : "9t7cG_X",
  "cluster_name" : "elasticsearch",
  "cluster_uuid" : "HJzRxrbsR7yWEqlrfW2VUg",
  "version" : {
    "number" : "6.5.2",
    "build_flavor" : "default",
    "build_type" : "zip",
    "build_hash" : "9434bed",
    "build_date" : "2018-11-29T23:58:20.891072Z",
    "build_snapshot" : false,
    "lucene_version" : "7.5.0",
    "minimum_wire_compatibility_version" : "5.6.0",
    "minimum_index_compatibility_version" : "5.0.0"
  },
  "tagline" : "You Know, for Search"
}
```
* Veya windows konsol üzerinden *"curl -X GET http://localhost:9200"* komutu çalıştırılabilir. 
curl  yok ise;
* [cURL](https://curl.haxx.se/download.html) indirilip, dosyalar çıkartıldıktan sonra *".\curl-7.63.0-win64-mingw\bin"* içindeki "curl.exe" çalıştırılarak kurulumu gerçekleştirilebilir.

## Temel Kavramlar

Elasticsearch kendine özel bir terminoloji barındırmaktadır. Günümüzde alıştığımız **database,table,row** gibi kelimelerin yanı sıra **indices,types,documents** gibi kelimeler karşımıza çıkmaktadır. Bu kavramlar farklı şekillerde karşımıza çıksa da, işlevleri temel olarak aynıdır. 
* İlişkisel veri tabanı ve Elasticsearch kavramları aşağıdaki gibi temsil edilebilir.

    * Relational DB ⇒ Databases ⇒ Tables ⇒ Rows ⇒ Columns
    * Elasticsearch ⇒ Indices ⇒ Types ⇒ Documents ⇒ Fields

* **Index** kavramı, ilişkisel veri tabanlardaki **veri tabanı** kavramıyla aynı anlama gelmektedir. Bir indexin isimlendirmesinde;
  * Sadece küçük harf kullanılır
  * " \, /, * , ?, ", <, >, |, ` ` (Boşluk karakteri), ,, #" gibi karakterleri içeremez
  * " -, _, + " karakterleriyle başlayamaz
  * 255 bytedan daha büyük olamaz
  
* Elasticsearch birden fazla **index(database)** bulundurabilir. Bu indexler içinde birden fazla **type(table)** olabilir ve her type içinde çok sayıda **documents(row)** içerebilir. Bu document'ler de birden fazla **fields(column)**'dan oluşabilmektedir. Daha detaylı bilgi için [Elasticsearch dokümantasyonunu](https://www.elastic.co/guide/index.html) inceleyebilirsiniz.

## Kullanımı

* **PUT** kullanılarak bir index ve type içine veri eklenebilmektedir. Bu işlem gerçekleştirilirken yaygın olarak JSON formatı kullanılmaktadır. İlgili index veya type daha önce oluşturulmamış olsa bile Elasticsearch tarafından otomatik olarak oluşturulmaktadır. Sorgunun sonundaki *1* değeri ilgili verinin id değerini göstermektedir. Bu sorguda olduğu gibi kullanıcı tarafından da verilebilirken, kullanıcı tarafından verilmediği zaman Elasticsearch tarafından "XBVN4WgBXsOsF6gbiSSc" gibi bir id değeri oluşturulmaktadır.
```
PUT kimchy/_doc/1
{
    "user": "kimchy",
    "post_date": "2009-11-15T13:12:00",
    "message": "Trying out Elasticsearch, so far so good?"
}
```

* **GET** komutu kullanılarak index içinde arama yapıldığında, herhangi bir parametre belirtilemiş veya bir filtereleme işlemi uygulanmamış ise indexin tamamını getirir.
```
GET companydatabase/_search
{
  "query": {
    "match_all": {}
  }
}
```

* Eğer sonuçların sadece x tanesi görüntülenmek isteniyorsa **size=x** şeklinde kullanılır. ( Örn. size=50 )
```
GET companydatabase/_search?size=x 
{
  "query": {
    "match_all": {}
  }
}
```

* Elasticsearch'te indexin veya type'ın daha önceden oluşturulmuş olması gerekmez. Yukarıdaki komutlardan biri kullanıldığında index veya type daha önce oluşturulmamış olsa bile, Elasticsearch tarafından otomatik olarak oluşturulmaktadır. Ancak otomatik olarak oluşturulmasından kaynaklı olarak, field'ların tipi de elastic search tarafından belirlenir. Bu durumu kontrol edebilmek için index veya type'a daha önceden map işlemi uygulanması gerekmektedir. Aşağıda örnek bir map işlemi verilmiştir.
```
PUT companydatabase
{
  "mappings" : {
    "employees" : {
      "properties" : {
        "FirstName" : { "type" : "keyword" },
        "LastName" : { "type" : "keyword" },
        "Designation" : { "type" : "keyword" },
        "Salary" : { "type" : "integer" },
        "DateOfJoining" : { "type" : "date", "format": "yyyy-MM-dd" },
        "Address" : { "type" : "keyword" },
        "Gender" : { "type" : "keyword" },
        "Age" : { "type" : "integer" },
        "MaritalStatus" : { "type" : "keyword" },
        "Interests" : { "type" : "keyword" }
      }
    }
  }
}
```

* **DELETE** komutu kullanılarak ilgili index silinebilir.
```
DELETE companydatabase/
```

* **POST** komutu yardımıyla daha önce map işlemi uyguladığımız index'in içine aşağıdaki şekilde veri basılabilmektedir.
```
POST companydatabase/_bulk
{"index":{"_index":"companydatabase","_type":"employees"}}
{"FirstName":"ELVA","LastName":"RECHKEMMER","Designation":"CEO","Salary":"154000","DateOfJoining":"1993-01-11","Address":"8417 Blue Spring St. Port Orange, FL 32127","Gender":"Female","Age":62,"MaritalStatus":"Unmarried","Interests":"Body Building,Illusion,Protesting,Taxidermy,TV watching,Cartooning,Skateboarding"}
{"index":{"_index":"companydatabase","_type":"employees"}}
{"FirstName":"JENNEFER","LastName":"WENIG","Designation":"President","Salary":"110000","DateOfJoining":"2013-02-07","Address":"16 Manor Station Court Huntsville, AL 35803","Gender":"Female","Age":45,"MaritalStatus":"Unmarried","Interests":"String Figures,Working on cars,Button Collecting,Surf Fishing"}
.
.
.
```

* Veriler tek tek kullanıcı tarafından eklenebildiği gibi, bir JSON dosyası ile toplu bir şekilde de eklenebilmektedir. Bunun için dosyanın bulunduğu dizine gidip, bir curl komutu yardımıyla Elasticsearch üzerine JSON dosyası içerisindeki veriler aşağıdaki şekilde basılabilmektedir.
```
curl -XPOST localhost:9200/companydatabase/_bulk --data-binary @Employees50K.json -H Content-Type:application/json
```

* Verileri eklediğimiz index üzerinden, belirli alanlar için aşağıdaki şekilde farklı sorgulamalar yapılabilir.
```
GET companydatabase/_search?q=LastName:RECHKEMMER

GET companydatabase/_search?q=Designation:President
```

* Bu sorgulamalar daha komplex bir şekilde de yapılabilmektedir. Aşağıdaki sorguda **size** ile kaç adet sonuç döndürmesi istendiği ve **from** ile de kaçıncı kayıttan başlanması istendiği belirtilmiştir.
```
GET companydatabase/_search
{
  "query": {
    "match": {
      "Gender": "Female"
    }
  },
    "size": 10,
    "from": 0
}
```

* Sayısal veri içeren sütunlar için **range** değerleri belirlenerek, ilgili koşulları sağlayan veriler için arama yapılabilir. 
  * Bu sorgulamalar yapılırken aşağıdaki anahtar kelimeler kullanılabilir
    * gt => Greater than
    * gte => Greater than or equal to
    * lt => Less than
    * lte => Less than or equal to 
```
GET companydatabase/_search
{
  "query": {
    "range": {
      "Age": {
        "gte": 40,
        "lte": 50
      }
    }
  }
}
```

* Yine sorgulama yapılırken, istenilen bir field'a göre sıralama **sort** kullanılarak yapılabilmektedir. Bunun için **desc** ve **asc** anahtar kelimeleri kullanılabilir.
```
GET companydatabase/_search
{
  "query": {
    "range": {
      "Age": {
        "gte": 40,
        "lte": 50
      }
    }
  }
  ,"sort": [
    {
      "Age": {
        "order": "desc"
      }
    }
  ],
    "size": 20,
    "from": 0
}
```

### Aggregation İşlemleri

* Belirlenen bir field için **min/max** komutları kullanılarak, en küçük veya en büyük değer bulunabilmektedir. Bu sorgu aggregation işlemininin yanı sıra bir arama sorgusu olarak da çalıştığı için arama sonuçlarını da döndürmektedir. Bu sonuçları görmeyip sadece aggregation işleminin sonucunu görmek için **"size":0** kullanılır.
```
 GET companydatabase/_search
{
  "aggs": {
    "minAge": {
      "min": {
        "field": "Age"
      }
    }
  },
  "size":0
}
```
```
GET companydatabase/_search
{
  "aggs": {
    "maxSalary": {
      "max": {
        "field": "Salary"
      }
    }
  },
  "size":0
}
```

* Min/max işlemleri aşağıdaki şekilde herhangi bir sorgunun sonucuna uygulanarak da alınabilmektedir.
```
GET companydatabase/_search
{
  "query": {
    "term": {
    "Designation": {
      "value": "President"
    }
  }}, 
  "aggs": {
    "minAge": {
      "min": {
        "field": "Age"
      }
    }
  },
  "size":0
}
```

* Bir field için aggregation fonksiyonlarını tek tek ayrı olarak sorgulamak yerine aşağıdaki sorgu kullanılarak tek bir sorguda **Count,Min,Max,Avg,Sum** değerleri görülebilir.
```
GET companydatabase/_search
{
  "aggs": {
    "ageStats": {
      "stats": {
        "field": "Age"
      }
    }
  },"size": 0
}
```

* Sadece Count,Min,Max,Avg,Sum değil de ilgili field için daha kapsamlı aggregation işlemlerini tek sorguda görmek için (Varyans,Kareler toplamı gibi) aşağıdaki sorgu kullanılabilir.
```
GET companydatabase/_search
{
  "aggs": {
    "ageStats": {
      "extended_stats": {
        "field": "Age"
      }
    }
  },"size": 0
}

Sorgusu için aşağıdaki gibi bir sonuç elde edilmektedir;

"aggregations" : {
    "ageStats" : {
      "count" : 50000,
      "min" : 20.0,
      "max" : 65.0,
      "avg" : 29.26478,
      "sum" : 1463239.0,
      "sum_of_squares" : 4.4176147E7,
      "variance" : 27.09559155159995,
      "std_deviation" : 5.205342596947865,
      "std_deviation_bounds" : {
        "upper" : 39.67546519389573,
        "lower" : 18.854094806104268
      }
```

* Bir field içindeki unique metinler aşağıdaki sorgu kullanılarak bulunabilmektedir.
```
{
  "aggs": {
    "uniqueNames": {
      "cardinality": {
        "field": "FirstName"
      }
    }
  },"size": 0
}
```

* Bir metinin ilgili field içinde kaç kere geçtiği aşağıdaki sorgu kullanılarak bulunabilmektedir. İlk kullanılan size (**"size":10**) kaç adet metinin dönmesini istendiğini gösterirken, ikinci kullanılan size ise daha önceden de açıklandığı gibi sorgu sayma işlemininin yanı sıra bir arama sorgusu olarak da çalıştığı için arama sonuçlarını da döndürmektedir. Bu sonuçları görmeyip sadece sayma işleminin sonucunu görmek için **"size":0** kullanılır.
```
GET companydatabase/_search
{
  "aggs": {
    "textAggTrying1": {
      "terms": {
        "field": "Designation",
        "size": 10
      }
    }
  },"size": 0
}
```

* Belirli bir field için aralık değeri belirlenerek, her aralığa düşen nümerik veri sayısı aşağıdaki sorguyla bulunabilmektedir.
```
GET companydatabase/_search?size=0
{
  "aggs": {
    "byAge": {
      "histogram": {
        "field": "Age",
        "interval": 10
      }
    }
  }
}

Sorgusu için aşağıdaki gibi bir sonuç elde edilmektedir;

"aggregations" : {
    "byAge" : {
      "buckets" : [
        {
          "key" : 20.0,
          "doc_count" : 29338
        },
        {
          "key" : 30.0,
          "doc_count" : 18876
        },
        {
          "key" : 40.0,
          "doc_count" : 1082
        },
        {
          "key" : 50.0,
          "doc_count" : 527
        },
        {
          "key" : 60.0,
          "doc_count" : 177
        }
      ]
    }
  }
```

* Bu aralık kullanıcı tarafından istenildiği değerlerle ve istenilen isimle de verilebilmektedir. **key** değeri ilgili aralığın ne isim alacağı, **from** aralığın alt değeri **to** ise aralığın üst değerini temsil etmektedir.
```
GET companydatabase/_search?size=0
{
  "aggs": {
    "byAgeLv2": {
      "range": {
        "field": "Age",
        "ranges": [
          {
            "key": "Up to 40", 
            "from": 0,
            "to": 40
          },
          {
            "key": "40-50"
            , "from": 40
            , "to": 50
          },
          {
            "key": "Above 50"
            ,"from": 50
          }
        ]
      }
    }
  }
}
```

* Aralık verme işlemi nümerik verilerin yanı sıra tarih üzerinde de farklı anahtar kelimeler kullanılarak yapılabilmektedir. **min_doc_count** parameteresi kullanılarak o aralığa düşen minimum veri sayısı belirlenebilmektedir.
```
GET companydatabase/_search?size=0
{
  "aggs": {
    "queryWithDate": {
      "date_histogram": {
        "field": "DateOfJoining",
        "interval": "year",
        "order": 
        {
          "_key": "asc"
        }
        , "min_doc_count": 1
      }
    }
  }
}
```
* **include/exclude** anahtar kelimeleri kullanılarak ilgili field için bir metini içeren veya içermeyen veriler bulunabilmektedir. **include** ilgili metini içeren kayıtları sorgularken kullanılırken, **exclude** ilgili metini içermeyen kayıtları sorgularken kullanılmaktadır.
```
GET companydatabase/_search?size=0
{
  "aggs": {
    "testName": {
      "terms": {
        "field": "Address",
        "size": 10
        , "include": ".*Street.*"
        , "exclude": ".*North.*"
      }
    }
  }
}
```

* Bir field'ın değerinin belirli bir değere eşit olup olmadığı **Must/Must not** sorgularıyla gerçekleştirilmektedir.
```
GET companydatabase/_search
{
  "query": 
    {
      "bool": 
        {"must": [
          {"match": {
            "FirstName": "FRANKLIN"
          }}
        ],"must_not": [
          {"match": {
            "Address": "7244 Wentworth Ave. Palm Harbor, FL 34683"
          }}
        ]}
        
    }
}
```

* Bu sorgulara ek olarak yine sonuç üzerinde sıralama işlemi de gerçekleştirilebilmektedir.
```
GET companydatabase/_search
{
  "query": {
    "bool": {
      "must": 
    {
      "match": {
      "FirstName": "FRANKLIN"
    }},"must_not": [
      {"range": {
        "Age": {
          "gte": 40,
          "lte": 60
        }
      }}
    ]
  }},"sort": [
      {
        "Age": {
          "order": "desc"
        }
      }
    ]
}
```
