import os
import tensorflow as tf
from tensorflow import keras
from tensorflow.python.framework.graph_util_impl import convert_variables_to_constants

(train_images, train_labels), (test_images, test_labels) = tf.keras.datasets.mnist.load_data()

train_labels = train_labels[:1000]
test_labels = test_labels[:1000]

train_images = train_images[:1000].reshape(-1, 28 * 28) / 255.0
test_images = test_images[:1000].reshape(-1, 28 * 28) / 255.0


# Returns a short sequential model
def create_model():
  model = tf.keras.models.Sequential([
    keras.layers.Dense(512, activation=tf.nn.relu, input_shape=(784,)),
    keras.layers.Dropout(0.2),
    keras.layers.Dense(10, activation=tf.nn.softmax)
  ])
  
  model.compile(optimizer='adam', 
                loss=tf.keras.losses.sparse_categorical_crossentropy,
                metrics=['accuracy'])
  
  return model


# Create a basic model instance
model = create_model()
model.summary()

all_saver = tf.train.Saver()

model = create_model()
outputs = [node.op.name for node in model.outputs]
session = keras.backend.get_session()

untrained_graph = convert_variables_to_constants(session, session.graph_def, outputs)
tf.train.write_graph(untrained_graph, "./logdir/", "untrained.pb", as_text=False)

model.fit(train_images, train_labels,  epochs = 10, 
          validation_data = (test_images,test_labels)) 


trained_graph = convert_variables_to_constants(session, session.graph_def, outputs)
tf.train.write_graph(trained_graph, "./logdir/", "trained.pb", as_text=False)
