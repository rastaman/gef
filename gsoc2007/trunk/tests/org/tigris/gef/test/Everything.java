/*
 * SwingWT demo. Displays most components, dialogs and things
 * as an example.
 *
 * @author R. Rawson-Tetley
 *
 * $Log: Everything.java,v $
 * Revision 1.53  2004/11/05 11:31:50  bobintetley
 * getPixmap() variant that uses Class context and relative resources (Shaun Jackman)
 *
 * Revision 1.52  2004/11/04 20:49:15  bobintetley
 * Relative resources breaks under Sun VMs. Gone back to the getPixmap() method
 *
 * Revision 1.51  2004/11/04 20:24:20  bobintetley
 * Whoops, should at least compile before checkin :)
 *
 * Revision 1.50  2004/11/04 20:16:02  bobintetley
 * Relative image resources and moved filechooser images into swingwtx.swing
 *
 * Revision 1.49  2004/11/04 10:26:46  bobintetley
 * getPixmap() call to look for images on the filesystem or as a classpath
 *    resource.
 *
 * Revision 1.48  2004/11/02 12:50:09  bobintetley
 * Fix to editorpane display and spinner column size
 *
 * Revision 1.47  2004/10/30 20:11:54  bobintetley
 * Code cleanup
 *
 * Revision 1.46  2004/10/07 10:43:20  bobintetley
 * Scrollpane fix for demo, and juggling of TODO list
 *
 * Revision 1.45  2004/06/23 07:34:16  bobintetley
 * MacOSX users are now first class citizens and SwingWT now works properly
 *
 * Revision 1.44  2004/05/25 01:04:11  dannaab
 * Misc bugfixes, ActionMap.java added, added swt source to lib dir (for debugging
 * purposes), misc import optimization
 *
 * Revision 1.43  2004/05/05 17:19:39  bobintetley
 * Fix to make JComboBox update the selected item on it's model as the user
 * chooses another item. Removed debug code from Everything demo
 *
 * Revision 1.42  2004/05/04 09:31:41  bobintetley
 * PlainDocument/View support and implementation. Build script supports java/javax
 * packages - fix to build script to use nested args in bootclasspath (single path broke on my Ant 1.6.1/Linux)
 *
 * Revision 1.41  2004/04/21 11:38:24  bobintetley
 * *** empty log message ***
 *
 * Revision 1.40  2004/04/19 12:49:32  bobintetley
 * JTaskTray implementation (and demo), along with Frame repaint fix
 *
 * Revision 1.39  2004/04/18 14:21:49  bobintetley
 * JSpinner implementation
 *
 * Revision 1.38  2004/04/16 14:38:32  bobintetley
 * Table and Tree cell editor support
 *
 * Revision 1.37  2004/01/20 07:38:01  bobintetley
 * Bug fixes and compatibility methods
 *
 * Revision 1.36  2004/01/16 10:07:28  bobintetley
 * ItemEvent/Listener support for combo
 *
 * Revision 1.35  2004/01/16 09:55:52  bobintetley
 * Fixes
 *
 * Revision 1.34  2004/01/15 18:13:10  bobintetley
 * TreeWillExpand support
 *
 * Revision 1.33  2004/01/13 08:43:50  bobintetley
 * Implemented JSlider and added it to the demos
 *
 * Revision 1.32  2004/01/10 11:46:26  bobintetley
 * JTree/TreePath fixes by Sachin (broken path, rootVisible support) and Rob
 *   (Missing root node from path, couldn't represent TreePath as string)
 *
 * Revision 1.31  2004/01/09 11:49:48  bobintetley
 * Updated notes and set demo back to normal
 *
 * Revision 1.30  2004/01/09 11:47:26  bobintetley
 * Automatic JButton mapping!
 *
 * Revision 1.29  2003/12/22 20:48:48  bobintetley
 * Text and image support together for JButton
 *
 * Revision 1.28  2003/12/17 15:24:33  bobintetley
 * Threading fixes
 *
 * Revision 1.27  2003/12/14 09:38:08  bobintetley
 * Added swing.ButtonGroup to demo
 *
 * Revision 1.26  2003/12/14 08:47:37  bobintetley
 * Added useful comments and CVS log header
 *
 */

package org.tigris.gef.test;

import org.tigris.gef.base.AlignAction;
import org.tigris.gef.base.CmdAdjustGrid;
import org.tigris.gef.base.CmdAdjustGuide;
import org.tigris.gef.base.CmdAdjustPageBreaks;
import org.tigris.gef.base.CmdCopy;
import org.tigris.gef.base.CmdExit;
import org.tigris.gef.base.CmdGroup;
import org.tigris.gef.base.CmdOpen;
import org.tigris.gef.base.CmdOpenWindow;
import org.tigris.gef.base.CmdPaste;
import org.tigris.gef.base.CmdPrint;
import org.tigris.gef.base.CmdPrintPageSetup;
import org.tigris.gef.base.CmdRemoveFromGraph;
import org.tigris.gef.base.CmdReorder;
import org.tigris.gef.base.CmdSave;
import org.tigris.gef.base.CmdSavePGML;
import org.tigris.gef.base.CmdSaveSVG;
import org.tigris.gef.base.CmdSelectAll;
import org.tigris.gef.base.CmdSelectInvert;
import org.tigris.gef.base.CmdSelectNext;
import org.tigris.gef.base.CmdShowProperties;
import org.tigris.gef.base.CmdSpawn;
import org.tigris.gef.base.CmdUngroup;
import org.tigris.gef.base.CmdUseReshape;
import org.tigris.gef.base.CmdUseResize;
import org.tigris.gef.base.CmdUseRotate;
import org.tigris.gef.base.DistributeAction;
import org.tigris.gef.base.NudgeAction;
import org.tigris.gef.event.ModeChangeEvent;
import org.tigris.gef.event.ModeChangeListener;
import org.tigris.gef.graph.GraphEdgeRenderer;
import org.tigris.gef.graph.GraphModel;
import org.tigris.gef.graph.GraphNodeRenderer;
import org.tigris.gef.graph.presentation.Graph;
import org.tigris.gef.graph.presentation.GraphFrame;
import org.tigris.gef.graph.presentation.JGraph;
import org.tigris.gef.swt.JGraphFrame;
import org.tigris.gef.swt.SwtUtil;
import org.tigris.gef.ui.IStatusBar;
import org.tigris.gef.ui.IToolBar;
import org.tigris.gef.undo.RedoAction;
import org.tigris.gef.undo.UndoAction;
import org.tigris.gef.util.Localizer;

import swingwt.awt.BorderLayout;
import swingwt.awt.Color;
import swingwt.awt.FlowLayout;
import swingwt.awt.GridLayout;
import swingwt.awt.event.ActionEvent;
import swingwt.awt.event.ActionListener;
import swingwt.awt.event.KeyEvent;
import swingwt.awt.event.WindowAdapter;
import swingwt.awt.event.WindowEvent;
import swingwtx.swing.AbstractAction;
import swingwtx.swing.ImageIcon;
import swingwtx.swing.JButton;
import swingwtx.swing.JCheckBoxMenuItem;
import swingwtx.swing.JColorChooser;
import swingwtx.swing.JFileChooser;
import swingwtx.swing.JFrame;
import swingwtx.swing.JLabel;
import swingwtx.swing.JMenu;
import swingwtx.swing.JMenuBar;
import swingwtx.swing.JMenuItem;
import swingwtx.swing.JOptionPane;
import swingwtx.swing.JPanel;
import swingwtx.swing.JRadioButtonMenuItem;
import swingwtx.swing.JTabbedPane;
import swingwtx.swing.JToolBar;
import swingwtx.swing.KeyStroke;
import swingwtx.swing.SwingWTUtils;

public class Everything extends JFrame implements IStatusBar, Cloneable,
ModeChangeListener, GraphFrame{
    
    private JPanel comps = null;
    
    //private ImageIcon imgPic = SwingWTUtils.getPixmap("pic.gif", SwingWTUtils.SWINGWT_PIXMAP_SUBDIR, "/demo/");
    //private ImageIcon imgComputer = SwingWTUtils.getPixmap("Computer.png", SwingWTUtils.SWINGWT_PIXMAP_SUBDIR, "/demo/");
    private ImageIcon imgPic = SwingWTUtils.getPixmap(Everything.class, "pic.gif");
    private ImageIcon imgComputer = SwingWTUtils.getPixmap(Everything.class, "Computer.png");
    protected JMenuBar _menubar;
    public void test() {
        setTitle("Test");
        setSize(900, 500);
        setLocation(200, 200);
        

        
        getContentPane().setLayout(new BorderLayout());

        // Central panel to hold components
        comps = new JPanel();
        comps.setLayout(new FlowLayout());
          
        // Menu stuff
        _menubar = new JMenuBar();
        this.setUpMenus();
        setJMenuBar(_menubar);
        
        getContentPane().add(comps, BorderLayout.CENTER);
        
        // Show the frame
        show();
        
    }
    protected void setUpMenus() {
        JMenuItem openItem, saveItem, printItem, exitItem;
        JMenuItem deleteItem, copyItem, pasteItem;
        JMenuItem groupItem, ungroupItem;
        JMenuItem toBackItem, backwardItem, toFrontItem, forwardItem;

        JMenu file = new JMenu(Localizer.localize("GefBase", "File"));
        file.setMnemonic('F');
        _menubar.add(file);
        //file.add(new CmdNew());
        // NOT YET IMPLEMENTED
        openItem = file.add(SwtUtil.translateAction(new CmdOpen()));
        saveItem = file.add(SwtUtil.translateAction(new CmdSave()));
        file.add(SwtUtil.translateAction(new CmdSavePGML()));
        file.add(SwtUtil.translateAction(new CmdSaveSVG()));
        CmdPrint cmdPrint = new CmdPrint();
        printItem = file.add(SwtUtil.translateAction(cmdPrint));
        file.add(SwtUtil.translateAction(new CmdPrintPageSetup(cmdPrint)));
        file.add(SwtUtil.translateAction(new CmdOpenWindow("org.tigris.gef.base.PrefsEditor",
                "Preferences...")));
        //file.add(new CmdClose());
        exitItem = file.add(SwtUtil.translateAction(new CmdExit()));

        JMenu edit = new JMenu(Localizer.localize("GefBase", "Edit"));
        edit.setMnemonic('E');
        _menubar.add(edit);

        JMenuItem undoItem = edit.add(SwtUtil.translateAction(new UndoAction(Localizer.localize(
                "GefBase", "Undo"))));
        undoItem.setMnemonic(Localizer.localize("GefBase", "UndoMnemonic")
                .charAt(0));
        JMenuItem redoItem = edit.add(SwtUtil.translateAction(new RedoAction(Localizer.localize(
                "GefBase", "Redo"))));
        redoItem.setMnemonic(Localizer.localize("GefBase", "RedoMnemonic")
                .charAt(0));

        JMenu select = new JMenu(Localizer.localize("GefBase", "Select"));
        edit.add(select);
        select.add(SwtUtil.translateAction(new CmdSelectAll()));
        select.add(SwtUtil.translateAction(new CmdSelectNext(false)));
        select.add(SwtUtil.translateAction(new CmdSelectNext(true)));
        select.add(SwtUtil.translateAction(new CmdSelectInvert()));

        edit.addSeparator();

        copyItem = edit.add(SwtUtil.translateAction(new CmdCopy()));
        copyItem.setMnemonic('C');
        pasteItem = edit.add(SwtUtil.translateAction(new CmdPaste()));
        pasteItem.setMnemonic('P');

        deleteItem = edit.add(SwtUtil.translateAction(new CmdRemoveFromGraph()));
        edit.addSeparator();
        edit.add(SwtUtil.translateAction(new CmdUseReshape()));
        edit.add(SwtUtil.translateAction(new CmdUseResize()));
        edit.add(SwtUtil.translateAction(new CmdUseRotate()));

        JMenu view = new JMenu(Localizer.localize("GefBase", "View"));
        _menubar.add(view);
        view.setMnemonic('V');
        view.add(SwtUtil.translateAction(new CmdSpawn()));
        view.add(SwtUtil.translateAction(new CmdShowProperties()));
        //view.addSeparator();
        //view.add(new CmdZoomIn());
        //view.add(new CmdZoomOut());
        //view.add(new CmdZoomNormal());
        view.addSeparator();
        view.add(SwtUtil.translateAction(new CmdAdjustGrid()));
        view.add(SwtUtil.translateAction(new CmdAdjustGuide()));
        view.add(SwtUtil.translateAction(new CmdAdjustPageBreaks()));

        JMenu arrange = new JMenu(Localizer.localize("GefBase", "Arrange"));
        _menubar.add(arrange);
        arrange.setMnemonic('A');
        groupItem = arrange.add(SwtUtil.translateAction(new CmdGroup()));
        groupItem.setMnemonic('G');
        ungroupItem = arrange.add(SwtUtil.translateAction(new CmdUngroup()));
        ungroupItem.setMnemonic('U');

        JMenu align = new JMenu(Localizer.localize("GefBase", "Align"));
        arrange.add(align);
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_TOPS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_BOTTOMS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_LEFTS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_RIGHTS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_H_CENTERS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_V_CENTERS)));
        align.add(SwtUtil.translateAction(new AlignAction(AlignAction.ALIGN_TO_GRID)));

        JMenu distribute = new JMenu(Localizer
                .localize("GefBase", "Distribute"));
        arrange.add(distribute);
        distribute.add(SwtUtil.translateAction(new DistributeAction(DistributeAction.H_SPACING)));
        distribute.add(SwtUtil.translateAction(new DistributeAction(DistributeAction.H_CENTERS)));
        distribute.add(SwtUtil.translateAction(new DistributeAction(DistributeAction.V_SPACING)));
        distribute.add(SwtUtil.translateAction(new DistributeAction(DistributeAction.V_CENTERS)));

        JMenu reorder = new JMenu(Localizer.localize("GefBase", "Reorder"));
        arrange.add(reorder);
        toBackItem = reorder.add(SwtUtil.translateAction(new CmdReorder(CmdReorder.SEND_TO_BACK)));
        toFrontItem = reorder.add(SwtUtil.translateAction(new CmdReorder(CmdReorder.BRING_TO_FRONT)));
        backwardItem = reorder.add(SwtUtil.translateAction(new CmdReorder(CmdReorder.SEND_BACKWARD)));
        forwardItem = reorder.add(SwtUtil.translateAction(new CmdReorder(CmdReorder.BRING_FORWARD)));

        JMenu nudge = new JMenu(Localizer.localize("GefBase", "Nudge"));
        arrange.add(nudge);
        nudge.add(SwtUtil.translateAction(new NudgeAction(NudgeAction.LEFT)));
        nudge.add(SwtUtil.translateAction(new NudgeAction(NudgeAction.RIGHT)));
        nudge.add(SwtUtil.translateAction(new NudgeAction(NudgeAction.UP)));
        nudge.add(SwtUtil.translateAction(new NudgeAction(NudgeAction.DOWN)));

        KeyStroke ctrlO = KeyStroke.getKeyStroke(KeyEvent.VK_O,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlS = KeyStroke.getKeyStroke(KeyEvent.VK_S,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlP = KeyStroke.getKeyStroke(KeyEvent.VK_P,
                KeyEvent.CTRL_MASK);
        KeyStroke altF4 = KeyStroke.getKeyStroke(KeyEvent.VK_F4,
                KeyEvent.ALT_MASK);

        KeyStroke delKey = KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0);
        KeyStroke ctrlZ = KeyStroke.getKeyStroke(KeyEvent.VK_Z,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlY = KeyStroke.getKeyStroke(KeyEvent.VK_Y,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlC = KeyStroke.getKeyStroke(KeyEvent.VK_C,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlV = KeyStroke.getKeyStroke(KeyEvent.VK_V,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlG = KeyStroke.getKeyStroke(KeyEvent.VK_G,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlU = KeyStroke.getKeyStroke(KeyEvent.VK_U,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B,
                KeyEvent.CTRL_MASK);
        KeyStroke ctrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F,
                KeyEvent.CTRL_MASK);
        KeyStroke sCtrlB = KeyStroke.getKeyStroke(KeyEvent.VK_B,
                KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);
        KeyStroke sCtrlF = KeyStroke.getKeyStroke(KeyEvent.VK_F,
                KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK);

        openItem.setAccelerator(ctrlO);
        saveItem.setAccelerator(ctrlS);
        printItem.setAccelerator(ctrlP);
        exitItem.setAccelerator(altF4);

        deleteItem.setAccelerator(delKey);
        undoItem.setAccelerator(ctrlZ);
        redoItem.setAccelerator(ctrlY);
        copyItem.setAccelerator(ctrlC);
        pasteItem.setAccelerator(ctrlV);

        groupItem.setAccelerator(ctrlG);
        ungroupItem.setAccelerator(ctrlU);

        toBackItem.setAccelerator(sCtrlB);
        toFrontItem.setAccelerator(sCtrlF);
        backwardItem.setAccelerator(ctrlB);
        forwardItem.setAccelerator(ctrlF);

    }
    
    public void showMessage() {
        JOptionPane.showMessageDialog(this, "You did something and I caught the event!");
    }
    
    public void showConfirm() {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Are you sure you wanted to press that?"))
            System.out.println("You picked yes");
        else
            System.out.println("You picked no");
    }
    
    public void checkClose() {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Sure you want to close?")) {
            dispose();                        
            System.exit(0);
        }
    }
    
    public void showFileDialog() {
        try {
            JFileChooser jf = new JFileChooser();
            jf.setSelectedFile(new java.io.File(System.getProperty("user.home")));
            int picked = jf.showOpenDialog(this);
            if (picked == JFileChooser.CANCEL_OPTION)
                System.out.println("You cancelled the dialog");
            else {
                System.out.println("You chose: " + jf.getSelectedFile().getAbsolutePath());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void showColorDialog() {
        try {
            JColorChooser jf = new JColorChooser();
            Color c = JColorChooser.showDialog(this, "Choose that colour!", null);
            if (c == null)
                System.out.println("You cancelled the dialog");
            else {
                System.out.println("You chose: " + c.toString());
            }
        }
        catch (Exception e)  {
            e.printStackTrace();
        }
    }
    
 
    public static void main(String[] args) {
	if (SwingWTUtils.isMacOSX()) {
	    SwingWTUtils.initialiseMacOSX(new Runnable() {
		public void run() {
                    new Everything().test();
		}
	    });
	}
	else
        {          
            new Everything().test();
        }
    }
    public void showStatus(String s) {
        // TODO Auto-generated method stub
        
    }
    public void modeChange(ModeChangeEvent mce) {
        // TODO Auto-generated method stub
        
    }
    public Graph getGraph() {
        // TODO Auto-generated method stub
        return null;
    }
    public GraphEdgeRenderer getGraphEdgeRenderer() {
        // TODO Auto-generated method stub
        return null;
    }
    public GraphModel getGraphModel() {
        // TODO Auto-generated method stub
        return null;
    }
    public GraphNodeRenderer getGraphNodeRenderer() {
        // TODO Auto-generated method stub
        return null;
    }
    public IToolBar getToolBar() {
        // TODO Auto-generated method stub
        return null;
    }
    public void init() {
        // TODO Auto-generated method stub
        
    }
    public void init(JGraph jg) {
        // TODO Auto-generated method stub
        
    }
    public void setGraphEdgeRenderer(GraphEdgeRenderer rend) {
        // TODO Auto-generated method stub
        
    }
    public void setGraphModel(GraphModel gm) {
        // TODO Auto-generated method stub
        
    }
    public void setGraphNodeRenderer(GraphNodeRenderer rend) {
        // TODO Auto-generated method stub
        
    }
    public void setToolBar(IToolBar tb) {
        // TODO Auto-generated method stub
        
    }
    public Object clone() {
        return null; //needs-more-work
    }    
}
