package pl.edu.pw.mini.jozwickij.ttfedit.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import pl.edu.pw.mini.jozwickij.ttfedit.DefaultProperties;
import pl.edu.pw.mini.jozwickij.ttfedit.Resources;
import pl.edu.pw.mini.jozwickij.ttfedit.TTFont;
import pl.edu.pw.mini.jozwickij.ttfedit.table.TTFTable;
import pl.edu.pw.mini.jozwickij.ttfedit.tables.common.objects.TTFTable_nameRecord;
import pl.edu.pw.mini.jozwickij.ttfedit.tools.TTFilter;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Debug;
import pl.edu.pw.mini.jozwickij.ttfedit.util.Util;

public class GUIFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final int STATE_READY 	= 0;
	private static final int STATE_OPENING 	= 1;
	private static final int STATE_SAVING 	= 2;
	private JTabbedPane jtp = null;
	private JMenu menuFile = null;
	private JMenu menuView = null;
	private JMenu menuHelp = null;
	private JMenuBar menuBar = null;
	private ImageIcon fontIcon = null;
	private TTFont font = null;
	private GUIFrame frame = this;
	private JFileChooser fc = null;
	private JFileChooser jfc = null;
	private final static Dimension SIZE = new Dimension(640,480);
	private AtomicInteger state = new AtomicInteger(STATE_READY);
	private final static Map<String,String> templates = new TreeMap<String,String>();
	private boolean showAdvanced = false;
	private KeyListener helpKeyListener = new HelpKeyListener();
	private Font jFont = null;
	
	static {
		templates.put("1.Regular","./template/Vera.ttf");
		templates.put("2.Italic", "./template/VeraIt.ttf");
		templates.put("3.Bold", "./template/VeraBd.ttf");
		templates.put("4.Bold Italic", "./template/VeraBI.ttf");
		templates.put("5.Mono", "./template/VeraMono.ttf");
		templates.put("6.Mono Italic", "./template/VeraMoIt.ttf");
		templates.put("7.Mono Bold", "./template/VeraMoBd.ttf");
		templates.put("8.Mono Bold Italic", "./template/VeraMoBI.ttf");
		templates.put("9.Serif", "./template/VeraSe.ttf");
		templates.put("a.Serif Bold", "./template/VeraSeBd.ttf");
	}
	
	protected void initialize() {}
	
	public GUIFrame() {
		super();
		this.initialize();		
		this.setSize(SIZE);
		this.setFocusable(true);
		this.getContentPane().setLayout(new BorderLayout());
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.jbInit();
		this.add(jtp, BorderLayout.CENTER);
		jtp.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
		try {
			java.net.URL imgURL = Resources.get(GUIFrame.class,"./res/font16.png");
			fontIcon = new ImageIcon(imgURL);
			this.setIconImage(fontIcon.getImage());
		}
		catch (Exception e) { Debug.printlnErr(e); }
		this.doTitle();
		this.setVisible(true);	
		this.setExtendedState(this.getExtendedState() | GUIFrame.MAXIMIZED_BOTH);
		Util.installKeyListener(this, helpKeyListener);		
	}
	
	private void makeHelpMenu() {
		JMenuItem about = new JMenuItem("About");
		JMenuItem showHelp = new JMenuItem("Show Help");
		about.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				About.showAbout();				
			}
		});
		showHelp.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				HelpWindow.showHelp();				
			}
		});
		menuHelp.add(about);
		menuHelp.add(showHelp);		
	}
	
	private JMenu makeTemplateMenu() {
		JMenu newMenu = new JMenu("New");
		for (final Entry<String,String> en : templates.entrySet()) {
			JMenuItem jmit = new JMenuItem(en.getKey().substring(2));
			jmit.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_loadFont(en.getValue());					
				}}
			);
			newMenu.add(jmit);			
		}
		return newMenu;
	}
	
	private JMenuItem makePreviewItem() {
		JMenuItem pview = new JMenuItem("Show preview");
		pview.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				PreviewFont.preview(font);				
			}
		});
		return pview;
	}
	
	private JFileChooser getDirChooser() {
		if (jfc==null) {
			jfc = new JFileChooser();
			jfc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			jfc.setFileFilter(new FileFilter() {
	
				@Override
				public boolean accept(File f) {
					return f.isDirectory() && f.canRead();
				}
				@Override
				public String getDescription() {
					return "Directory";
				}
			});
		}
		return jfc;
	}
	
	private JMenuItem makeTestItem() {
		JMenuItem pview = new JMenuItem("Test fonts in directory");
		pview.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				getDirChooser();
				if (jfc.showOpenDialog(frame)!=JFileChooser.APPROVE_OPTION)
					return;
			    File selFile = jfc.getSelectedFile();
			    if (selFile!=null)
			    	ScannerWindow.showWindow(selFile.getAbsolutePath());				
			}
		});
		return pview;
	}
	
	private void doTitle() {
		this.setTitle("TrueType Tables editor");
	}
	
	private void jbInit() {
		this.menuBar = new JMenuBar();
		this.menuFile = new JMenu("File");
		this.menuView = new JMenu("View");
		this.menuHelp = new JMenu("Help");
		JMenuItem m_exit = null, m_open = null, m_save = null, m_save_install = null;
		JCheckBoxMenuItem m_showAdv = null;
		this.menuFile.add(makeTemplateMenu());
		this.menuView.add(makePreviewItem());
		this.menuFile.add(m_open = new JMenuItem("Open"));
		this.menuFile.add(m_save = new JMenuItem("Save as"));
		if (Util.isWindows()) {
			this.menuFile.add(m_save_install = new JMenuItem("Save and install"));
		}
		this.menuFile.add(makeTestItem());
		this.menuFile.add(m_exit = new JMenuItem("Exit"));
		this.menuView.add(m_showAdv = new JCheckBoxMenuItem("Show advanced", false));
		this.makeHelpMenu();
		
		this.addWindowListener(new WindowListener() {
			public void windowOpened(WindowEvent e) {}
			public void windowClosing(WindowEvent e) {}

			public void windowClosed(WindowEvent e) {
				if (state.get() == STATE_SAVING) {
					return;
				}
				System.exit(0);				
			}
			public void windowIconified(WindowEvent e) {}
			public void windowDeiconified(WindowEvent e) {}
			public void windowActivated(WindowEvent e) {}
			public void windowDeactivated(WindowEvent e) {}
		});
		
		this.jtp = new JTabbedPane(JTabbedPane.LEFT);
		Properties p = Util.readConfig("./.cache");
		File f = new File(p.getProperty("lastPath","./"));
		this.fc = new JFileChooser(f);
		
		class Saver implements ActionListener
		{
			private boolean doInstall = false;
			
			public Saver(boolean install) {
				this.doInstall = install;
			}
			
			public void actionPerformed(ActionEvent e) {
				if (state.get() != STATE_READY) {
					return;
				}
				if (frame.font!=null) {
					if (doInstall) {
						String fn = fc.getSelectedFile().getName();
						fc.setSelectedFile(Util.getWindowsFontsDir(fn));
					}
					fc.setFileFilter(new TTFilter());
					if (fc.showSaveDialog(frame)!=JFileChooser.APPROVE_OPTION)
						return;
					
				    File selFile = fc.getSelectedFile();
				    if (selFile!=null) {
				    	try {
							frame._saveFont(selFile.getAbsolutePath(), doInstall);							
						}
				    	catch (Exception ex) {
				    		Util.showException(ex, frame, "Exception while saving font");
							frame.doTitle();
						}
				    }
				}
				
			}
		};
		m_open.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				
				if (state.get() != STATE_READY) {
					return;
				}				
				fc.setFileFilter(new TTFilter());
				if (fc.showOpenDialog(frame)!=JFileChooser.APPROVE_OPTION)
					return;
			    File selFile = fc.getSelectedFile();
			    if (selFile!=null) {
			    	try {
						if (!selFile.getAbsolutePath().toLowerCase().endsWith(TTFont.TTF_EXT)) {
							selFile = new File (selFile.getAbsolutePath()+TTFont.TTF_EXT);
						}
			    		Properties p = new Properties();
						p.setProperty("lastPath",selFile.getAbsolutePath());
						Util.saveConfig("./.cache",p);
			    		frame._loadFont(selFile.getAbsolutePath());
					}
			    	catch (Exception ex) {
			    		Util.showException(ex, frame, "Exception while loading font");
			    		frame.font = null;
						frame.doTitle();
					}
			    }
				
			}
		});
		m_save.addActionListener(new Saver(false));
		if (m_save_install!=null) {
			m_save_install.addActionListener(new Saver(true));
		}
		m_exit.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				if (state.get() == STATE_SAVING) {
					return;
				}
				System.exit(0);				
			}
		});
		m_showAdv.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent e) {
				showAdvanced = !showAdvanced;
				if (font!=null)
					getView();
			}
		});
		this.menuBar.add(menuFile);
		this.menuBar.add(menuView);
		this.menuBar.add(menuHelp);
		this.setJMenuBar(menuBar);
	}
	
	private void getView() {
		jtp.removeAll();
		Iterator<Entry<String,TTFTable>> it = font.getTables().iterator();
		
		int i = 0;
		while (it.hasNext()) {
			final Entry<String,TTFTable> ent = it.next();	
			//if (/*i==1 || i==3 ||*/ i==7/* || i==9 || i==14*/) {// 1 3 7 9 14
				final JComponent view = ent.getValue().getView(frame, jFont);
				final String key = ent.getKey();
							
				if (view!=null) {
					System.out.println(i);
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							if (ent.getValue().isViewUserFriendly() || showAdvanced) {
								jtp.addTab(key,fontIcon,view);
								Util.installKeyListener(view, helpKeyListener);
							}
						}
					});
				}
			//}
			i++;
		}
		this.setTitle(font.getDescription());
	}
	
	public void _loadFont(final String path) {
		new Thread() {

			public void run() {
				try {
					state.set(STATE_OPENING);
					frame.loadFont(path);
				}
		    	catch (Exception ex) {
					JOptionPane.showMessageDialog(	frame,Util.describeException(ex),
													"Exception while loading font",
													JOptionPane.ERROR_MESSAGE);
					frame.doTitle();
					frame.font = null;
				}
		    	finally {
		    		state.set(STATE_READY);
		    	}
			}
		}.start();
	}
	
	public void _saveFont(final String path, final boolean install) {
		new Thread() {

			public void run() {
				String tlt = frame.getTitle();
				try {
					state.set(STATE_SAVING);
					frame.saveFont(path, install);					
				}
		    	catch (Exception ex) {
					JOptionPane.showMessageDialog(	frame,Util.describeException(ex),
													"Exception while saving font",
													JOptionPane.ERROR_MESSAGE);
					frame.doTitle();
				}
		    	finally {
		    		state.set(STATE_READY);
		    	}
		    	frame.setTitle(tlt);
			}
		}.start();
	}
	
	public void loadFont(String path) throws Exception {
		jtp.removeAll();
		if (font!=null) {
			font.destroy();
		}
		frame.setTitle("Loading font...");
		font = new TTFont(path,TTFont.MODE_READ);
		InputStream is = null;
		try {
			jFont = Font.createFont(Font.TRUETYPE_FONT, is=new URL(path).openStream());
		}
		catch (Exception e) {}
		finally {
			if (is!=null)
				try {
					is.close();
				}
			catch (Exception ex) {};
		}
		getView();
	}
	
	public void saveFont(String path, boolean install) throws Exception {
		frame.setTitle("Saving font...");
		TTFTable_nameRecord.resetTempNames();
		font.save(path);
		if (install) {
			File f = new File("./AddFont.exe").getAbsoluteFile();
			Runtime.getRuntime().exec(f.getAbsolutePath(), new String[] { f.getAbsolutePath(), path });
		}
	}
	
	public TTFont getLoadedFont() {
		return font;
	}
	
	protected static void parseArgs(String[] args) {
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-debug"))
				DefaultProperties.DEBUG = true;
			else if (arg.equalsIgnoreCase("-extview"))
				DefaultProperties.SHOW_TABLES = true;
			else if (arg.toLowerCase().startsWith("-lowmem")) {
				DefaultProperties.LOW_MEM = true;
				String limit = arg.toLowerCase().replaceAll("-lowmem","");
				try {
					int l = Integer.parseInt(limit);
					DefaultProperties.LOW_MEM_TRESHOLD = l;
				}
				catch (Exception e) {}
			}
		}
	}
	
	public static void main(String[] args)
	{
		parseArgs(args);
		GUIFrame gui = null;
		try {
			gui = new GUIFrame();		
		}
		catch(Exception e) {
			Util.showException(e, gui, "Exception");
		}
	}
}
