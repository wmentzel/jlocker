/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dialogs;

import abstractreps.ManagementUnit;
import manager.DataManager;
import nonabstractreps.Building;
import nonabstractreps.Floor;
import nonabstractreps.Locker;
import nonabstractreps.Walk;

import javax.swing.*;
import java.util.List;

/**
 * A simple dialog to rename a (school) class.
 * TODO rewrite
 *
 * @author Willi
 */
public class RenameClassDialog extends javax.swing.JDialog {
    DataManager dataManager;

    /**
     * Creates new form ChangeClassDialog
     *
     * @param parent
     * @param dataManager
     * @param modal
     */
    public RenameClassDialog(java.awt.Frame parent, DataManager dataManager, boolean modal) {
        super(parent, modal);
        initComponents();

        this.dataManager = dataManager;

        // center on screen
        setLocationRelativeTo(null);

        // button that is clicked when you hit enter
        getRootPane().setDefaultButton(okButton);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        centerPanel = new javax.swing.JPanel();
        classLabel = new javax.swing.JLabel();
        classTextField = new javax.swing.JTextField();
        changeToLabel = new javax.swing.JLabel();
        changeToTextField = new javax.swing.JTextField();
        southPanel = new javax.swing.JPanel();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Klasse umbennen");
        setResizable(false);
        getContentPane().setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.GridLayout(2, 2, 20, 10));

        classLabel.setText("Klasse");
        centerPanel.add(classLabel);
        centerPanel.add(classTextField);

        changeToLabel.setText("ändern zu");
        centerPanel.add(changeToLabel);
        centerPanel.add(changeToTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 0, 10);
        getContentPane().add(centerPanel, gridBagConstraints);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });
        southPanel.add(okButton);

        cancelButton.setText("Abbrechen");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });
        southPanel.add(cancelButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 10, 10);
        getContentPane().add(southPanel, gridBagConstraints);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed

        String newClassName = changeToTextField.getText();
        String previousClassName = classTextField.getText();
        boolean searchForAgeGroup = previousClassName.indexOf('.') == -1;
        int numMatches = 0;

        List<Building> buildings = dataManager.getBuildingList();

        for (Building building : buildings) {
            List<Floor> floors = building.getFloorList();

            for (Floor floor : floors) {
                List<Walk> walks = floor.getWalkList();

                for (Walk walk : walks) {
                    List<ManagementUnit> cols = walk.getManagementUnitList();

                    for (ManagementUnit col : cols) {
                        List<Locker> lockers = col.getLockerList();
                        String sSubClass;

                        for (Locker locker : lockers) {
                            String sFoundClass = locker.getOwnerClass();
                            sSubClass = "";

                            System.out.println("gesucht nach: " + previousClassName);
                            System.out.println("gefunden: " + sFoundClass);

                            //7, E, Kurs
                            if (searchForAgeGroup) // with dot
                            {
                                int iDotIndex = sFoundClass.indexOf('.');

                                if (iDotIndex != -1) {
                                    sSubClass = sFoundClass.substring(iDotIndex);
                                    sFoundClass = sFoundClass.substring(0, iDotIndex);
                                }

                            }
                            // else 7.2, E.2 ...

                            if (sFoundClass.equals(previousClassName)) {
                                numMatches++;
                                locker.setClass(newClassName + sSubClass);

                                System.out.println("neue Klassenbezeichnung: " + newClassName + sSubClass);
                            }
                            System.out.println("-------------------------");
                        }
                    }
                }
            }
        }

        if (numMatches == 0) {
            JOptionPane.showMessageDialog(null, "Kann Schließfachmieter besucht die Klasse " + previousClassName + "!", "Fehler", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Die Klasse von " + numMatches + " Schließfachmietern wurde erfolgreich geändert!", "Information", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel changeToLabel;
    private javax.swing.JTextField changeToTextField;
    private javax.swing.JLabel classLabel;
    private javax.swing.JTextField classTextField;
    private javax.swing.JButton okButton;
    private javax.swing.JPanel southPanel;
    // End of variables declaration//GEN-END:variables
}
