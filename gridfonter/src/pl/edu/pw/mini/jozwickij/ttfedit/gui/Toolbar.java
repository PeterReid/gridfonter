package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLayeredPane;
import javax.swing.JToggleButton;

import pl.edu.pw.mini.jozwickij.ttfedit.Resources;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class Toolbar extends JInternalFrame {

	private static final long serialVersionUID = 1L;
	private GlyphEdit edit = null;
	
	private static final URL zoomInURL  = Resources.get(Toolbar.class,"./res/zoom+.png");
	private static final URL zoomOutURL = Resources.get(Toolbar.class,"./res/zoom-.png");
	private static final URL addptURL   = Resources.get(Toolbar.class,"./res/addpt.png");
	private static final URL contourURL = Resources.get(Toolbar.class,"./res/contour.png");
	private static final URL deleteURL  = Resources.get(Toolbar.class,"./res/delete.png");
	
	private final JToggleButton addpt = addptURL!=null ? 
			new JToggleButton(new ImageIcon(addptURL)) : new JToggleButton("Add");
	private final JToggleButton contour = contourURL!=null ? 
			new JToggleButton(new ImageIcon(contourURL)) : new JToggleButton("Contour");
	private final JToggleButton delete = deleteURL!=null?
			new JToggleButton(new ImageIcon(deleteURL)) : new JToggleButton("Delete");
			
	public Toolbar(final GlyphEdit edit) {
		super("Toolbar",false,false,false,false);
		this.setBounds(0,0,200,55);
		this.setLocation(20,40);
		this.setLayer(JLayeredPane.MODAL_LAYER);
		this.edit = edit;
		this.setLayout(new GridLayout(1,0));
		
		JButton zoom_in = zoomInURL!=null ?
				new JButton(new ImageIcon(zoomInURL)) : new JButton("+");
		JButton zoom_out = zoomOutURL!=null ?
				new JButton(new ImageIcon(zoomOutURL)) : new JButton("-");
		zoom_in.setToolTipText("Zoom in");
		zoom_out.setToolTipText("Zoom out");
		addpt.setToolTipText("<html>Add point to simple glyph (right click to select predecessor), hold CTRL for off-curve<br>Add component to composite glyph</html>");
		contour.setToolTipText("Add new contour to simple glyph (hold CTRL to repeat)");
		delete.setToolTipText("<html>Delete points of simple glyphs<br>Delete components of composite glyphs</html>");
		this.add(zoom_out);
		this.add(zoom_in);
		this.add(addpt);
		this.add(contour);
		this.add(delete);
		zoom_in.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setZoom(1.0/0.9);
			}			
		});
		zoom_out.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				setZoom(0.9);
			}			
		});
		addpt.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boolean newStateSelected = addpt.isSelected();
				edit.setEditorAddMode(newStateSelected);
				if (newStateSelected) {
					contour.setSelected(false);
					delete.setSelected(false);
				}
			}			
		});
		contour.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boolean newStateSelected = contour.isSelected();
				boolean cntrMode = edit.getEditorContourMode();
				if (!newStateSelected && cntrMode && 
					(e.getModifiers() & ActionEvent.CTRL_MASK)!=0)
				{
					contour.setSelected(true);
					edit.setEditorContourMode(true);
					return;
				}
				edit.setEditorContourMode(newStateSelected);
				if (newStateSelected) {
					addpt.setSelected(false);
					delete.setSelected(false);
				}
			}			
		});
		delete.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				boolean newStateSelected = delete.isSelected();
				edit.setEditorDelMode(newStateSelected);
				if (newStateSelected) {
					addpt.setSelected(false);
					contour.setSelected(false);
				}
			}			
		});	
		Util.installKeyListener(this, new HelpKeyListener());
	}
	
	private void setZoom(double size) {
		edit.addTransform(AffineTransform.getScaleInstance(size,size));		
	}

	public void resetButtons() {
		edit.resetEditorMode();
		this.addpt.setSelected(false);
		this.contour.setSelected(false);
		this.delete.setSelected(false);
	}	
}
