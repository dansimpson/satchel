/**
 * ---------------------------------------------------------------------------
 *
 * Copyright (c) 2011 Dan Simpson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * ---------------------------------------------------------------------------
 */

package org.ds.satchel

import org.scalatest._
import org.scalatest.matchers.ShouldMatchers

class SatchelSpec extends FlatSpec with ShouldMatchers {

  "A satchel" should "register with it's companion" in {
    new Satchel(
      name = "test",
      root = "core/tests")

    Satchel.find("test") should not be (null)
  }

  "A satchel" should "handle arbitray directory depth" in {
    val satchel = new Satchel(
      name = "test",
      root = "core/tests",
      patterns = List("/deep/**/*.js"))

    satchel.assetlist should be(List("/deep/test.js", "/deep/deep/test.js", "/deep/deep/deep/test.js"))
    satchel.processed should be("!d\n!dd\n!ddd\n")
  }

  "A satchel" should "remove duplicate files" in {
    val satchel = new Satchel(
      name = "test",
      root = "core/tests",
      patterns = List("/test.js", "/deep/test.js", "/test.js"))

    satchel.assetlist should be(List("/test.js", "/deep/test.js"))
  }

}