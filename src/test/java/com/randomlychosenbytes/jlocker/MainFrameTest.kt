package com.randomlychosenbytes.jlocker

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFalse
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.Pupil
import com.randomlychosenbytes.jlocker.model.SuperUser
import com.randomlychosenbytes.jlocker.uicomponents.LockerPanel
import com.randomlychosenbytes.jlocker.utils.generateKey
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.swing.JCheckBox
import javax.swing.JOptionPane
import javax.swing.JPanel
import javax.swing.JTextField

@ExtendWith(MockitoExtension::class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MainFrameTest {

    private val dataManager = mock<DataManager>()

    init {
        State.dataManager = dataManager
    }

    @Test
    fun `should show error message when locker id is already taken`() {

        val lockerToBeEdited = Locker(id = "1")
        mainFrame.selectLocker(LockerPanel(lockerToBeEdited))

        whenever(lockerIDTextField.text).thenReturn("2")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(false)

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            mainFrame.setLockerInformation()

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    null,
                    "Diese Schließfach-ID existiert bereits! Wählen Sie eine andere.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `show show error when new locker id is empty`() {

        val lockerToBeEdited = Locker(id = "1")
        mainFrame.selectLocker(LockerPanel(lockerToBeEdited))

        whenever(lockerIDTextField.text).thenReturn("")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(true)

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            mainFrame.setLockerInformation()

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    null,
                    "Bitte geben Sie eine gültige Schließfach-ID ein.",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `should show error message when new pupil height is not a number`() {

        val currentLocker = Locker(Locker(id = ""))
        mainFrame.selectLocker(LockerPanel(currentLocker))

        whenever(lockerIDTextField.text).thenReturn("1")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(true)
        whenever(lockTextField.text).thenReturn("99-99-99")
        whenever(noteTextArea.text).thenReturn("this is a note")
        whenever(lastNameTextField.text).thenReturn("Draper")
        whenever(firstNameTextField.text).thenReturn("Don")
        whenever(heightInCmTextField.text).thenReturn("x")

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            mainFrame.setLockerInformation()

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    null,
                    "Die eingegebene Größe ist ungültig!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `should show error message when rent start date is invalid`() {

        val lockerToBeEdited = Locker(id = "1")
        mainFrame.selectLocker(LockerPanel(lockerToBeEdited))

        whenever(lockerIDTextField.text).thenReturn("1")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(true)
        whenever(lockTextField.text).thenReturn("99-99-99")
        whenever(noteTextArea.text).thenReturn("this is a note")
        whenever(lastNameTextField.text).thenReturn("Draper")
        whenever(firstNameTextField.text).thenReturn("Don")
        whenever(heightInCmTextField.text).thenReturn("150")
        whenever(rentedFromDateTextField.text).thenReturn("24.24.2020")

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            mainFrame.setLockerInformation()

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Anfangsdatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `should show error message when rent end date is invalid`() {

        val lockerToBeEdited = Locker(id = "1")
        mainFrame.selectLocker(LockerPanel(lockerToBeEdited))

        whenever(lockerIDTextField.text).thenReturn("1")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(true)
        whenever(lockTextField.text).thenReturn("99-99-99")
        whenever(noteTextArea.text).thenReturn("this is a note")
        whenever(lastNameTextField.text).thenReturn("Draper")
        whenever(firstNameTextField.text).thenReturn("Don")
        whenever(heightInCmTextField.text).thenReturn("150")
        whenever(rentedFromDateTextField.text).thenReturn("01.01.2020")
        whenever(rentedUntilDateTextField.text).thenReturn("24.24.2020")

        Mockito.mockStatic(JOptionPane::class.java).use { jOptionPaneMock ->
            mainFrame.setLockerInformation()

            jOptionPaneMock.verify {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Enddatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
            }
        }
    }

    @Test
    fun `should set locker information correctly`() {

        val currentLocker = Locker(Locker(id = ""))
        mainFrame.selectLocker(LockerPanel(currentLocker))

        whenever(lockerIDTextField.text).thenReturn("1")
        whenever(dataManager.isLockerIdUnique(any())).thenReturn(true)

        whenever(lockTextField.text).thenReturn("99-99-99")
        whenever(noteTextArea.text).thenReturn("this is a note")
        whenever(lastNameTextField.text).thenReturn("Draper")
        whenever(firstNameTextField.text).thenReturn("Don")
        whenever(heightInCmTextField.text).thenReturn("150")
        whenever(rentedFromDateTextField.text).thenReturn("01.01.2021")
        whenever(rentedUntilDateTextField.text).thenReturn("01.12.2021")
        whenever(classTextField.text).thenReturn("12")
        whenever(hasContractCheckbox.isSelected).thenReturn(true)

        mainFrame.setLockerInformation()

        assertThat(currentLocker.id).isEqualTo("1")
        assertThat(currentLocker.lockCode).isEqualTo("99-99-99")
        assertThat(currentLocker.pupil.firstName).isEqualTo("Don")
        assertThat(currentLocker.pupil.lastName).isEqualTo("Draper")
        assertThat(currentLocker.pupil.heightInCm).isEqualTo(150)
        assertThat(currentLocker.pupil.rentedFromDate).isEqualTo("01.01.2021")
        assertThat(currentLocker.pupil.rentedUntilDate).isEqualTo("01.12.2021")
        assertThat(currentLocker.pupil.schoolClassName).isEqualTo("12")
        assertThat(currentLocker.pupil.hasContract).isEqualTo(true)

        assertThat(currentLocker.isFree).isFalse()
    }

    @Test
    fun `should hide panel when there are no lockers on the current walk`() {
        mainFrame.showLockerInformation()
        verify(containerPanel).isVisible = false
    }

    @Test
    fun `should show correct default data when locker is empty`() {

        whenever(dataManager.currentUser).thenReturn(SuperUser("11111111"))
        whenever(dataManager.superUserMasterKey).thenReturn(generateKey())

        val currentLocker = Locker(id = "")
        mainFrame.selectLocker(LockerPanel(currentLocker))

        verify(containerPanel).isVisible = true
        verify(lastNameTextField).text = ""
        verify(firstNameTextField).text = ""
        verify(classTextField).text = ""
        verify(heightInCmTextField).text = ""
        verify(hasContractCheckbox).isSelected = false
        verify(moneyTextField).text = ""
        verify(previousAmountTextField).text = ""
        verify(rentedFromDateTextField).text = ""
        verify(rentedUntilDateTextField).text = ""
        verify(remainingTimeInMonthsTextField).text = ""

        verify(lockerIDTextField).text = ""
        verify(outOfOrderCheckbox).isSelected = false

        verify(codeTextField).text = "00-00-00"
        verify(lockTextField).text = ""
        verify(noteTextArea).text = ""
    }

    @Test
    fun `should show correct data when locker is not empty`() {

        val now = LocalDate.now()
        val inThreeMonths = now.plusMonths(3)

        val format = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        val pupil = Pupil().apply {
            firstName = "Don"
            lastName = "Draper"
            paidAmount = 50
            previouslyPaidAmount = 50
            schoolClassName = "10"
            rentedFromDate = now.format(format)
            rentedUntilDate = inThreeMonths.format(format)
            heightInCm = 150
            hasContract = true
        }

        val locker = Locker().apply {
            lockCode = "99-99-99"
            moveInNewOwner(pupil)
        }

        whenever(dataManager.currentUser).thenReturn(SuperUser("11111111"))
        whenever(dataManager.superUserMasterKey).thenReturn(generateKey())

        mainFrame.selectLocker(LockerPanel(locker))

        verify(containerPanel).isVisible = true
        verify(lastNameTextField).text = "Draper"
        verify(firstNameTextField).text = "Don"
        verify(classTextField).text = "10"
        verify(heightInCmTextField).text = "150"
        verify(hasContractCheckbox).isSelected = true
        verify(moneyTextField).text = "50"
        verify(previousAmountTextField).text = "50"
        verify(rentedFromDateTextField).text = now.format(format)
        verify(rentedUntilDateTextField).text = inThreeMonths.format(format)
        verify(remainingTimeInMonthsTextField).text = "3 Monate"

        verify(lockerIDTextField).text = ""
        verify(outOfOrderCheckbox).isSelected = false

        verify(codeTextField).text = "00-00-00"
        verify(lockTextField).text = "99-99-99"
        verify(noteTextArea).text = ""
    }

    @Mock
    private lateinit var lockerIDTextField: JTextField

    @Mock
    private lateinit var outOfOrderCheckbox: JCheckBox

    @Mock
    private lateinit var codeTextField: JTextField

    @Mock
    private lateinit var lockTextField: JTextField

    @Mock
    private lateinit var noteTextArea: JTextField

    @Mock
    private lateinit var containerPanel: JPanel

    @Mock
    private lateinit var lastNameTextField: JTextField

    @Mock
    private lateinit var firstNameTextField: JTextField

    @Mock
    private lateinit var classTextField: JTextField

    @Mock
    private lateinit var heightInCmTextField: JTextField

    @Mock
    private lateinit var hasContractCheckbox: JCheckBox

    @Mock
    private lateinit var moneyTextField: JTextField

    @Mock
    private lateinit var previousAmountTextField: JTextField

    @Mock
    private lateinit var rentedFromDateTextField: JTextField

    @Mock
    private lateinit var rentedUntilDateTextField: JTextField

    @Mock
    private lateinit var remainingTimeInMonthsTextField: JTextField

    @InjectMocks
    private lateinit var mainFrame: MainFrame
}