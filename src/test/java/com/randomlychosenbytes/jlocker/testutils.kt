package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.LockerCabinet

fun createModuleWithLockerCabinetOf(vararg lockers: Locker) = LockerCabinet().apply {
    this.lockers.addAll(lockers)
}