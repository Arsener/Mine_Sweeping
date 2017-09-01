package com.MineSweeping;

import java.awt.CardLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

public class RecordFrame extends JFrame {
	// 此界面的一些组件
	private JButton reset;
	private JLabel title, rankTitle, nameTitle, timeTitle;
	private JLabel[] rankRecord, nameRecord, timeRecord;
	private JRadioButton easy, normal, hard;
	private ButtonGroup group;

	public RecordFrame() {
		init();
	}

	public void init() {
		reset = new JButton("Reset");//重置纪录的按键
		title = new JLabel("RankList");//大标题
		title.setFont(new Font("Serif", Font.PLAIN, 36));// 设置大标题的字体

		rankTitle = new JLabel("Rank");
		nameTitle = new JLabel("Name");
		timeTitle = new JLabel("Time(s)");
		rankRecord = new JLabel[5];// 显示排名的标签
		nameRecord = new JLabel[5];// 显示玩家姓名的标签
		timeRecord = new JLabel[5];// 显示时间的标签

		//切换显示的纪录级别
		easy = new JRadioButton("Easy", true);
		normal = new JRadioButton("Normal");
		hard = new JRadioButton("Hard");
		group = new ButtonGroup();

		group.add(easy);
		group.add(normal);
		group.add(hard);
		easy.setBounds(20, 300, 55, 30);
		normal.setBounds(75, 300, 70, 30);
		hard.setBounds(145, 300, 55, 30);

		// 从历史记录文件中读取记录，一开始弹出此界面时先显示EASY_MODE级别的记录
		FileReader fr = null;
		try {
			fr = new FileReader("easyRecord.txt");

			for (int i = 0; i < 5; i++) {
				//设置每个标签显示的内容
				rankRecord[i] = new JLabel("" + (i + 1));
				rankRecord[i].setBounds(50, 60 + (2 * i + 1) * 20 + 10, 50, 30);
				nameRecord[i] = new JLabel("");
				char ch = '.';
				while ((ch = (char) fr.read()) != '$')// 文件中两条信息中由'$'分割
					nameRecord[i].setText(nameRecord[i].getText() + ch);
				nameRecord[i].setBounds(100, 60 + (2 * i + 1) * 20 + 10, 110, 30);
				timeRecord[i] = new JLabel("");
				while ((ch = (char) fr.read()) != '$')
					timeRecord[i].setText(timeRecord[i].getText() + ch);
				timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}

		setVisible(true);
		setLayout(null);
		setResizable(false);
		setBounds(520, 190, 310, 385);
		title.setBounds(90, 10, 150, 50);
		reset.setBounds(210, 300, 70, 30);
		rankTitle.setBounds(50, 60, 60, 30);
		nameTitle.setBounds(110, 60, 80, 30);
		timeTitle.setBounds(210, 60, 80, 30);

		// 将各组件添加到RecordFrame中
		add(title);
		add(reset);
		add(rankTitle);
		add(nameTitle);
		add(timeTitle);
		add(easy);
		add(normal);
		add(hard);
		for (int i = 0; i < 5; i++) {
			add(rankRecord[i]);
			add(nameRecord[i]);
			add(timeRecord[i]);
		}

		// 设置事件监听以切换不同级别的记录显示
		easy.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileReader fr = null;
				try {
					fr = new FileReader("easyRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) fr.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) fr.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
			}
		});

		normal.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileReader fr = null;
				try {
					fr = new FileReader("normalRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) fr.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) fr.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
			}
		});

		hard.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileReader fr = null;
				try {
					fr = new FileReader("hardRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) fr.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) fr.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
			}
		});

		//设置重置按键的事件监听
		reset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				FileWriter fwEasy = null;
				FileWriter fwNormal = null;
				FileWriter fwHard = null;
				//将三个记录历史成绩的文件全部重写
				try {
					fwEasy = new FileWriter("easyRecord.txt");
					fwEasy.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
					fwEasy.close();
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
				
				try {
					fwNormal = new FileWriter("normalRecord.txt");
					fwNormal.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
					fwNormal.close();
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
				
				try {
					fwHard = new FileWriter("hardRecord.txt");
					fwHard.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
					fwHard.close();
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}

				FileReader frEasy = null;
				//各自级别的记录都需要刷新
				try {
					frEasy = new FileReader("easyRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) frEasy.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) frEasy.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
				
				FileReader frNormal = null;
				try {
					frNormal = new FileReader("normalRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) frNormal.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) frNormal.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
				
				FileReader frHard = null;
				try {
					frHard = new FileReader("hardRecord.txt");

					for (int i = 0; i < 5; i++) {
						nameRecord[i].setText("");
						char ch = '.';
						while ((ch = (char) frHard.read()) != '$')
							nameRecord[i].setText(nameRecord[i].getText() + ch);
						timeRecord[i].setText("");
						while ((ch = (char) frHard.read()) != '$')
							timeRecord[i].setText(timeRecord[i].getText() + ch);
						timeRecord[i].setBounds(210, 60 + (2 * i + 1) * 20 + 10, 80, 30);
					}
				} catch (IOException e2) {
					System.out.println(e2.toString());
				}
			}
		});
		// 关闭事件
		myEvent();
	}

	//关闭窗口事件
	public void myEvent() {
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				dispose();// 释放
			}
		});
	}
}