package com.alessandrocandolini.di_alternatives

sealed class MyList<out T>
object Empty : MyList<Nothing>()
data class NonEmpty<out T>(val head : T, val tail : MyList<T>) : MyList<T>()

fun <A,B> MyList<A>.map(
    f : (A) -> B) : MyList<B> = when (this) {
    is NonEmpty -> NonEmpty(f(head), tail.map(f))
    is Empty -> Empty
}


fun <T> MyList<T>.length() : Int {

    tailrec fun loop(remaining : MyList<T>, accumulator : Int) : Int =
        when (remaining) {
            is NonEmpty -> loop(remaining.tail, accumulator + 1)
            is Empty -> 0
        }


    return loop(this, 0)
}

