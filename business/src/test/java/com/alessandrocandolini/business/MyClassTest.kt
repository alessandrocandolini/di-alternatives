package com.alessandrocandolini.business;

import io.kotest.core.spec.style.FunSpec
import io.kotest.core.spec.style.StringSpec
import io.kotest.data.blocking.forAll
import io.kotest.matchers.shouldBe

class ExampleUnitTest : FunSpec({
    test("String length should return the length of the string") {
        2 + 2 shouldBe 4
    }
})

class ExamplePropertyTest: StringSpec({
    "String size" {
        forAll<String, String> { a, b ->
            (a + b).length == a.length + b.length
        }
    }
})