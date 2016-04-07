/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc4380.jhinze;

import com.jcraft.jsch.ChannelSftp;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Font;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.undo.UndoManager;

/**
 *
 * @author lk0
 */
public class TextEditorController {

    private TextEditorAppearanceChooser apChooser;
    private TextEditorClientGUI main;
    private TextEditorClientModel model;
    private TextEditorSearch search;
    private boolean transfering;
    private boolean initdir;
    private Clipboard cp;
    private int fileTag = 0;
    private String home;
    private Color background;
    private Color foreground;
    private Color highlight = Color.yellow;
    private Font font;

    public TextEditorController() {
        cp = Toolkit.getDefaultToolkit().getSystemClipboard();
        main = new TextEditorClientGUI();
        model = new TextEditorClientModel();
        main.getButtonDisconnect().setEnabled(false);
        main.getTextFieldPort().setText("22");
        background = new Color(255, 255, 255);
        foreground = new Color(0, 0, 0);
        font = new Font("Tahoma", Font.PLAIN, 11);
        apChooser = new TextEditorAppearanceChooser(main, true, font, background, foreground, highlight);
        search = new TextEditorSearch(main, false, highlight);
        setLookofTabs(main.getTextEditorPanel().getMainTabbedPane());
        setupListeners();
        setupKeyboardListener();
        setupChangeTabListener();
        updateButtonStates();
        setConnectedMenuItemState(false);
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                main.setVisible(true);
            }
        });
    }

    private void search() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            search.showSearch(doc.getEditorPaneTextArea(), highlight);
        }
    }

    private void changeAppearance() {
        if (search.isVisible()) {
            search.setVisible(false);
        }
        apChooser.prompt();
        if (apChooser.isChanged()) {
            background = apChooser.getBackground();
            foreground = apChooser.getForeground();
            highlight = apChooser.getHighlight();
            font = new Font(apChooser.getChosenFont().getFamily(),
                    apChooser.getChosenFont().getStyle(), apChooser.getChosenFont().getSize());
            int openTabs = main.getTextEditorPanel().getMainTabbedPane().getTabCount();
            for (int i = 0; i < openTabs; i++) {
                TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel()
                        .getMainTabbedPane().getComponentAt(i);
                doc.getEditorPaneTextArea().setBackground(background);
                doc.getEditorPaneTextArea().setForeground(foreground);
                doc.getEditorPaneTextArea().setFont(font);
                doc.getEditorPaneTextArea().setCaretColor(foreground);
                doc.getEditorPaneTextArea().revalidate();
                doc.getEditorPaneTextArea().repaint();
            }
        }
    }

    private void setLookofTabs(JTabbedPane tabPane) {
        tabPane.setUI(new BasicTabbedPaneUI() {

            @Override
            protected void installDefaults() {
                super.installDefaults();
                highlight = Color.lightGray;
                lightHighlight = Color.white;
                shadow = Color.gray;
                darkShadow = Color.darkGray;
                focus = Color.black;
            }
        });
    }

    private void docChanged(TextEditorTextArea doc) {
        if (!doc.isChanged()) {
            doc.setChanged(true);
            main.getTextEditorPanel().getButtonSave().setEnabled(true);
            main.getMenuItemSave().setEnabled(true);
        }
    }

    private void setupChangeTabListener() {
        main.getTextEditorPanel().getMainTabbedPane().addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                updateButtonStates();
                if (search.isVisible()) {
                    search.setVisible(false);
                }
            }

        });
    }

    private void setupChangeListener(final TextEditorTextArea doc) {
        doc.getEditorPaneTextArea().getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                docChanged(doc);
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                docChanged(doc);
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                docChanged(doc);
            }

        });
    }

    private void setupCloseTabListener(final TextEditorTabPanel tabInfo) {
        final Color def = tabInfo.getLabelClose().getBackground();
        tabInfo.getLabelClose().addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                closeTab(tabInfo.getUID());
            }

            @Override
            public void mousePressed(MouseEvent e) {
                tabInfo.getLabelClose().setBackground(Color.DARK_GRAY);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                tabInfo.getLabelClose().setBackground(def);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //do not care
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //do not care
            }

        });
    }

    private void close() {
        int index = main.getTextEditorPanel().getMainTabbedPane().getSelectedIndex();
        if (index > -1) {
            checkAndCloseTab(index);
        }
    }

    private void closeTab(String uid) {
        checkAndCloseTab(main.getTextEditorPanel().getMainTabbedPane().indexOfTab(uid));
    }

    private boolean checkAndCloseTab(int index) {
        if (index > -1) {
            TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel().getMainTabbedPane().getComponentAt(index);
            if (doc.isChanged()) {
                int response;
                if (doc.getFile() == null) {
                    response = JOptionPane.showConfirmDialog(main, "Save " + "new" + " before closing?", "Save File?",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                } else {
                    response = JOptionPane.showConfirmDialog(main, "Save " + doc.getFile().toString() + " before closing?", "Save File?",
                            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                }
                if (response == JOptionPane.CANCEL_OPTION) {
                    return false;
                }
                if (response == JOptionPane.YES_OPTION) {
                    main.getTextEditorPanel().getMainTabbedPane().setSelectedIndex(index);
                    saveFile();
                    main.getTextEditorPanel().getMainTabbedPane().remove(index);
                    return true;
                }
                if (response == JOptionPane.NO_OPTION) {
                    main.getTextEditorPanel().getMainTabbedPane().remove(index);
                    return true;
                }
            } else {
                main.getTextEditorPanel().getMainTabbedPane().remove(index);
            }
        }
        return true;
    }

    private void backSpace() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            int start = doc.getEditorPaneTextArea().getSelectionStart();
            int end = doc.getEditorPaneTextArea().getSelectionEnd();
            try {
                if (end - start > 0) {
                    doc.getEditorPaneTextArea().getDocument().remove(start, end - start);
                } else {
                    if (start > 0) {
                        doc.getEditorPaneTextArea().getDocument().remove(start - 1, 1);
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private void copy() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            StringSelection selected = new StringSelection(
                    doc.getEditorPaneTextArea().getSelectedText());
            cp.setContents(selected, null);
        }
    }

    private void cut() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            copy();
            delete();
        }
    }

    private void delete() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            int start = doc.getEditorPaneTextArea().getSelectionStart();
            int end = doc.getEditorPaneTextArea().getSelectionEnd();
            try {
                if (end - start > 0) {
                    doc.getEditorPaneTextArea().getDocument().remove(start, end - start);
                } else {
                    if (start < doc.getEditorPaneTextArea().getDocument().getLength()) {
                        doc.getEditorPaneTextArea().getDocument().remove(start, 1);
                    }
                }
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void paste() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            try {
                int start = doc.getEditorPaneTextArea().getSelectionStart();
                int end = doc.getEditorPaneTextArea().getSelectionEnd();
                if (end - start > 0) {
                    doc.getEditorPaneTextArea().getDocument().remove(start, end - start);
                }
                doc.getEditorPaneTextArea().getDocument().insertString(
                        doc.getEditorPaneTextArea().getCaretPosition(),
                        (String) cp.getData(DataFlavor.stringFlavor), null);
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (UnsupportedFlavorException ex) {
                Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(TextEditorController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void selectAll() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            doc.getEditorPaneTextArea().selectAll();
        }
    }

    private void undo() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null && doc.getOops().canUndo()) {
            doc.getOops().undo();
            updateUndoRedoButtons(doc);
        }
    }

    private void redo() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null && doc.getOops().canRedo()) {
            doc.getOops().redo();
            updateUndoRedoButtons(doc);
        }
    }

    private TextEditorTextArea getCurrentTextEditorTab() {
        int index = main.getTextEditorPanel().getMainTabbedPane().getSelectedIndex();
        if (index > -1) {
            return (TextEditorTextArea) main.getTextEditorPanel().getMainTabbedPane().getComponentAt(index);
        }
        return null;
    }

    private void updateButtonStates() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        updateUndoRedoButtons(doc);
        if (doc != null) {
            if (doc.isChanged()) {
                setSaveButtons(true);
            } else {
                setSaveButtons(false);
            }
            setEditButtons(true);
        } else {
            setSaveButtons(false);
            setEditButtons(false);
        }
    }

    private void setSaveButtons(boolean b) {
        main.getMenuItemSave().setEnabled(b);
        main.getTextEditorPanel().getButtonSave().setEnabled(b);
    }

    private void setEditButtons(boolean b) {
        main.getTextEditorPanel().getButtonCut().setEnabled(b);
        main.getTextEditorPanel().getButtonCopy().setEnabled(b);
        main.getTextEditorPanel().getButtonPaste().setEnabled(b);
        main.getTextEditorPanel().getButtonDelete().setEnabled(b);
        main.getTextEditorPanel().getButtonSelectAll().setEnabled(b);
        main.getTextEditorPanel().getButtonSearch().setEnabled(b);
        main.getMenuItemCut().setEnabled(b);
        main.getMenuItemCopy().setEnabled(b);
        main.getMenuItemPaste().setEnabled(b);
        main.getMenuItemDelete().setEnabled(b);
        main.getMenuItemSelectAll().setEnabled(b);
        main.getMenuItemClose().setEnabled(b);
        main.getMenuItemSearch().setEnabled(b);
        main.getTextEditorPanel().getButtonClose().setEnabled(b);
    }

    private void updateUndoRedoButtons(TextEditorTextArea doc) {
        if (doc != null) {
            if (!doc.getOops().canUndo()) {
                main.getMenuItemUndo().setEnabled(false);
                main.getTextEditorPanel().getButtonUndo().setEnabled(false);
                doc.getMenuItemUndo().setEnabled(false);
            } else {
                main.getMenuItemUndo().setEnabled(true);
                main.getTextEditorPanel().getButtonUndo().setEnabled(true);
                doc.getMenuItemUndo().setEnabled(true);
            }
            if (!doc.getOops().canRedo()) {
                main.getMenuItemRedo().setEnabled(false);
                main.getTextEditorPanel().getButtonRedo().setEnabled(false);
                doc.getMenuItemRedo().setEnabled(false);
            } else {
                main.getMenuItemRedo().setEnabled(true);
                main.getTextEditorPanel().getButtonRedo().setEnabled(true);
                doc.getMenuItemRedo().setEnabled(true);
            }
        } else {
            main.getMenuItemUndo().setEnabled(false);
            main.getTextEditorPanel().getButtonUndo().setEnabled(false);
            main.getMenuItemRedo().setEnabled(false);
            main.getTextEditorPanel().getButtonRedo().setEnabled(false);
        }
    }

    private boolean currentDocHasFocus() {
        TextEditorTextArea doc = getCurrentTextEditorTab();
        if (doc != null) {
            return doc.getEditorPaneTextArea().hasFocus();
        }
        return false;
    }

    private void setupKeyboardListener() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {

            @Override
            public boolean dispatchKeyEvent(java.awt.event.KeyEvent e) {
                if (currentDocHasFocus() && e.getID() == java.awt.event.KeyEvent.KEY_PRESSED) {

                    if (e.getKeyCode() == KeyEvent.VK_A && e.isControlDown()) {
                        selectAll();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_F && e.isControlDown()) {
                        search();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_BACKSPACE) {
                        if (e.isControlDown()) {
                            close();
                        } else {
                            backSpace();
                        }
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()) {
                        copy();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_X && e.isControlDown()) {
                        cut();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()) {
                        paste();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_DELETE) {
                        delete();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_Z && e.isControlDown()) {
                        undo();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_R && e.isControlDown()) {
                        redo();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_S && e.isControlDown()) {
                        saveFile();
                        e.consume();
                        return true;
                    }

                    if (e.getKeyCode() == KeyEvent.VK_F1) {
                        about();
                        e.consume();
                        return true;
                    }

                }
                return false;
            }
        });
    }

    private void setupCaretListener(final TextEditorTextArea doc) {

        doc.getEditorPaneTextArea().addCaretListener(new CaretListener() {

            @Override
            public void caretUpdate(CaretEvent e) {
                int caretPos = doc.getEditorPaneTextArea().getCaretPosition();
                int rowNum = (caretPos == 0) ? 1 : 0;
                for (int offset = caretPos; offset > 0;) {
                    try {
                        offset = Utilities.getRowStart(doc.getEditorPaneTextArea(), offset) - 1;
                    } catch (BadLocationException ex) {
                        Logger.getLogger(TextEditorController.class
                                .getName()).log(Level.SEVERE, null, ex);
                    }
                    rowNum++;
                }
                doc.getLabelLineNumber().setText("Line: " + rowNum);
            }
        });
    }

    private void setupPopupMenuListeners(TextEditorTextArea doc) {
        doc.getEditorPaneTextArea().setComponentPopupMenu(doc.getPopupMenu());

        doc.getMenuItemSearch().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }

        });

        doc.getMenuItemCopy().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }

        });

        doc.getMenuItemCut().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cut();
            }

        });

        doc.getMenuItemDelete().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }

        });

        doc.getMenuItemPaste().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }

        });

        doc.getMenuItemRedo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }

        });

        doc.getMenuItemSelectAll().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }

        });

        doc.getMenuItemUndo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }

        });
    }

    private void setupUndoListener(final TextEditorTextArea doc) {
        doc.getEditorPaneTextArea().getDocument().addUndoableEditListener(new UndoableEditListener() {

            @Override
            public void undoableEditHappened(UndoableEditEvent e) {
                doc.getOops().addEdit(e.getEdit());
                updateUndoRedoButtons(doc);
            }
        });
    }

    private void setupListeners() {
        main.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }

            @Override
            public void windowClosed(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void windowIconified(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void windowDeiconified(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void windowActivated(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void windowDeactivated(WindowEvent e) {
                //I currently do not care if and when this happens.
            }

        });

        main.getPasswordFieldPassword().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                main.getButtonConnect().doClick();
            }

        });

        main.getMenuItemTreeDelete().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteNode();
            }

        });

        main.getMenuItemFileDelete().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                deleteNode();
            }

        });

        main.getMenuItemAppearance().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeAppearance();
            }

        });

        main.getMenuItemTreeNew().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }

        });

        main.getMenuItemSearch().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }

        });

        main.getMenuItemRename().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                rename();
            }

        });

        main.getMenuItemTreeRename().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                rename();
            }

        });

        main.getMenuItemTreeNewDirectory().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newDir();
            }

        });

        main.getMenuItemNewDirectory().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newDir();
            }

        });

        main.getMenuItemNewFile().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                newFile();
            }

        });

        main.getMenuItemHome().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }

        });

        main.getMenuItemTreeHome().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                goHome();
            }

        });

        main.getMenuItemTreeOpen().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }

        });

        main.getMenuItemTreeUpload().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }

        });

        main.getMenuItemTreeDownload().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                downloadFile();
            }

        });

        main.getTreeFileStructure().addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    TreePath path = main.getTreeFileStructure().getPathForLocation(e.getX(), e.getY());
                    Rectangle bounds = main.getTreeFileStructure().getPathBounds(path);
                    if (bounds != null
                            && bounds.contains(e.getX(), e.getY())
                            && ((DefaultMutableTreeNode) path.getLastPathComponent()).isLeaf()) {
                        openFile();
                    }
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    TreePath path = main.getTreeFileStructure().getPathForLocation(e.getX(), e.getY());
                    Rectangle bounds = main.getTreeFileStructure().getPathBounds(path);
                    main.getTreeFileStructure().setSelectionPath(path);
                    if (bounds != null
                            && bounds.contains(e.getX(), e.getY())) {
                        main.getPopupMenuFileTree().show(main.getTreeFileStructure(), e.getX(), e.getY());
                    }
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //I currently do not care if and when this happens.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //I currently do not care if and when this happens.
            }

        });

        main.getTreeFileStructure().addTreeWillExpandListener(new TreeWillExpandListener() {

            @Override
            public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
                willExpand(event);
            }

            @Override
            public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
                willCollapse(event);
            }

        });

        main.getButtonConnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                connect();
            }
        });

        main.getButtonDisconnect().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                disconnect();
            }
        });

        main.getMenuItemUpload().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                uploadFile();
            }
        });

        main.getMenuItemDownload().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                downloadFile();
            }
        });

        main.getMenuItemExit().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exit();
            }
        });

        main.getMenuItemAbout().addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                about();
            }
        });

        main.getMenuItemClose().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }

        });

        main.getMenuItemOpen().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                openFile();
            }

        });

        main.getMenuItemSave().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }

        });

        main.getMenuItemUndo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }

        });

        main.getMenuItemSelectAll().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }

        });

        main.getMenuItemRedo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }

        });

        main.getMenuItemPaste().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }

        });

        main.getMenuItemDelete().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }

        });

        main.getMenuItemCut().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cut();
            }

        });

        main.getMenuItemCopy().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }

        });

        main.getTextEditorPanel().getButtonSave().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveFile();
            }

        });

        main.getTextEditorPanel().getButtonSearch().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                search();
            }

        });

        main.getTextEditorPanel().getButtonClose().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                close();
            }

        });

        main.getTextEditorPanel().getButtonCut().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cut();
            }

        });

        main.getTextEditorPanel().getButtonCopy().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                copy();
            }

        });

        main.getTextEditorPanel().getButtonPaste().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }

        });

        main.getTextEditorPanel().getButtonDelete().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delete();
            }

        });

        main.getTextEditorPanel().getButtonSelectAll().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectAll();
            }

        });

        main.getTextEditorPanel().getButtonUndo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                undo();
            }

        });

        main.getTextEditorPanel().getButtonRedo().addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                redo();
            }

        });
    }

    private void setConnectedMenuItemState(boolean b) {
        main.getMenuItemTreeUpload().setEnabled(b);
        main.getMenuItemTreeDownload().setEnabled(b);
        main.getMenuItemTreeDelete().setEnabled(b);
        main.getMenuItemTreeNew().setEnabled(b);
        main.getMenuItemTreeOpen().setEnabled(b);
        main.getMenuItemUpload().setEnabled(b);
        main.getMenuItemDownload().setEnabled(b);
        main.getMenuItemOpen().setEnabled(b);
        main.getMenuItemFileDelete().setEnabled(b);
        main.getMenuItemNew().setEnabled(b);
        main.getMenuItemNewDirectory().setEnabled(b);
        main.getMenuItemTreeNewDirectory().setEnabled(b);
        main.getMenuItemRename().setEnabled(b);
        main.getMenuItemTreeRename().setEnabled(b);
        main.getMenuItemHome().setEnabled(b);
        main.getMenuItemTreeHome().setEnabled(b);
    }

    private void connect() {
        if (!model.isConnected()) {
            final int port = parsePortField();
            if (port > 0) {
                updateStatus("Connecting...");
                main.getButtonConnect().setEnabled(false);
                final String host = main.getTextFieldHost().getText();
                final String user = main.getTextFieldUserName().getText();
                final String pass = new String(main.getPasswordFieldPassword().getPassword());
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        return model.connect(host, port, user, pass);
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get()) {
                                updateStatus("Connected");
                                main.getButtonDisconnect().setEnabled(true);
                                setInitialDirectory();
                                setConnectedMenuItemState(true);
                            } else {
                                JOptionPane.showMessageDialog(main,
                                        "Connection error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                                main.getButtonConnect().setEnabled(true);
                                updateStatus("Not Connected");
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }                        
                    }
                };
                worker.execute();
            } else {
                JOptionPane.showMessageDialog(main,
                        "Invalid port number",
                        "Error", JOptionPane.PLAIN_MESSAGE);
            }
        }
    }

    private void disconnect() {
        while (main.getTextEditorPanel().getMainTabbedPane().getComponentCount() > 1) {
            if (!checkAndCloseTab(main.getTextEditorPanel().getMainTabbedPane().getSelectedIndex())) {
                return;
            }
        }
        updateStatus("disconnecting..");
        SwingWorker worker = new SwingWorker<Boolean, Object>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return model.disconnect();
            }

            @Override
            protected void done() {
                try {
                    if (get()) {
                        updateStatus("Not Connected");
                        main.getTreeRoot().removeAllChildren();
                        main.getTreeRoot().setUserObject("..");
                        main.getTreeModel().nodeStructureChanged(main.getTreeRoot());
                        main.getButtonDisconnect().setEnabled(false);
                        main.getButtonConnect().setEnabled(true);
                        setConnectedMenuItemState(false);
                    } else {
                        updateStatus("Disconnect Error");
                    }
                } catch (InterruptedException ex) {
                    //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };
        worker.execute();
    }

    private void uploadFile() {
        if (!transfering) {
            final DefaultMutableTreeNode node;
            DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (!lastNode.toString().equals("..")) {
                if (lastNode.isLeaf()) {
                    node = (DefaultMutableTreeNode) lastNode.getParent();
                } else {
                    node = lastNode;
                }
                final File file;
                main.getFileChooser().setSelectedFile(new File(""));
                main.getFileChooser().showOpenDialog(main);
                file = main.getFileChooser().getSelectedFile();
                if (!file.exists() || file.isDirectory()) {
                    return;
                }
                int childrens = node.getChildCount();
                for (int i = 0; i < childrens; i++) {
                    DefaultMutableTreeNode kin = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (kin.isLeaf() && kin.toString().equals(file.getName())) {
                        JOptionPane.showMessageDialog(main,
                                "File Name Already Exists",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                transfering = true;
                main.getTreeFileStructure().setToggleClickCount(0);
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (model.isConnected()) {
                            boolean b = model.uploadFile(file, getAbsolutePath(node), main.getLabelStatus());
                            return b;
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!get()) {
                                JOptionPane.showMessageDialog(main,
                                        "File Upload Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                            } else {
                                node.add(new DefaultMutableTreeNode(file.getName()));
                                main.getTreeModel().nodeStructureChanged(node);
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        transfering = false;
                        main.getTreeFileStructure().setToggleClickCount(2);
                    }
                };
                updateStatus("Connected");
                worker.execute();
            }
        }
    }

    private void downloadFile() {
        if (!transfering) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (node.isLeaf() && !node.toString().equals("..")) {
                transfering = true;
                main.getTreeFileStructure().setToggleClickCount(0);
                final File file;
                main.getFileChooser().setSelectedFile(new File(node.toString()));
                main.getFileChooser().showSaveDialog(main);
                file = main.getFileChooser().getSelectedFile();
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (model.isConnected()) {
                            return model.downloadFile(getAbsolutePath(node), file, main.getLabelStatus());
                        } else {
                            return false;
                        }
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!get()) {
                                JOptionPane.showMessageDialog(main,
                                        "File Download Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        updateStatus("Connected");
                        main.getTreeFileStructure().setToggleClickCount(2);
                        transfering = false;
                    }
                };
                worker.execute();
            }
        }
    }

    private void saveFile() {
        final TextEditorTextArea doc = getCurrentTextEditorTab();
        if (!transfering && doc != null) {
            updateStatus("Saving...");

            SwingWorker worker = new SwingWorker<Boolean, Object>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (model.isConnected()) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) doc.getFile().getParent();
                        boolean b = model.saveFile(
                                doc.getEditorPaneTextArea().getDocument().getText(0,
                                        doc.getEditorPaneTextArea().getDocument().getLength()), getAbsolutePath(node) + "/" + doc.getFile().toString());
                        return b;
                    } else {
                        return false;
                    }
                }

                @Override
                protected void done() {
                    try {
                        if (!get()) {
                            JOptionPane.showMessageDialog(main,
                                    "File Save Error",
                                    "Error", JOptionPane.PLAIN_MESSAGE);
                        }
                    } catch (InterruptedException ex) {
                        //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ExecutionException ex) {
                        //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    doc.setChanged(false);
                    main.getTextEditorPanel().getButtonSave().setEnabled(false);
                    main.getMenuItemSave().setEnabled(false);
                    updateStatus("Connected");
                }

            };
            worker.execute();
        }
    }

    private void openFile() {
        if (!transfering) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (node.isLeaf() && !node.toString().equals("..")) {
                updateStatus("Opening...");
                int openTabs = main.getTextEditorPanel().getMainTabbedPane().getTabCount();
                for (int i = 0; i < openTabs; i++) {
                    TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel()
                            .getMainTabbedPane().getComponentAt(i);
                    if (doc.getFile() == node) {
                        main.getTextEditorPanel().getMainTabbedPane().setSelectedIndex(i);
                        updateStatus("Connected");
                        return;
                    }
                }
                SwingWorker worker = new SwingWorker<String, Object>() {
                    @Override
                    protected String doInBackground() throws Exception {
                        if (model.isConnected()) {
                            return model.openFile(getAbsolutePath(node));
                        }
                        return null;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (get() == null) {
                                JOptionPane.showMessageDialog(main,
                                        "File Open Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                                updateStatus("Connected");
                                return;
                            } else {
                                TextEditorTextArea doc = new TextEditorTextArea();
                                doc.getEditorPaneTextArea().setBackground(background);
                                doc.getEditorPaneTextArea().setForeground(foreground);
                                doc.getEditorPaneTextArea().setCaretColor(foreground);
                                doc.getEditorPaneTextArea().setFont(font);
                                doc.setFile(node);
                                TextEditorTabPanel tabInfo = new TextEditorTabPanel(node.toString(), fileTag);
                                doc.getLabelFileName().setText("File: " + getAbsolutePath(node));
                                doc.setOops(new UndoManager());
                                doc.getEditorPaneTextArea().getDocument().insertString(0, get(), null);
                                doc.getLabelLineNumber().setText("Line: 1");
                                doc.getEditorPaneTextArea().setCaretPosition(0);
                                main.getTextEditorPanel().getMainTabbedPane().add(doc);
                                main.getTextEditorPanel().getMainTabbedPane().addTab(tabInfo.getUID(), doc);
                                main.getTextEditorPanel().getMainTabbedPane().setTabComponentAt(
                                        main.getTextEditorPanel().getMainTabbedPane().indexOfTab(tabInfo.getUID()), tabInfo);
                                main.getTextEditorPanel().getMainTabbedPane().setSelectedIndex(main.getTextEditorPanel()
                                        .getMainTabbedPane().indexOfTab(tabInfo.getUID()));
                                setupCloseTabListener(tabInfo);
                                setupCaretListener(doc);
                                setupUndoListener(doc);
                                setupPopupMenuListeners(doc);
                                setupChangeListener(doc);
                                doc.getEditorPaneTextArea().requestFocus();
                                fileTag++;
                            }

                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (BadLocationException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        updateStatus("Connected");
                    }
                };
                worker.execute();
            }
        }
    }

    private void newDir() {
        if (!transfering) {
            final DefaultMutableTreeNode node;
            DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (lastNode.isLeaf()) {
                node = (DefaultMutableTreeNode) lastNode.getParent();
            } else {
                node = lastNode;
            }
            if (node != null) {
                final String dirName = JOptionPane.showInputDialog(main, "Directory Name");
                if (dirName == null) {
                    return;
                }
                int childrens = node.getChildCount();
                for (int i = 0; i < childrens; i++) {
                    DefaultMutableTreeNode kin = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (!kin.isLeaf() && kin.toString().equals(dirName)) {
                        JOptionPane.showMessageDialog(main,
                                "Directory Name Already Exists",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                updateStatus("Making dir..");
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (model.isConnected()) {
                            boolean b = model.mkdir(getAbsolutePath(node) + "/" + dirName);
                            return b;
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!get()) {
                                JOptionPane.showMessageDialog(main,
                                        "Directory Creation Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                                updateStatus("Connected");
                                return;
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        DefaultMutableTreeNode newDir = new DefaultMutableTreeNode(dirName);
                        node.add(newDir);
                        newDir.add(new DefaultMutableTreeNode(".."));
                        main.getTreeModel().nodeStructureChanged(node);
                        main.getTreeFileStructure().setSelectionPath(new TreePath(newDir.getPath()));
                        updateStatus("Connected");
                    }
                };
                worker.execute();
            }
        }
    }

    private void newFile() {
        if (!transfering) {
            final DefaultMutableTreeNode node;
            DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (lastNode.isLeaf()) {
                node = (DefaultMutableTreeNode) lastNode.getParent();
            } else {
                node = lastNode;
            }
            if (node != null) {
                final String fileName = JOptionPane.showInputDialog(main, "File Name");
                if (fileName == null) {
                    return;
                }
                int childrens = node.getChildCount();
                for (int i = 0; i < childrens; i++) {
                    DefaultMutableTreeNode kin = (DefaultMutableTreeNode) node.getChildAt(i);
                    if (kin.isLeaf() && kin.toString().equals(fileName)) {
                        JOptionPane.showMessageDialog(main,
                                "File Name Already Exists",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                updateStatus("Touching..");
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (model.isConnected()) {
                            boolean b = model.touchFile(getAbsolutePath(node) + "/" + fileName);
                            return b;
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!get()) {
                                JOptionPane.showMessageDialog(main,
                                        "File Creation Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                                updateStatus("Connected");
                                return;
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(fileName);
                        node.add(newNode);
                        main.getTreeModel().nodeStructureChanged(node);
                        main.getTreeFileStructure().setSelectionPath(new TreePath(newNode.getPath()));
                        updateStatus("Connected");
                    }
                };
                worker.execute();
            }
        }
    }

    private void deleteFile() {
        if (!transfering) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (node.isLeaf() && !node.toString().equals("..")) {
                int response = JOptionPane.showConfirmDialog(main, "Delete " + node.toString() + "?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    updateStatus("Deleting..");
                    SwingWorker worker = new SwingWorker<Boolean, Object>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            if (model.isConnected()) {
                                DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
                                boolean b = model.rm(getAbsolutePath(nodeParent) + "/" + node.toString());
                                return b;
                            }
                            return false;
                        }

                        @Override
                        protected void done() {
                            try {
                                if (!get()) {
                                    JOptionPane.showMessageDialog(main,
                                            "File Delete Error",
                                            "Error", JOptionPane.PLAIN_MESSAGE);
                                    updateStatus("Connected");
                                    return;
                                } else {
                                    DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
                                    nodeParent.remove(node);
                                    if (nodeParent.getChildCount() == 0) {
                                        nodeParent.add(new DefaultMutableTreeNode(".."));
                                    }
                                    main.getTreeModel().nodeStructureChanged(nodeParent);
                                    int openTabs = main.getTextEditorPanel().getMainTabbedPane().getTabCount();
                                    for (int i = 0; i < openTabs; i++) {
                                        TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel()
                                                .getMainTabbedPane().getComponentAt(i);
                                        if (doc.getFile() == node) {
                                            main.getTextEditorPanel().getMainTabbedPane().remove(i);
                                            break;
                                        }
                                    }
                                    updateStatus("Connected");
                                }
                            } catch (InterruptedException ex) {
                                //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                }
            }
        }
    }

    private void rename() {
        if (!transfering) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            final DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
            if (!node.toString().equals("..") && !node.isRoot()) {
                int openTabs = main.getTextEditorPanel().getMainTabbedPane().getTabCount();
                for (int i = 0; i < openTabs; i++) {
                    TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel()
                            .getMainTabbedPane().getComponentAt(i);
                    if (doc.getFile() == node) {
                        JOptionPane.showMessageDialog(main,
                                "Can Not Rename Open File, Close First.",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                final String newName = JOptionPane.showInputDialog(main, "Rename " + node.toString() + " to?");
                if (newName == null) {
                    return;
                }
                int childrens = nodeParent.getChildCount();
                for (int i = 0; i < childrens; i++) {
                    DefaultMutableTreeNode kin = (DefaultMutableTreeNode) nodeParent.getChildAt(i);

                    if (node.isLeaf() && kin.isLeaf() && newName.equals(kin.toString())) {
                        JOptionPane.showMessageDialog(main,
                                "File Name Already Exists",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                    if (!node.isLeaf() && !kin.isLeaf() && newName.equals(kin.toString())) {
                        JOptionPane.showMessageDialog(main,
                                "Directory Name Already Exists",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                }
                updateStatus("Renaming..");
                SwingWorker worker = new SwingWorker<Boolean, Object>() {
                    @Override
                    protected Boolean doInBackground() throws Exception {
                        if (model.isConnected()) {
                            boolean b = model.rename(getAbsolutePath(nodeParent) + "/" + node.toString(),
                                    getAbsolutePath(nodeParent) + "/" + newName);
                            return b;
                        }
                        return false;
                    }

                    @Override
                    protected void done() {
                        try {
                            if (!get()) {
                                JOptionPane.showMessageDialog(main,
                                        "File Rename Error",
                                        "Error", JOptionPane.PLAIN_MESSAGE);
                                updateStatus("Connected");
                                return;
                            } else {
                                int index = nodeParent.getIndex(node);
                                DefaultMutableTreeNode renamedNode = new DefaultMutableTreeNode(newName);
                                Enumeration kids = node.children();
                                while (kids.hasMoreElements()) {
                                    renamedNode.add((DefaultMutableTreeNode) kids.nextElement());
                                }
                                nodeParent.remove(node);
                                nodeParent.insert(renamedNode, index);
                                main.getTreeModel().nodeStructureChanged(nodeParent);
                                main.getTreeFileStructure().setSelectionPath(new TreePath(renamedNode.getPath()));
                                updateStatus("Connected");
                            }
                        } catch (InterruptedException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (ExecutionException ex) {
                            //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                };
                worker.execute();
            }
        }
    }

    private void deleteDir() {
        if (!transfering) {
            final DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (!node.isLeaf()) {
                int response = JOptionPane.showConfirmDialog(main, "Delete Directory " + node.toString() + "?", "Confirm Delete",
                        JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (response == JOptionPane.YES_OPTION) {
                    int childrens = node.getChildCount();
                    if (childrens > 1 || (childrens == 1 && !node.getChildAt(0).toString().equals(".."))) {
                        JOptionPane.showMessageDialog(main,
                                "Directory is not empty",
                                "Error", JOptionPane.PLAIN_MESSAGE);
                        return;
                    }
                    updateStatus("Deleting..");
                    SwingWorker worker = new SwingWorker<Boolean, Object>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            if (model.isConnected()) {
                                DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
                                boolean b = model.rmdir(getAbsolutePath(nodeParent) + "/" + node.toString());
                                return b;
                            }
                            return false;
                        }

                        @Override
                        protected void done() {
                            try {
                                if (!get()) {
                                    JOptionPane.showMessageDialog(main,
                                            "Directory Delete Error",
                                            "Error", JOptionPane.PLAIN_MESSAGE);
                                    updateStatus("Connected");
                                    return;
                                } else {
                                    DefaultMutableTreeNode nodeParent = (DefaultMutableTreeNode) node.getParent();
                                    nodeParent.remove(node);
                                    if (nodeParent.getChildCount() == 0) {
                                        nodeParent.add(new DefaultMutableTreeNode(".."));
                                    }
                                    main.getTreeModel().nodeStructureChanged(nodeParent);
                                    int openTabs = main.getTextEditorPanel().getMainTabbedPane().getTabCount();
                                    for (int i = 0; i < openTabs; i++) {
                                        TextEditorTextArea doc = (TextEditorTextArea) main.getTextEditorPanel()
                                                .getMainTabbedPane().getComponentAt(i);
                                        if (doc.getFile() == node) {
                                            main.getTextEditorPanel().getMainTabbedPane().remove(i);
                                            break;
                                        }
                                    }
                                    updateStatus("Connected");
                                }
                            } catch (InterruptedException ex) {
                                //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ExecutionException ex) {
                                //Logger.getLogger(TextEditorClientController.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    };
                    worker.execute();
                }
            }
        }
    }

    private void deleteNode() {
        if (!transfering) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) main.getTreeFileStructure().getLastSelectedPathComponent();
            if (node.isLeaf()) {
                deleteFile();
            }
            if (!node.isLeaf()) {
                deleteDir();
            }
        }
    }

    private void exit() {
        if (model.isConnected()) {
            int response = JOptionPane.showConfirmDialog(main, "Close Connection and Exit?", "Connection is Active",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (response == JOptionPane.NO_OPTION) {
                return;
            }
        }
        disconnect();
        synchronized (model) {
            main.dispose();
        }
    }

    private void about() {
        JOptionPane.showMessageDialog(main,
                "Text Editor Client\nimplemented by Justin Hinze\nprototype version."
                + "\n**Security keys are disabled, this program should not be used beyond test of concept**",
                "About", JOptionPane.PLAIN_MESSAGE);
    }

    private int parsePortField() {
        try {
            return Integer.parseInt(main.getTextFieldPort().getText());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void expand(String path, DefaultMutableTreeNode parent) {
        Vector<ChannelSftp.LsEntry> list = model.ls(path); // List source directory structure.
        Vector<ChannelSftp.LsEntry> dirs = new Vector<>();
        Vector<ChannelSftp.LsEntry> files = new Vector<>();
        parent.removeAllChildren();
        for (ChannelSftp.LsEntry item : list) {
            if (item.getFilename().equals("..") || item.getFilename().equals(".")) {
                continue;
            }
            if (item.getAttrs().isDir()) {
                dirs.add(item);
            } else {
                files.add(item);
            }
        }
        if (files.isEmpty() && dirs.isEmpty()) {
            parent.add(new DefaultMutableTreeNode(".."));
            return;
        }

        Collections.sort(dirs, new Comparator<ChannelSftp.LsEntry>() {

            @Override
            public int compare(ChannelSftp.LsEntry o1, ChannelSftp.LsEntry o2) {
                return o1.getFilename().compareToIgnoreCase(o2.getFilename());
            }

        });
        Collections.sort(files, new Comparator<ChannelSftp.LsEntry>() {

            @Override
            public int compare(ChannelSftp.LsEntry o1, ChannelSftp.LsEntry o2) {
                return o1.getFilename().compareToIgnoreCase(o2.getFilename());
            }

        });
        for (ChannelSftp.LsEntry item : dirs) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(item.getFilename());
            parent.add(node);
            node.add(new DefaultMutableTreeNode(".."));
        }
        for (ChannelSftp.LsEntry item : files) {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(item.getFilename());
            parent.add(node);
        }
        main.getTreeModel().nodeStructureChanged(parent);
    }

    private void setInitialDirectory() {
        if (model.isConnected()) {
            main.getTreeRoot().setUserObject(new DefaultMutableTreeNode("/"));
            home = model.pwd();
            String dirs[] = home.split("/");
            final ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (String s : dirs) {
                if (s.length() > 0) {
                    nodes.add(new DefaultMutableTreeNode(s));
                }
            }
            if (nodes.size() > 1) {
                main.getTreeRoot().add(nodes.get(0));
                for (int i = 1; i < nodes.size(); i++) {
                    nodes.get(i - 1).add(nodes.get(i));
                }

            } else {
                if (nodes.size() > 0) {
                    main.getTreeRoot().add(nodes.get(0));
                }
            }
            expand(getAbsolutePath(nodes.get(nodes.size() - 1)), nodes.get(nodes.size() - 1));
            initdir = false;
            expandToDir(nodes.get(nodes.size() - 1));
            initdir = true;
        }
    }

    private void expandToDir(DefaultMutableTreeNode node) {
        if (!node.isRoot()) {
            expandToDir((DefaultMutableTreeNode) node.getParent());
        }
        main.getTreeFileStructure().expandPath(new TreePath(node.getPath()));
    }

    private void goHome() {
        if (!transfering && model.isConnected()) {
            main.getTreeRoot().removeAllChildren();
            main.getTreeModel().nodeStructureChanged(main.getTreeRoot());
            String dirs[] = home.split("/");
            final ArrayList<DefaultMutableTreeNode> nodes = new ArrayList<>();
            for (String s : dirs) {
                if (s.length() > 0) {
                    nodes.add(new DefaultMutableTreeNode(s));
                }
            }
            if (nodes.size() > 1) {
                main.getTreeRoot().add(nodes.get(0));
                for (int i = 1; i < nodes.size(); i++) {
                    nodes.get(i - 1).add(nodes.get(i));
                }

            } else {
                if (nodes.size() > 0) {
                    main.getTreeRoot().add(nodes.get(0));
                }
            }
            expand(getAbsolutePath(nodes.get(nodes.size() - 1)), nodes.get(nodes.size() - 1));
            initdir = false;
            expandToDir(nodes.get(nodes.size() - 1));
            initdir = true;
        }
    }

    private String getAbsolutePath(DefaultMutableTreeNode node) {
        if (node.getParent() == null) {
            return "";
        } else {
            return "" + getAbsolutePath((DefaultMutableTreeNode) node.getParent()) + "/" + node.toString();
        }
    }

    private void willExpand(TreeExpansionEvent event) throws ExpandVetoException {
        if (!transfering && initdir && model.isConnected()) {
            String dir;
            DefaultMutableTreeNode node
                    = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
            if (node.isRoot()) {
                dir = "/";

            } else {
                dir = getAbsolutePath(node);
            }
            Vector ls = model.ls(dir);
            if (ls != null) {
                expand(dir, node);
            } else {
                JOptionPane.showMessageDialog(main,
                        "Unable To Open directory",
                        "Error", JOptionPane.PLAIN_MESSAGE);
                throw new ExpandVetoException(event);
            }
        }
    }

    private void willCollapse(TreeExpansionEvent event) {
        if (!transfering && initdir && model.isConnected()) {
            DefaultMutableTreeNode node
                    = (DefaultMutableTreeNode) event.getPath().getLastPathComponent();
            node.removeAllChildren();
            node.add(new DefaultMutableTreeNode(".."));
            main.getTreeModel().nodeStructureChanged(node);
        }
    }

    private void updateStatus(String s) {
        main.getLabelStatus().setText(s);
    }

    public static void main(String args[]) {
        new TextEditorController();
    }
}
