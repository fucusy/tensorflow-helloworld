package example

import java.io.IOException
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Arrays
import java.util.List
import org.tensorflow.DataType
import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.Session 
import org.tensorflow.Tensor 
import org.tensorflow.TensorFlow 
import org.tensorflow.types.UInt8 

import java.lang.Float

class GraphBuilder(private var g: Graph) {

  def div(x: Output[Float], y: Output[Float]): Output[Float] =
    binaryOp("Div", x, y)

  def sub[T](x: Output[T], y: Output[T]): Output[T] = binaryOp("Sub", x, y)

  def resizeBilinear[T](images: Output[T],
                        size: Output[Integer]): Output[Float] =
    binaryOp3("ResizeBilinear", images, size)

  def expandDims[T](input: Output[T], dim: Output[Integer]): Output[T] =
    binaryOp3("ExpandDims", input, dim)

  def cast[T, U](value: Output[T], `type`: Class[U]): Output[U] = {
    val dtype: DataType = DataType.fromClass(`type`)
    g.opBuilder("Cast", "Cast")
      .addInput(value)
      .setAttr("DstT", dtype)
      .build()
      .output[U](0)
  }

  def decodeJpeg(contents: Output[String], channels: Long): Output[UInt8] =
    g.opBuilder("DecodeJpeg", "DecodeJpeg")
      .addInput(contents)
      .setAttr("channels", channels)
      .build()
      .output[UInt8](0)

  def constant[T](name: String, value: AnyRef, `type`: Class[T]): Output[T] = {
    val t = Tensor.create[T](value, `type`)
	g.opBuilder("Const", name)
        .setAttr("dtype", DataType.fromClass(`type`))
        .setAttr("value", t)
        .build()
        .output[T](0)
  }
  def constant[T](name: String, value: Array[Byte], `type`: Class[T]): Output[T] = {
    val t = Tensor.create[T](value, `type`)
	g.opBuilder("Const", name)
        .setAttr("dtype", DataType.fromClass(`type`))
        .setAttr("value", t)
        .build()
        .output[T](0)
  }
  def constant[T](name: String, value: Int, `type`: Class[T]): Output[T] = {
    val t = Tensor.create[T](value, `type`)
	g.opBuilder("Const", name)
        .setAttr("dtype", DataType.fromClass(`type`))
        .setAttr("value", t)
        .build()
        .output[T](0)
  }
  def constant[T](name: String, value: Array[Int], `type`: Class[T]): Output[T] = {
    val t = Tensor.create[T](value, `type`)
	g.opBuilder("Const", name)
        .setAttr("dtype", DataType.fromClass(`type`))
        .setAttr("value", t)
        .build()
        .output[T](0)
  }
  def constant[T](name: String, value: Float, `type`: Class[T]): Output[T] = {
    val t = Tensor.create[T](value, `type`)
	g.opBuilder("Const", name)
        .setAttr("dtype", DataType.fromClass(`type`))
        .setAttr("value", t)
        .build()
        .output[T](0)
  }


  def constant(name: String, value: Array[Byte]): Output[String] =
    this.constant(name, value, classOf[String])

  def constant(name: String, value: Int): Output[Integer] =
    this.constant(name, value, classOf[Integer])

  def constant(name: String, value: Array[Int]): Output[Integer] =
    this.constant(name, value, classOf[Integer])

  def constant(name: String, value: Float): Output[Float] =
    this.constant(name, value, classOf[Float])

  private def binaryOp[T](`type`: String,
                          in1: Output[T],
                          in2: Output[T]): Output[T] =
    g.opBuilder(`type`, `type`)
      .addInput(in1)
      .addInput(in2)
      .build()
      .output[T](0)

  private def binaryOp3[T, U, V](`type`: String,
                                 in1: Output[U],
                                 in2: Output[V]): Output[T] =
    g.opBuilder(`type`, `type`)
      .addInput(in1)
      .addInput(in2)
      .build()
      .output[T](0)

}

object Hello extends App {
private def printUsage(s: PrintStream): Unit = {
    val url: String =
      "https://storage.googleapis.com/download.tensorflow.org/models/inception5h.zip"
    s.println(
      "Java program that uses a pre-trained Inception model (http://arxiv.org/abs/1512.00567)")
    s.println("to label JPEG images.")
    s.println("TensorFlow version: " + TensorFlow.version())
    s.println()
    s.println("Usage: label_image <model dir> <image file>")
    s.println()
    s.println("Where:")
    s.println(
      "<model dir> is a directory containing the unzipped contents of the inception model")
    s.println("            (from " + url + ")")
    s.println("<image file> is the path to a JPEG image file")
  }
private def constructAndExecuteGraphToNormalizeImage(
      imageBytes: Array[Byte]): Tensor[Float] =
     {
 	val g = new Graph()
      val b: GraphBuilder = new GraphBuilder(g)
//   float using (value - Mean)/Scale.
      val H: Int = 224
      val W: Int = 224
      val mean: Float = 117f
      val scale: Float = 1f
// have been more appropriate.
      val input: Output[String] = b.constant("input", imageBytes)
      val output: Output[Float] = b.div(
        b.sub(
          b.resizeBilinear(
            b.expandDims(b.cast(b.decodeJpeg(input, 3), classOf[Float]),
                         b.constant("make_batch", 0)),
            b.constant("size", Array(H, W))),
          b.constant("mean", mean)
        ),
        b.constant("scale", scale)
      )
	val s  = new Session(g) // Generally, there may be multiple output tensors, all of them must be closed to prevent resource leaks.
	s.runner()
          .fetch(output.op().name())
          .run()
          .get(0)
          .expect(classOf[Float])
    }

private def executeInceptionGraph(graphDef: Array[Byte],
                                    image: Tensor[Float]): Array[Float] = {
    val g = new Graph() 
	g.importGraphDef(graphDef)
    val s = new Session(g)
	val result  = 
          s.runner()
            .feed("input", image)
            .fetch("output")
            .run()
            .get(0)
            .expect(classOf[Float])
	val rshape: Array[Long] = result.shape()
	if (result.numDimensions() != 2 || rshape(0) != 1) {
          throw new RuntimeException(
            String.format(
              "Expected model to produce a [1 N] shaped tensor where N is the number of labels, instead it produced one with shape %s",
              Arrays.toString(rshape)))
        }
        val nlabels: Int = rshape(1).toInt
        result.copyTo(Array.ofDim[Float](1, nlabels))(0)
      }
    
private def maxIndex(probabilities: Array[Float]): Int = {
    var best: Int = 0
    for (i <- 1 until probabilities.length
         if probabilities(i) > probabilities(best)) {
      best = i
    }
    best
  }
  private def readAllBytesOrExit(path: Path): Array[Byte] = {
    try{
		return Files.readAllBytes(path)
	}
    catch {
      case e: IOException => {
        System.err.println("Failed to read [" + path + "]: " + e.getMessage)
        System.exit(1)
      }

    }
    null
  }

  private def readAllLinesOrExit(path: Path): List[String] = {
    try Files.readAllLines(path, Charset.forName("UTF-8"))
    catch {
      case e: IOException => {
        System.err.println("Failed to read [" + path + "]: " + e.getMessage)
        System.exit(0)
      }

    }
    null
  }


 override def main(args: Array[String]): Unit = {

    if (args.length != 2) {
      printUsage(System.err)
      System.exit(1)
    }
    val modelDir: String = args(0)
    val imageFile: String = args(1)
    val graphDef: Array[Byte] = readAllBytesOrExit(
      Paths.get(modelDir, "tensorflow_inception_graph.pb"))
    val labels: List[String] = readAllLinesOrExit(
      Paths.get(modelDir, "imagenet_comp_graph_label_strings.txt"))
    val imageBytes: Array[Byte] = readAllBytesOrExit(Paths.get(imageFile))
    val image = constructAndExecuteGraphToNormalizeImage(imageBytes)
	val labelProbabilities: Array[Float] =
        executeInceptionGraph(graphDef, image)
	val bestLabelIdx: Int = maxIndex(labelProbabilities)
	println("BEST MATCH: %s (%.2f%% likely)".format(
	  labels.get(bestLabelIdx),
	  labelProbabilities(bestLabelIdx) * 100f))
  }

}



