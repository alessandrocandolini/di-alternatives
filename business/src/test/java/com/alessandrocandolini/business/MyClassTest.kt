package com.alessandrocandolini.business;

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object ExampleUnitTest : FunSpec({
    test("String length should return the length of the string") {
        2 + 2 shouldBe 4
    }
})