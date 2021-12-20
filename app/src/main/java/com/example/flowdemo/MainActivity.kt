package com.example.flowdemo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    lateinit var maiViewModel: MainViewModel
    lateinit var testViewModel: TestViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        maiViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        testViewModel = ViewModelProvider(this).get(TestViewModel::class.java)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                testViewModel.simpleStateFLow.collect {
                    Log.d("***", "activity -collect: $it")
                }
            }
        }

//        testViewModel.flowConvertToSharedFlow(
//            SharingStarted.WhileSubscribed(
//                stopTimeoutMillis = 100L,
//                replayExpirationMillis = 200L
//            )
//        )
        start()
        btn_retry.setOnClickListener {
            start()
        }
    }

    fun start() {
                maiViewModel.loginAndGetUserInfo()
//        maiViewModel.loginAndGetUserInfo2()
//        testViewModel.flowFilter()
//        testViewModel.flow1()
//        testViewModel.flowFlowOn()
//        testViewModel.flowLaunchIn()
//        testViewModel.flowCancel()
//        testViewModel.flowBackpressure()
//        testViewModel.flowMap()
//        testViewModel.flowTake()
//        testViewModel.flowReduce()
//        testViewModel.flowFold()
//        testViewModel.flowZip()
//        testViewModel.flowCombine()
//        testViewModel.flowFlattenConcat()
//        testViewModel.flowFlattenMerge()
//        testViewModel.flowFlatMapConcat()
//        testViewModel.flowFlatMapMerge()
//        testViewModel.flowFlatMapLatest()
//        testViewModel.flowCatch()
//        testViewModel.flowRetry()
//        testViewModel.flowRetryWhen()
//        testViewModel.stateFlow1()
//        testViewModel.shareFlow1()
//        testViewModel.flowDebounce()
//        testViewModel.flowTransformReturnFLow()

    }
}