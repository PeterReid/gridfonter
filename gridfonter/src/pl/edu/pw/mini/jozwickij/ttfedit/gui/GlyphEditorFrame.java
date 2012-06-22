package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_glyfGeneric;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class GlyphEditorFrame extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private JFrame frame = null;
	private JPanel view = null;
	private GlyphEdit edit = null;
	private Toolbar toolbar = null;
	private GlyphBox activeBox = null;
	private JScrollBar scrollX = null; 
	private JScrollBar scrollY = null;
	private JPanel panel = new JPanel();
			
	public GlyphEditorFrame(Component top, JPanel jview) throws Exception {
		super("Glyph Editor",true,true,true,false);
		if (! (top instanceof JFrame) ) {
			throw new Exception("No frame available for GlyphEditorFrame!");
		}
		this.frame = (JFrame)top;
		this.setRequestFocusEnabled(true);
		this.setFocusable(true);
		this.view = jview;
		this.reposition();
		this.edit = new GlyphEdit(this);
		this.toolbar = new Toolbar(edit);
		this.setupComponents();
		Util.installKeyListener(this, new HelpKeyListener());
		this.setMinimumSize(new Dimension(600,400));
	}
	
	private void setupComponents() {
		
		int sl = BufferedImageWrapper.getScreenLen();
		scrollX = new JScrollBar(JScrollBar.HORIZONTAL, 0, sl, GlyphEditIcon.MINCOORD, GlyphEditIcon.MAXCOORD);
		scrollY = new JScrollBar(JScrollBar.VERTICAL, 0, sl, GlyphEditIcon.MINCOORD, GlyphEditIcon.MAXCOORD);
		this.panel.add(edit, BorderLayout.CENTER);
		this.setLayout(new BorderLayout());
		this.add(panel, BorderLayout.CENTER);
		this.add(scrollX, BorderLayout.SOUTH);
		this.add(scrollY, BorderLayout.EAST);
		
		scrollX.addAdjustmentListener(edit);
		scrollY.addAdjustmentListener(edit);
		scrollX.setUnitIncrement(DefaultProperties.SCROLL_AMOUNT);
		scrollY.setUnitIncrement(DefaultProperties.SCROLL_AMOUNT);
		this.addMouseWheelListener(edit);
	}
	
	private void reposition() {
		this.setBounds(frame.getRootPane().getBounds());
		this.setLocation(0,0);		
	}
	
	private void addWidget(Component cmp, int level) {
		if (frame.getLayeredPane().getComponentZOrder(cmp)!=-1) {
			frame.getLayeredPane().remove(cmp);
		}
		frame.getLayeredPane().add(cmp,level);
	}

	public void activate(TTFTable_glyfGeneric glf, GlyphBox box) {
		try {
			this.activeBox = box;
			this.reposition();
			this.addWidget(this, JLayeredPane.MODAL_LAYER);
			this.addWidget(toolbar, JLayeredPane.POPUP_LAYER);		
			edit.loadGlyph(glf);
			edit.setFitTransform(this.getSize());
			this.setVisible(true);
			this.toolbar.resetButtons();
			this.toolbar.setVisible(true);
		}
		catch (OutOfMemoryError oom) {
			Util.showException(oom, null, "Try to increase -Xmx JVM startup parameter!");
		}
		catch (Exception e) {
			Util.showException(e, null, "Exception");
		}
	}

	@Override
	public void doDefaultCloseAction() {
		if (activeBox!=null) {
			activeBox.updateIcon();
		}
		this.setVisible(false);
		this.toolbar.setVisible(false);
		frame.getLayeredPane().remove(this);
		frame.getLayeredPane().remove(toolbar);
		edit.unloadGlyph();
		/* fix scroll issue in panel */
		view.requestFocusInWindow();
		view.requestFocus();
	}
}
