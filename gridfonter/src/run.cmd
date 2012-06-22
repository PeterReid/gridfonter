@ECHO OFF
java -Xms256m -Xmx256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=100000 -Xincgc -Dsun.java2d.d3d=True -Dsun.java2d.d3dtexbpp=16 -Dsun.java2d.translaccel=true -Dsun.java2d.ddscale=true -cp .;classes.jar pl.edu.pw.mini.jozwickij.ttfedit.gui.GUIFrame
pause