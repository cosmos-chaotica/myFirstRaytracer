
package com.firstray.data

import Vec3._
import scala.math._

case class Vec3(x :Double, y: Double, z:Double) {
  def normalize: Vec3 = {
    val nor2 = length2
    if (nor2 > 0) {
      val invNor = 1 / sqrt(nor2)
      Vec3(x * invNor, y * invNor, z * invNor)
    } else {
      this
    }
  }
  def *(v: Vec3): Vec3 = Vec3(x * v.x, y * v.y, z * v.y)
  def dot(v: Vec3): Double = x * v.x + y * v.y + z * v.z
  def -(v: Vec3): Vec3 = Vec3(x - v.x, y - v.y, z - v.z)
  def unary_-(): Vec3 = Vec3(-x, -y, -z)
  def +(v: Vec3): Vec3 = Vec3(x + v.x, y + v.y, z + v.z)
  def length2: Double = x * x + y * y + z * z
  def length: Double = sqrt(length)
  override def toString: String = "[" + x + ", " + y + ", " + z + "]"
}

object Vec3 {
  def apply(): Vec3 = Vec3(0.0, 0.0, 0.0)
  def apply(a: Double): Vec3 = Vec3(a, a, a)
  
  implicit def sToVec(s: Double): Vec3 = Vec3(s)
  
}