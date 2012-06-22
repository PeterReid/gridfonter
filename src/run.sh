#!/bin/bash
java -Xms256m -Xmx256m -XX:+UseConcMarkSweepGC -XX:+UseParNewGC -XX:+CMSParallelRemarkEnabled -XX:SoftRefLRUPolicyMSPerMB=100000 -Xincgc -Dsun.java2d.opengl=True -Dsun.java2d.translaccel=true -cp .;classes.jar pl.edu.pw.mini.jozwickij.ttfedit.gui.GUIFrame
