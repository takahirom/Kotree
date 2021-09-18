package com.github.takahirom.compose

import androidx.compose.runtime.Composition
import androidx.compose.runtime.Recomposer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield

fun Composition.launchComposeInsideLogger(composer: Recomposer, mainScope: CoroutineScope) {
    mainScope.launch {
        composer.state.collect {
            println("composer:$it")
        }
    }
    val slotTable = Class.forName("androidx.compose.runtime.CompositionImpl")
        .getDeclaredField("slotTable")
        .apply {
            isAccessible = true
        }
        .get(this)
    GlobalScope.launch {
        var lastSlotTableString = ""
        while (true) {
            val slotTableString = Class.forName("androidx.compose.runtime.SlotTable")
                .getMethod("asString")
                .apply {
                    isAccessible = true
                }
                .invoke(slotTable) as String

            if (slotTableString != lastSlotTableString) {
                lastSlotTableString = slotTableString
                val groups = Class.forName("androidx.compose.runtime.SlotTable")
                    .getDeclaredField("groups")
                    .apply {
                        isAccessible = true
                    }
                    .get(slotTable) as IntArray
                val slots = Class.forName("androidx.compose.runtime.SlotTable")
                    .getDeclaredField("slots")
                    .apply {
                        isAccessible = true
                    }
                    .get(slotTable) as Array<Any?>


                println("------")
                println("slotTable:")
                println(slotTableString)
                println("groups:")
                groups.toList().windowed(5, 5, false)
                    .forEachIndexed { index, group ->
                        val (key, groupInfo, parentAnchor, size, dataAnchor) = group
                        println("index: $index, key: $key, groupInfo: $groupInfo, parentAnchor: $parentAnchor, size: $size, dataAnchor: $dataAnchor")
                    }
                println("slots:")
                println(slots.joinToString(","))
            }
            yield()
        }
    }
}