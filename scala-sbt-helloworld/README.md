
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


Run
=====
1. change directory to this README.md path
2. run `mvn -q compile exec:java`

you sould see similar result like

```
2019-01-19 14:30:04.008030: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.1 SSE4.2 AVX AVX2 FMA
Hello from 1.12.0
```

