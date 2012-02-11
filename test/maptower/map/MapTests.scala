package maptower.map

import org.junit.Test
import org.junit.Assert._
import com.mongodb.casbah.Imports._

class MapTests {
  val zero = Point(0, 0)
  val one = Point(1, 1)
  val two = Point(2, 2)
  val three = Point(3, 3)
  val four = Point(4, 4)
  val five = Point(5, 5)

  @Test
  def pointTests {
    assertEquals(zero, Point.center)
    assertTrue(zero < one)
    assertTrue(one < two)
    assertTrue(three > zero)
  }

  @Test
  def boundsTests {
    val b = new Bounds(zero, two)

    assertTrue(b.contains(zero))
    assertTrue(b.contains(one))
    assertTrue(b.contains(two))
    assertTrue(!b.contains(three))
  }

  @Test
  def wayTests {
    val w = Way(0, "", "", Seq(zero, one, two, three, four, five))
    assertEquals(6, w.points.size)

    val b = new Bounds(two, three)
    val t = w.trimmed(b)
    assertEquals(1, t.size)
    assertEquals(4, t(0).points.size)
  }
}