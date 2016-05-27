package com.firstray.core

import scala.math._
import scala.util.control.Breaks._
import com.firstray.data.Tracerble
import com.firstray.core.FirstRay._
import com.firstray.data.Vec3
import scala.collection.immutable.VectorBuilder


class FirstRay(val width:Int, val height: Int, val setting: RaySetting = 5) extends Renderer[Vector[Byte], Tracerble]{
  val INFINITY = 1e8
  var objVec = List[Tracerble]()
  def trace(rayorg: Vec3, raydir: Vec3, depth: Int = 0): Vec3 = {
    var obj0:Tracerble = null
    var tnear = INFINITY
    for(obj <- objVec) {
      var t0 = INFINITY
      var t1 = INFINITY
      val inter = obj.intersect(rayorg, raydir)
      if (inter != None) {
        t0 = inter.get._1
        t1 = inter.get._2
        if (t0 < 0) t0 = t1
        if (t0 < tnear) {
          tnear = t0
          obj0 = obj
        }
      }
    }
    
    if (obj0 == null) return Vec3(2)
    
    var surfaceColor:Vec3 = 0
    var phit:Vec3         = rayorg + raydir * tnear
    var nhit:Vec3          = phit - obj0.center
    nhit = nhit normalize
    
    var bias = 1e-4
    var inside = false
    
    if ((raydir dot nhit) > 0) {
      nhit = -nhit
      inside = true
    }
    if ((obj0.transparency > 0 || obj0.reflection > 0) && depth < setting) {
      var facingratio = -raydir.dot(nhit)
      var fresneleffect = mix(pow(1 - facingratio, 3), 1, 0.1)
      var refldir = raydir - nhit * 2 * raydir.dot(nhit)
      refldir = refldir normalize
      var reflection = trace(phit + nhit * bias, refldir, depth + 1)
      var refraction: Vec3 = 0
      if (obj0.transparency != 0) {
        var ior = 1.1
        var eta = if(inside) ior else 1 / ior
        var cosi = -nhit.dot(raydir)
        var k = 1 - eta * eta * (1 - cosi * cosi)
        var refrdir = raydir * eta + nhit * (eta * cosi - sqrt(k))
        refrdir = refrdir.normalize
        refraction = trace(phit - nhit * bias, refrdir, depth + 1) 
      }
      surfaceColor = (
          reflection * fresneleffect +
          refraction * (1 - fresneleffect) * obj0.transparency) * obj0.surfaceColor
    } else {
      for ((obj, i) <- objVec.zipWithIndex) {
        if (obj.emissionColor.x > 0) {
          var transmission:Vec3 = 1
          var lightDirection = obj.center - phit
          lightDirection = lightDirection.normalize
          breakable {
            for ((obj2, j) <- objVec.zipWithIndex) {
              if (i != j) {
                var inter = obj2.intersect(phit + nhit * bias, lightDirection)
                if (inter != None) {
                  var (t0, t1) = inter.get
                  transmission = 0
                  break
                }
                
              }
            }
          }
          surfaceColor = surfaceColor + (obj0.surfaceColor * transmission *
              max(0.0, nhit.dot(lightDirection)) * obj.emissionColor)
        }
      }
    }
    
    surfaceColor + obj0.emissionColor
  }
  
  def render: Vector[Byte] = {
    var image = new VectorBuilder[Byte]()
    var invWidth = 1 / width.toDouble
    var invHeight = 1 / height.toDouble
    var fov = 30
    var aspectratio = width / height.toDouble
    var angle = tan(Pi * 0.5 * fov / 180.0)
    
    for (y <- 0 until height) {
      for (x <- 0 until width) {
        var xx = (2 * ((x + 0.5) * invWidth) - 1) * angle * aspectratio
        var yy = (1 - 2 * ((y + 0.5) * invHeight)) * angle
        var raydir = Vec3(xx, yy, -1)
        raydir = raydir.normalize
        var pixel = trace(Vec3(), raydir)
        image += (min(1.0, pixel.x) * 255).toByte
        image += (min(1.0, pixel.y) * 255).toByte
        image += (min(1.0, pixel.z) * 255).toByte
       //println("[" + x + "," + y + "]:" + pixel)
      }
    }
    image.result()
  }
  
  def appendObject(obj: Tracerble): Unit = {
    objVec = obj :: objVec
  }
}

object FirstRay {
  type RaySetting = Int
  private def mix(a: Double, b: Double, mix: Double): Double = 
    b * mix + a * (1 - mix)
}