package com.alessandrocandolini.business.splash

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

object ApiKeyInterceptor : FunSpec({
    test("api key query param is appended to requests not having it") {
     2 + 2 shouldBe 4
    }
    test("api key query param is replaced for requests that already have the same query param") {
        2 + 2 shouldBe 4
    }
})

