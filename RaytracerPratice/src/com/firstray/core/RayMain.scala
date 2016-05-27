package com.firstray.core

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils
import java.nio.ByteBuffer;
import java.io.File
import javafx.scene.Group
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage
import javafx.scene.paint.Color;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import scala.collection.mutable.ArrayBuffer
import javax.imageio.ImageIO
import com.firstray.data.Vec3
import com.firstray.data.RSphere


class RayMain extends Application {
  override def start(ps: Stage):Unit = {
      var root = new Group();
      var canvas = new Canvas(800, 600);
      var gc = canvas.getGraphicsContext2D();
      canvas.setTranslateX(0);
      canvas.setTranslateY(0);
      renderun(gc)
      var scene = new Scene(root, 800, 600)
      root.getChildren().add(canvas)
      ps.setScene(scene)
      ps.show();
      var writableImage = scene.snapshot(null)
      var outFile = new File("firstrender.png")
      ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null),
          "png", outFile);
  }
  
  def renderun(gc: GraphicsContext): Unit = {
    val raytracer = new FirstRay(800, 600, 20)
    raytracer.appendObject(RSphere(Vec3(0.0, -10004, -20), 10000, Vec3(0.20, 0.20, 0.20), 0, 0.0))
    raytracer.appendObject(RSphere(Vec3(0.0,      0, -20), 4, Vec3(1.00, 0.32, 0.36), 1, 0.5))
    raytracer.appendObject(RSphere(Vec3(5.0,     -1, -15), 2, Vec3(0.90, 0.76, 0.46), 1, 0.0))
    raytracer.appendObject(RSphere(Vec3(5.0,      0, -25), 3, Vec3(0.20, 0.20, 0.20), 1, 0.0))
    raytracer.appendObject(RSphere(Vec3(-5.5,      0, -15), 3, Vec3(0.65, 0.77, 0.97), 1, 0.0))
    
    raytracer.appendObject(RSphere(Vec3( 0.0,     20, -30), 3, Vec3(0.00, 0.00, 0.00), 0, 0.0, Vec3(3)))
    val result = raytracer.render
    
    val pixelWriter = gc.getPixelWriter
    val pixelFormat = PixelFormat.getByteRgbInstance
    
    pixelWriter.setPixels(0, 0, 800, 
                600, pixelFormat, result.toArray,
                0, 800 * 3)
  }
  
}


object RayMain {
      def main(argv: Array[String]): Unit = {
      Application.launch(classOf[RayMain], argv: _*)
    }
}