/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csc4380.jhinze;

import java.awt.Color;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JColorChooser;
import javax.swing.JDialog;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

/**
 *
 * @author lk0
 */
public class TextEditorAppearanceChooser extends JDialog {

    private int fontSize;
    private int fontStyle;
    private Font font;
    private Color background;
    private Color foreground;
    private Color highlight;
    private int def_fontSize;
    private int def_fontStyle;
    private Font def_font;
    private Color def_background;
    private Color def_foreground;
    private Color def_highlight;
    private Font tmp_font;
    private int tmp_fontSize;
    private int tmp_fontStyle;
    private Integer fontSizes[] = {8, 9, 10, 11, 12, 14, 16, 18, 20, 22, 24, 26, 28, 36, 48, 72};
    private HashMap<Integer, String> fontStyles = new HashMap<Integer, String>() {
        {
            put(Font.PLAIN, "Plain");
            put(Font.BOLD, "Bold");
            put(Font.ITALIC, "Italic");
            put(Font.BOLD + Font.ITALIC, "Bold Italic");
        }
    };
    private String fonts[];
    private boolean changed = false;
    private Highlighter.HighlightPainter painter;

    /**
     * Creates new form TextEditorAppearanceChooser
     */
    public TextEditorAppearanceChooser(final Frame parent, boolean modal) {
        super(parent, "Appearance");
        setModal(modal);
        initComponents();
        envInits();
        this.background = new Color(255, 255, 255);
        this.foreground = new Color(0, 0, 0);
        this.fontSize = 11;
        this.fontStyle = Font.PLAIN;
        this.font = new Font("Tahoma", fontStyle, fontSize);
        this.def_background = new Color(255, 255, 255);
        this.def_foreground = new Color(0, 0, 0);
        this.def_fontSize = 11;
        this.def_font = new Font("Tahoma", fontStyle, fontSize);
        this.def_highlight = Color.YELLOW;
        this.highlight = Color.YELLOW;

        setDefaultAp();
    }

    public TextEditorAppearanceChooser(final Frame parent, boolean modal,
            Font fontSet, Color background, Color foreground, Color highlight) {
        super(parent, "Appearance");
        setModal(modal);
        initComponents();
        envInits();
        this.def_fontSize = fontSet.getSize();
        this.def_font = fontSet;
        this.def_background = background;
        this.def_foreground = foreground;
        this.def_fontStyle = fontSet.getStyle();
        this.fontStyle = fontSet.getStyle();
        this.fontSize = fontSet.getSize();
        this.font = fontSet;
        this.background = background;
        this.foreground = foreground;
        this.highlight = highlight;
        this.def_highlight = highlight;
        setDefaultAp();
    }

    private void envInits() {
        Collections.unmodifiableMap(fontStyles);
        fonts = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getAvailableFontFamilyNames();
        Arrays.sort(fonts);
        listFonts.setListData(fonts);
        listFontSizes.setListData(fontSizes);
    }

    private void setDefaultAp() {
        buttonHighlight.setBackground(getHighlight());
        buttonHighlight.repaint();
        buttonBackground.setBackground(background);
        buttonBackground.repaint();
        buttonForeground.setBackground(foreground);
        buttonForeground.repaint();
        textAreaPreview.setBackground(background);
        textAreaPreview.setForeground(foreground);
        textAreaPreview.setFont(font);
        textAreaPreview.repaint();
        textAreaPreview.getHighlighter().removeAllHighlights();
        painter = new DefaultHighlighter.DefaultHighlightPainter(getHighlight());
        try {
            textAreaPreview.getHighlighter().addHighlight(9, 21, painter);
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditorAppearanceChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void scrollToSelected() {
        int fontIndex = Arrays.binarySearch(fonts, font.getFamily());
        int fontSizeIndex = Arrays.binarySearch(fontSizes, font.getSize());
        tmp_fontStyle = font.getStyle();
        listFonts.setSelectedIndex(fontIndex);
        listFonts.ensureIndexIsVisible(fontIndex);
        listFontSizes.setSelectedIndex(fontSizeIndex);
        listFontSizes.ensureIndexIsVisible(fontSizeIndex);
        comboBoxStyles.setSelectedItem(fontStyles.get(tmp_fontStyle));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        buttonApply = new javax.swing.JButton();
        buttonDefault = new javax.swing.JButton();
        buttonCancel = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listFonts = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listFontSizes = new javax.swing.JList();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        comboBoxStyles = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        panelPreviewContainer = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        textAreaPreview = new javax.swing.JTextArea();
        buttonBackground = new javax.swing.JButton(){

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0,0,getWidth(), getHeight());
            }
        };
        buttonForeground = new javax.swing.JButton(){

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0,0,getWidth(), getHeight());
            }
        };
        buttonHighlight = new javax.swing.JButton() {

            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0,0,getWidth(), getHeight());
            }
        };

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Appearance Chooser");
        setAlwaysOnTop(true);
        setName(""); // NOI18N
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        buttonApply.setText("Apply");
        buttonApply.setMaximumSize(new java.awt.Dimension(67, 23));
        buttonApply.setMinimumSize(new java.awt.Dimension(67, 23));
        buttonApply.setPreferredSize(new java.awt.Dimension(67, 23));
        buttonApply.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonApplyActionPerformed(evt);
            }
        });

        buttonDefault.setText("Default");
        buttonDefault.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonDefaultActionPerformed(evt);
            }
        });

        buttonCancel.setText("Cancel");
        buttonCancel.setMaximumSize(new java.awt.Dimension(67, 23));
        buttonCancel.setMinimumSize(new java.awt.Dimension(67, 23));
        buttonCancel.setPreferredSize(new java.awt.Dimension(67, 23));
        buttonCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonCancelActionPerformed(evt);
            }
        });

        listFonts.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFonts.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFontsValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(listFonts);

        jLabel1.setText("Font Family");

        listFontSizes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        listFontSizes.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listFontSizesValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(listFontSizes);

        jLabel2.setText("Size");

        jLabel3.setText("Background ");

        jLabel4.setText("Foreground");

        jPanel3.setLayout(new java.awt.BorderLayout());

        comboBoxStyles.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Plain", "Bold", "Italic", "Bold Italic" }));
        comboBoxStyles.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxStylesItemStateChanged(evt);
            }
        });

        jLabel5.setText("Style");

        jLabel6.setText("Highlight");

        panelPreviewContainer.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panelPreviewContainer.setMaximumSize(new java.awt.Dimension(62, 40));
        panelPreviewContainer.setMinimumSize(new java.awt.Dimension(62, 40));
        panelPreviewContainer.setLayout(new java.awt.BorderLayout());

        textAreaPreview.setEditable(false);
        textAreaPreview.setColumns(20);
        textAreaPreview.setLineWrap(true);
        textAreaPreview.setRows(1);
        textAreaPreview.setText("Preview. Highlighted.");
        textAreaPreview.setFocusable(false);
        jScrollPane3.setViewportView(textAreaPreview);

        panelPreviewContainer.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        buttonBackground.setToolTipText("Click To Edit");
        buttonBackground.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        buttonBackground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonBackgroundActionPerformed(evt);
            }
        });

        buttonForeground.setToolTipText("Click To Edit");
        buttonForeground.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        buttonForeground.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonForegroundActionPerformed(evt);
            }
        });

        buttonHighlight.setToolTipText("Click To Edit");
        buttonHighlight.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        buttonHighlight.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonHighlightActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(panelPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 302, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel1)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel2)
                                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                        .addComponent(comboBoxStyles, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addComponent(jLabel5)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(buttonApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addComponent(buttonDefault)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(9, 9, 9))
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(buttonBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(buttonForeground, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(buttonHighlight, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(24, 24, 24))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, Short.MAX_VALUE))))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(panelPreviewContainer, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(buttonBackground, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(buttonForeground, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel6))
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(comboBoxStyles, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(92, 92, 92)
                                .addComponent(buttonHighlight, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(buttonApply, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonDefault)
                    .addComponent(buttonCancel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 319, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 252, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void buttonApplyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonApplyActionPerformed
        font = new Font(textAreaPreview.getFont().getFamily(), tmp_fontStyle, textAreaPreview.getFont().getSize());
        fontSize = font.getSize();
        fontStyle = font.getStyle();
        background = new Color(textAreaPreview.getBackground().getRGB());
        foreground = new Color(textAreaPreview.getForeground().getRGB());
        highlight = new Color(buttonHighlight.getBackground().getRGB());
        changed = true;
        this.setVisible(false);
    }//GEN-LAST:event_buttonApplyActionPerformed

    private void buttonDefaultActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonDefaultActionPerformed
        buttonBackground.setBackground(def_background);
        buttonBackground.repaint();
        buttonForeground.setBackground(def_foreground);
        buttonForeground.repaint();
        buttonHighlight.setBackground(def_highlight);
        buttonHighlight.repaint();
        textAreaPreview.setBackground(def_background);
        textAreaPreview.setForeground(def_foreground);
        textAreaPreview.setFont(def_font);
        textAreaPreview.repaint();
        background = def_background;
        foreground = def_foreground;
        font = def_font;
        fontSize = def_fontSize;
        fontStyle = def_fontStyle;
        highlight = def_highlight;
        scrollToSelected();
        textAreaPreview.getHighlighter().removeAllHighlights();
        painter = new DefaultHighlighter.DefaultHighlightPainter(getHighlight());
        try {
            textAreaPreview.getHighlighter().addHighlight(9, 21, painter);
        } catch (BadLocationException ex) {
            Logger.getLogger(TextEditorAppearanceChooser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_buttonDefaultActionPerformed

    private void buttonCancelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonCancelActionPerformed
        this.setVisible(false);
    }//GEN-LAST:event_buttonCancelActionPerformed

    private void listFontsValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFontsValueChanged
        if (listFonts.getSelectedIndex() == -1) {
            return;
        }
        if (!evt.getValueIsAdjusting()) {
            Font newFont = new Font(fonts[listFonts.getSelectedIndex()], tmp_fontStyle, tmp_fontSize);
            if (newFont != null) {
                tmp_font = newFont;
                textAreaPreview.setFont(tmp_font);
                textAreaPreview.repaint();
            }
        }
    }//GEN-LAST:event_listFontsValueChanged

    private void listFontSizesValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listFontSizesValueChanged
        if (listFontSizes.getSelectedIndex() == -1) {
            return;
        }
        if (!evt.getValueIsAdjusting()) {
            tmp_fontSize = fontSizes[listFontSizes.getSelectedIndex()].intValue();
            Font newFont = new Font(tmp_font.getFontName(), tmp_fontStyle, tmp_fontSize);
            if (newFont != null) {
                textAreaPreview.setFont(newFont);
                textAreaPreview.repaint();
            }
        }
    }//GEN-LAST:event_listFontSizesValueChanged

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        this.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    private void comboBoxStylesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_comboBoxStylesItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            for (Entry<Integer, String> e : fontStyles.entrySet()) {
                if (Objects.equals(evt.getItem(), e.getValue())) {
                    tmp_fontStyle = e.getKey();
                    break;
                }
            }
            Font newFont = new Font(tmp_font.getFontName(), tmp_fontStyle, tmp_fontSize);
            if (newFont != null) {
                textAreaPreview.setFont(newFont);
                textAreaPreview.repaint();
            }
        }
    }//GEN-LAST:event_comboBoxStylesItemStateChanged

    private void buttonBackgroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonBackgroundActionPerformed
        Color newBg = JColorChooser.showDialog(rootPane, "Background Color", background);
        if (newBg != null) {
            buttonBackground.setBackground(newBg);
            textAreaPreview.setBackground(newBg);
            buttonBackground.repaint();
            textAreaPreview.repaint();
        }
    }//GEN-LAST:event_buttonBackgroundActionPerformed

    private void buttonForegroundActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonForegroundActionPerformed
        Color newFg = JColorChooser.showDialog(rootPane, "Foreground Color", foreground);
        if (newFg != null) {
            buttonForeground.setBackground(newFg);
            textAreaPreview.setForeground(newFg);
            buttonForeground.repaint();
            textAreaPreview.repaint();
        }
    }//GEN-LAST:event_buttonForegroundActionPerformed

    private void buttonHighlightActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonHighlightActionPerformed
        Color newFg = JColorChooser.showDialog(rootPane, "Hilight Color", getHighlight());
        if (newFg != null) {
            buttonHighlight.setBackground(newFg);
            buttonHighlight.repaint();
            painter = new DefaultHighlighter.DefaultHighlightPainter(newFg);
            textAreaPreview.getHighlighter().removeAllHighlights();
            try {
                textAreaPreview.getHighlighter().addHighlight(9, 21, painter);
            } catch (BadLocationException ex) {
                Logger.getLogger(TextEditorAppearanceChooser.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }//GEN-LAST:event_buttonHighlightActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton buttonApply;
    private javax.swing.JButton buttonBackground;
    private javax.swing.JButton buttonCancel;
    private javax.swing.JButton buttonDefault;
    private javax.swing.JButton buttonForeground;
    private javax.swing.JButton buttonHighlight;
    private javax.swing.JComboBox comboBoxStyles;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JList listFontSizes;
    private javax.swing.JList listFonts;
    private javax.swing.JPanel panelPreviewContainer;
    private javax.swing.JTextArea textAreaPreview;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the fontSize
     */
    public int getFontSize() {
        return fontSize;
    }

    /**
     * @param fontSize the fontSize to set
     */
    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    /**
     * @return the background
     */
    public Color getBackground() {
        return background;
    }

    /**
     * @param background the background to set
     */
    public void setBackground(Color background) {
        this.background = background;
    }

    /**
     * @return the foreground
     */
    public Color getForeground() {
        return foreground;
    }

    /**
     * @param foreground the foreground to set
     */
    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public void prompt() {
        changed = false;
        setDefaultAp();
        scrollToSelected();
        double parentX = this.getParent().getLocation().getX();
        double parentY = this.getParent().getLocation().getY();
        int parentW = this.getParent().getWidth();
        int parentH = this.getParent().getHeight();
        this.setLocation((int) (parentX + parentW / 2) - this.getWidth() / 2, (int) (parentY + parentH / 2) - this.getHeight() / 2);
        setVisible(true);
    }

    /**
     * @param font the font to set
     */
    public void setChosenFont(Font font) {
        this.font = font;
    }

    /**
     * @return the changed
     */
    public boolean isChanged() {
        return changed;
    }

    /**
     * @return the fontStyle
     */
    public int getFontStyle() {
        return fontStyle;
    }

    public Font getChosenFont() {
        return this.font;
    }

    /**
     * @return the highlight
     */
    public Color getHighlight() {
        return highlight;
    }
}
