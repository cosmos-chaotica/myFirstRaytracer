package com.firstray.data

trait Tracerble {
  def center: Vec3
  def surfaceColor: Vec3
  def emissionColor: Vec3
  def transparency: Double
  def reflection:   Double
  def intersect(rayorg:Vec3, raydir:Vec3): Option[(Double, Double)]
}