
Env
=====
- Apache Maven 3.3.9
- Java 1.8
- Linux

run `mvn -v` you should see similar result as 

```
Apache Maven 3.3.9
Maven home: /usr/share/maven
Java version: 1.8.0_191, vendor: Oracle Corporation
Java home: /usr/lib/jvm/java-8-openjdk-amd64/jre
Default locale: en, platform encoding: UTF-8
OS name: "linux", version: "4.4.0-1072-aws", arch: "amd64", family: "unix"
```

Prepare model data
=====

1. change directory to this README.md path
2. run `bash ./download_model.sh`

you should see the result similar to below

```

--2019-01-19 14:45:14--  https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip
Resolving storage.googleapis.com (storage.googleapis.com)... 172.217.14.240, 2607:f8b0:400a:804::2010
Connecting to storage.googleapis.com (storage.googleapis.com)|172.217.14.240|:443... connected.
HTTP request sent, awaiting response... 200 OK
Length: 49937555 (48M) [application/zip]
Saving to: ‘inception5h.zip’

     0K .......... .......... .......... .......... ..........  0% 3.40M 14s
 48750K .......... .......                                    100%  223M=0.6s

2019-01-19 14:45:14 (82.5 MB/s) - ‘inception5h.zip’ saved [49937555/49937555]

Archive:  inception5h.zip
  inflating: imagenet_comp_graph_label_strings.txt  
  inflating: tensorflow_inception_graph.pb  
  inflating: LICENSE                 
```


Run
=====
1. change directory to this README.md path
2. run `mvn -q compile exec:java -Dexec.args="./model_data flower.jpg"`

you sould see similar result like

```
2019-01-19 14:52:00.579840: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX AVX2 FMA
BEST MATCH: daisy (99.25% likely)
```

