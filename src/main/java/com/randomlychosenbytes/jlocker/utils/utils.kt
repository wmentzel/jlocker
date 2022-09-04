package com.randomlychosenbytes.jlocker.utils

import com.randomlychosenbytes.jlocker.EntityCoordinates
import com.randomlychosenbytes.jlocker.model.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

fun isDateValid(dateStr: String): Boolean = try {
    LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("dd.MM.yyyy"))
    true
} catch (e: DateTimeParseException) {
    false
}

fun moveOwner(sourceLocker: Locker, destLocker: Locker) {

    if (sourceLocker.isFree) {
        throw IllegalStateException("The source locker does not have an owner who could be moved to a new locker.")
    }

    if (!destLocker.isFree) {
        throw IllegalStateException("The destination locker still has an owner who has to be unassigned before a new owner can be assigned.")
    }

    destLocker.moveInNewOwner(sourceLocker.pupil)
    sourceLocker.empty()
}

fun List<Building>.getAllLockerCoordinates(): Sequence<EntityCoordinates<Locker>> = sequence {
    forEachIndexed { bIndex, building ->
        building.floors.forEachIndexed { fIndex, floor ->
            floor.walks.forEachIndexed { wIndex, walk ->
                walk.modules.mapIndexed { index, module ->
                    (module as? LockerCabinet)?.let {
                        index to module
                    }
                }.filterNotNull().forEach { (mwIndex, module) ->
                    module.lockers.forEachIndexed { lIndex, locker ->
                        yield(EntityCoordinates(locker, bIndex, fIndex, wIndex, mwIndex, lIndex))
                    }
                }
            }
        }
    }
}

fun Sequence<EntityCoordinates<Locker>>.findLockers(
    id: String? = null,
    lastName: String? = null,
    firstName: String? = null,
    onlyWithContract: Boolean = false,
    schoolClass: String? = null,
    height: String? = null,
    money: String? = null,
    remainingTimeInMonths: String? = null,
    fromDate: String? = null,
    untilDate: String? = null,
    lock: String? = null,
    onlyEmpty: Boolean = false
) = mapNotNull(fun(foundLockerCoords: EntityCoordinates<Locker>): EntityCoordinates<Locker>? {
    val locker = foundLockerCoords.entity

    if (onlyEmpty && !locker.isFree) {
        return null
    }

    if (id != null) {
        if (locker.id != id) {
            return null
        }
    }

    if (lastName != null) {
        if (locker.isFree || lastName != locker.pupil.lastName) {
            return null
        }
    }

    if (firstName != null) {
        if (locker.isFree || firstName != locker.pupil.firstName) {
            return null
        }
    }

    if (onlyWithContract) {
        if (locker.isFree || !locker.pupil.hasContract) {
            return null
        }
    }


    if (schoolClass != null) {
        if (locker.isFree || schoolClass != locker.pupil.schoolClassName) {
            return null
        }
    }

    if (height != null) {
        if (locker.isFree || height.toIntOrNull() != locker.pupil.heightInCm) {
            return null
        }
    }

    if (money != null) {
        if (locker.isFree || money.toIntOrNull() != locker.pupil.paidAmount) {
            return null
        }
    }

    if (remainingTimeInMonths != null) {
        if (locker.isFree || remainingTimeInMonths.toIntOrNull() != locker.pupil.remainingTimeInMonths) {
            return null
        }
    }

    if (fromDate != null) {
        if (locker.isFree || fromDate != locker.pupil.rentedFromDate) {
            return null
        }
    }

    if (untilDate != null) {
        if (locker.isFree || untilDate != locker.pupil.rentedUntilDate) {
            return null
        }
    }

    if (lock != null) {
        if (lock != locker.lockCode) {
            return null
        }
    }

    return foundLockerCoords
})

fun List<Building>.renameSchoolClass(previousClassName: String, newClassName: String): Int {
    val searchForAgeGroup = '.' !in previousClassName

    var numMatches = 0

    for (building in this) {
        val floors: List<Floor> = building.floors
        for (floor in floors) {
            val walks: List<Walk> = floor.walks
            for (walk in walks) {
                val cols: List<Module> = walk.modules
                for (col in cols) {

                    val module = col as? LockerCabinet ?: continue
                    val lockers: List<Locker> = module.lockers
                    var sSubClass: String

                    for (locker in lockers) {
                        if (locker.isFree) {
                            continue
                        }
                        var sFoundClass = locker.pupil.schoolClassName
                        sSubClass = ""

                        //7, E, Kurs
                        if (searchForAgeGroup) {
                            if ('.' in sFoundClass) {
                                sSubClass = sFoundClass.substringAfter(".")
                                sFoundClass = sFoundClass.substringBefore(".")
                            }
                        }
                        // else 7.2, E.2 ...
                        if (sFoundClass == previousClassName) {
                            numMatches++
                            locker.pupil.schoolClassName =
                                if (sSubClass.isEmpty()) newClassName else "$newClassName.$sSubClass"
                        }
                    }
                }
            }
        }
    }

    return numMatches
}