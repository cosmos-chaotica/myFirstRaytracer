package com.firstray.data

import scala.math._

case class RSphere(
    center: Vec3, 
    radius: Double, 
    surfaceColor: Vec3, 
    reflection: Double, 
    transparency: Double, 
    emissionColor: Vec3 = 0) extends RObject with Tracerble {
  val radius2 = radius * radius
  def intersect(rayorg:Vec3, raydir:Vec3): Option[(Double, Double)] = {
    val l = center - rayorg
    val tca = l.dot(raydir)
    if (tca < 0) None
    else {
      val d2 = l.dot(l) - tca * tca
      if (d2 > radius2) None
      else {
        val thc = sqrt(radius2 - d2)
        val t0 = tca - thc
        val t1 = tca + thc
        Option(t0, t1)
      }
    }
  }
}