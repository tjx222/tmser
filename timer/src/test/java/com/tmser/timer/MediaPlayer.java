/**
 * 
 */
package com.tmser.timer;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.ControllerEvent;
import javax.media.ControllerListener;
import javax.media.EndOfMediaEvent;
import javax.media.Manager;
import javax.media.NoPlayerException;
import javax.media.Player;
import javax.media.PrefetchCompleteEvent;
import javax.media.RealizeCompleteEvent;
import javax.media.Time;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

public class MediaPlayer extends JFrame implements ControllerListener {
	static TrayIcon trayIcon = null; // 托盘图标
	static SystemTray tray = null; // 本操作系统托盘的实例  
	
    public static void main(String[] args) {
    	MediaPlayer sp = new MediaPlayer();
        sp.play();
    }
   
    private Player player;
    private Component visual;
    private Component control = null;
   
    MediaPlayer(){
    	  if (SystemTray.isSupported()) // 如果操作系统支持托盘  
          {  
              this.tray();  
          }  
    }
    
    void tray() {
		tray = SystemTray.getSystemTray(); // 获得本操作系统托盘的实例
		ImageIcon icon = new ImageIcon(getClass().getResource("/icon_clock_alt.png")); // 将要显示到托盘中的图标
		
		PopupMenu pop = new PopupMenu(); // 构造一个右键弹出式菜单
		MenuItem show = new MenuItem("打开程序(s)");
		MenuItem exit = new MenuItem("退出程序(x)");
		
		pop.add(show);
		pop.add(exit);
		trayIcon = new TrayIcon(icon.getImage(), "开心农场收菜工", pop);
		// 添加鼠标监听器，当鼠标在托盘图标上双击时，默认显示窗口
		trayIcon.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) // 鼠标双击
				{
					tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
					setExtendedState(JFrame.NORMAL);
					setVisible(true); // 显示窗口
					toFront();
				}
			}
		});
		
		show.addActionListener(new ActionListener() // 点击“显示窗口”菜单后将窗口显示出来
		{
			public void actionPerformed(ActionEvent e) {
				tray.remove(trayIcon); // 从系统的托盘实例中移除托盘图标
				setExtendedState(JFrame.NORMAL);
				setVisible(true); // 显示窗口
				toFront();
			}
		});
		
		exit.addActionListener(new ActionListener() // 点击“退出演示”菜单后退出程序
		{
			public void actionPerformed(ActionEvent e) {
			    if(player != null) {
	                    player.close();
	             }
				System.exit(0); // 退出程序
			}
		});
	}
    
    public void play(){
        this.setTitle("JMF Sample1");
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                if(player != null) {
                    player.close();
                }
                System.exit(0);
            }
            
            @Override
            public void windowIconified(WindowEvent e) {
                try {
                 tray.add(trayIcon); // 将托盘图标添加到系统的托盘实例中
                 setVisible(false); // 使窗口不可视
                 dispose();
                } catch (AWTException ex) {
                 ex.printStackTrace();
                }
               }
            
        });
        this.setSize(500,400);

        this.setVisible(true);
        URL url = null;
        try {
            //准备一个要播放的视频文件的URL
            url = new URL("file:/G:/ddd.avi");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }       
        try {
            //通过调用Manager的createPlayer方法来创建一个Player的对象
            //这个对象是媒体播放的核心控制对象
            player = Manager.createPlayer(url);
        } catch (NoPlayerException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        //对player对象注册监听器，能噶偶在相关事件发生的时候执行相关的动作
        player.addControllerListener(this);
       
        //让player对象进行相关的资源分配
        player.realize();
    }
   
    private int videoWidth = 0;
    private int videoHeight = 0;
    private int controlHeight = 30;
    private int insetWidth = 10;
    private int insetHeight = 30;
   
    //监听player的相关事件
    public void controllerUpdate(ControllerEvent ce) {
        if (ce instanceof RealizeCompleteEvent) {
            //player实例化完成后进行player播放前预处理
            player.prefetch();
        } else if (ce instanceof PrefetchCompleteEvent) {
            if (visual != null)
                return;

            //取得player中的播放视频的组件，并得到视频窗口的大小
            //然后把视频窗口的组件添加到Frame窗口中，
 /*           if ((visual = player.getVisualComponent()) != null) {
                Dimension size = visual.getPreferredSize();
                videoWidth = size.width;
                videoHeight = size.height;
                this.add(visual);
            } else {
                videoWidth = 320;
            }
           
            //取得player中的视频播放控制条组件，并把该组件添加到Frame窗口中
            if ((control = player.getControlPanelComponent()) != null) {
                controlHeight = control.getPreferredSize().height;
                this.add(control, BorderLayout.SOUTH);
            }
           
            //设定Frame窗口的大小，使得满足视频文件的默认大小
            this.setSize(videoWidth + insetWidth, videoHeight + controlHeight + insetHeight);
            this.validate();*/
           
            //启动视频播放组件开始播放
            player.start();
        } else if (ce instanceof EndOfMediaEvent) {
            //当播放视频完成后，把时间进度条恢复到开始，并再次重新开始播放
            player.setMediaTime(new Time(0));
            player.start();
        }
    }

}
