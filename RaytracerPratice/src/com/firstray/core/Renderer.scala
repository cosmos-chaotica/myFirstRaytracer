package com.firstray.core

import com.firstray.data.Vec3


trait Renderer[Result, Obj] {
  def trace(rayorg: Vec3, raydir: Vec3, depth: Int = 0): Vec3
  def render: Result
  def appendObject(obj: Obj): Unit
}