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

#print(model.graph == tf.get_default_graph())

all_saver = tf.train.Saver()

checkpoint_path = "training_1/cp.ckpt"
checkpoint_dir = os.path.dirname(checkpoint_path)

# Create checkpoint callback
cp_callback = tf.keras.callbacks.ModelCheckpoint(checkpoint_path, 
                                                 save_weights_only=False,
                                                 verbose=1)

model = create_model()
saved_model_path = tf.contrib.saved_model.save_keras_model(model, "./untrained_models")
#all_saver.save(sess, './untrained_models_saver/data')
outputs = [node.op.name for node in model.outputs]
session = keras.backend.get_session()

min_graph = convert_variables_to_constants(session, session.graph_def, outputs)
tf.train.write_graph(min_graph, "./logdir/", "untrained.pb", as_text=False)

model.fit(train_images, train_labels,  epochs = 10, 
          validation_data = (test_images,test_labels),
          callbacks = [cp_callback])  # pass callback to training
saved_model_path = tf.contrib.saved_model.save_keras_model(model, "./trained_models")

#all_saver.save(sess, './trained_models_saver/data')

min_graph = convert_variables_to_constants(session, session.graph_def, outputs)
tf.train.write_graph(min_graph, "./logdir/", "trained.pb", as_text=False)
