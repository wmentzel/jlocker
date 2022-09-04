package com.randomlychosenbytes.jlocker.dialogs

import com.randomlychosenbytes.jlocker.MainFrame
import com.randomlychosenbytes.jlocker.model.Walk
import java.awt.Frame
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import java.awt.Insets
import javax.swing.*

class WalkDialog(
    parent: Frame,
    modal: Boolean,
    private val currentWalk: Walk?,
    private val createWalk: ((String) -> Unit)?
) : JDialog(parent, modal) {

    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        var gridBagConstraints: GridBagConstraints
        centerPanel = JPanel()
        entityNameLabel = JLabel()
        entityNameTextField = JTextField()
        okButton = JButton()
        cancelButton = JButton()
        defaultCloseOperation = DISPOSE_ON_CLOSE
        isResizable = false
        contentPane.layout = GridBagLayout()
        centerPanel.layout = GridBagLayout()
        entityNameLabel.text = "Name"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.BOTH
        gridBagConstraints.insets = Insets(0, 0, 10, 10)
        centerPanel.add(entityNameLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.insets = Insets(0, 0, 10, 0)
        centerPanel.add(entityNameTextField, gridBagConstraints)
        okButton.text = "OK"
        okButton.addActionListener { okButtonActionPerformed() }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        centerPanel.add(okButton, gridBagConstraints)
        cancelButton.text = "Abbrechen"
        cancelButton.addActionListener { cancelButtonActionPerformed() }
        centerPanel.add(cancelButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(10, 10, 10, 10)
        contentPane.add(centerPanel, gridBagConstraints)
        pack()
    } // </editor-fold>

    private fun cancelButtonActionPerformed() {
        dispose()
    }

    private fun okButtonActionPerformed() {
        if (currentWalk != null) {
            currentWalk.name = entityNameTextField.text
        } else {
            val name = entityNameTextField.text
            if (name.isBlank()) {
                JOptionPane.showMessageDialog(
                    this,
                    "Bitte geben Sie einen Namen ein!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }

            createWalk?.invoke(entityNameTextField.text)
        }
        (parent as MainFrame).updateComboBoxes()
        dispose()
    }

    private lateinit var cancelButton: JButton
    private lateinit var centerPanel: JPanel
    private lateinit var entityNameLabel: JLabel
    private lateinit var entityNameTextField: JTextField
    private lateinit var okButton: JButton

    init {
        initComponents()

        // button that is clicked when you hit enter
        getRootPane().defaultButton = okButton

        // focus in the middle
        setLocationRelativeTo(null)
        if (currentWalk != null) {
            title = "Gangname bearbeiten"
            entityNameTextField.text = currentWalk.name
        } else {
            title = "Gang hinzufügen"
        }
    }
}