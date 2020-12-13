package com.randomlychosenbytes.jlocker.utils

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.randomlychosenbytes.jlocker.createModuleWithLockerCabinetOf
import com.randomlychosenbytes.jlocker.model.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RenameSchoolClassTest {

    @Test
    fun shouldRenameClass() {

        val locker1 = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "12"
            })
        }

        val locker2 = Locker(id = "2").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Peggy"
                lastName = "Olsen"
                heightInCm = 150
                schoolClassName = "12"
            })
        }

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        modules.addAll(
                            listOf(
                                createModuleWithLockerCabinetOf(locker1, locker2)
                            )
                        )
                    }))
                }))
            })

        val numRenamed = buildings.renameSchoolClass("12", "13")

        assertThat(locker1.pupil.schoolClassName).isEqualTo("13")
        assertThat(locker2.pupil.schoolClassName).isEqualTo("13")
        assertThat(numRenamed).isEqualTo(2)
    }

    @Test
    fun shouldRenameClass2() {

        val locker1 = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "7.1"
            })
        }

        val locker2 = Locker(id = "2").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Peggy"
                lastName = "Olsen"
                heightInCm = 150
                schoolClassName = "7.2"
            })
        }

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        modules.addAll(
                            listOf(
                                createModuleWithLockerCabinetOf(locker1, locker2)
                            )
                        )
                    }))
                }))
            })

        val numRenamed = buildings.renameSchoolClass("7", "8")

        assertThat(locker1.pupil.schoolClassName).isEqualTo("8.1")
        assertThat(locker2.pupil.schoolClassName).isEqualTo("8.2")
        assertThat(numRenamed).isEqualTo(2)
    }

    @Test
    fun shouldRenameClass3() {

        val locker1 = Locker(id = "1").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Don"
                lastName = "Draper"
                heightInCm = 175
                schoolClassName = "7.1"
            })
        }

        val locker2 = Locker(id = "2").apply {
            moveInNewOwner(Pupil().apply {
                firstName = "Peggy"
                lastName = "Olsen"
                heightInCm = 150
                schoolClassName = "7.2"
            })
        }

        val buildings = listOf(
            Building("main building").apply {
                floors.addAll(listOf(Floor("ground floor").apply {
                    walks.addAll(listOf(Walk("main walk").apply {
                        modules.addAll(
                            listOf(
                                createModuleWithLockerCabinetOf(locker1, locker2)
                            )
                        )
                    }))
                }))
            })

        val numRenamed = buildings.renameSchoolClass("7.1", "8.1")

        assertThat(locker1.pupil.schoolClassName).isEqualTo("8.1")
        assertThat(locker2.pupil.schoolClassName).isEqualTo("7.2")
        assertThat(numRenamed).isEqualTo(1)
    }
}