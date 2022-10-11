package com.example.tddtest

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import org.junit.Assert

class TestObserver<T>(
    scope : CoroutineScope,
    flow : Flow<T>
) {
        private val values = mutableListOf<T>()
        private val job = scope.launch {
            flow.collect{
                values.add(it)
            }
        }

        fun assertNoValues() : TestObserver<T>{
            Assert.assertEquals(emptyList<T>(), this.values)
            return this
        }

        fun assertValues(vararg values : T) : TestObserver<T>{
            Assert.assertEquals(values.toList(),this.values)
            return this
        }

        fun finish(){
            job.cancel()
        }
}

fun <T> Flow<T>.test(scope: CoroutineScope) : TestObserver<T>{
    return TestObserver(scope = scope, flow = this)
}