
Pre-request
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


Run
=====
1. change directory to this README.md path
2. run `mvn -q compile exec:java`

you sould see similar result like

```
2019-01-19 14:30:04.008030: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX AVX2 FMA
Hello from 1.12.0
```

