package com.seoplee.androidstudy.flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.IllegalStateException
import kotlin.system.measureTimeMillis

fun simpleFlow(): Flow<*> = (1..3).asFlow()
    .map {
        if( it > 2) throw IllegalStateException()
        it + 1
    }

fun main() = runBlocking<Unit> {
    simpleFlow()
        .onCompletion { cause ->
            if(cause != null) {
                println("Done Exceptionally")
            } else {
                println("Done")
            }
        }
        .catch{ emit("예외 발생") }
        .collect { value -> println(value) }
}