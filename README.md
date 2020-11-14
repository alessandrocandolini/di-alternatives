![Build](https://github.com/alessandrocandolini/di-alternatives/workflows/Build/badge.svg)


# The raise and fall of DI containers (in the OOP land)

Multi-module single platform gradle kotlin/android project, setup using gradle kotlin DSL, to explore pros, cons, rationale and alternatives to dependency injection and dependency injection containers in OOP. 

CI/CD style of development is achieved by configuring basic github actions. 

## What this project is about

* Learn about dependency injection and mainstream techniques and libraries for deal with it at scale; focus will be particularly on the dagger compiler and runtime 
* Be provocative and challenge the role of DI in modern codebases, showing that DI frameworks are a solution to the sympthoms rather than to the actual underlying problem, and tentative advocate that DI is an antipattern; the aim of this part is to develop a better understanding of how DI fits into a bigger picture (more info about this in the section below 

## What this project is NOT about

Although any mobile application will have to touch some (if not all) of the following areas to a certain degree, these are **not** meant to be the primary concern of this project and this project should not be considered as a showcase of these: 

* mobile architecture: the architecture here is old-fashioned, it's a OOP "clean" architecture (sort of) using a flavour of MVP on the view/presentation layer; no attempt has been made at being accurate/rigorous/dogmatic/fancy about this (and particularly there is no interest in following any of the google guidelines). 
* concurrency: concurrency is a fashinating, non-trivial topic with far-reaching consequences in many areas including mobile apps (where its relevance is sometimes underestimated), and there are plenty of different models of concurrency that can be exploited to approach the subject and its implementation (eg, Java-like shared mutable state with blocking or non-blocking synchronisation, CSP, actor model, STM, streams, reactive extensions, join calculus, etc; some of these are provided out of the box as part of the kotlin standard library); none of these is taken into account here, we will develop instead a simpler application with basic asynchronous support using kotlin coroutines. 
* offline-first application: despite the fact that at a certain point we would like to setup a local DB for this application in order to make the application a bit more realistic, no attempt will be made at exploring mobile DBs, ORMs, and offline first patterns (CRDTs, etc) 
* functional idioms: some of the code will reveal some FP influence/preference, particularly in the part that emphatises limits of a DI approach; however, illustrating FP concepts, ideas and patterns is not the scope in this project, and a conscious effort has been made to avoid heavy functional artillery
* Kotlin multiplatform
* CI/CD: exploring github actions for android project in detail 

## Background and motivation

Usage of DI and DI containers has been raising in popularity in Android development these days, and they have been around for ages in Java enterprise. This project is about refreshing these concepts, show how to apply them by leveraging some of the modern libraries and patterns available in the java/kotlin/android ecosystem, illustrate the many benefits that we can get, and at the same time challenge this viewpoint and contribute to the conversation on whether an argument can be made that DI containers should instead be considered as something fixing the symptomps rather than the actual underlying issue, and what the alternatives could be. 


Dependency injection (referred to as DI hereafter) is many things. First and foremost, it is an important OOP pattern and concept. 
More on this later on, but in short it's about externalising the dependencies of a class (via constructor injection when the dependency is mandatory, or via setters when the dependency is optional). 
If/when the external dependencies are defined in terms of interfaces designed from the point of view of the consumer (instead of using directly classes, and/or using interfaces that expose an API which is 1:1 with the underlying implementation, ie, an interface leaking the implementation details), it supports inversion of control and it makes the dependencies pluggable from outside.
Supposedly, this increases (among others): modularity, testability, etc. 
Why is this? Because, at least **virtually**, it makes possible to replace / override the dependencies from outside, without having to touch the class code. 
Why am I saying **virtually**? I'll expand later however the main point here is that dependencies are not just signatures of the methods of an interface: there is also **behaviour** associated with those methods, and you have to be careful when replacing an external dependency to fulfill the expectations in terms of **behaviour**, not just about whether the signatures match.

Indeed, in OOP land there are plenty of advantages in using DI, compared to more naive and less modular solutions. 

Systematic adoption of the DI pattern typically leads to a proliferation of classes, and DI containers (eg, libraries/frameworks like dagger, guice, in the Java land, etc) have been invented to help mitigating the growing cost of managing the dependency graph in larger scale projects. 
The aim of these tools is complementaty to the usage of the DI pattern: they to resolve the dependency graph (either at runtime or compile time, depending on the tool). 

This has been the state of art of Java DI for many years. From `@autowire` in spring framework, to `@inject` and `@provides` annotations make them way into `javax` itself, to the advent of many libraries like guice, dagger and others, DI tools have become a standard in the development of OOP applications at non-trivial scale. 
Over the last years, the approach has gained lot of attraction in the mobile community. 
Despite the enormous popularity of this approach though, it's probably time to question and rethink the approach andfrom the ground up and ask whether there are better alternatives DI. 
Let's look at DI with more provocative eyeglasses: What is the problem that DI is trying to solve? Is DI really solving it, or is just creating more problems? Is DI a code smell/antipattern? 
DI frameworks has effectively solved problems in organising dependencies in larger scale OOP codebases, but it's always worth trying to look at different directions and put DI in perspective, instead of being sold to only one technique. 


## Tech stack 


Libraries used:
* [kotlin standard library for JVM](https://kotlinlang.org/api/latest/jvm/stdlib/) 
* [kotlin coroutines](https://github.com/Kotlin/kotlinx.coroutines) (including coroutines testing) 
* [kotlin serialization](https://github.com/Kotlin/kotlinx.serialization) for json parsing
* Square's [retrofit2](https://square.github.io/retrofit/) and [okttp](https://square.github.io/okhttp/) (including mock server) 
* [dagger](https://dagger.dev/dev-guide/) and particularly [hint](https://dagger.dev/hilt/) for DI 
* [kotest](https://kotest.io/) for unit, integration and property-based testing (bye bye junit!)
* [coil](https://github.com/coil-kt/coil) for images
* [arrow](https://github.com/arrow-kt/arrow) as a functional companion library, when appropriate/needed

Limited usage of Google's androidx/jetpack stack has been made on purpose (with the exception of the new jetpack compose). This repo is about concepts, not about mastering a particular tech stack. Also, i'm quite opinionated on androidx/jetpack libraries being badly designed, pretty disfunctional, and being just a badly re-invented wheel that could be achieved with better, more general, more re-usable abstractions. 

