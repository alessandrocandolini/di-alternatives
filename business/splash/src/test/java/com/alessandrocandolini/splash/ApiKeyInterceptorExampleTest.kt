package com.alessandrocandolini.splash

import com.alessandrocandolini.await
import com.alessandrocandolini.hasQueryParamMatching
import com.alessandrocandolini.withMockServer
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
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
   verify(mock.doSomething(someData), times(1))

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


-----

I think the values of PBTs are primarily:
* the way you express the test: it makes you think in terms of foundational, stronger, core properties of what you are building, that should represent your component and must be satisfied regardless of the inputs. When you start thinking and designing at such level of abstraction, when you start having an appreciation for stronger properties, you tend to write code that has clearer goals, strong behaviour, precise requirements, well defined boundaries, well understood behaviour, etc. Those properties express your expectations on the software at a scale at which you are not required to come up by yourself with a list of examples that hopefully cover all the scenarios. Instead of reasoning in terms of "finding the examples", you reason in terms of properties that you know must be true no matter what, and you delegate to the PBT engine to exercise the system in search for violations of the expected behaviour. You stress the integrity of your expectations much more in depth. So, even when few of the inputs are still manually plugged  (in our case, urls are still picked from a list of hardcoded ones, to simplify the generator; body and http methods are totally generic though), there is still value in the way you express the properties and the way you separate those from the actual source of data. It's powerful to isolate properties that should hold regardless of the actual data, you start reasoning at a higher level of abstraction
* it's cheaper: our PBT is only one test in this case, but it covers much more than the individual arbitrary example tests that we have written before, one test replaces the need of many example tests while still capturing the essence of the component that we are testing
So in general PBT lead to fewer, cheaper, and at the same time stronger tests :slightly_smiling_face:
PBT is not always possible (as i mentioned), and sometimes it's not the right technique to use. BUt it's a nice tool to have in your toolkit for those cases where it leads to cheaper and safer code

*/


// Regardless of whether we want to be example-based or property-based, we have two approaches to test this:
// * one is direct and low level: we check that thr interceptor hydrates the incoming request with a query param. The test will inspect the request to check if the api key was correctly appended
// * another one is slightly indirect and higher level: we prepare a (stub) server behaving as per specification, we make requests to it using the interceptor and we assert that the request were authorised
// The first one tests the HOW. The second one tests the BEHAVIOUR.
// The first one is more sensitive to implementation details (because we are testing the how), the second one requires though to prepare a well-behaving stub server
// The second one has the additional advantage to force you to look at the same problem from an angle which is different than the one used when writing the implementation of the interceptor
// The first one would also force us to have a better understanding of the internals of okhttp (what is a Interceptor.Chain and how to create it?)

class ApiKeyInterceptorExampleBasedTest : BehaviorSpec({

    given("a stub server that replies 200 if and only if the request url contains exactly one appid query param with value equal to a valid api key, 401 otherwise (as per api specification)") {

        val aValidApiKey = "I'm a valid api key"
        val interceptor: Interceptor = ApiKeyInterceptor { aValidApiKey }

        // specification of the server (from api description)
        val dispatcher = { request : RecordedRequest ->
            if (request.hasQueryParamMatching(ApiKeyInterceptor.API_KEY_QUERY_PARAM, aValidApiKey)) {
                200
            } else {
                401
            }.let { code ->
                MockResponse().setResponseCode(code)
                    .addHeader("Content-Type", "application/json; charset=utf-8")
                    .setBody("{}")
            }
        }

        `when`("an unauthenticated request is performed using a client with no ApiKeyInterceptor plugged in") {
            then("the response should be 401 (this test just tests the mock server behaves as per server specification)") {
                withMockServer(dispatcher) { server ->

                    val clientWithoutInterceptr = OkHttpClient.Builder().build()
                    val aRequest: Request = Request.Builder()
                        .get()
                        .url(server.url("/api?api=test"))
                        .build()
                    val r = clientWithoutInterceptr.newCall(aRequest).await()
                    r.code shouldBe 401
                }
            }
        }

        `when`("an unauthenticated request is performed using a client with an instance of the ApiKeyInterceptor plugged in") {
            then("the response should be 200 (ie, the interceptor appends the authentication key)") {
                withMockServer(dispatcher) { server ->
                    val clientWithInterceptor = OkHttpClient.Builder().addInterceptor(interceptor).build()
                    val aRequest: Request = Request.Builder()
                        .get()
                        .url(server.url("/api?api=test"))
                        .build()
                    val r = clientWithInterceptor.newCall(aRequest).await()
                    r.code shouldBe 200
                }
            }
        }


        `when`("a request accidentally authenticated with the wrong key is performed using a client with an instance of the ApiKeyInterceptor plugged in") {
            then("the response should be 200 (ie, the interceptor overrides the authentication key)") {
                withMockServer(dispatcher) { server ->
                    val client = OkHttpClient.Builder()
                        .addInterceptor(interceptor)
                        .build()
                    val request: Request = Request.Builder()
                        .get()
                        .url(server.url("/api?api=test&appid=something"))
                        .build()
                    val r = client.newCall(request).await()
                    r.code shouldBe 200
                }

            }
        }
    }

})