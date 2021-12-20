package com.example.flowdemo

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

/**
 * @description
 * @author: created by peihao.feng
 * @date: 2021/12/7
 */
class TestViewModel : ViewModel() {


    private fun currTime() = System.currentTimeMillis()

    var start: Long = System.currentTimeMillis()

    val testFun = {
        "hello"
    }

    fun createFlow() {
        flow {
            delay(1000)
            emit("hello")
        }
        flowOf("hello")
        arrayOf(1, 2, 3, 4).asFlow()
        listOf(1, 2, 3, 4).asFlow()
        testFun.asFlow()
        suspend { 1 + 1 }.asFlow()
    }

    fun flow1() {
        viewModelScope.launch {
            flow {
                emit("hello")
                delay(1000)
                emit("world")
                throw NullPointerException("nullPointerException")
            }.onStart {
                Log.d("***", "onStart")
            }.onCompletion {
                Log.d("***", "onCompletion")
            }.catch {
                Log.d("***", "catch:${it.message}")
            }.flowOn(Dispatchers.Default).collect {
                Log.d("***", "collect:$it")
            }
        }
    }

    fun flowFlowOn() {
        viewModelScope.launch {
            flow {
                emit("hello")
                Log.d("***", "flow: ${Thread.currentThread().name}")
            }.flowOn(Dispatchers.Default).collect {
                Log.d("***", "collect: ${Thread.currentThread().name}")
            }
        }
    }

    fun flowLaunchIn() {
        viewModelScope.launch {
            flow {
                emit("hello")
                Log.d("***", "flow: ${Thread.currentThread().name}")
            }.flowOn(Dispatchers.Default)
                .onEach { Log.d("***", "collect: ${Thread.currentThread().name}") }
                .launchIn(CoroutineScope(Dispatchers.IO))
        }
    }

    fun flowCancel() {
        val flow = flow {
            for (i in 1..9) {
                emit(i)
                Log.d("***", "flow1 emit$i")
                delay(1000)
            }
        }.cancellable()
            .flowOn(Dispatchers.Default)
            .onEach {
                Log.d("***", "flow1 onEach$it")
            }.launchIn(CoroutineScope(Dispatchers.IO))

        viewModelScope.launch {
            delay(3000)
            flow.cancel()
        }
        viewModelScope.launch {
            flow {
                for (i in 1..9) {
                    emit(i)
                    Log.d("***", "flow2 emit$i")
                    delay(1000)
                }
            }.collect {
                if (it == 3) cancel()
            }
        }
        viewModelScope.launch {
            flowOf(1, 2, 3, 4, 5).onEach {
                delay(1000)
                Log.d("***", "flow3 onEach$it")
            }.cancellable().collect {
                if (it == 2) cancel()
            }
        }
    }

    fun flowBackpressure() {
        viewModelScope.launch {
            flow {
                for (i in 1..3) {
                    emit(i)
                    delay(1000)
                }
            }.collect {
                delay(3000)
                Log.d("***", "flow collect$it")
            }
            Log.d("***", "time1: ${currTime() - start}")
        }
        viewModelScope.launch {
            flow {
                for (i in 1..3) {
                    emit(i)
                    delay(1000)
                }
            }.buffer(50).collect {
                delay(3000)
                Log.d("***", "flow2 collect$it")
            }
            Log.d("***", "time2: ${currTime() - start}")
        }
        viewModelScope.launch {
            flow {
                for (i in 1..3) {
                    emit(i)
                    delay(1000)
                }
            }.conflate().collect {
                delay(3000)
                Log.d("***", "flow3 collect$it")
            }
            Log.d("***", "time3: ${currTime() - start}")
        }
        viewModelScope.launch {
            flow {
                for (i in 1..3) {
                    emit(i)
                    delay(1000)
                }
            }.collectLatest {
                delay(3000)
                Log.d("***", "flow4 collect$it")
            }
            Log.d("***", "time4: ${currTime() - start}")
        }
    }

    fun flowMap() {
//        viewModelScope.launch {
//            flow {
//                List(3) { emit(it) }
//            }.map {
//                it * 2
//            }.collect {
//                Log.d("***", "flow1 collect:$it")
//            }
//        }
        viewModelScope.launch {
            flow {
                List(3) { emit(it) }
            }.map {
                flow {
                    delay(1000)
                    val newValue = it * 2
                    emit("newValue $newValue")
                }
            }.collect {
                Log.d("***", "flow2 collect:${it.single()}")
            }
        }
    }

    fun flowFilter() {
        viewModelScope.launch {
            flow {
                List(3) { emit(it) }
            }.filter {
                it > 1
            }.collect {
                Log.d("***", "flow1 collect:$it")
            }
        }
    }

    fun flowTransform() {
        viewModelScope.launch {
            flow {
                emit(1)
            }.transform {
                emit("**1 $it")
                emit("**2 $it")
            }.collect {
                Log.d("***", "collect: it")
            }
        }
    }

    fun flowTake() {
        viewModelScope.launch {
            flow {
                List(5) { emit(it) }
            }.take(2).collect {
                Log.d("***", "collect: $it")
            }
        }
        // 1,2
    }

    fun flowReduce() {
        viewModelScope.launch {
            val result = flow {
                List(5) { emit(it) }
            }.reduce { accumulator: Int, value: Int ->
                accumulator + value
            }
            Log.d("***", "result: $result")
        }
        // 0+ 0+1+2+3+4
    }

    fun flowFold() {
        viewModelScope.launch {
            val result = flow {
                List(5) { emit(it) }
            }.fold(2) { accumulator: Int, value: Int ->
                accumulator + value
            }
            Log.d("***", "result: $result")
        }
        // 2 +0+1+2+3+4
    }

    fun flowZip() {
        viewModelScope.launch {
            val flow1 = arrayOf(1, 2).asFlow()
            val flow2 = arrayOf("one", "two", "three").asFlow()
            flow1.zip(flow2) { t1, t2 ->
                "$t1-$t2"
            }.collect {
                Log.d("***", "collect: $it")
            }
        }
        //1-one 2-two 3-three
    }

    fun flowCombine() {
        viewModelScope.launch {
            val flowA = (1..4).asFlow().onEach { delay(100) }
            val flowB = flowOf("one", "two", "three", "four", "five").onEach { delay(200) }
            flowA.combine(flowB) { a, b -> "$a and $b" }
                .collect { Log.d("***", "collect: $it") }
        }
        // 1 and one, 2 and one, 3 and one, 3 and two, 4 and two, 5 and two, 5 and three, 5 and four, 5 and five
    }

    fun flowFlattenConcat() {
        viewModelScope.launch {
            val flowA = (1..3).asFlow().onEach { delay(100) }
            val flowB = flowOf("one", "two", "three")
            flowOf(flowA, flowB)
                .flattenConcat()
                .collect { Log.d("***", it.toString()) }
        }
        //1,2,3,one,two,three
    }

    fun flowFlattenMerge() {
        viewModelScope.launch {
            val flowA = (1..3).asFlow().onEach { delay(100) }
            val flowB = flowOf("one", "two", "three").onEach { delay(200) }
            flowOf(flowA, flowB)
                .flattenMerge(2)
                .collect { Log.d("***", it.toString()) }
        }
        // 1, one, 2, 3, two, 4, 5, three, four, five
    }

    fun flowFlatMapConcat() {
        viewModelScope.launch {
            flow {
                List(3) { emit(it) }
            }.flatMapConcat {
                flow {
                    emit("first $it")
                    delay(100)
                    emit("second $it")
                }
            }.collect {
                Log.d("***", "result: $it")
            }
        }
    }

    fun flowFlatMapMerge() {
        viewModelScope.launch {
            flow {
                List(3) { emit(it) }
            }.flatMapMerge(2) {
                flow {
                    emit("first $it")
                    delay(100)
                    emit("second $it")
                }
            }.collect {
                Log.d("***", "result: $it")
            }
        }
    }

    fun flowFlatMapLatest() {
        viewModelScope.launch {
            (1..5).asFlow()
                .onEach { delay(100) }
                .flatMapLatest {
                    flow {
                        emit("$it: First")
                        delay(500)
                        emit("$it: Second")
                    }
                }
                .collect {
                    Log.d("***", "$it at ${currTime() - start} ms from start")
                }
        }
    }

    fun flowCatch() {
        viewModelScope.launch {
            flow {
                emit("hello")
                throw NullPointerException("nullPointerException")
            }.onStart {
                Log.d("***", "onStart")
            }.catch {
                Log.d("***", "catch: ${it.message}")
            }.onCompletion { cause ->
                if (cause != null)
                    Log.d("***", "Flow completed exceptionally")
                else
                    Log.d("***", "onCompletion")
            }.collect {
                Log.d("***", "collect: $it")
            }
        }
        viewModelScope.launch {
            flow {
                emit("hello")
            }.onEach {
                Log.d("***", "onEach")
            }.catch {
                Log.d("***", "catch: ${it.message}")
            }.onCompletion { cause ->
                Log.d("***", "onCompletion")
            }.collect()
        }
    }

    fun flowRetry() {
        viewModelScope.launch {
            (1..5).asFlow().onEach {
                if (it == 3) throw IllegalStateException("Error on $it")
            }.retry(2) {
                if (it is IllegalStateException) {
                    return@retry true
                }
                false
            }.onEach { Log.d("***", "onEach $it") }
                .catch { Log.d("***", it.message ?: "catch") }
                .collect()
        }
    }

    fun flowRetryWhen() {
        viewModelScope.launch {
            (1..5).asFlow().onEach {
                if (it == 3) throw IllegalStateException("Error on $it")
            }.retryWhen { cause, attempt ->
                cause is IllegalStateException && attempt < 2
            }.onEach { Log.d("***", "onEach $it") }
                .catch { Log.d("***", it.message ?: "catch") }
                .collect()
        }
    }

    val simpleStateFLow = MutableStateFlow(1)

    fun stateFlow1() {
        viewModelScope.launch {
            for (i in 1..5) {
                simpleStateFLow.emit(i)
                delay(300)
            }
        }
        viewModelScope.launch {
            simpleStateFLow.collect {
                Log.d("***", "collect1: $it")
            }
        }
        viewModelScope.launch {
            delay(1000)
            simpleStateFLow.collect {
                Log.d("***", "collect2: $it")
            }
        }
    }

    val flow1 = flow {
        emit(1)
    }

    fun flow2StateFlow() {
        viewModelScope.launch {
            val stateFlow = flow1.stateIn(this)
            stateFlow.collect {
                Log.d("***", "stateFlow collect: $it")
            }
        }
    }

    fun shareFlow1() {
        val simpleSharedFlow = MutableSharedFlow<Int>(replay = 3, extraBufferCapacity = 2)
        viewModelScope.launch {
            (1..5).forEach {
                simpleSharedFlow.emit(it)
                delay(100)
            }
        }
        viewModelScope.launch {
            simpleSharedFlow.collect {
                Log.d("***", "collect1: $it")
            }
        }
        viewModelScope.launch {
            delay(300)
            simpleSharedFlow.collect {
                Log.d("***", "collect2: $it")
            }
        }
    }

    fun flowConvertToSharedFlow1(started: SharingStarted) {
        var startTime = 0L
        val flow = arrayOf(1, 2, 3, 4, 5)
            .asFlow()
            .onStart { startTime = currTime() }
            .onEach {
                Log.d("***", "Emit $it ${currTime() - startTime}ms")
                delay(100)
            }
        viewModelScope.launch {
            val sharedFlow = flow.shareIn(this, started, replay = 2)
            delay(400)
            Log.d("***", "current time ")
            sharedFlow.collect {
                Log.d("***", "received convert shared flow $it at ${currTime() - startTime}ms")
            }
        }
    }

    fun flowConvertToSharedFlow(started: SharingStarted) {
        var startTime = 0L
        val flow = arrayOf(1, 2, 3, 4, 5)
            .asFlow()
            .onStart { startTime = currTime() }
            .onEach {
                Log.d("***", "Emit $it ${currTime() - startTime}ms")
                delay(100)
            }
        viewModelScope.apply {
            val sharedFlow = flow.shareIn(this, started, replay = 2)
            val job = launch {
                Log.d("***", "current time ")
                sharedFlow.collect {
                    Log.d("***", "received convert shared flow $it at ${currTime() - start}ms")
                }
            }
            launch {
                delay(500L)
                job.cancel()
                delay(320L)
                sharedFlow.collect {
                    Log.d("***", "received again shared flow $it")
                }
                Log.d("***", "shared flow has stop")
            }
        }
    }

    fun flowDebounce(){
        viewModelScope.launch {
            flow {
                List(10){
                    delay(it*100L)
                    emit(it)
                }
            }.debounce(1000).collect {
                Log.d("***","collect: $it")
            }
        }
    }

    fun flowTransformReturnFLow(){
        viewModelScope.launch {
            flow {
                delay(100)
                emit(flowOf("hello"))
            }.onEach {
                Log.d("***","flow1 onEach: $it")
            }.single().transform {
                delay(200)
                emit(flowOf("$it --1"))
            }.single()
        }
    }
}