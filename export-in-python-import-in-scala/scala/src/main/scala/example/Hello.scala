package example

import java.io.IOException
import java.io.PrintStream
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.List
import scala.collection.JavaConverters._

import org.tensorflow.DataType
import org.tensorflow.Graph
import org.tensorflow.Output
import org.tensorflow.Session
import org.tensorflow.Tensor
import org.tensorflow.TensorFlow
import org.tensorflow.types.UInt8
import java.lang.Float
import java.util


import java.lang.String.format
import java.io.ByteArrayOutputStream
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.util


// MnistReader is modified from https://github.com/jeffgriffith/mnist-reader/blob/master/src/main/java/mnist/MnistReader.java
object MnistReader {
  val LABEL_FILE_MAGIC_NUMBER = 2049
  val IMAGE_FILE_MAGIC_NUMBER = 2051

  def getLabels(infile: String): Array[Int] = {
    val bb = loadFileToByteBuffer(infile)
    assertMagicNumber(LABEL_FILE_MAGIC_NUMBER, bb.getInt)
    val numLabels = bb.getInt
    val labels = new Array[Int](numLabels)
    var i = 0
    while ( {
      i < numLabels
    }) {
      labels(i) = bb.get & 0xFF // To unsigned

      {
        i += 1; i
      }
    }
    labels
  }

  def getImages(infile: String): util.List[Array[Array[Int]]] = {
    val bb = loadFileToByteBuffer(infile)
    assertMagicNumber(IMAGE_FILE_MAGIC_NUMBER, bb.getInt)
    val numImages = bb.getInt
    val numRows = bb.getInt
    val numColumns = bb.getInt
    val images = new util.ArrayList[Array[Array[Int]]]
    var i = 0
    while (i < numImages) {
      images.add(readImage(numRows, numColumns, bb))
      i += 1
    }
    images
  }

  private def readImage(numRows: Int, numCols: Int, bb: ByteBuffer) : Array[Array[Int]] = {
    var image : Array[Array[Int]] = Array.ofDim(numRows, numCols)
    var row = 0
    while ( row < numRows){
      image(row) = readRow(numCols, bb)
      row += 1
    }
    image
  }

  private def readRow(numCols: Int, bb: ByteBuffer) = {
    val row = new Array[Int](numCols)
    var col = 0
    while (col < numCols){
      row(col) = bb.get & 0xFF
      col += 1
    }
    row
  }

  def assertMagicNumber(expectedMagicNumber: Int, magicNumber: Int): Unit = {
    if (expectedMagicNumber != magicNumber) expectedMagicNumber match {
      case LABEL_FILE_MAGIC_NUMBER =>
        throw new RuntimeException("This is not a label file.")
      case IMAGE_FILE_MAGIC_NUMBER =>
        throw new RuntimeException("This is not an image file.")
      case _ =>
        throw new RuntimeException("Expected magic number %d, found %d".format(expectedMagicNumber, magicNumber))
    }
  }

  /** *****
    * Just very ugly utilities below here. Best not to subject yourself to
    * them. ;-)
    * *****/
  def loadFileToByteBuffer(infile: String): ByteBuffer = ByteBuffer.wrap(loadFile(infile))

  def loadFile(infile: String): Array[Byte] = try {
    val f = new RandomAccessFile(infile, "r")
    val chan = f.getChannel
    val fileSize = chan.size
    val bb = ByteBuffer.allocate(fileSize.toInt)
    chan.read(bb)
    bb.flip
    val baos = new ByteArrayOutputStream
    var i = 0
    while ( i < fileSize){
      baos.write(bb.get)
      i += 1
    }
    chan.close()
    f.close()
    baos.toByteArray
  } catch {
    case e: Exception =>
      throw new RuntimeException(e)
  }

  def renderImage(image: Array[Array[Int]]): String = {
    val sb = new StringBuffer
    var row = 0
    while ( {
      row < image.length
    }) {
      sb.append("|")
      var col = 0
      while ( {
        col < image(row).length
      }) {
        val pixelVal = image(row)(col)
        if (pixelVal == 0) sb.append(" ")
        else if (pixelVal < 256 / 3) sb.append(".")
        else if (pixelVal < 2 * (256 / 3)) sb.append("x")
        else sb.append("X")

        {
          col += 1; col - 1
        }
      }
      sb.append("|\n")

      {
        row += 1; row - 1
      }
    }
    sb.toString
  }

  def repeat(s: String, n: Int): String = {
    val sb = new StringBuilder
    var i = 0
    while ( {
      i < n
    }) sb.append(s) {
      i += 1; i - 1
    }
    sb.toString
  }
}

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
    val dtype = DataType.fromClass(`type`)
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
                          in2: Output[T]) =
    g.opBuilder(`type`, `type`)
      .addInput(in1)
      .addInput(in2)
      .build()
      .output[T](0)

  private def binaryOp3[T, U, V](`type`: String,
                                 in1: Output[U],
                                 in2: Output[V]) =
    g.opBuilder(`type`, `type`)
      .addInput(in1)
      .addInput(in2)
      .build()
      .output[T](0)
}

object Hello extends App {
  private def executeInceptionGraph(graphDef: Array[Byte],
                                    image: Tensor[_]): Array[Array[scala.Float]] = {
    val g = new Graph()

    g.importGraphDef(graphDef)
    val s = new Session(g)
    val result =
      s.runner()
        .feed("dense_2_input:0", image)
        .fetch("dense_3/Softmax")
        .run()
        .get(0)
       // .expect(classOf[Float])
    val rshape: Array[Long] = result.shape()
    val nlabels: Int = rshape(1).toInt
    val count: Int = rshape(0).toInt
    var res = Array.ofDim[scala.Float](count, nlabels)
    result.copyTo(res)
  }

  private def maxIndex(probabilities: Array[scala.Float]): Int = {
    probabilities.zipWithIndex.maxBy(_._1)._2
  }

  private def readAllBytesOrExit(path: Path): Array[Byte] = {
    try {
      return Files.readAllBytes(path)
    }
    catch {
      case e: IOException =>
        System.err.println("Failed to read [" + path + "]: " + e.getMessage)
        System.exit(1)
    }
    null
  }

  private def readAllLinesOrExit(path: Path): util.List[String] = {
    try {
      return Files.readAllLines(path, Charset.forName("UTF-8"))
    }
    catch {
      case e: IOException =>
        System.err.println("Failed to read [" + path + "]: " + e.getMessage)
        System.exit(0)
    }
    null
  }


  override def main(args: Array[String]): Unit = {
    val modelPath: String = args(0)
    val graphDef: Array[Byte] = readAllBytesOrExit(Paths.get(modelPath))
    val images = MnistReader.getImages("./mnist_data/t10k-images-idx3-ubyte").asScala
          .take(1000)
        .map(_.flatten)
      .map(_.map(_.toFloat / 255)).toArray
    val labels = MnistReader.getLabels("./mnist_data/t10k-labels-idx1-ubyte")

    val inputTensor: Tensor[_] = Tensor.create(images)
    val labelProbabilities: Array[Array[scala.Float]] =
      executeInceptionGraph(graphDef, inputTensor)
    val bestLabelIdx: Array[Int] = labelProbabilities.map(maxIndex)

    val correctCount = bestLabelIdx.zip(labels).count{case (a: Int, b: Int) => a == b}
    val acc = correctCount * 1.0 / bestLabelIdx.length
    println("model path %s, acc: %.2f".format(modelPath, acc))
  }
}
