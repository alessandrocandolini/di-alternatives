![Build](https://github.com/alessandrocandolini/di-alternatives/workflows/Build/badge.svg)


# The raise and fall of DI containers (in the OOP land)

Multi-module mono-platform gradle kotlin/android project, setup using gradle kotlin DSL, to explore pros, cons, rationale and alternatives to dependency resolution and injection through containers in OOP. 

CI/CD style of development is achieved by configuring basic github actions. 

## What this project is about

* Learn about dependency injection and mainstream techniques and libraries for deal with it at scale; focus will be particularly on the dagger compiler and runtime 
* Be provocative and challenge the role of DI in modern codebases, showing that DI frameworks are a solution to the sympthoms rather than to the actual underlying problem, and tentative advocate that DI is an antipattern; the aim of this part is to develop a better understanding of how DI fits into a bigger picture (more info about this in the [section below](https://github.com/alessandrocandolini/di-alternatives#background-and-motivation]))

## What this project is NOT about

Although any mobile application will have to touch some (if not all) of the following areas to a certain degree, these are **not** meant to be the primary concern of this project and this project should not be considered as a showcase of these: 

* mobile architecture: the architecture here is old-fashioned, it's a OOP "clean" architecture (sort of) using a flavour of MVP on the view/presentation layer; no attempt has been made at being accurate/rigorous/dogmatic/fancy about this (and particularly there is no interest in following any of the google guidelines). 
* concurrency: concurrency is a fashinating, non-trivial topic with far-reaching consequences in many areas including mobile apps (where its relevance is sometimes underestimated), and there are plenty of different models of concurrency that can be exploited to approach the subject and its implementation (eg, Java-like shared mutable state with blocking or non-blocking synchronisation, CSP, actor model, STM, streams, reactive extensions, join calculus, etc; some of these are provided out of the box as part of the kotlin standard library); none of these is taken into account here, we will develop instead a simpler application with basic asynchronous support using kotlin coroutines. 
* offline-first application: despite the fact that at a certain point we would like to setup a local DB for this application in order to make the application a bit more realistic, no attempt will be made at exploring mobile DBs, ORMs, and offline first patterns (CRDTs, etc) 
* functional idioms: some of the code will reveal some FP influence/preference, particularly in the part that emphatises limits of a DI approach; however, illustrating FP concepts, ideas and patterns is not the scope in this project, and a conscious effort has been made to avoid heavy functional artillery
* Kotlin multiplatform
* CI/CD: exploring github actions for android project in detail 

## Background and motivation


Usage of dependency injection (hereafter, DI for short) *containers* have been around for ages in the Java world and recently they have undergone an increase in popularity in Android development too. 
From `@Autowired` available in the [https://spring.io](spring framework)  to achieve annotations-driven (runtime) dependency resolution and injection of collaborating beans, to `@Inject`, `@Named`, `@Singleton` etc annotations finally landing in `javax` itself (to ensure class portability), to the advent of many Java production-ready DI containers, like [https://github.com/google/guice](guice) (Java reflection-based) and later [https://dagger.dev/](dagger2) (using compile-time Java annotation processing) and others, DI tools have become a standard in the development of OOP applications at non-trivial scale. 
Over the last years, the approach has gained lot of attraction in the mobile community too. 

This project is about refreshing these concepts, show how to apply them in a kotlin/android multi-module project by leveraging some of the modern libraries and patterns available in the java/kotlin/android ecosystem (eg, Dagger), illustrate the many benefits that we can get. At the same time though, this project aims at challenging this viewpoint and contribute to the conversation on whether an argument can be made that DI containers should instead be considered as something fixing the symptomps rather than the actual underlying issue, and what the alternatives could be. 
In fact, despite the enormous popularity of this approach, it's probably time to question and rethink the approach andfrom the ground up and ask whether there are better alternatives DI. 
DI frameworks has effectively solved problems in organising dependencies in larger scale OOP codebases (do they?), but it's always worth trying to look at different directions and put DI in perspective, instead of being sold to only one technique. After showcase the basics of DI containers, this will drive us to explore the territory of **coherent type classes for compiler-enabled automatic dependency injection**. 

## Tech stack 

Main technologies used: 
* [kotlin standard library for JVM](https://kotlinlang.org/api/latest/jvm/stdlib/) 
* [kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines) (including coroutines testing) 
* [kotlin serialization](https://github.com/Kotlin/kotlinx.serialization) for json parsing
* [retrofit2](https://square.github.io/retrofit/) and [okttp3](https://square.github.io/okhttp/) (including mock server) 
* [dagger2](https://dagger.dev/dev-guide/) and particularly [hint](https://dagger.dev/hilt/) for DI 
* [kotest](https://kotest.io/) for unit, integration and property-based testing (bye bye junit!)
* [coil](https://github.com/coil-kt/coil) for images
* [arrow](https://github.com/arrow-kt/arrow) as a functional companion library, when appropriate/needed
* [sqldelight](https://github.com/cashapp/sqldelight) for a SQL-first approach to deal with sql DBs

[anvil](https://github.com/square/anvil) is a very interesting approach to scale dagger projects in Kotlin, however here I've decided to use [hint](https://dagger.dev/hilt/), primarily because the ergonomics is optimised for the Android usecase. More info on anvil can be found [here](https://developer.squareup.com/blog/introducing-anvil/).

This project does NOT make usage of [koin](https://github.com/InsertKoinIO/koin). The lack of compile-time safety is already enough as an argument to stop me from taking koin seriously into account (without even touching the conversation around the fact that koin should more properly be considered a service locator). 

No mocking library has been used on purpose. Mocking in unit tests is arguably an antipattern, but that's a topic for another project ;) 

Limited usage of Google's androidx/jetpack stack has been made on purpose (with the exception of the new jetpack compose). This repo is about concepts, not about mastering a particular tech stack. Also, despite the fact that i rarely have strong opinions on libraries and tools (i have preferences but i rarely have strong opinions), i'm instead quite opinionated on androidx/jetpack libraries being pourly designed, pretty disfunctional, unsounded, flimsy, optimised for toy examples instead of production-ready projects at scale (despite being used in such context!), and in summary being just a badly re-invented wheel that could have been achieved with better, more general, more sounded and re-usable abstractions; ultimately they contribute to make the overall android experience worst instead of better, and they keep the status of android development in the stone age. 

## How to 

Compilation pipeline to package the final Android application apk:
```
./gradlew assembleDebug
./gradlew assembleRelease
```

Run `kotest` tests in Java-only modules using the `kotest` custom test runner 
```
./gradlew :business:kotest
```

To run all tests across all modules, using junit runner
```
./gradlew test
```

To check if upgrades of the dependencies are available (thanks to the [https://github.com/ben-manes/gradle-versions-plugin](gradle versions plugin)) 
```
./gradlew checkDependencyUpdates
```
