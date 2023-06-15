/*
 * Copyright (C) 2023 Lectra
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 * License-Filename: LICENSE
 */
package com.lectra.kapoeira.domain

import com.lectra.kapoeira.domain.MergeMaps._
import zio.test.Assertion.{equalTo, hasSameElements}
import zio.test.{Assertion, DefaultRunnableSpec, Gen, assert, check}

object MergeMapsSpec extends DefaultRunnableSpec {
  val spec = suite("merge Maps with Associative")(
    suite("sequences")(
      testM("Merge sequences") {
        check(
          Gen.listOfBounded(1, 10)(Gen.anyInt),
          Gen.listOfBounded(1, 10)(Gen.anyInt)
        ) { case (xs, ys) =>
          assert(xs.merge(ys))(Assertion.hasSameElements(xs ++ ys))
        }
      },
      testM("associativity") {
        check(
          Gen.listOfBounded(1, 10)(Gen.anyInt),
          Gen.listOfBounded(1, 10)(Gen.anyInt),
          Gen.listOfBounded(1, 10)(Gen.anyInt)
        ) { case (xs, ys, zs) =>
          //(xs + (ys + zs)) === ((xs + ys) + zs)
          assert(xs.merge(ys.merge(zs)))(equalTo((xs.merge(ys)).merge(zs)))
        }
      }
    ),
    suite("maps")(
      testM("Merge maps of sequences") {
        check(
          Gen.mapOfBounded(1, 10)(
            Gen.elements("key1", "key2"),
            Gen.listOfBounded(1, 10)(Gen.anyInt)
          ),
          Gen.mapOfBounded(1, 10)(
            Gen.elements("key2", "key3"),
            Gen.listOfBounded(1, 10)(Gen.anyInt)
          )
        ) { case (xs, ys) =>
          assert(xs.merge(ys))(
            hasSameElements(
              (xs.get("key1").map(l => "key1" -> l).toList ++
                ys.get("key3").map(l => "key3" -> l).toList ++
                (xs
                  .getOrElse("key2", List.empty)
                  .merge(ys.getOrElse("key2", List.empty)) match {
                  case Nil => Option.empty
                  case ls  => Some("key2" -> ls)
                }).toList).toMap
            )
          )
        }
      },
      testM("associativity") {
        check(
          Gen.mapOfBounded(1, 10)(
            Gen.elements("key1", "key2", "key3"),
            Gen.listOfBounded(1, 10)(Gen.anyInt)
          ),
          Gen.mapOfBounded(1, 10)(
            Gen.elements("key1", "key2", "key3"),
            Gen.listOfBounded(1, 10)(Gen.anyInt)
          ),
          Gen.mapOfBounded(1, 10)(
            Gen.elements("key1", "key2", "key3"),
            Gen.listOfBounded(1, 10)(Gen.anyInt)
          )
        ) { case (xs, ys, zs) =>
          assert(xs.merge(ys.merge(zs)))(equalTo((xs.merge(ys)).merge(zs)))
        }
      }
    )
  )
}
