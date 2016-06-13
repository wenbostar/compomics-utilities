package com.compomics.util.gui.protein;

import com.compomics.util.Util;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import java.io.File;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 * A dialog for advanced database settings.
 *
 * @author Marc Vaudel
 */
public class AdvancedProteinDatabaseDialog extends javax.swing.JDialog {

    /**
     * The database folder.
     */
    private File dbFolder;
    /**
     * The user preferences.
     */
    private UtilitiesUserPreferences userPreferences;

    /**
     * Creates a new AdvancedProteinDatabaseDialog.
     *
     * @param parent the parent frame
     */
    public AdvancedProteinDatabaseDialog(java.awt.Frame parent) {
        super(parent, true);
        initComponents();
        initGUI();
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    /**
     * Initiates the GUI.
     */
    private void initGUI() {
        userPreferences = UtilitiesUserPreferences.loadUserPreferences();
        dbFolder = userPreferences.getProteinTreeFolder();
        updateText();
    }

    /**
     * Updates the text in the text field.
     */
    private void updateText() {
        if (dbFolder != null) {
            databasesFolderTxt.setText(dbFolder.getAbsolutePath());
        }
        fastaSuffixTxt.setText(userPreferences.getTargetDecoyFileNameSuffix());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        backgroundPanel = new javax.swing.JPanel();
        folderPanel = new javax.swing.JPanel();
        clearDatabaseFolderButton = new javax.swing.JButton();
        browseDatabaseFolderButton = new javax.swing.JButton();
        databasesFolderTxt = new javax.swing.JTextField();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        fileProcessingPanel = new javax.swing.JPanel();
        fastaSuffixTxt = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Database Details");

        backgroundPanel.setBackground(new java.awt.Color(230, 230, 230));

        folderPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Databases Folder"));
        folderPanel.setOpaque(false);

        clearDatabaseFolderButton.setText("Clear");
        clearDatabaseFolderButton.setPreferredSize(new java.awt.Dimension(75, 25));
        clearDatabaseFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearDatabaseFolderButtonActionPerformed(evt);
            }
        });

        browseDatabaseFolderButton.setText("Browse");
        browseDatabaseFolderButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                browseDatabaseFolderButtonActionPerformed(evt);
            }
        });

        databasesFolderTxt.setEditable(false);

        javax.swing.GroupLayout folderPanelLayout = new javax.swing.GroupLayout(folderPanel);
        folderPanel.setLayout(folderPanelLayout);
        folderPanelLayout.setHorizontalGroup(
            folderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, folderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(databasesFolderTxt, javax.swing.GroupLayout.DEFAULT_SIZE, 432, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(browseDatabaseFolderButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(clearDatabaseFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        folderPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {browseDatabaseFolderButton, clearDatabaseFolderButton});

        folderPanelLayout.setVerticalGroup(
            folderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(folderPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(folderPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(clearDatabaseFolderButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(browseDatabaseFolderButton)
                    .addComponent(databasesFolderTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        folderPanelLayout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {browseDatabaseFolderButton, clearDatabaseFolderButton});

        okButton.setText("OK");
        okButton.setPreferredSize(new java.awt.Dimension(75, 25));
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Cancel");
        cancelButton.setPreferredSize(new java.awt.Dimension(75, 25));
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        fileProcessingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("FASTA File Decoy Suffix"));
        fileProcessingPanel.setOpaque(false);

        fastaSuffixTxt.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        javax.swing.GroupLayout fileProcessingPanelLayout = new javax.swing.GroupLayout(fileProcessingPanel);
        fileProcessingPanel.setLayout(fileProcessingPanelLayout);
        fileProcessingPanelLayout.setHorizontalGroup(
            fileProcessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, fileProcessingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fastaSuffixTxt)
                .addContainerGap())
        );
        fileProcessingPanelLayout.setVerticalGroup(
            fileProcessingPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(fileProcessingPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(fastaSuffixTxt, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout backgroundPanelLayout = new javax.swing.GroupLayout(backgroundPanel);
        backgroundPanel.setLayout(backgroundPanelLayout);
        backgroundPanelLayout.setHorizontalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(folderPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, backgroundPanelLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(fileProcessingPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        backgroundPanelLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        backgroundPanelLayout.setVerticalGroup(
            backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(backgroundPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(folderPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(fileProcessingPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(backgroundPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(backgroundPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Save the settings and close the dialog.
     *
     * @param evt
     */
    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        UtilitiesUserPreferences tempUserPreferences = UtilitiesUserPreferences.loadUserPreferences();
        tempUserPreferences.setProteinTreeFolder(dbFolder);
        tempUserPreferences.setTargetDecoyFileNameSuffix(fastaSuffixTxt.getText().trim());
        UtilitiesUserPreferences.saveUserPreferences(tempUserPreferences);
        dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    /**
     * Close the dialog without saving the settings.
     *
     * @param evt
     */
    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
        dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    /**
     * Open a file chooser where the user can select where to save the protein
     * tree.
     *
     * @param evt
     */
    private void browseDatabaseFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_browseDatabaseFolderButtonActionPerformed
        dbFolder = Util.getUserSelectedFolder(this, "Please select new folder", dbFolder.getAbsolutePath(), "Databases Folder", "OK", false);
        updateText();
    }//GEN-LAST:event_browseDatabaseFolderButtonActionPerformed

    /**
     * Clear the database folder.
     *
     * @param evt
     */
    private void clearDatabaseFolderButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearDatabaseFolderButtonActionPerformed

        int outcome = JOptionPane.showConfirmDialog(this, "This operation cannot be undone, continue?", "Warning", JOptionPane.WARNING_MESSAGE);

        if (outcome == JOptionPane.YES_OPTION) {
            ArrayList<String> notDeleted = new ArrayList<String>();
            for (File file : dbFolder.listFiles()) {
                boolean success = false;
                try {
                    success = Util.deleteDir(file);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (!success) {
                    notDeleted.add(file.getName());
                }
            }
            if (!notDeleted.isEmpty()) {
                String report = "An error occurred when deleting ";
                boolean first = true;
                for (String name : notDeleted) {
                    if (first) {
                        first = false;
                    } else {
                        report += ", ";
                    }
                    report += name;
                }
                JOptionPane.showMessageDialog(this, report, "Error when emptying folder", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_clearDatabaseFolderButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel backgroundPanel;
    private javax.swing.JButton browseDatabaseFolderButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton clearDatabaseFolderButton;
    private javax.swing.JTextField databasesFolderTxt;
    private javax.swing.JTextField fastaSuffixTxt;
    private javax.swing.JPanel fileProcessingPanel;
    private javax.swing.JPanel folderPanel;
    private javax.swing.JButton okButton;
    // End of variables declaration//GEN-END:variables
}
