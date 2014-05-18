/**
 * 
 */
package com.tmser.timer;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;


public class TimeTask extends JFrame implements ActionListener{

	private static final long serialVersionUID = 1L;
	private String path = null;
	private Map<Integer,String> map = new HashMap<Integer,String>();
	private JPanel mainPanel = null;
	private JPanel btPanel = null;
	private JRadioButton closeRadio = null;
	private JRadioButton resetRadio = null;
	private JRadioButton logoutRadio = null;
	private JRadioButton planRadio = null;
	private ButtonGroup btGroup = null;	
	private JLabel titleLabel = null;
	private JLabel timeLabel = null;	
	private JLabel lookAndFell = null;
	private JTextField timeField =null;
	private JTextField planField = null;
	private JButton browerBt = null;
	private JButton ConfirmBt = null;
	private JButton CancelBt = null;
	private JComboBox laf = null;
	
	public TimeTask(){
		init();
	}
	/**
	 * 填充下拉列表
	 * @return Vector<String>
	 */
	private Vector<String> initComboBos() {
		//获得所有java自带的样式
		LookAndFeelInfo[] lafInfo =  UIManager.getInstalledLookAndFeels();
		Vector<String> lafs = new Vector<String>();
		for (int i = 0; i < lafInfo.length; i++) {
			//将样式存入map中，以便在更改样式时能找到。
			map.put(i,lafInfo[i].getClassName());
			String[] temp = lafInfo[i].getClassName().split("\\.");
			String str = temp[temp.length-1].replace("LookAndFeel", "");
			lafs.addElement(str);
		}
		return lafs;
	}
	@SuppressWarnings("static-access")
	private void init(){
		this.mainPanel = (JPanel) this.getContentPane();
		this.mainPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		this.btGroup = new ButtonGroup();
		
		gbc.gridx = 1;
		gbc.gridy = 0;
		this.titleLabel = new JLabel("定时任务小工具");
		this.mainPanel.add(this.titleLabel,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 1;
		this.closeRadio = new JRadioButton("关机");
		this.closeRadio.addActionListener(this);
		this.closeRadio.setSelected(true);
		this.btGroup.add(this.closeRadio);
		this.mainPanel.add(this.closeRadio,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 1;
		this.resetRadio = new JRadioButton("重启");
		this.resetRadio.addActionListener(this);
		this.btGroup.add(this.resetRadio);
		this.mainPanel.add(this.resetRadio,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 1;
		this.logoutRadio = new JRadioButton("注销");
		this.resetRadio.addActionListener(this);
		this.btGroup.add(this.logoutRadio);
		this.mainPanel.add(this.logoutRadio,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 2;
		this.planRadio = new JRadioButton("计划任务");
		this.planRadio.addActionListener(this);
		this.btGroup.add(this.planRadio);
		this.mainPanel.add(this.planRadio,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 2;
		this.planField = new JTextField(10);
		this.planField.setEditable(false);
		this.mainPanel.add(this.planField,gbc);
		
		gbc.gridx = 2;
		gbc.gridy = 2;
		this.browerBt = new JButton("浏览");
		this.browerBt.setEnabled(false);
		this.browerBt.addActionListener(this);
		this.mainPanel.add(this.browerBt,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 3;
		this.timeLabel= new JLabel("请输入时间(分钟)");
		this.mainPanel.add(this.timeLabel,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		this.timeField= new JTextField(10);
		this.mainPanel.add(this.timeField,gbc);
		
		gbc.gridx = 0;
		gbc.gridy = 4;
		this.lookAndFell= new JLabel("请选择样式");
		this.mainPanel.add(this.lookAndFell,gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 4;
		this.laf = new JComboBox(initComboBos());
		this.laf.addActionListener(this);
		this.mainPanel.add(this.laf,gbc);
		
		
		gbc.gridx = 1;
		gbc.gridy = 5;
		this.btPanel = new JPanel();
		this.ConfirmBt = new JButton("确定");
		this.ConfirmBt.addActionListener(this);
		this.CancelBt = new JButton("取消");
		this.CancelBt.addActionListener(this);
		this.btPanel.add(this.ConfirmBt);
		this.btPanel.add(this.CancelBt);
		this.mainPanel.add(this.btPanel,gbc);
		
		
		
		
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.height - 200)/2;
		int y = (d.width - 350)/2;
		this.setBounds(x, y, 350, 200);
		this.setTitle("定时任务");
		this.setVisible(true);
		this.setResizable(false);
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
	}
	

	@Override
	public void actionPerformed(ActionEvent event) {
		/**************************************
		 * 判断计划任务是否选中，如果是则浏览按钮可用
		 * 否则不可用，并将显示程序路径的输入框清空 
		 * ************************************
		 */
		if(this.planRadio.isSelected()){
			this.browerBt.setEnabled(true);
		}else{
			this.planField.setText("");
			this.browerBt.setEnabled(false);
		}
		/**************************************
		 * 更改样式，通过找到选中样式的索引对应在
		 * map中找到 定义样式的类路径
		 * ************************************
		 */
		int index = this.laf.getSelectedIndex();
		try {
			UIManager.setLookAndFeel(this.map.get(index));
			//更新样式
			SwingUtilities.updateComponentTreeUI(this);
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		} catch (InstantiationException e1) {
			e1.printStackTrace();
		} catch (IllegalAccessException e1) {
			e1.printStackTrace();
		} catch (UnsupportedLookAndFeelException e1) {
			e1.printStackTrace();
		}
		
		//当浏览按钮按下时弹出文件选中对话框。
		if(event.getActionCommand().equals("浏览")){
			System.out.println("浏览");
			 JFileChooser chooser = new JFileChooser();
			 //设置文件的过滤，这里是可执行文件
			    FileNameExtensionFilter filter = new FileNameExtensionFilter(
			        "可执行文件bat&exe&msi", "bat", "exe","msi");
			    chooser.setFileFilter(filter);
			    int returnVal = chooser.showOpenDialog(null);
			    //返回文件的完整路径
			    if(returnVal == JFileChooser.APPROVE_OPTION) {
			       path = chooser.getSelectedFile().getPath();
			       this.planField.setText(path);
			      
			    }

		}
		//点击确定按钮执行的操作
		if(event.getActionCommand().equals("确定")){
			System.out.println("确定");
			//获得系统运行环境
			Runtime runTime = Runtime.getRuntime();
			String time = this.timeField.getText();
			//对输入时间进行验证
			if(time == null || time.equals("")){
				JOptionPane.showMessageDialog(this, "请输入延迟时间");
			}else{				
				if(this.planRadio.isSelected()){
					//在选中计划任务后检查是否选中了可执行文件
					if(this.path == null || this.path.equals("")){
						JOptionPane.showMessageDialog(this, "请选择可执行文件");
					}else{						
						try {						
							Thread.sleep((long) (Double.parseDouble(this.timeField.getText().trim())*60*1000));
							runTime.exec(path);
						}  catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				//关闭计算机
				if(this.closeRadio.isSelected()){
					try {
						Thread.sleep((long) (Double.parseDouble(this.timeField.getText().trim())*60*1000));
						runTime.exec("shutdown -s");
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//重启计算机
				if(this.resetRadio.isSelected()){
					try {
						Thread.sleep((long) (Double.parseDouble(this.timeField.getText().trim())*60*1000));
						runTime.exec("shutdown -r");
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				//注销计算机
				if(this.logoutRadio.isSelected()){
					try {
						Thread.sleep((long) (Double.parseDouble(this.timeField.getText().trim())*60*1000));
						runTime.exec("shutdown -l");
					}catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}			
		}
		if(event.getActionCommand().equals("取消")){
			System.exit(0);
		}
		
	}
	
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args){
		new TimeTask();
	}

}
