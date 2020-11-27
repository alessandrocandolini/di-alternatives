package com.alessandrocandolini.business.splash

import com.alessandrocandolini.business.toDispatcher
import com.alessandrocandolini.business.withMockServer
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.core.spec.style.FunSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestResult
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.Gen
import io.kotest.property.arbitrary.*
import io.kotest.property.checkAll
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest


/*
Dimensions of testing behaviour correctness (here we are not talking about eg testing performance, memory leaks, etc)

 1. jvm vs instrumented testing (ie, run locally vs run on the device; ui are instrumented but not all instrumented are ui)
 2. unit vs integration vs end-to-end (test pyramid)
 3. example-driven vs property-based

Example-driven tests follow this structure:
  * Given: i prepare some carefully selected input data / stubs
  * When: run a computation
  * Then: i check that the actual output is as expected (this can be an assert on an output values or a verify statement of some mocks)

So, high level,  we have either
   actual == expected
or
   verify(mock.doSoemthing(someData), times(1))

Pros and cons of example-driven tests:
   Pro:
     * easy to write
   Cons:
     * every time you cherry pick some examples, you are left with the question of whether
     those examples provide a exhaustive coverage of the possible cases , maybe you are missing CORNER CASES
   * it's not a complete test (you are limited to a finite number of discrete cases that you can test; this is a general truth in testing)

With property testing we move from data to properties, ie, propositions, ie, functions, ie, requirements that must hold
regardless of the specific input/output params.

   P = property = something you evaluate independently from data
   P == true
   P = { inputData -> ... }

Simple math examples of example tests
   max(x, y)
   max(1,2) = 2
   max(-1,-2) = -1
   max(1,1) = 1
   max(1,0) = 1

and corresponding properties:
   { x, y -> val m = max(x,y); m == x ||  m == y  }
   { x, y -> val m = max(x,y) <= x && max(x,y) <= y }
   { x, y -> val m = max(x,y) = max(y,x) } // commutativity
   { x -> val m = max(x,x) = x }

Notice: properties as lambdas, instead of particular values. Values are input of the function.
The property holds regardless of the input data, is a rule about the relationship between input and output.

Notice: we have fewer properties compared to number of examples.

Another example: list concatenation  (++)
   { l1, l2 -> l1 ++ l2 != l2 ++ l1 }  // it's not commutative
   { l1, l2 -> (l1 ++ l2).length != l2.length + l1.length }

Property-based testing
testing properties / statements of your system that must hold/be valid REGARDLESS of the actual input data


  1. higher level, they stress a design of your code in terms of stronger specifications
  2. cheaper to write (you write less tests and you test more scenarios)
  3. corner cases for free
  4. expressiveness

Example tests can be seen as a particular property where
 { input, expectedResult ->
    val actual = mycode(input)
    actual == expected
 }
but this does not tell you what is the property, ie, why you expect expectedResult, expectedResult comes from outside ,
which is a bit fragile, it does not tell why that's the case.

-----

In the case of the interceptor, the property that DEFINES our interceptor (the reason why we built it) is:

For every request (no matter what the http verb, headers, body, path, query params are), the request without
interceptor should always have response code 401 and the request performed with a client having the interceptor should instead
return 200.
*/


class ApiKeyInterceptorExampleTest : BehaviorSpec({

    given("the server replies with 200 if and only if the request is authenticated, 401 otherwise") {

        val aValidApiKey = "I'm a valid api key"
        val interceptor: Interceptor = ApiKeyInterceptor { aValidApiKey }

        val authDispatcher: (RecordedRequest) -> MockResponse = { request ->
            when (request.requestUrl?.queryParameterValues(ApiKeyInterceptor.API_KEY_QUERY_PARAM)) {
                listOf(aValidApiKey) -> 200
                else -> 401
            }.let { code ->
                MockResponse().setResponseCode(code)
            }
        }

        `when`("query param is not present in the GET request and no interceptor is plugged") {
            then("the response should be 401") {
                withMockServer {

                    val client = OkHttpClient.Builder().build()
                    val fullUrl = url("/api?api=test")
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(fullUrl)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 401
                }
            }
        }

        `when`("query param is not present in the GET request and the interceptor is plugged") {
            then("the response should be 200") {
                withMockServer {
                    val client = OkHttpClient.Builder().addInterceptor(interceptor).build()
                    val fullUrl = url("/api?api=test")
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(fullUrl)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 200
                }
            }
        }


        `when`("query param is present already in the GET request with an invalid key and the interceptor is plugged") {
            then("the response should be 200") {
                withMockServer {
                    val fullUrl = url("/api?api=test&appid=something")
                    val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
                    dispatcher = authDispatcher.toDispatcher()
                    val request: Request = Request.Builder()
                        .get()
                        .url(fullUrl)
                        .build()
                    val r = client.newCall(request).execute()
                    r.code shouldBe 200
                }

            }
        }
    }

})

class ApiKeyInterceptorExampleTestPropertyTest : FunSpec() {

    private val server = MockWebServer()
    private val aValidApiKey = "I'm a valid api key"
    private val interceptor: Interceptor = ApiKeyInterceptor { aValidApiKey }

    override fun beforeTest(testCase: TestCase) {
        super.beforeTest(testCase)

        server.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse =
                when (request.requestUrl?.queryParameterValues(ApiKeyInterceptor.API_KEY_QUERY_PARAM)) {
                    listOf(aValidApiKey) -> 200
                    else -> 401
                }.let { code ->
                    MockResponse().setResponseCode(code)
                }

        }
        server.start()
    }

    override fun afterTest(testCase: TestCase, result: TestResult) {
        super.afterTest(testCase, result)
        server.shutdown()
    }

    init {
        test("For every request (no matter what the http verb, headers, body, path, query params are), request with no interceptor plugged should return 401 & request with interceptor plugged should return 200") {

            val unauthenticatedClient = OkHttpClient.Builder().build()
            val authenticatedClient =
                unauthenticatedClient.newBuilder().addInterceptor(interceptor).build()

            val requestGen: Gen<Request> = requestGen { server.url(it) }

            checkAll(requestGen) { request ->

                val unauthenticatedResponse = unauthenticatedClient.newCall(request).execute()
                val authenticatedResponse = authenticatedClient.newCall(request).execute()

                unauthenticatedResponse.code == 401 && authenticatedResponse.code == 200

            }

        }
    }

    companion object ApiKeyInterceptorExampleTestPropertyTest {

        enum class HttpMethod {
            POST,PATCH,DELETE,PUT,GET,HEAD
        }

        fun requestGen(pathToFullUrl: (String) -> HttpUrl): Gen<Request> {

            fun HttpMethod.toOkHttpMethodName() = name.toUpperCase()

            val nonEmptyBodyGen: Arb<String> = Arb.string().filter { it.isNotBlank() }

            val httpUrlGen: Arb<HttpUrl> = Arb.of(
                "api/v1/",
                "/api?api=test&appid=something",
                "/api?api=test"
            ).map { u -> pathToFullUrl(u) }

            val httpMethodWithBodyGen : Arb<HttpMethod> = Arb.of(HttpMethod.POST, HttpMethod.PATCH, HttpMethod.DELETE, HttpMethod.PUT)
            val httpMethodWithoutBodyGen : Arb<HttpMethod> = Arb.of(HttpMethod.GET, HttpMethod.HEAD)

            val requestsWithBody: Arb<Request> = Arb.bind(httpUrlGen, httpMethodWithBodyGen, nonEmptyBodyGen) { httpUrl, httpMethod, body ->
                Request.Builder()
                    .method(httpMethod.toOkHttpMethodName(), body.toRequestBody())
                    .url(httpUrl)
                    .build()
            }

            val requestsWithoutBody: Arb<Request> = Arb.bind(httpUrlGen, httpMethodWithoutBodyGen) { httpUrl, httpMethod ->
                Request.Builder()
                    .method(httpMethod.toOkHttpMethodName(), null)
                    .url(httpUrl)
                    .build()
            }

            return Arb.choice(
                requestsWithBody, requestsWithoutBody
            )

        }
    }
}


