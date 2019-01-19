Pre-request
=====
- Sbt  1.2.7
- Linux


run `sbt sbtVersion` you should see similar result as 

```
[info] Loading project definition from /home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to scala-sbt-helloworld (in build file:/home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/)
[info] 1.2.7
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
2. run `sbt run`

you sould see similar result like

```
[info] Loading project definition from /home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to scala-sbt-helloworld (in build file:/home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/)
[info] Compiling 1 Scala source to /home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/target/scala-2.12/classes ...
[info] Done compiling.
[info] Packaging /home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/target/scala-2.12/scala-sbt-helloworld_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello
2019-01-19 15:22:06.666394: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX AVX2 FMA
Hello from 1.12.0
[success] Total time: 5 s, completed Jan 19, 2019 3:22:06 PM
```

