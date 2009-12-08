package contextawaremodel.gui;

import edu.stanford.smi.protegex.owl.model.OWLModel;
import java.io.PrintStream;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class MainWindow extends javax.swing.JFrame {

    private GUIAgentExternal guiAgent;
    private OWLModel owlModel;
    
    /** Creates new form MainWindow */
    public MainWindow(GUIAgentExternal guiAgent, OWLModel owlModel) {
        this.guiAgent = guiAgent;
        
        initComponents();

        ontologyTree.setModel(new OntologyTreeModel(owlModel, new DefaultMutableTreeNode("Context Element")));
        ontologyTree.setCellRenderer(new OntologyTreeCellRenderer());
        ontologyTree.putClientProperty("JTree.lineStyle", "None");

        toolBar.setVisible(false);

        // Capture the System.out into the messages text area
        PrintStream myOut = new PrintStream(new MyPrintStream(System.out, messagesTextArea), true);
        System.setOut(myOut);        
    }

    public void addIndividual(String name) {
        ((OntologyTreeModel) ontologyTree.getModel()).addIndividual(name);
        
        ontologyTree.repaint();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        requestToggleButton = new javax.swing.JToggleButton();
        splitPane = new javax.swing.JSplitPane();
        jScrollPane1 = new javax.swing.JScrollPane();
        ontologyTree = new javax.swing.JTree();
        jScrollPane2 = new javax.swing.JScrollPane();
        messagesTextArea = new javax.swing.JTextArea();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        requestMenuItem = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JSeparator();
        shutdownMenuItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        visualizationMenuItem = new javax.swing.JMenuItem();
        memoryMonitorMenuItem = new javax.swing.JMenuItem();
        jadeGuiMenuItem = new javax.swing.JMenuItem();
        realTimePlotMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("Context Aware Model :: Calin Bindea, Cristian Patrasciuc");
        setName("mainWndow"); // NOI18N

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        requestToggleButton.setText("jToggleButton1");
        requestToggleButton.setFocusable(false);
        requestToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        requestToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(requestToggleButton);

        splitPane.setDividerLocation(200);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.75);

        jScrollPane1.setBorder(null);

        ontologyTree.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Ontology Tree: "));
        jScrollPane1.setViewportView(ontologyTree);

        splitPane.setTopComponent(jScrollPane1);

        jScrollPane2.setBorder(null);

        messagesTextArea.setColumns(20);
        messagesTextArea.setRows(5);
        messagesTextArea.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Messages: "));
        jScrollPane2.setViewportView(messagesTextArea);

        splitPane.setRightComponent(jScrollPane2);

        fileMenu.setText("File");

        requestMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        requestMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/page_white_add.png"))); // NOI18N
        requestMenuItem.setText("New Actor Request...");
        fileMenu.add(requestMenuItem);
        fileMenu.add(jSeparator1);

        shutdownMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F4, java.awt.event.InputEvent.ALT_MASK));
        shutdownMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/stop.png"))); // NOI18N
        shutdownMenuItem.setText("Shutdown Platform");
        shutdownMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shutdownMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(shutdownMenuItem);

        menuBar.add(fileMenu);

        viewMenu.setText("View");

        visualizationMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/shape.png"))); // NOI18N
        visualizationMenuItem.setText("Context Visualization Window");
        visualizationMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                visualizationMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(visualizationMenuItem);

        memoryMonitorMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/monitor.png"))); // NOI18N
        memoryMonitorMenuItem.setText("Memory Monitor");
        memoryMonitorMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                memoryMonitorMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(memoryMonitorMenuItem);

        jadeGuiMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/logosmall.jpg"))); // NOI18N
        jadeGuiMenuItem.setText("JADE GUI");
        jadeGuiMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jadeGuiMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(jadeGuiMenuItem);

        realTimePlotMenuItem.setText("Real Time Plot");
        realTimePlotMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realTimePlotMenuItemActionPerformed(evt);
            }
        });
        viewMenu.add(realTimePlotMenuItem);

        menuBar.add(viewMenu);

        helpMenu.setText("Help");

        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/contextawaremodel/gui/images/information.png"))); // NOI18N
        aboutMenuItem.setText("About...");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
            .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(splitPane, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void shutdownMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_shutdownMenuItemActionPerformed
        if ( JOptionPane.showConfirmDialog(this, "Are you sure you want to shutdown the platform?", "Shutdown Platform", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
            guiAgent.shutdownPlatform();
        }
    }//GEN-LAST:event_shutdownMenuItemActionPerformed

    private void visualizationMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_visualizationMenuItemActionPerformed
        guiAgent.showContextVisualizationWindow();
    }//GEN-LAST:event_visualizationMenuItemActionPerformed

    private void memoryMonitorMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_memoryMonitorMenuItemActionPerformed
        guiAgent.showMemoryMonitor();
    }//GEN-LAST:event_memoryMonitorMenuItemActionPerformed

    private void jadeGuiMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jadeGuiMenuItemActionPerformed
        guiAgent.startJADEGUI();
    }//GEN-LAST:event_jadeGuiMenuItemActionPerformed

    private void realTimePlotMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realTimePlotMenuItemActionPerformed
        String s = JOptionPane.showInputDialog(this, "Update interval (in milliseconds): ", "5000");
        try {
            long interval = Long.parseLong(s);
            guiAgent.startRealTimePlot(interval);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "The number you entered is not an integer value.", "Number format error", JOptionPane.ERROR_MESSAGE);
            return;
        }
    }//GEN-LAST:event_realTimePlotMenuItemActionPerformed

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        AboutDialog ad = new AboutDialog(this, true);
        ad.setLocationRelativeTo(this);
        ad.setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JMenuItem jadeGuiMenuItem;
    private javax.swing.JMenuItem memoryMonitorMenuItem;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JTextArea messagesTextArea;
    private javax.swing.JTree ontologyTree;
    private javax.swing.JMenuItem realTimePlotMenuItem;
    private javax.swing.JMenuItem requestMenuItem;
    private javax.swing.JToggleButton requestToggleButton;
    private javax.swing.JMenuItem shutdownMenuItem;
    private javax.swing.JSplitPane splitPane;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JMenuItem visualizationMenuItem;
    // End of variables declaration//GEN-END:variables

}