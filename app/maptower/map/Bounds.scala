package maptower.map

case class Bounds(ul: Point, br: Point) {
  def contains(p: Point) = {
    false
  }
}