package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.model.Task
import java.awt.*
import java.awt.print.PrinterException
import java.util.*
import javax.swing.*
import javax.swing.table.DefaultTableModel

class TasksFrame : JFrame() {
    private val columnData = arrayOf("Datum", "Aufgabe", "erledigt")
    private val table: JTable = JTable() // model, header
    private lateinit var tablemodel: DefaultTableModel

    private fun createTableModel() {

        val tableData = dataManager.tasks.reversed().map { task ->
            arrayOf(task.creationDate, task.description, task.isDone)
        }.toTypedArray()

        tablemodel = object : DefaultTableModel(tableData, columnData) {
            var types = arrayOf(
                String::class.java,
                String::class.java,
                java.lang.Boolean::class.java // needs to be Java's Boolean, otherwise no checkbox will be displayed
            )

            override fun getColumnClass(columnIndex: Int): Class<*> {
                return types[columnIndex]
            }
        }

        table.model = tablemodel
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        tableScrollPane = JScrollPane()
        componentsPanel = JPanel()
        middlePanel = JPanel()
        deleteDoneTasksButton = JButton()
        deleteAllButton = JButton()
        printTasksButton = JButton()
        tasksPanel = JPanel()
        descriptionTextField = JTextField()
        addButton = JButton()
        bottomPanel = JPanel()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        title = "Aufgaben"
        preferredSize = Dimension(800, 600)
        contentPane.add(tableScrollPane, BorderLayout.CENTER)
        componentsPanel.layout = GridBagLayout()
        deleteDoneTasksButton.text = "Erledigte Aufgaben löschen"
        deleteDoneTasksButton.addActionListener { deleteDoneTasksButtonActionPerformed() }
        middlePanel.add(deleteDoneTasksButton)
        deleteAllButton.text = "Alle Löschen"
        deleteAllButton.addActionListener { deleteAllButtonActionPerformed() }
        middlePanel.add(deleteAllButton)
        printTasksButton.text = "Drucken"
        printTasksButton.addActionListener { printTasksButtonActionPerformed() }
        middlePanel.add(printTasksButton)
        var gridBagConstraints: GridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        componentsPanel.add(middlePanel, gridBagConstraints)
        tasksPanel.layout = BorderLayout(10, 0)
        tasksPanel.add(descriptionTextField, BorderLayout.CENTER)
        addButton.text = "Hinzufügen"
        addButton.addActionListener { addButtonActionPerformed() }
        tasksPanel.add(addButton, BorderLayout.EAST)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        gridBagConstraints.insets = Insets(0, 10, 0, 10)
        componentsPanel.add(tasksPanel, gridBagConstraints)
        okButton.text = "OK"
        okButton.addActionListener { okButtonActionPerformed() }
        bottomPanel.add(okButton)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { cancelButtonActionPerformed() }
        bottomPanel.add(cancelButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        componentsPanel.add(bottomPanel, gridBagConstraints)
        contentPane.add(componentsPanel, BorderLayout.SOUTH)
        pack()
    } // </editor-fold>

    private fun deleteDoneTasksButtonActionPerformed() {
        val tasks: List<Task> = dataManager.tasks
        val newTasksList: MutableList<Task> = LinkedList()
        val size = table.rowCount
        for (i in 0 until size) {
            if (!(table.getValueAt(size - i - 1, 2) as Boolean)) {
                newTasksList.add(tasks[i])
            }
        }
        if (newTasksList.size == tasks.size) {
            JOptionPane.showMessageDialog(
                null,
                "Sie haben keine Aufgaben ausgewählt!",
                "Löschen",
                JOptionPane.INFORMATION_MESSAGE
            )
        } else {
            val answer = JOptionPane.showConfirmDialog(
                null,
                "Wollen Sie wirklich alle erledigten Aufgaben löschen?",
                "Löschen",
                JOptionPane.YES_NO_OPTION
            )
            if (answer == JOptionPane.YES_OPTION) {
                dataManager.tasks = newTasksList
                createTableModel()
            }
        }
    }

    private fun deleteAllButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie wirklich alle Aufgaben löschen?",
            "Löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            while (tablemodel.rowCount > 0) {
                tablemodel.removeRow(0)
            }
            dataManager.tasks.clear()
            tableScrollPane.updateUI()
        }
    }

    private fun printTasksButtonActionPerformed() {
        try {
            table.print()
        } catch (ex: PrinterException) {
            JOptionPane.showMessageDialog(
                null,
                "Das Drucken ist aufgrund eines Fehlers nicht möglich!",
                "Fehler",
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    private fun addButtonActionPerformed() {
        val description = descriptionTextField.text
        val tasks = dataManager.tasks
        if (description.isBlank()) {
            JOptionPane.showMessageDialog(
                null,
                "Sie müssen eine Beschreibung eingeben!",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
        } else {
            tasks.add(Task(description))
            createTableModel()
            descriptionTextField.text = ""
        }
    }

    private fun okButtonActionPerformed() {
        dataManager.tasks.reversed().forEachIndexed { index, task ->
            task.isDone = table.getValueAt(index, 2) as Boolean
        }
        dispose()
    }

    private fun cancelButtonActionPerformed() {
        dispose()
    }

    private lateinit var addButton: JButton
    private lateinit var bottomPanel: JPanel
    private lateinit var cancelButton: JButton
    private lateinit var componentsPanel: JPanel
    private lateinit var deleteAllButton: JButton
    private lateinit var deleteDoneTasksButton: JButton
    private lateinit var descriptionTextField: JTextField
    private lateinit var middlePanel: JPanel
    private lateinit var okButton: JButton
    private lateinit var printTasksButton: JButton
    private lateinit var tableScrollPane: JScrollPane
    private lateinit var tasksPanel: JPanel

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)

        createTableModel()

        tableScrollPane.setViewportView(table)
    }
}