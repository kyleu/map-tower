package maptower.map

case class Bounds(min: Point, max: Point) {
  def contains(p: Point) = p >= min && p <= max
}