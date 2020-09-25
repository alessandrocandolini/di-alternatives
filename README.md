![Build](https://github.com/alessandrocandolini/di-alternatives/workflows/Build/badge.svg)


# The raise and fall of DI frameworks (in the Java OOP land)


## Background and motivation

Dependency injection (referred to as DI hereafter) is many things. 

First and foremost, it is an important OOP pattern and concept. 
More on this later on, but in short it's about externalising the dependencies of a class (via constructor injection when the dependency is mandatory, or via setters when the dependency is optional). 
If/when the external dependencies are defined in terms of interfaces designed from the point of view of the consumer (instead of using directly classes, and/or using interfaces that expose an API which is 1:1 with the underlying implementation, ie, an interface leaking the implementation details), it supports inversion of control and it makes the dependencies pluggable from outside.
Supposedly, this increases (among others): modularity, testability, etc. 
Why is this? Because, at least **virtually**, it makes possible to replace / override the dependencies from outside, without having to touch the class code. 
Why am I saying **virtually**? I'll expand later however the main point here is that dependencies are not just signatures of the methods of an interface: there is also **behaviour** associated with those methods, and you have to be careful when replacing an external dependency to fulfill the expectations in terms of **behaviour**, not just about whether the signatures match.

Indeed, in OOP land there are plenty of advantages in using DI, compared to more naive and less modular solutions. 

Systematic adoption of the DI pattern typically leads to a proliferation of classes, and DI tools (eg, libraries/frameworks like dagger, guice, in the Java land, etc) have been invented to help mitigating the growing cost of managing the dependency graph in larger scale projects. 
The aim of these tools is complementaty to the usage of the DI pattern: they to resolve the dependency graph (either at runtime or compile time, depending on the tool). 

This has been the state of art of Java DI for many years. From `@autowire` in spring framework, to `@inject` and `@provides` annotations make them way into `javax` itself, to the advent of many libraries like guice, dagger and others, DI tools have become a standard in the development of OOP applications at non-trivial scale. 
Over the last years, the approach has gained lot of attraction in the mobile community. 
Despite the enormous popularity of this approach though, it's probably time to question and rethink the approach andfrom the ground up and ask whether there are better alternatives DI. 
Let's look at DI with more provocative eyeglasses: What is the problem that DI is trying to solve? Is DI really solving it, or is just creating more problems? Is DI a code smell/antipattern? 
DI frameworks has effectively solved problems in organising dependencies in larger scale OOP codebases, but it's always worth trying to look at different directions and put DI in perspective, instead of being sold to only one technique. 


## The project

Multi-module gradle kotlin jvm / android project, setup using gradle kotlin DSL.
Business logic is implemented in a purely java/kotlin module, with no dependency on Android, on purpose.
No attempt at making the project multi-platform has been made, and most of the dependencies (dagger, retrofit etc) only work for the JVM target.
For tests, the kotest library is used.