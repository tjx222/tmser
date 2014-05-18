/**
 * 
 */
package com.tmser.timer.camera;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.media.CaptureDeviceInfo;
import javax.media.CaptureDeviceManager;
import javax.media.DataSink;
import javax.media.Format;
import javax.media.Manager;
import javax.media.MediaLocator;
import javax.media.Player;
import javax.media.Processor;
import javax.media.ProcessorModel;
import javax.media.control.FormatControl;
import javax.media.format.AudioFormat;
import javax.media.format.VideoFormat;
import javax.media.protocol.DataSource;
import javax.media.protocol.FileTypeDescriptor;
import javax.media.protocol.SourceCloneable;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.sun.media.protocol.vfw.VFWCapture;
import com.sun.media.protocol.vfw.VFWSourceStream;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	private CaptureDeviceInfo captureDevice;
	private CaptureDeviceInfo audioDevice;
	private Component visualComponent;
	private Processor processor;
	private Player player;
	private DataSink fileWriter;
	private FormatControl formatControl;
	private JPanel pnlVideo = new JPanel(new BorderLayout());
	JPanel contentPane;
	JPanel pnlControl = new JPanel();
	JButton btnStart = new JButton("开始采集");
	JButton btnStop = new JButton("停止采集");
	JFileChooser fileChooser = new JFileChooser();
	
	public MainFrame()
	{
		super("视频采集软件");
		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
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
		contentPane = (JPanel) this.getContentPane();
		//获得视频设备信息
		this.captureDevice = getCaptureDeviceInfo();
		
		this.audioDevice = getAudioDeviceInfo();
		//
		try {
			//获得数据源
			DataSource videoSource =	Manager.createDataSource(captureDevice.getLocator());
			DataSource audioSource =	Manager.createDataSource(audioDevice.getLocator());
			
			//把所得到的数据源用一个数据数组合二为一，以便播放
			final DataSource[] dataSources = new DataSource[2];
			
			dataSources[0] = videoSource;
			dataSources[1] = audioSource;
			
			DataSource dsLocal = Manager.createMergingDataSource(dataSources);
			
			
			//克隆数据源
			DataSource cloneableSource = Manager.createCloneableDataSource(dsLocal);
			//创建player
			player = Manager.createRealizedPlayer(((SourceCloneable)cloneableSource).createClone());
			//获取默认格式，我这里是RGB
			formatControl = (FormatControl) player.getControl("javax.media.control.FormatControl");
			Format defaultFormat = formatControl.getFormat();
			//设置输出文件的格式，这里是avi
			FileTypeDescriptor outputType = new FileTypeDescriptor(FileTypeDescriptor.MSVIDEO);
			ProcessorModel processorModel = new ProcessorModel(cloneableSource, new Format[]{ defaultFormat }, outputType);
			//创建一个处理器
			processor = Manager.createRealizedProcessor(processorModel);
		} catch (Exception e) {
			processException(e);
		}
		//展示图形的swing组件
		visualComponent = player.getVisualComponent();
		if (visualComponent!=null) {
			pnlVideo.add(visualComponent);
			contentPane.add(pnlVideo);
		}
		//
		btnStop.setEnabled(false);
		pnlControl.add(btnStart);
		pnlControl.add(btnStop);
		contentPane.add("South", pnlControl);
		//点击开始事件
		btnStart.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		btnStop.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		//
		pack();
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	/**
	 * 点击开始采集
	 */
	private void start() {
		String locatorString = getLocatorString();
		if(locatorString==null)
		{
			return;
		}
		//开始采集不可用
		btnStart.setEnabled(false);
		//从处理器中拿到数据源
		DataSource source = processor.getDataOutput();
		MediaLocator dest = new MediaLocator( locatorString );
		try {
			//本地文件传输
			fileWriter = Manager.createDataSink(source, dest);
			fileWriter.open();
			fileWriter.start();
		} catch (Exception e) {
			processException(e);
		}
		processor.start();
		player.start();
		btnStop.setEnabled(true);
	}

	private void stop() {
		btnStop.setEnabled(false);
		//停止捕获
		player.stop();
		processor.stop();
		try {
			fileWriter.stop();
			fileWriter.close();
		} catch (IOException e) {
			processException(e);
		}
		btnStart.setEnabled(true);
	}
	
	private void processException(Exception e)
	{
		e.printStackTrace();
		JOptionPane.showMessageDialog(this, e.toString(), "错误", JOptionPane.ERROR_MESSAGE);
		System.exit(0);
	}
	/**
	 * 构建输出路径的URL
	 * @return
	 */
	private String getLocatorString()
	{

		if( JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(this))
		{
			return null;
		}
		File file = fileChooser.getSelectedFile();
		if (file==null) {
			return null;
		}
		String locatorString = file.getAbsolutePath();
		if( !locatorString.endsWith(".avi" ))
		{
			locatorString += ".avi";
		}
		locatorString = "file://" + locatorString;
		return locatorString;
	}
	/**
	 * 获得截取视频的设备信息
	 * @return 设备信息
	 */
	private CaptureDeviceInfo getCaptureDeviceInfo()
	{
		
	////////////////////////////////////////////////////////
	//由于JMF与win7不兼容缘故，这段话可以避免你在win7里面运行时出现问题的
		String name = VFWCapture.capGetDriverDescriptionName(0);
		if(name != null){
			VFWSourceStream.autoDetect(0);
		}else{
			
		}
		
		//定义视频格式
		Format videoFormat = new VideoFormat(null);
		@SuppressWarnings("unchecked")
		//获得满足条件的视频设备列表
		Vector<CaptureDeviceInfo> deviceList = CaptureDeviceManager.getDeviceList(videoFormat);
		String deviceName = null;
		if(deviceList.size()<1)
		{
			JOptionPane.showMessageDialog(this, "未检测到视频输入设备。", "错误", JOptionPane.ERROR_MESSAGE);
			System.exit(0);
		}else if(deviceList.size() == 1){
			deviceName = deviceList.get(0).getName();
		}else{
			String[] deviceNames = new String[deviceList.size()];
			for (int i = 0; i < deviceList.size(); i ++ ) {			
				deviceNames[i] = deviceList.get(i).getName();			
			}
			deviceName = (String) JOptionPane.showInputDialog(this, "请选择视频输入设备", "请选择", JOptionPane.QUESTION_MESSAGE, null, deviceNames, deviceNames[0]);
			if (deviceName==null) {
				System.exit(0);
			}
		}
		//
		CaptureDeviceInfo captureDevice;
		for (int i = 0; i < deviceList.size(); i ++ ) {	
			captureDevice = deviceList.get(i);
			if( deviceName.equals(captureDevice.getName()) )
			{
				return captureDevice;
			}
		}
		return null;
	}
	
	private CaptureDeviceInfo getAudioDeviceInfo(){
			//定义视频格式
			Format audioFormat = new AudioFormat(AudioFormat.LINEAR,44100,16,2);
			@SuppressWarnings("unchecked")
			//获得满足条件的视频设备列表
			Vector<CaptureDeviceInfo> deviceList = CaptureDeviceManager.getDeviceList(audioFormat);
			String deviceName = null;
			if(deviceList.size()<1)
			{
				JOptionPane.showMessageDialog(this, "未检测到视频输入设备。", "错误", JOptionPane.ERROR_MESSAGE);
				System.exit(0);
			}else if(deviceList.size() == 1){
				deviceName = deviceList.get(0).getName();
			}else{
				String[] deviceNames = new String[deviceList.size()];
				for (int i = 0; i < deviceList.size(); i ++ ) {			
					deviceNames[i] = deviceList.get(i).getName();			
				}
				deviceName = (String) JOptionPane.showInputDialog(this, "请选择视频输入设备", "请选择", JOptionPane.QUESTION_MESSAGE, null, deviceNames, deviceNames[0]);
				if (deviceName==null) {
					System.exit(0);
				}
			}
			//
			CaptureDeviceInfo captureDevice;
			for (int i = 0; i < deviceList.size(); i ++ ) {	
				captureDevice = deviceList.get(i);
				if( deviceName.equals(captureDevice.getName()) )
				{
					return captureDevice;
				}
			}
			return null;
	}
}