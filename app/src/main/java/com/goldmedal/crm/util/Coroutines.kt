package com.goldmedal.crm.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.reflect.UndeclaredThrowableException

object Coroutines {

    fun main(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            work()
        }

    fun io(work: suspend (() -> Unit)) =
        CoroutineScope(Dispatchers.IO).launch {
                work()
        }

}