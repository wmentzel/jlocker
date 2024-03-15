package com.randomlychosenbytes.jlocker

import com.randomlychosenbytes.jlocker.State.Companion.dataManager
import com.randomlychosenbytes.jlocker.dialogs.*
import com.randomlychosenbytes.jlocker.model.*
import com.randomlychosenbytes.jlocker.uicomponents.LockerPanel
import com.randomlychosenbytes.jlocker.uicomponents.ModulePanel
import com.randomlychosenbytes.jlocker.utils.isDateValid
import java.awt.*
import java.awt.event.*
import javax.swing.*
import javax.swing.event.PopupMenuEvent
import javax.swing.event.PopupMenuListener
import kotlin.system.exitProcess

/**
 * This is the main windows of the application. It is displayed right after
 * the login-dialog/create new user dialog.i
 */
class MainFrame : JFrame() {

    private lateinit var currentModuleList: MutableList<Module>
    private lateinit var currentWalk: Walk
    private lateinit var currentFloor: Floor
    private lateinit var currentBuilding: Building

    private var currentLocker: Locker? = null

    private val currentFloorList: MutableList<Floor>
        get() = currentBuilding.floors

    private val currentWalkList: MutableList<Walk>
        get() = currentFloor.walks

    private var searchFrame: SearchFrame? = null
    private var tasksFrame: TasksFrame? = null

    private lateinit var timer: Timer

    fun initialize() {

        initComponents()

        // center on screen
        setLocationRelativeTo(null)

        //
        // Set application title from resources
        //
        title = "${dataManager.appTitle} ${dataManager.appVersion}"

        //
        // Ask to save changes on exit
        //
        addWindowListener(
            object : WindowAdapter() {
                override fun windowClosing(winEvt: WindowEvent) {
                    exit()
                }
            }
        )

        //
        // Initialize status message timer
        //
        val resetStatusMessage = ActionListener { statusMessageLabel.text = "" }
        timer = Timer(5000, resetStatusMessage).apply {
            isRepeats = true
        }

        //
        // Show CreateUserDialog if it is the first run
        //
        if (!dataManager.ressourceFile.exists()) {
            CreateUsersDialog(this, true).apply {
                isVisible = true
            }
        }

        //
        // LogIn
        //
        LogInDialog(this, true).apply {
            isVisible = true
        }

        currentBuilding = dataManager.buildingList.first()
        currentFloor = currentFloorList.first()
        currentWalk = currentWalkList.first()
        currentModuleList = currentWalk.modules
        currentLocker = currentWalk.modules
            .filterIsInstance<LockerCabinet>()
            .flatMap(LockerCabinet::lockers)
            .firstOrNull()

        //
        // Initialize UI
        //
        updateComboBoxes()

        // If the super user is logged in, he is allowed to change to passwords.
        changeUserPWMenuItem.isEnabled = dataManager.currentUser is SuperUser
    }

    /**
     * Put the lockers of the current walk in the layout manager.
     */
    fun drawLockerOverview() {

        LockerPanel.isFirst = true

        // Remove old panels
        lockerOverviewPanel.removeAll()

        fun addModulePanelLeftOf(modulePanel: ModulePanel) {
            val index = currentModuleList.indexOfFirst {
                it === modulePanel.module
            }

            currentModuleList.add(index, LockerCabinet())
            drawLockerOverview()
        }

        fun addModulePanelRightOf(modulePanel: ModulePanel) {
            val index = currentModuleList.indexOfFirst {
                it === modulePanel.module
            }
            currentModuleList.add(index + 1, LockerCabinet())
            drawLockerOverview()
        }

        fun remove(modulePanel: ModulePanel) {
            if (currentModuleList.size <= 1) {
                return
            }

            val answer = JOptionPane.showConfirmDialog(
                null,
                "Wollen Sie diesen ${modulePanel.module} wirklich löschen?",
                "Löschen",
                JOptionPane.YES_NO_OPTION
            )
            if (answer == JOptionPane.YES_OPTION) {
                currentModuleList.removeIf {
                    modulePanel.module === it
                }
                State.mainFrame.drawLockerOverview()
            }
        }

        modulePanels = currentModuleList.map { oldModule ->
            ModulePanel(
                oldModule,
                ::addModulePanelLeftOf,
                ::addModulePanelRightOf,
                ::remove
            )
        }
        // add each Module to the lockerOverviewPanel
        modulePanels.forEach(lockerOverviewPanel::add)

        currentWalk.modules = currentModuleList

        // TODO: set selected locker
        /*        moduleWrappers.asSequence().mapIndexed { index, moduleWrapper ->
                    index to moduleWrapper
                }.firstOrNull { (index, moduleWrapper) ->
                    moduleWrapper.module is LockerCabinet
                }?.let { (moduleWrapperIndex, moduleWrapper) ->
                    (moduleWrapper.module as? LockerCabinet)?.lockers?.firstOrNull()?.setSelected()
                    dataManager.currentManagementUnitIndex = moduleWrapperIndex
                    dataManager.currentLockerIndex = 0
                }
        */
        showLockerInformation()
        lockerOverviewPanel.updateUI()
    }

    private lateinit var modulePanels: List<ModulePanel>

    fun updateDummyRows() {
        val maxRows = modulePanels.maxOfOrNull {
            (it.module as? LockerCabinet)?.lockers?.size ?: 0
        } ?: 0

        modulePanels.onEach {
            it.updateDummyRows(maxRows)
        }
    }

    fun selectLocker(lockerPanel: LockerPanel) {

        dataManager.currentLockerPanel?.setAppropriateColor()
        dataManager.currentLockerPanel = lockerPanel
        currentLocker = lockerPanel.locker

        lockerPanel.setSelectedColor()

        showLockerInformation()
    }

    /**
     * When a locker is clicked, it's data is displayed in the respective
     * GUI components (surname, name, etc.)
     */
    fun showLockerInformation() {
        if (currentLocker == null) {
            containerPanel.isVisible = false
            return
        }

        //
        // Initialize all childs of userDataPanel
        //
        containerPanel.isVisible = true
        val locker = currentLocker!!
        if (locker.isFree) {
            lastNameTextField.text = ""
            firstNameTextField.text = ""
            classTextField.text = ""
            heightInCmTextField.text = ""
            hasContractCheckbox.isSelected = false
            moneyTextField.text = ""
            previousAmountTextField.text = ""
            rentedFromDateTextField.text = ""
            rentedUntilDateTextField.text = ""
            remainingTimeInMonthsTextField.text = ""
        } else {
            lastNameTextField.text = locker.pupil.lastName
            firstNameTextField.text = locker.pupil.firstName
            classTextField.text = locker.pupil.schoolClassName
            heightInCmTextField.text = locker.pupil.heightInCm.toString()
            hasContractCheckbox.isSelected = locker.pupil.hasContract
            moneyTextField.text = locker.pupil.paidAmount.toString()
            previousAmountTextField.text = locker.pupil.previouslyPaidAmount.toString()
            rentedFromDateTextField.text = locker.pupil.rentedFromDate
            rentedUntilDateTextField.text = locker.pupil.rentedUntilDate
            remainingTimeInMonthsTextField.text = locker.pupil.remainingTimeInMonths.let {
                "$it ${if (it == 1) "Monat" else "Monate"}"
            }
        }

        lockerIDTextField.text = locker.id
        outOfOrderCheckbox.isSelected = locker.isOutOfOrder

        // Combobox initialization
        if (dataManager.currentUser is SuperUser) {
            codeTextField.text = locker.getCurrentCode(dataManager.superUserMasterKey)
        } else {
            codeTextField.text = "00-00-00"
        }
        lockTextField.text = locker.lockCode
        noteTextArea.text = locker.note
    }

    private fun getOrCreatePupil(locker: Locker): Pupil {
        if (locker.isFree) {
            locker.moveInNewOwner(Pupil())
        }
        return locker.pupil
    }

    /**
     * When executed the data from the GUI components is written into the locker
     * object.
     */
    fun setLockerInformation() {

        val currentLocker = currentLocker!!

        val id = lockerIDTextField.text
        if (currentLocker.id == id || dataManager.isLockerIdUnique(id)) {
            currentLocker.id = id
        } else {
            JOptionPane.showMessageDialog(
                null,
                "Diese Schließfach-ID existiert bereits! Wählen Sie eine andere.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }

        if (id.isBlank()) {
            JOptionPane.showMessageDialog(
                null,
                "Bitte geben Sie eine gültige Schließfach-ID ein.",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
            return
        }
        currentLocker.isOutOfOrder = outOfOrderCheckbox.isSelected
        currentLocker.lockCode = lockTextField.text
        currentLocker.note = noteTextArea.text
        val lastName = lastNameTextField.text
        if (lastName.isNotBlank()) {
            getOrCreatePupil(currentLocker).lastName = lastName
        }
        val firstName = firstNameTextField.text
        if (firstName.isNotBlank()) {
            getOrCreatePupil(currentLocker).firstName = firstName
        }
        val heightString = heightInCmTextField.text
        if (heightString.isNotBlank()) {
            try {
                val height = heightString.toInt()
                getOrCreatePupil(currentLocker).heightInCm = height
            } catch (e: NumberFormatException) {
                JOptionPane.showMessageDialog(
                    null,
                    "Die eingegebene Größe ist ungültig!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        val from = rentedFromDateTextField.text
        if (from.isNotBlank()) {
            if (isDateValid(from)) {
                getOrCreatePupil(currentLocker).rentedFromDate = from
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Anfangsdatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        val until = rentedUntilDateTextField.text
        if (until.isNotBlank()) {
            if (isDateValid(until)) {
                getOrCreatePupil(currentLocker).rentedUntilDate = until
            } else {
                JOptionPane.showMessageDialog(
                    null,
                    "Das Enddatum ist ungültig (Format DD.MM.YYYY)!",
                    "Fehler",
                    JOptionPane.ERROR_MESSAGE
                )
                return
            }
        }
        if (!currentLocker.isFree) {
            val months = currentLocker.pupil.remainingTimeInMonths
            remainingTimeInMonthsTextField.text = months.toString() + " " + if (months == 1) "Monat" else "Monate"
        }
        val schoolClassName = classTextField.text
        if (schoolClassName.isNotBlank()) {
            getOrCreatePupil(currentLocker).schoolClassName = schoolClassName
        }
        if (hasContractCheckbox.isSelected) {
            getOrCreatePupil(currentLocker).hasContract = hasContractCheckbox.isSelected
        }
    }

    fun setStatusMessage(message: String?) {
        if (timer.isRunning) {
            timer.restart()
        } else {
            timer.start()
        }
        statusMessageLabel.text = message
    }

    /**
     * Determines the scroll position to bring a certain locker into sight.
     */
    fun bringCurrentLockerInSight() {
// TODO
//        val r = dataManager.currentManamentUnit.bounds
//        lockerOverviewScrollPane.horizontalScrollBar.value = r.x
//        lockerOverviewScrollPane.verticalScrollBar.value = dataManager.currentLocker.bounds.y
    }

    /**
     * Initializes a combo box with a given list of entities.
     */
    private fun initializeComboBox(list: List<Entity>, combobox: JComboBox<String>) {
        combobox.model = DefaultComboBoxModel(list.map { it.name }.toTypedArray())
    }

    /**
     * Sets all three combo boxes to the current indices of the building,
     * floor and walk.
     */
    fun updateComboBoxes() {
        initializeComboBox(dataManager.buildingList, buildingComboBox)
        buildingComboBox.selectedIndex = dataManager.buildingList.indexOf(currentBuilding)

        initializeComboBox(currentFloorList, floorComboBox)
        floorComboBox.selectedIndex = currentFloorList.indexOf(currentFloor)

        initializeComboBox(currentWalkList, walkComboBox)
        walkComboBox.selectedIndex = currentWalkList.indexOf(currentWalk)

        removeBuildingButton.isEnabled = dataManager.buildingList.size > 1
        removeFloorButton.isEnabled = currentFloorList.size > 1
        removeWalkButton.isEnabled = currentWalkList.size > 1

        drawLockerOverview()
    }

    private fun exit() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie Ihre Änderungen speichern?",
            "Speichern und beenden",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.CANCEL_OPTION) {
            return
        }
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.saveAndCreateBackup()
        }

        exitProcess(0)
    }


    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private fun initComponents() {
        centerPanel = JPanel()
        comboBoxPanel = JPanel()
        buildingsPanel = JPanel()
        buildingsLabel = JLabel()
        buildingComboBox = JComboBox()
        addBuildingButton = JButton()
        removeBuildingButton = JButton()
        editBuildingButton = JButton()
        floorPanel = JPanel()
        floorLabel = JLabel()
        floorComboBox = JComboBox()
        addFloorButton = JButton()
        removeFloorButton = JButton()
        editFloorButton = JButton()
        walksPanel = JPanel()
        walkLabel = JLabel()
        walkComboBox = JComboBox()
        addWalkButton = JButton()
        removeWalkButton = JButton()
        editWalkButton = JButton()
        lockerOverviewScrollPane = JScrollPane()
        lockerOverviewPanel = JPanel()
        userScrollPane = JScrollPane()
        containerPanel = JPanel()
        leftFlowLayout = JPanel()
        legendPanel = JPanel()
        noContractPanel = JPanel()
        noContractLabel = JLabel()
        noContractColorLabel = JPanel()
        freePanel = JPanel()
        freeLabel = JLabel()
        freeColorPanel = JPanel()
        rentedPanel = JPanel()
        rentedLabel = JLabel()
        rentedColorPanel = JPanel()
        outOfOrderPanel = JPanel()
        outOfOrderLabel = JLabel()
        outOfOrderColorPanel = JPanel()
        oneMonthRemainingPanel = JPanel()
        oneMonthRemainingLabel = JLabel()
        oneMonthRemainingColorPanel = JPanel()
        dataPanel = JPanel()
        userPanel = JPanel()
        lockerIDLabel = JLabel()
        lockerIDTextField = JTextField()
        surnameLabel = JLabel()
        lastNameTextField = JTextField()
        nameLabel = JLabel()
        firstNameTextField = JTextField()
        classLabel = JLabel()
        classTextField = JTextField()
        sizeLabel = JLabel()
        heightInCmTextField = JTextField()
        noteLabel = JLabel()
        noteTextArea = JTextField()
        middlePanel = JPanel()
        lockerPanel = JPanel()
        fromDateLabel = JLabel()
        rentedFromDateTextField = JTextField()
        untilDateLabel = JLabel()
        rentedUntilDateTextField = JTextField()
        remainingTimeInMonthsLabel = JLabel()
        remainingTimeInMonthsTextField = JTextField()
        currentPinLabel = JLabel()
        codeTextField = JTextField()
        lockLabel = JLabel()
        lockTextField = JTextField()
        checkBoxPanel = JPanel()
        outOfOrderCheckbox = JCheckBox()
        hasContractCheckbox = JCheckBox()
        moneyPanel = JPanel()
        gridLayoutPanel = JPanel()
        moneyLabel = JLabel()
        moneyTextField = JTextField()
        previousAmountLabel = JLabel()
        previousAmountTextField = JTextField()
        currentAmountTextField = JTextField()
        addAmountButton = JButton()
        statusPanel = JPanel()
        buttonPanel = JPanel()
        saveButton = JButton()
        emptyButton = JButton()
        statusMessageLabel = JLabel()
        menuBar = JMenuBar()
        fileMenu = JMenu()
        showTasksMenuItem = JMenuItem()
        saveMenuItem = JMenuItem()
        loadMenuItem = JMenuItem()
        exitMenu = JMenuItem()
        editMenu = JMenu()
        changeClassMenuItem = JMenuItem()
        moveLockerMenuItem = JMenuItem()
        moveClassMenuItem = JMenuItem()
        changeUserPWMenuItem = JMenuItem()
        settingsMenuItem = JMenuItem()
        searchMenu = JMenu()
        searchMenuItem = JMenuItem()
        helpMenu = JMenu()
        aboutMenuItem = JMenuItem()
        defaultCloseOperation = DO_NOTHING_ON_CLOSE
        preferredSize = Dimension(1280, 800)
        centerPanel.background = primaryColor
        centerPanel.layout = BorderLayout()
        comboBoxPanel.background = primaryColor
        comboBoxPanel.layout = FlowLayout(FlowLayout.LEFT)
        buildingsPanel.background = primaryColor
        buildingsLabel.text = "Gebäude"
        buildingsPanel.add(buildingsLabel)
        buildingComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(@Suppress("unused") evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(evt: PopupMenuEvent) {
                buildingComboBoxPopupMenuWillBecomeInvisible()
            }

            override fun popupMenuWillBecomeVisible(@Suppress("unused") evt: PopupMenuEvent) {}
        })
        buildingsPanel.add(buildingComboBox)
        addBuildingButton.background = primaryColor
        addBuildingButton.text = "+"
        addBuildingButton.addActionListener { addBuildingButtonActionPerformed() }
        buildingsPanel.add(addBuildingButton)
        removeBuildingButton.background = primaryColor
        removeBuildingButton.text = "-"
        removeBuildingButton.addActionListener { removeBuildingButtonActionPerformed() }
        buildingsPanel.add(removeBuildingButton)
        editBuildingButton.background = primaryColor
        editBuildingButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editBuildingButton.addActionListener { editBuildingButtonActionPerformed() }
        buildingsPanel.add(editBuildingButton)
        comboBoxPanel.add(buildingsPanel)
        floorPanel.background = primaryColor
        floorLabel.text = "Etage"
        floorPanel.add(floorLabel)
        floorComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(@Suppress("unused") evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(@Suppress("unused") evt: PopupMenuEvent) {
                floorComboBoxPopupMenuWillBecomeInvisible()
            }

            override fun popupMenuWillBecomeVisible(@Suppress("unused") evt: PopupMenuEvent) {}
        })
        floorPanel.add(floorComboBox)
        addFloorButton.background = primaryColor
        addFloorButton.text = "+"
        addFloorButton.addActionListener { addFloorButtonActionPerformed() }
        floorPanel.add(addFloorButton)
        removeFloorButton.background = primaryColor
        removeFloorButton.text = "-"
        removeFloorButton.addActionListener { removeFloorButtonActionPerformed() }
        floorPanel.add(removeFloorButton)
        editFloorButton.background = primaryColor
        editFloorButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editFloorButton.addActionListener { editFloorButtonActionPerformed() }
        floorPanel.add(editFloorButton)
        comboBoxPanel.add(floorPanel)
        walksPanel.background = primaryColor
        walkLabel.text = "Gang"
        walksPanel.add(walkLabel)
        walkComboBox.addPopupMenuListener(object : PopupMenuListener {
            override fun popupMenuCanceled(@Suppress("unused") evt: PopupMenuEvent) {}
            override fun popupMenuWillBecomeInvisible(@Suppress("unused") evt: PopupMenuEvent) {
                walkComboBoxPopupMenuWillBecomeInvisible()
            }

            override fun popupMenuWillBecomeVisible(@Suppress("unused") evt: PopupMenuEvent) {}
        })
        walksPanel.add(walkComboBox)
        addWalkButton.background = primaryColor
        addWalkButton.text = "+"
        addWalkButton.addActionListener { addWalkButtonActionPerformed() }
        walksPanel.add(addWalkButton)
        removeWalkButton.background = primaryColor
        removeWalkButton.text = "-"
        removeWalkButton.addActionListener { removeWalkButtonActionPerformed() }
        walksPanel.add(removeWalkButton)
        editWalkButton.background = primaryColor
        editWalkButton.icon = ImageIcon(javaClass.getResource("/gray gear.png")) // NOI18N
        editWalkButton.addActionListener { editWalkButtonActionPerformed() }
        walksPanel.add(editWalkButton)
        comboBoxPanel.add(walksPanel)
        centerPanel.add(comboBoxPanel, BorderLayout.NORTH)
        lockerOverviewScrollPane.border = null
        lockerOverviewPanel.background = primaryColor
        lockerOverviewPanel.layout = GridLayout(1, 0, 5, 0)
        lockerOverviewScrollPane.setViewportView(lockerOverviewPanel)
        centerPanel.add(lockerOverviewScrollPane, BorderLayout.CENTER)
        containerPanel.background = primaryColor
        containerPanel.layout = FlowLayout(FlowLayout.LEFT)
        leftFlowLayout.background = primaryColor
        leftFlowLayout.layout = GridBagLayout()
        legendPanel.background = primaryColor
        noContractPanel.background = primaryColor
        noContractLabel.text = "kein Vertrag"
        noContractPanel.add(noContractLabel)
        noContractColorLabel.background = Color(0, 0, 255)
        noContractColorLabel.preferredSize = Dimension(8, 8)
        val noContractColorLabelLayout = GroupLayout(noContractColorLabel)
        noContractColorLabel.layout = noContractColorLabelLayout
        noContractColorLabelLayout.setHorizontalGroup(
            noContractColorLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        noContractColorLabelLayout.setVerticalGroup(
            noContractColorLabelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        noContractPanel.add(noContractColorLabel)
        legendPanel.add(noContractPanel)
        freePanel.background = primaryColor
        freeLabel.text = "Frei"
        freePanel.add(freeLabel)
        freeColorPanel.background = Color(255, 255, 255)
        freeColorPanel.border = BorderFactory.createLineBorder(Color(0, 0, 0))
        freeColorPanel.preferredSize = Dimension(8, 8)
        val freeColorPanelLayout = GroupLayout(freeColorPanel)
        freeColorPanel.layout = freeColorPanelLayout
        freeColorPanelLayout.setHorizontalGroup(
            freeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 6, Short.MAX_VALUE.toInt())
        )
        freeColorPanelLayout.setVerticalGroup(
            freeColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 6, Short.MAX_VALUE.toInt())
        )
        freePanel.add(freeColorPanel)
        legendPanel.add(freePanel)
        rentedPanel.background = primaryColor
        rentedLabel.text = "Vermietet"
        rentedPanel.add(rentedLabel)
        rentedColorPanel.background = Color(0, 102, 0)
        rentedColorPanel.preferredSize = Dimension(8, 8)
        val rentedColorPanelLayout = GroupLayout(rentedColorPanel)
        rentedColorPanel.layout = rentedColorPanelLayout
        rentedColorPanelLayout.setHorizontalGroup(
            rentedColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        rentedColorPanelLayout.setVerticalGroup(
            rentedColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        rentedPanel.add(rentedColorPanel)
        legendPanel.add(rentedPanel)
        outOfOrderPanel.background = primaryColor
        outOfOrderLabel.text = "Defekt"
        outOfOrderPanel.add(outOfOrderLabel)
        outOfOrderColorPanel.background = Color(255, 0, 0)
        outOfOrderColorPanel.preferredSize = Dimension(8, 8)
        val outOfOrderColorPanelLayout = GroupLayout(outOfOrderColorPanel)
        outOfOrderColorPanel.layout = outOfOrderColorPanelLayout
        outOfOrderColorPanelLayout.setHorizontalGroup(
            outOfOrderColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        outOfOrderColorPanelLayout.setVerticalGroup(
            outOfOrderColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        outOfOrderPanel.add(outOfOrderColorPanel)
        legendPanel.add(outOfOrderPanel)
        oneMonthRemainingPanel.background = primaryColor
        oneMonthRemainingLabel.text = "Mietdauer >= 1 Monat"
        oneMonthRemainingPanel.add(oneMonthRemainingLabel)
        oneMonthRemainingColorPanel.background = Color(255, 153, 0)
        oneMonthRemainingColorPanel.preferredSize = Dimension(8, 8)
        val oneMonthRemainingColorPanelLayout = GroupLayout(oneMonthRemainingColorPanel)
        oneMonthRemainingColorPanel.layout = oneMonthRemainingColorPanelLayout
        oneMonthRemainingColorPanelLayout.setHorizontalGroup(
            oneMonthRemainingColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        oneMonthRemainingColorPanelLayout.setVerticalGroup(
            oneMonthRemainingColorPanelLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGap(0, 8, Short.MAX_VALUE.toInt())
        )
        oneMonthRemainingPanel.add(oneMonthRemainingColorPanel)
        legendPanel.add(oneMonthRemainingPanel)
        var gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        leftFlowLayout.add(legendPanel, gridBagConstraints)
        dataPanel.background = primaryColor
        dataPanel.layout = GridBagLayout()
        userPanel.background = primaryColor
        userPanel.layout = GridLayout(6, 2, 5, 3)
        lockerIDLabel.text = "Schließfach-ID           "
        userPanel.add(lockerIDLabel)
        userPanel.add(lockerIDTextField)
        surnameLabel.text = "Nachname"
        userPanel.add(surnameLabel)
        userPanel.add(lastNameTextField)
        nameLabel.text = "Vorname"
        userPanel.add(nameLabel)
        userPanel.add(firstNameTextField)
        classLabel.text = "Klasse"
        userPanel.add(classLabel)
        userPanel.add(classTextField)
        sizeLabel.text = "Größe"
        userPanel.add(sizeLabel)
        userPanel.add(heightInCmTextField)
        noteLabel.text = "Notiz"
        userPanel.add(noteLabel)
        userPanel.add(noteTextArea)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 20)
        dataPanel.add(userPanel, gridBagConstraints)
        middlePanel.background = primaryColor
        middlePanel.layout = GridBagLayout()
        lockerPanel.background = primaryColor
        lockerPanel.layout = GridLayout(5, 2, 5, 3)
        fromDateLabel.text = "von"
        lockerPanel.add(fromDateLabel)
        lockerPanel.add(rentedFromDateTextField)
        untilDateLabel.text = "bis"
        lockerPanel.add(untilDateLabel)
        lockerPanel.add(rentedUntilDateTextField)
        remainingTimeInMonthsLabel.text = "verbleibende Monate"
        lockerPanel.add(remainingTimeInMonthsLabel)
        lockerPanel.add(remainingTimeInMonthsTextField)
        currentPinLabel.text = "aktueller Code"
        lockerPanel.add(currentPinLabel)
        codeTextField.isEditable = false
        codeTextField.addMouseListener(object : MouseAdapter() {
            override fun mouseClicked(@Suppress("unused") evt: MouseEvent) {
                codeTextFieldMouseClicked()
            }
        })
        lockerPanel.add(codeTextField)
        lockLabel.text = "Schloss"
        lockerPanel.add(lockLabel)
        lockerPanel.add(lockTextField)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        middlePanel.add(lockerPanel, gridBagConstraints)
        checkBoxPanel.background = primaryColor
        checkBoxPanel.layout = GridBagLayout()
        outOfOrderCheckbox.background = primaryColor
        outOfOrderCheckbox.text = "defekt"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        checkBoxPanel.add(outOfOrderCheckbox, gridBagConstraints)
        hasContractCheckbox.background = primaryColor
        hasContractCheckbox.text = "Vertrag"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.weightx = 1.0
        checkBoxPanel.add(hasContractCheckbox, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.anchor = GridBagConstraints.WEST
        middlePanel.add(checkBoxPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 30)
        dataPanel.add(middlePanel, gridBagConstraints)
        moneyPanel.background = primaryColor
        moneyPanel.border =
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(accentColor), "Finanzen")
        moneyPanel.layout = GridBagLayout()
        gridLayoutPanel.background = primaryColor
        gridLayoutPanel.layout = GridLayout(3, 2, 10, 3)
        moneyLabel.text = "Kontostand"
        gridLayoutPanel.add(moneyLabel)
        moneyTextField.isEditable = false
        gridLayoutPanel.add(moneyTextField)
        previousAmountLabel.text = "zuletzt eingezahlt"
        gridLayoutPanel.add(previousAmountLabel)
        previousAmountTextField.isEditable = false
        gridLayoutPanel.add(previousAmountTextField)
        currentAmountTextField.columns = 3
        gridLayoutPanel.add(currentAmountTextField)
        addAmountButton.background = primaryColor
        addAmountButton.text = "Einzahlen"
        addAmountButton.addActionListener { addAmountButtonActionPerformed() }
        gridLayoutPanel.add(addAmountButton)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.insets = Insets(5, 5, 5, 5)
        moneyPanel.add(gridLayoutPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.NORTH
        dataPanel.add(moneyPanel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(5, 10, 0, 0)
        leftFlowLayout.add(dataPanel, gridBagConstraints)
        statusPanel.background = primaryColor
        statusPanel.layout = GridBagLayout()
        buttonPanel.background = primaryColor
        buttonPanel.layout = GridBagLayout()
        saveButton.background = primaryColor
        saveButton.text = "Speichern"
        saveButton.addActionListener { saveButtonActionPerformed() }
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.insets = Insets(0, 0, 0, 10)
        buttonPanel.add(saveButton, gridBagConstraints)
        emptyButton.background = primaryColor
        emptyButton.text = "Leeren"
        emptyButton.addActionListener { emptyButtonActionPerformed() }
        buttonPanel.add(emptyButton, GridBagConstraints())
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.anchor = GridBagConstraints.WEST
        statusPanel.add(buttonPanel, gridBagConstraints)
        statusMessageLabel.text = "Status"
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(0, 20, 0, 0)
        statusPanel.add(statusMessageLabel, gridBagConstraints)
        gridBagConstraints = GridBagConstraints()
        gridBagConstraints.gridwidth = GridBagConstraints.REMAINDER
        gridBagConstraints.anchor = GridBagConstraints.WEST
        gridBagConstraints.insets = Insets(10, 10, 10, 0)
        leftFlowLayout.add(statusPanel, gridBagConstraints)
        containerPanel.add(leftFlowLayout)
        userScrollPane.setViewportView(containerPanel)
        centerPanel.add(userScrollPane, BorderLayout.SOUTH)
        contentPane.add(centerPanel, BorderLayout.CENTER)
        fileMenu.text = "Datei"
        showTasksMenuItem.text = "Aufgaben anzeigen"
        showTasksMenuItem.addActionListener { showTasksMenuItemActionPerformed() }
        fileMenu.add(showTasksMenuItem)
        saveMenuItem.text = "Speichern"
        saveMenuItem.addActionListener { saveMenuItemActionPerformed() }
        fileMenu.add(saveMenuItem)
        loadMenuItem.text = "Laden"
        loadMenuItem.addActionListener { loadMenuItemActionPerformed() }
        fileMenu.add(loadMenuItem)
        exitMenu.text = "Beenden"
        exitMenu.addActionListener { exit() }
        fileMenu.add(exitMenu)
        menuBar.add(fileMenu)
        editMenu.text = "Bearbeiten"
        changeClassMenuItem.text = "Klassenänderung"
        changeClassMenuItem.addActionListener { changeClassMenuItemActionPerformed() }
        editMenu.add(changeClassMenuItem)
        moveLockerMenuItem.text = "Schließfachumzug"
        moveLockerMenuItem.addActionListener { moveLockerMenuItemActionPerformed() }
        editMenu.add(moveLockerMenuItem)
        moveClassMenuItem.text = "Klassenumzug"
        moveClassMenuItem.addActionListener { moveClassMenuItemActionPerformed() }
        editMenu.add(moveClassMenuItem)
        changeUserPWMenuItem.text = "Benutzerpasswörter ändern"
        changeUserPWMenuItem.addActionListener { changeUserPWMenuItemActionPerformed() }
        editMenu.add(changeUserPWMenuItem)
        settingsMenuItem.text = "Einstellungen"
        settingsMenuItem.addActionListener { settingsMenuItemActionPerformed() }
        editMenu.add(settingsMenuItem)
        menuBar.add(editMenu)
        searchMenu.text = "Suche"
        searchMenuItem.text = "Suche"
        searchMenuItem.addActionListener { searchMenuItemActionPerformed() }
        searchMenu.add(searchMenuItem)
        menuBar.add(searchMenu)
        helpMenu.text = "Hilfe"
        aboutMenuItem.text = "Über jLocker"
        aboutMenuItem.addActionListener { aboutMenuItemActionPerformed() }
        helpMenu.add(aboutMenuItem)
        menuBar.add(helpMenu)
        jMenuBar = menuBar
        pack()
    } // </editor-fold>

    private fun removeBuildingButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Gebäude wirklich löschen?",
            "Gebäude löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            dataManager.buildingList.remove(currentBuilding)
            currentBuilding = dataManager.buildingList.first()
            currentFloor = currentFloorList.first()
            currentWalk = currentWalkList.first()
            currentLocker =
                currentWalk.modules.filterIsInstance<LockerCabinet>().flatMap { it.lockers }
                    .firstOrNull()
            updateComboBoxes()
        }
    }

    private fun removeFloorButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diese Etage wirklich löschen?",
            "Etage löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            currentFloorList.remove(currentFloor)
            currentFloor = currentFloorList.first()
            currentWalk = currentWalkList.first()
            currentLocker =
                currentWalk.modules.filterIsInstance<LockerCabinet>().flatMap { it.lockers }
                    .first()
            updateComboBoxes()
        }
    }

    private fun removeWalkButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie diesen Gang wirklich löschen?",
            "Gang löschen",
            JOptionPane.YES_NO_OPTION
        )
        if (answer == JOptionPane.YES_OPTION) {
            currentWalkList.remove(currentWalk)
            currentWalk = currentWalkList.first()
            currentLocker =
                currentWalk.modules.filterIsInstance<LockerCabinet>().flatMap { it.lockers }
                    .first()

            updateComboBoxes()
        }
    }

    private fun saveButtonActionPerformed() {
        setLockerInformation()
    }

    private fun codeTextFieldMouseClicked() {
        if (dataManager.currentUser is SuperUser) {
            val dialog = EditCodesDialog(this, true, currentLocker!!)
            dialog.isVisible = true
        }
    }

    private fun emptyButtonActionPerformed() {
        val answer = JOptionPane.showConfirmDialog(
            null,
            "Wollen Sie dieses Schließfach wirklich leeren?",
            "Schließfach leeren",
            JOptionPane.YES_NO_OPTION
        )

        if (answer == JOptionPane.YES_OPTION) {
            currentLocker?.empty()
            showLockerInformation()
        }
    }

    fun createBuilding(name: String) {

        currentBuilding = Building(name).apply {
            dataManager.buildingList.add(this)
        }

        currentFloor = Floor("-").apply {
            currentFloorList.add(this)
        }

        currentWalk = Walk("-").apply {
            currentWalkList.add(this)
        }

        currentModuleList.add(LockerCabinet())

        currentLocker = null
    }

    private fun addBuildingButtonActionPerformed() {
        val dialog = BuildingDialog(this, true, null, ::createBuilding)
        dialog.isVisible = true
    }

    private fun editBuildingButtonActionPerformed() {
        val dialog = BuildingDialog(this, true, currentBuilding, null)
        dialog.isVisible = true
    }

    fun createFloor(name: String) {

        currentFloor = Floor(name).apply {
            currentFloorList.add(this)
        }


        currentWalk = Walk("-").apply {
            currentWalkList.add(this)
        }

        currentModuleList.add(LockerCabinet())

        currentLocker = null
    }

    private fun addFloorButtonActionPerformed() {
        val dialog = FloorDialog(this, true, null, ::createFloor)
        dialog.isVisible = true
    }

    private fun editFloorButtonActionPerformed() {
        val dialog = FloorDialog(this, true, currentFloor, null)
        dialog.isVisible = true
    }

    fun createWalk(name: String) {

        currentWalk = Walk(name).apply {
            currentWalkList.add(this)
        }

        currentModuleList.add(LockerCabinet())

        currentLocker = null
    }

    private fun addWalkButtonActionPerformed() {
        val dialog = WalkDialog(this, true, null, ::createWalk)
        dialog.isVisible = true
    }

    private fun editWalkButtonActionPerformed() {
        val dialog = WalkDialog(this, true, currentWalk, null)
        dialog.isVisible = true
    }

    private fun buildingComboBoxPopupMenuWillBecomeInvisible() {
        currentBuilding = dataManager.buildingList[buildingComboBox.selectedIndex]
        initializeComboBox(currentFloorList, floorComboBox)

        // move on to next combobox
        floorComboBoxPopupMenuWillBecomeInvisible()
    }

    private fun floorComboBoxPopupMenuWillBecomeInvisible() {
        currentFloor = currentFloorList[floorComboBox.selectedIndex]
        initializeComboBox(currentWalkList, walkComboBox)

        // move on to next combobox
        walkComboBoxPopupMenuWillBecomeInvisible()
    }

    private fun walkComboBoxPopupMenuWillBecomeInvisible() {
        currentWalk = currentWalkList[walkComboBox.selectedIndex]
        removeBuildingButton.isEnabled = dataManager.buildingList.size > 1
        removeFloorButton.isEnabled = currentFloorList.size > 1
        removeWalkButton.isEnabled = currentWalkList.size > 1
        drawLockerOverview()
    }

    private fun addAmountButtonActionPerformed() {
        val currentLocker = currentLocker!!

        try {
            val amount: Int = currentAmountTextField.text.toInt()
            currentLocker.pupil.previouslyPaidAmount = amount
            val iNewFullAmount = currentLocker.pupil.paidAmount + amount
            currentLocker.pupil.paidAmount = iNewFullAmount
            previousAmountTextField.text = amount.toString()
            moneyTextField.text = iNewFullAmount.toString()
            currentAmountTextField.text = ""
        } catch (e: NumberFormatException) {
            JOptionPane.showMessageDialog(
                null,
                "Bitte geben Sie einen gültigen Geldbetrag ein!",
                "Fehler",
                JOptionPane.ERROR_MESSAGE
            )
        }
    }

    private fun showTasksMenuItemActionPerformed() {
        if (tasksFrame != null) {
            tasksFrame?.dispose()
        }
        tasksFrame = TasksFrame().apply {
            isVisible = true
        }
    }

    private fun saveMenuItemActionPerformed() {
        dataManager.saveAndCreateBackup()
    }

    private fun loadMenuItemActionPerformed() {
        dataManager.loadFromCustomFile(loadAsSuperUser = dataManager.currentUser is SuperUser)
        dataManager.initBuildingObject()
        drawLockerOverview()
    }

    private fun aboutMenuItemActionPerformed() {
        val dialog = AboutBox(this, true)
        dialog.isVisible = true
    }

    private fun changeUserPWMenuItemActionPerformed() {
        val dialog = CreateUsersDialog(this, true)
        dialog.isVisible = true
    }

    private fun settingsMenuItemActionPerformed() {
        val dialog = SettingsDialog(this, true)
        dialog.isVisible = true
    }

    private fun changeClassMenuItemActionPerformed() {
        val dialog = RenameClassDialog(this, true)
        dialog.isVisible = true
    }

    private fun searchMenuItemActionPerformed() {
        searchFrame = SearchFrame(this).apply {
            isVisible = true
        }
    }

    private fun moveClassMenuItemActionPerformed() {
        val dialog = MoveClassDialog(this)
        dialog.isVisible = true
    }

    private fun moveLockerMenuItemActionPerformed() {
        val dialog = MoveLockerDialog(this, true)
        dialog.isVisible = true
    }

    private lateinit var aboutMenuItem: JMenuItem
    private lateinit var addAmountButton: JButton
    private lateinit var addBuildingButton: JButton
    private lateinit var addFloorButton: JButton
    private lateinit var addWalkButton: JButton
    private lateinit var buildingComboBox: JComboBox<String>
    private lateinit var buildingsLabel: JLabel
    private lateinit var buildingsPanel: JPanel
    private lateinit var buttonPanel: JPanel
    private lateinit var centerPanel: JPanel
    private lateinit var changeClassMenuItem: JMenuItem
    private lateinit var changeUserPWMenuItem: JMenuItem
    private lateinit var checkBoxPanel: JPanel
    private lateinit var classLabel: JLabel
    private lateinit var classTextField: JTextField
    private lateinit var codeTextField: JTextField
    private lateinit var comboBoxPanel: JPanel
    private lateinit var containerPanel: JPanel
    private lateinit var currentAmountTextField: JTextField
    private lateinit var currentPinLabel: JLabel
    private lateinit var dataPanel: JPanel
    private lateinit var editBuildingButton: JButton
    private lateinit var editFloorButton: JButton
    private lateinit var editMenu: JMenu
    private lateinit var editWalkButton: JButton
    private lateinit var emptyButton: JButton
    private lateinit var exitMenu: JMenuItem
    private lateinit var fileMenu: JMenu
    private lateinit var floorComboBox: JComboBox<String>
    private lateinit var floorLabel: JLabel
    private lateinit var floorPanel: JPanel
    private lateinit var freeColorPanel: JPanel
    private lateinit var freeLabel: JLabel
    private lateinit var freePanel: JPanel
    private lateinit var fromDateLabel: JLabel
    private lateinit var rentedFromDateTextField: JTextField
    private lateinit var gridLayoutPanel: JPanel
    private lateinit var hasContractCheckbox: JCheckBox
    private lateinit var helpMenu: JMenu
    private lateinit var leftFlowLayout: JPanel
    private lateinit var legendPanel: JPanel
    private lateinit var loadMenuItem: JMenuItem
    private lateinit var lockLabel: JLabel
    private lateinit var lockTextField: JTextField
    private lateinit var lockerIDLabel: JLabel
    private lateinit var lockerIDTextField: JTextField
    private lateinit var lockerOverviewPanel: JPanel
    private lateinit var lockerOverviewScrollPane: JScrollPane
    private lateinit var lockerPanel: JPanel
    private lateinit var menuBar: JMenuBar
    private lateinit var middlePanel: JPanel
    private lateinit var moneyLabel: JLabel
    private lateinit var moneyPanel: JPanel
    private lateinit var moneyTextField: JTextField
    private lateinit var moveClassMenuItem: JMenuItem
    private lateinit var moveLockerMenuItem: JMenuItem
    private lateinit var nameLabel: JLabel
    private lateinit var firstNameTextField: JTextField
    private lateinit var noContractColorLabel: JPanel
    private lateinit var noContractLabel: JLabel
    private lateinit var noContractPanel: JPanel
    private lateinit var noteLabel: JLabel
    private lateinit var noteTextArea: JTextField
    private lateinit var oneMonthRemainingColorPanel: JPanel
    private lateinit var oneMonthRemainingLabel: JLabel
    private lateinit var oneMonthRemainingPanel: JPanel
    private lateinit var outOfOrderCheckbox: JCheckBox
    private lateinit var outOfOrderColorPanel: JPanel
    private lateinit var outOfOrderLabel: JLabel
    private lateinit var outOfOrderPanel: JPanel
    private lateinit var previousAmountLabel: JLabel
    private lateinit var previousAmountTextField: JTextField
    private lateinit var remainingTimeInMonthsLabel: JLabel
    private lateinit var remainingTimeInMonthsTextField: JTextField
    private lateinit var removeBuildingButton: JButton
    private lateinit var removeFloorButton: JButton
    private lateinit var removeWalkButton: JButton
    private lateinit var rentedColorPanel: JPanel
    private lateinit var rentedLabel: JLabel
    private lateinit var rentedPanel: JPanel
    private lateinit var saveButton: JButton
    private lateinit var saveMenuItem: JMenuItem
    private lateinit var searchMenu: JMenu
    private lateinit var searchMenuItem: JMenuItem
    private lateinit var settingsMenuItem: JMenuItem
    private lateinit var showTasksMenuItem: JMenuItem
    private lateinit var sizeLabel: JLabel
    private lateinit var heightInCmTextField: JTextField
    private lateinit var statusMessageLabel: JLabel
    private lateinit var statusPanel: JPanel
    private lateinit var surnameLabel: JLabel
    private lateinit var lastNameTextField: JTextField
    private lateinit var untilDateLabel: JLabel
    private lateinit var rentedUntilDateTextField: JTextField
    private lateinit var userPanel: JPanel
    private lateinit var userScrollPane: JScrollPane
    private lateinit var walkComboBox: JComboBox<String>
    private lateinit var walkLabel: JLabel
    private lateinit var walksPanel: JPanel
}