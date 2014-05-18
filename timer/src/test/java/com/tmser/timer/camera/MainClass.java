/**
 * 
 */
package com.tmser.timer.camera;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

public class MainClass {

	/**
	 * 程序运行入口
	 * @param args
	 */
	public static void main(String[] args) {
		MainFrame frmMain = new MainFrame();
		try {
			//设置主窗体的样式
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
			SwingUtilities.updateComponentTreeUI(frmMain);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		//设置窗口位于屏幕中央
		frmMain.setLocationRelativeTo(null);
		frmMain.setVisible(true);
	}

}

