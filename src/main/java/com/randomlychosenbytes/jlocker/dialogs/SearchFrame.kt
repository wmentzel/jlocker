package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.EntityCoordinates
import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.model.Locker
import com.randomlychosenbytes.jlocker.model.SuperUser
import com.randomlychosenbytes.jlocker.utils.findLockers
import com.randomlychosenbytes.jlocker.utils.getAllLockerCoordinates
import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.awt.print.PrinterException
import javax.swing.*
import javax.swing.table.DefaultTableModel

class SearchFrame(owner: JFrame) : JDialog(owner, true) {
    private val table: JTable = JTable().apply {
        autoCreateRowSorter = true
        addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(evt: MouseEvent) {
                rowClicked(evt)
            }
        })
    }
    private lateinit var foundLockers: List<EntityCoordinates<Locker>>

    private val columnData = listOfNotNull(
        "Schließfach-ID", "Name", "Vorname", "Klasse", "Größe", "Vertrag", "Geld", "Dauer", "von", "bis", "Schloss",
        "Codes".takeIf { dataManager.currentUser is SuperUser }
    ).toTypedArray()

    private val dataTypes: List<Class<*>> = listOfNotNull(
        String::class.java, String::class.java, String::class.java, String::class.java, Int::class.java,
        java.lang.Boolean::class.java, // needs to be Java's Boolean, otherwise no checkbox will be displayed
        Int::class.java, Int::class.java, String::class.java, String::class.java,
        String::class.java,
        String::class.java.takeIf { dataManager.currentUser is SuperUser }
    )

    private fun rowClicked(evt: MouseEvent) {
        val selectedRowIndex = (evt.source as JTable).selectedRow

        if (evt.clickCount != 2) {
            return
        }
        toBack()

        // TODO through the process of reordering the rows by a user defined column,
        // we don't know which index belongs to which locker. So, we have to search for the locker by id.
        val coords = getLockerFromRow(selectedRowIndex)

//        dataManager.currentBuildingIndex = coords.bValue
//        dataManager.currentFloorIndex = coords.fValue
//        dataManager.currentWalkIndex = coords.wValue
//        val iMUnitIndex = coords.mValue
//        val iLockerIndex = coords.lValue
//
//        // updateComboBoxes calls drawLockerOverview which itself
//        // sets the first available locker on the selected walk as the selected one.
//        // This behaviour is not as desired for this would overwrite the highlighted
//        // search result - the selected locker.
//        // So we save the indexes of the MUnit of the current locker and the index
//        // of the current locker itself and reset it after the call of this method.
//        mainFrame.updateComboBoxes()
//        //dataManager.currentLocker.setAppropriateColor()
//
//        // reset
//        dataManager.currentManagementUnitIndex = iMUnitIndex
//        dataManager.currentLockerIndex = iLockerIndex
//        //dataManager.currentLocker.setSelected()
//        mainFrame.showLockerInformation()
//        mainFrame.bringCurrentLockerInSight()
    }

    /**
     * This is necessary because the row order can be user defined, so the row
     * order and the found lockers order isn't the same
     */
    private fun getLockerFromRow(rowIndex: Int): EntityCoordinates<Locker> {

        val selectedLockerId = table.getValueAt(rowIndex, 0) as String

        return foundLockers.first {
            it.entity.id == selectedLockerId
        }
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        jCheckBox1 = JCheckBox()
        lockerDataScrollPane = JScrollPane()
        lockerDataPanel = JPanel()
        containerPanel = JPanel()
        lockerIDLabel = JLabel()
        lockerIDTextField = JTextField()
        lastNameLabel = JLabel()
        lastNameTextField = JTextField()
        firstNameLabel = JLabel()
        firstNameTextField = JTextField()
        classLabel = JLabel()
        classTextField = JTextField()
        sizeLabel = JLabel()
        sizeTextField = JTextField()
        hasContractLabel = JLabel()
        hasContractCheckbox = JCheckBox()
        moneyLabel = JLabel()
        moneyTextField = JTextField()
        durationInMonthsLabel = JLabel()
        durationTextField = JTextField()
        fromLabel = JLabel()
        fromDateTextField = JTextField()
        untilLabel = JLabel()
        untilDateTextField = JTextField()
        lockLabel = JLabel()
        lockTextField = JTextField()
        emtpyCheckboxLabel = JLabel()
        emptyCheckbox = JCheckBox()
        searchButton = JButton()
        fillPanel = JPanel()
        emptySelectedButton = JButton()
        printResultsButton = JButton()
        resultsScrollPane = JScrollPane()
        resultsPanel = JPanel()
        noDataFoundLabel = JLabel()
        jCheckBox1.text = "jCheckBox1"
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Suche"
        lockerDataPanel.layout = GridBagLayout()
        containerPanel.layout = GridLayout(12, 1, 0, 10)
        lockerIDLabel.text = "Schließfach-ID"
        containerPanel.add(lockerIDLabel)
        containerPanel.add(lockerIDTextField)
        lastNameLabel.text = "Nachname"
        containerPanel.add(lastNameLabel)
        containerPanel.add(lastNameTextField)
        firstNameLabel.text = "Vorname"
        containerPanel.add(firstNameLabel)
        containerPanel.add(firstNameTextField)
        classLabel.text = "Klasse"
        containerPanel.add(classLabel)
        containerPanel.add(classTextField)
        sizeLabel.text = "Größe"
        containerPanel.add(sizeLabel)
        containerPanel.add(sizeTextField)
        hasContractLabel.text = "Vertrag"
        containerPanel.add(hasContractLabel)
        containerPanel.add(hasContractCheckbox)
        moneyLabel.text = "Geld (€)"
        containerPanel.add(moneyLabel)
        containerPanel.add(moneyTextField)
        durationInMonthsLabel.text = "verbleibende Monate"
        containerPanel.add(durationInMonthsLabel)
        containerPanel.add(durationTextField)
        fromLabel.text = "Von"
        containerPanel.add(fromLabel)
        containerPanel.add(fromDateTextField)
        untilLabel.text = "Bis"
        containerPanel.add(untilLabel)
        containerPanel.add(untilDateTextField)
        lockLabel.text = "Schloss"
        containerPanel.add(lockLabel)
        containerPanel.add(lockTextField)
        emtpyCheckboxLabel.text = "leer"
        containerPanel.add(emtpyCheckboxLabel)
        containerPanel.add(emptyCheckbox)
        var gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        lockerDataPanel.add(containerPanel, gridBagConstraints)
        searchButton.text = "Suchen"
        searchButton.addActionListener { searchButtonActionPerformed() }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(10, 0, 10, 0)
        lockerDataPanel.add(searchButton, gridBagConstraints)
        val fillPanelLayout = GroupLayout(fillPanel)
        fillPanel.layout = fillPanelLayout
        fillPanelLayout.setHorizontalGroup(
            fillPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 260, Short.MAX_VALUE.toInt())
        )
        fillPanelLayout.setVerticalGroup(
            fillPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 30, Short.MAX_VALUE.toInt())
        )
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.weighty = 1.0
        lockerDataPanel.add(fillPanel, gridBagConstraints)
        emptySelectedButton.text = "Auswahl leeren"
        emptySelectedButton.addActionListener { emptySelectedButtonActionPerformed() }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 10, 10, 10)
        lockerDataPanel.add(emptySelectedButton, gridBagConstraints)
        printResultsButton.text = "Ergebnisse drucken"
        printResultsButton.addActionListener { printResultsButtonActionPerformed() }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        lockerDataPanel.add(printResultsButton, gridBagConstraints)
        lockerDataScrollPane.setViewportView(lockerDataPanel)
        contentPane.add(lockerDataScrollPane, BorderLayout.WEST)
        resultsPanel.layout = BorderLayout()
        noDataFoundLabel.text = "Es wurden keine Schließfächer gefunden, die den Suchkriterien entsprechen!"
        noDataFoundLabel.verticalAlignment = SwingConstants.TOP
        resultsPanel.add(noDataFoundLabel, BorderLayout.CENTER)
        resultsScrollPane.setViewportView(resultsPanel)
        contentPane.add(resultsScrollPane, BorderLayout.CENTER)
        pack()
    } // </editor-fold>

    private fun searchButtonActionPerformed() {

        foundLockers = dataManager.buildingList.getAllLockerCoordinates().findLockers(
            lockerIDTextField.text.takeIf { it.isNotBlank() },
            lastNameTextField.text.takeIf { it.isNotBlank() },
            firstNameTextField.text.takeIf { it.isNotBlank() },
            hasContractCheckbox.isSelected,
            classTextField.text.takeIf { it.isNotBlank() },
            sizeTextField.text.takeIf { it.isNotBlank() },
            moneyTextField.text.takeIf { it.isNotBlank() },
            durationTextField.text.takeIf { it.isNotBlank() },
            fromDateTextField.text.takeIf { it.isNotBlank() },
            untilDateTextField.text.takeIf { it.isNotBlank() },
            lockTextField.text.takeIf { it.isNotBlank() },
            emptyCheckbox.isSelected
        ).toList()

        val tableData = foundLockers.map { foundLockerCoords ->

            val locker = foundLockerCoords.entity

            val rowData: MutableList<Any> = if (locker.isFree) {
                mutableListOf(
                    locker.id,
                    locker.lockCode,
                )
            } else {
                mutableListOf(
                    locker.id,
                    locker.pupil.lastName,
                    locker.pupil.firstName,
                    locker.pupil.schoolClassName,
                    locker.pupil.heightInCm,
                    locker.pupil.hasContract,
                    locker.pupil.paidAmount,
                    locker.pupil.remainingTimeInMonths,
                    locker.pupil.rentedFromDate,
                    locker.pupil.rentedUntilDate,
                    locker.lockCode,
                )
            }

            if (dataManager.currentUser is SuperUser) {
                val codes = locker.getCodes(dataManager.superUserMasterKey).joinToString()
                rowData.add(codes)
            }

            rowData.toTypedArray()
        }

        resultsPanel.removeAll()

        if (foundLockers.isNotEmpty()) {
            table.model = object : DefaultTableModel(tableData.toTypedArray(), columnData) {
                override fun getColumnClass(columnIndex: Int): Class<*> {
                    return dataTypes[columnIndex]
                }

                override fun isCellEditable(row: Int, col: Int): Boolean {
                    return false
                }
            }
            resultsScrollPane.setViewportView(table)
        } else {
            resultsScrollPane.setViewportView(noDataFoundLabel)
        }

        printResultsButton.isEnabled = foundLockers.isNotEmpty()
        emptySelectedButton.isEnabled = foundLockers.isNotEmpty()
        resultsPanel.updateUI()
    }

    private fun printResultsButtonActionPerformed() {
        print("* printing... ")
        try {
            table.print()
            print("successful")
        } catch (ex: PrinterException) {
            print("failed")
        }
    }

    private fun emptySelectedButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie die markierten Schließfächer wirklich leeren?",
            "Bestätigung",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.NO_OPTION || answer == JOptionPane.CANCEL_OPTION || answer == JOptionPane.CLOSED_OPTION) {
            return
        }
        val selectedRows = table.selectedRows

        for (r in selectedRows.indices) {
            val row = selectedRows[r]
            val locker = getLockerFromRow(row).entity

            for (j in 1..9) {
                if (dataTypes[j] == String::class.java) {
                    table.setValueAt("", row, j)
                }
                if (dataTypes[j] == Int::class.java) {
                    table.setValueAt(0, row, j)
                }
                if (dataTypes[j] == Boolean::class.java) {
                    table.setValueAt(false, row, j)
                }
            }
            locker.empty()
        }
        JOptionPane.showMessageDialog(
            this,
            "Die ausgewälten Schließfächer wurden erfolgreich geleert!",
            "Info",
            JOptionPane.INFORMATION_MESSAGE
        )
    }

    private lateinit var classLabel: JLabel
    private lateinit var classTextField: JTextField
    private lateinit var containerPanel: JPanel
    private lateinit var durationInMonthsLabel: JLabel
    private lateinit var durationTextField: JTextField
    private lateinit var emptyCheckbox: JCheckBox
    private lateinit var emptySelectedButton: JButton
    private lateinit var emtpyCheckboxLabel: JLabel
    private lateinit var fillPanel: JPanel
    private lateinit var fromDateTextField: JTextField
    private lateinit var fromLabel: JLabel
    private lateinit var hasContractCheckbox: JCheckBox
    private lateinit var hasContractLabel: JLabel
    private lateinit var jCheckBox1: JCheckBox
    private lateinit var lockLabel: JLabel
    private lateinit var lockTextField: JTextField
    private lateinit var lockerDataPanel: JPanel
    private lateinit var lockerDataScrollPane: JScrollPane
    private lateinit var lockerIDLabel: JLabel
    private lateinit var lockerIDTextField: JTextField
    private lateinit var moneyLabel: JLabel
    private lateinit var moneyTextField: JTextField
    private lateinit var firstNameLabel: JLabel
    private lateinit var firstNameTextField: JTextField
    private lateinit var noDataFoundLabel: JLabel
    private lateinit var printResultsButton: JButton
    private lateinit var resultsPanel: JPanel
    private lateinit var resultsScrollPane: JScrollPane
    private lateinit var searchButton: JButton
    private lateinit var sizeLabel: JLabel
    private lateinit var sizeTextField: JTextField
    private lateinit var lastNameLabel: JLabel
    private lateinit var lastNameTextField: JTextField
    private lateinit var untilDateTextField: JTextField
    private lateinit var untilLabel: JLabel

    init {
        initComponents()

        isVisible = true

        // button that is clicked when you hit enter
        getRootPane().defaultButton = searchButton

        // focus in the middle
        setLocationRelativeTo(null)

        resultsPanel.removeAll()
        printResultsButton.isEnabled = false
        emptySelectedButton.isEnabled = false
    }
}