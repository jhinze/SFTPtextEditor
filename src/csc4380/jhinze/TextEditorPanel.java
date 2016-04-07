/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc4380.jhinze;

/**
 *
 * @author lk0
 */
public class TextEditorPanel extends javax.swing.JPanel {

    /**
     * Creates new form TextEditorPanel
     */
    public TextEditorPanel() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbarMain = new javax.swing.JToolBar();
        buttonSave = new javax.swing.JButton();
        buttonClose = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        buttonCut = new javax.swing.JButton();
        buttonCopy = new javax.swing.JButton();
        buttonPaste = new javax.swing.JButton();
        buttonDelete = new javax.swing.JButton();
        buttonSelectAll = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        buttonUndo = new javax.swing.JButton();
        buttonRedo = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        buttonSearch = new javax.swing.JButton();
        mainTabbedPane = new javax.swing.JTabbedPane();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        setRequestFocusEnabled(false);

        toolbarMain.setFloatable(false);
        toolbarMain.setRollover(true);

        buttonSave.setText("Save");
        buttonSave.setToolTipText("CTRL + S");
        buttonSave.setFocusable(false);
        buttonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonSave);

        buttonClose.setText("Close");
        buttonClose.setToolTipText("CTRL + BACKSPACE");
        buttonClose.setFocusable(false);
        buttonClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonClose);
        toolbarMain.add(jSeparator2);

        buttonCut.setText("Cut");
        buttonCut.setToolTipText("CTRL + X");
        buttonCut.setFocusable(false);
        buttonCut.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCut.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonCut);

        buttonCopy.setText("Copy");
        buttonCopy.setToolTipText("CTRL + C");
        buttonCopy.setFocusable(false);
        buttonCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonCopy);

        buttonPaste.setText("Paste");
        buttonPaste.setToolTipText("CTRL + V");
        buttonPaste.setFocusable(false);
        buttonPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonPaste);

        buttonDelete.setText("Delete");
        buttonDelete.setFocusable(false);
        buttonDelete.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonDelete.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonDelete);

        buttonSelectAll.setText("Select All");
        buttonSelectAll.setToolTipText("CTRL + A");
        buttonSelectAll.setFocusable(false);
        buttonSelectAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSelectAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonSelectAll);
        toolbarMain.add(jSeparator3);

        buttonUndo.setText("Undo");
        buttonUndo.setToolTipText("CTRL + Z");
        buttonUndo.setFocusable(false);
        buttonUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonUndo);

        buttonRedo.setText("Redo");
        buttonRedo.setToolTipText("CTRL + R");
        buttonRedo.setFocusable(false);
        buttonRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonRedo);
        toolbarMain.add(jSeparator1);

        buttonSearch.setText("Search");
        buttonSearch.setToolTipText("CTRL + F");
        buttonSearch.setFocusable(false);
        buttonSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        buttonSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarMain.add(buttonSearch);

        mainTabbedPane.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        mainTabbedPane.setFocusable(false);
        mainTabbedPane.setRequestFocusEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbarMain, javax.swing.GroupLayout.DEFAULT_SIZE, 571, Short.MAX_VALUE)
            .addComponent(mainTabbedPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbarMain, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.DEFAULT_SIZE, 421, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonClose;
    private javax.swing.JButton buttonCopy;
    private javax.swing.JButton buttonCut;
    private javax.swing.JButton buttonDelete;
    private javax.swing.JButton buttonPaste;
    private javax.swing.JButton buttonRedo;
    private javax.swing.JButton buttonSave;
    private javax.swing.JButton buttonSearch;
    private javax.swing.JButton buttonSelectAll;
    private javax.swing.JButton buttonUndo;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JTabbedPane mainTabbedPane;
    private javax.swing.JToolBar toolbarMain;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the buttonClose
     */
    public javax.swing.JButton getButtonClose() {
        return buttonClose;
    }

    /**
     * @return the buttonCopy
     */
    public javax.swing.JButton getButtonCopy() {
        return buttonCopy;
    }

    /**
     * @return the buttonCut
     */
    public javax.swing.JButton getButtonCut() {
        return buttonCut;
    }

    /**
     * @return the buttonDelete
     */
    public javax.swing.JButton getButtonDelete() {
        return buttonDelete;
    }

    /**
     * @return the buttonPaste
     */
    public javax.swing.JButton getButtonPaste() {
        return buttonPaste;
    }

    /**
     * @return the buttonRedo
     */
    public javax.swing.JButton getButtonRedo() {
        return buttonRedo;
    }

    /**
     * @return the buttonSave
     */
    public javax.swing.JButton getButtonSave() {
        return buttonSave;
    }

    /**
     * @return the buttonSelectAll
     */
    public javax.swing.JButton getButtonSelectAll() {
        return buttonSelectAll;
    }

    /**
     * @return the buttonUndo
     */
    public javax.swing.JButton getButtonUndo() {
        return buttonUndo;
    }

    /**
     * @return the mainTabbedPane
     */
    public javax.swing.JTabbedPane getMainTabbedPane() {
        return mainTabbedPane;
    }

    /**
     * @return the buttonSearch
     */
    public javax.swing.JButton getButtonSearch() {
        return buttonSearch;
    }
}
