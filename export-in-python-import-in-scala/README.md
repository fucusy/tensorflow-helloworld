Introduction
=====

This tiny project show that we can training model with tensorflow in python,
leverage tons of python library to speed up the training process, then
we can export the trained model into .pb file, and server it in scala/java program

References
=====
1. https://blog.metaflow.fr/tensorflow-how-to-freeze-a-model-and-serve-it-with-a-python-api-d4f3596b3adc
2. https://github.com/tensorflow/tensorflow/blob/de72c8ccce/tensorflow/java/src/main/java/org/tensorflow/examples/LabelImage.java
3. https://blog.metaflow.fr/tensorflow-saving-restoring-and-mixing-multiple-models-c4c94d5d7125

Pre-request
=====
- Sbt  1.2.7
- Linux / macOS
- Python 2.7


run `sbt sbtVersion` you should see similar result as 

```
[info] Loading project definition from /home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to scala-sbt-helloworld (in build file:/home/ubuntu/github/tensorflow-helloworld/scala-sbt-helloworld/)
[info] 1.2.7
```


Export in python
=====
1. change directory to `python` which is under current README.md path
2. run `bash prequest.sh`, to set up the environment
3. activate the env by running `source env/bin/activate`
4. run training code by `python train.py`, You will see similar result as below
, it also save two models into `./logdir/untrained.pb` and `./logdir/trained.pb`

```
_________________________________________________________________
Layer (type)                 Output Shape              Param #   
=================================================================
dense (Dense)                (None, 512)               401920    
_________________________________________________________________
dropout (Dropout)            (None, 512)               0         
_________________________________________________________________
dense_1 (Dense)              (None, 10)                5130      
=================================================================
Total params: 407,050
Trainable params: 407,050
Non-trainable params: 0
_________________________________________________________________
2019-01-21 11:56:17.791865: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: AVX2 FMA
Train on 1000 samples, validate on 1000 samples
Epoch 1/10
1000/1000 [==============================] - 0s 498us/step - loss: 1.2117 - acc: 0.6690 - val_loss: 0.7532 - val_acc: 0.7590
Epoch 2/10
1000/1000 [==============================] - 0s 224us/step - loss: 0.4480 - acc: 0.8760 - val_loss: 0.5687 - val_acc: 0.8240
Epoch 3/10
1000/1000 [==============================] - 0s 306us/step - loss: 0.2869 - acc: 0.9270 - val_loss: 0.4602 - val_acc: 0.8530
Epoch 4/10
1000/1000 [==============================] - 0s 193us/step - loss: 0.2094 - acc: 0.9470 - val_loss: 0.4583 - val_acc: 0.8520
Epoch 5/10
1000/1000 [==============================] - 0s 203us/step - loss: 0.1586 - acc: 0.9710 - val_loss: 0.4351 - val_acc: 0.8580
Epoch 6/10
1000/1000 [==============================] - 0s 210us/step - loss: 0.1203 - acc: 0.9760 - val_loss: 0.4065 - val_acc: 0.8650
Epoch 7/10
1000/1000 [==============================] - 0s 225us/step - loss: 0.0900 - acc: 0.9860 - val_loss: 0.3985 - val_acc: 0.8630
Epoch 8/10
1000/1000 [==============================] - 0s 246us/step - loss: 0.0678 - acc: 0.9910 - val_loss: 0.4077 - val_acc: 0.8710
Epoch 9/10
1000/1000 [==============================] - 0s 197us/step - loss: 0.0501 - acc: 0.9980 - val_loss: 0.3945 - val_acc: 0.8660
Epoch 10/10
1000/1000 [==============================] - 0s 194us/step - loss: 0.0458 - acc: 0.9970 - val_loss: 0.4068 - val_acc: 0.8700
```

5. validate the saved two models in python
    - running `python test_freezed_model.py --frozen_model_filename ./logdir/untrained.pb`
    , the output should be 
    ```
    2019-01-21 11:59:24.059137: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: AVX2 FMA
    ./logdir/untrained.pb model, accuracy: 14.90%
    ```
    
    - running `python test_freezed_model.py --frozen_model_filename ./logdir/trained.pb`
    , the output should be 
    ```
    2019-01-21 12:00:01.979594: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: AVX2 FMA
    ./logdir/trained.pb model, accuracy: 87.00%

    ```


Import from scala
=====

1. change directory to `scala` which is under current README.md path
2. prepare the mnist data by running `bash download_mnist.sh`, it will download
mnist and unzip it into directory, `mnist_data`
3. testing the precision of the two model exported from python
- running the untrained model, `sbt "run ../python/logdir/untrained.pb"`

you should see output like 
```
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from /Users/chenqiang/.sbt/1.0/plugins
[info] Loading project definition from /Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/project
[info] Updating ProjectRef(uri("file:/Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/project/"), "scala-build")...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/project/target/scala-2.12/sbt-1.0/classes ...
[info] Done compiling.
[info] Loading settings for project root from build.sbt ...
[info] Set current project to scala-sbt-helloworld (in build file:/Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/)
[info] Updating ...
[info] Done updating.
[info] Compiling 1 Scala source to /Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/target/scala-2.12/classes ...
[warn] there was one deprecation warning (since 2.11.0); re-run with -deprecation for details
[warn] one warning found
[info] Done compiling.
[info] Packaging /Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/target/scala-2.12/scala-sbt-helloworld_2.12-0.1.0-SNAPSHOT.jar ...
[info] Done packaging.
[info] Running example.Hello ../python/logdir/untrained.pb
2019-01-21 12:04:20.803438: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.2 AVX AVX2 FMA
model path ../python/logdir/untrained.pb, acc: 0.15
[success] Total time: 10 s, completed Jan 21, 2019 12:04:20 PM

```

   - running the untrained model, `sbt "run ../python/logdir/trained.pb"`
   
you should see output like 

```
[info] Loading settings for project global-plugins from idea.sbt ...
[info] Loading global plugins from /Users/chenqiang/.sbt/1.0/plugins
[info] Loading project definition from /Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/project
[info] Loading settings for project root from build.sbt ...
[info] Set current project to scala-sbt-helloworld (in build file:/Users/chenqiang/Documents/github/tensorflow-helloworld/export-in-python-import-in-scala/scala/)
[info] Running example.Hello ../python/logdir/trained.pb
2019-01-21 12:05:51.689668: I tensorflow/core/platform/cpu_feature_guard.cc:141] Your CPU supports instructions that this TensorFlow binary was not compiled to use: SSE4.2 AVX AVX2 FMA
model path ../python/logdir/trained.pb, acc: 0.87
[success] Total time: 2 s, completed Jan 21, 2019 12:05:51 PM
```