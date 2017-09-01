package com.MineSweeping;

import java.awt.Color;
import java.awt.Font;
import java.awt.TextArea;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.invoke.LambdaMetafactory;
import java.util.Random;

import javax.imageio.ImageTypeSpecifier;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

public class MyFrame extends JFrame implements MouseListener, Runnable {
	// 分别是简单、中等、困难三个级别的雷数，简单和中等级别雷区为正方形，困难级别雷区为长方形
	private final int EASY_MINE_COUNT = 10;
	private final int NORMAL_MINE_COUNT = 30;
	private final int HARD_MINE_COUNT = 85;

	// 分别是简单、中等、困难三个级别的雷区边长，简单和中等级别雷区为正方形，困难级别雷区为长方形
	private final int EASY_MINE_LENGTH = 9;
	private final int NORMAL_MINE_LENGTH = 14;
	private final int HARD_MINE_LENGTH = 29;
	private final int HARD_MINE_WIDTH = 14;

	// 定义三个级别的常量
	private final int EASY_MODE = 1;
	private final int NORMAL_MODE = 2;
	private final int HARD_MODE = 3;

	private int minesCount;// 实时记录剩余雷数
	private int timeCount;// 记录当前用时
	private int level = 1;// 记录当前难级别，游戏初始化时默认为简单级别难度
	private boolean timeFlag;// 标记是否是处于计时状态
	private boolean firstClickFlag;// 标记是否已经点击过一次
	private boolean canBeClicked;// 标记所有按键是否可以被点击，主要用于游戏结束后不让按键继续被点击
	private boolean winFlag, loseFlag;// 标志是否胜利、失败
	// 计算每一单元周围雷的数量时需要检查的三个方向
	private int[][] move = { { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 } };// 八个方向

	// 设置菜单栏、菜单、子菜单 （record）
	private JMenuBar menuBar;
	private JMenu set, record, setLevel, about;// 菜单，包含设置、纪录、关于等
	private JMenuItem exitChoice, easyMode, normalMode, hardMode, information, history;
	private JButton restart;// 重新开始按键
	private JButton[][] mines;// 雷区按键
	private int[][] map;// 地图，0代表没有雷，1代表有雷
	private JLabel restMine, time;// 剩余雷数、用时

	private JOptionPane win, lose;// 胜利和失败是分别弹出对话框
	private MinerRecord[] easyRecord, normalRecord, hardRecord;//记录历史纪录的数组

	// 构造方法
	public MyFrame() {
		timeFlag = false;
		firstClickFlag = true;
		canBeClicked = true;
		winFlag = false;
		loseFlag = false;
		init();
	}

	// 初始化方法
	public void init() {
		setLayout(null);// 不使用布局管理器，每个控件的位置用setBounds设定

		// 将菜单加入到界面中
		menuBar = new JMenuBar();
		set = new JMenu("Set");
		record = new JMenu("Record");
		about = new JMenu("About");
		menuBar.add(set);
		menuBar.add(record);
		menuBar.add(about);
		setLevel = new JMenu("Set Level");
		exitChoice = new JMenuItem("Exit");
		easyMode = new JMenuItem("Easy");
		normalMode = new JMenuItem("Normal");
		hardMode = new JMenuItem("Hard");
		information = new JMenuItem("Information");
		history = new JMenuItem("History");

		setLevel.add(easyMode);
		setLevel.add(normalMode);
		setLevel.add(hardMode);
		set.add(setLevel);
		set.add(exitChoice);
		record.add(history);
		about.add(information);
		setJMenuBar(menuBar);

		// 设置minesCount的值
		if (level == EASY_MODE)
			minesCount = EASY_MINE_COUNT;
		else if (level == NORMAL_MODE)
			minesCount = NORMAL_MINE_COUNT;
		else
			minesCount = HARD_MINE_COUNT;

		// 为easyMode选项添加时间监听
		easyMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();// 调用移除部件的方法
				level = EASY_MODE;// 改变级别
				restartGame();// 调用更新游戏的方法
			}
		});

		// 为normalMode选项添加时间监听
		normalMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();
				level = NORMAL_MODE;
				restartGame();
			}
		});

		// 为hardMode选项添加时间监听
		hardMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();
				level = HARD_MODE;
				restartGame();
			}
		});

		// 为exitChoice选项添加事件监听
		exitChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);// 退出
			}
		});

		//为history选项添加事件监听
		history.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				RecordFrame recordFrame = new RecordFrame();//弹出RankList界面
			}
		});

		//为information添加事件监听
		information.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//弹出信息
				new JOptionPane().showMessageDialog(null, "年级：2015级\n班级：2班\n姓名：龚明慧\n学号：3015216038\n荣誉出品！");
			}
		});

		easyRecord = new MinerRecord[5];
		normalRecord = new MinerRecord[5];
		hardRecord = new MinerRecord[5];
		
		//如果找不到文件就先创建文件
		File fileEasy = new File("easyRecord.txt");
		if(!fileEasy.exists()){
			FileWriter fwEasy = null;
			try {
				fwEasy = new FileWriter("easyRecord.txt");
				fwEasy.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
				fwEasy.close();
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}
		
		File fileNoemal = new File("normalRecord.txt");
		if(!fileNoemal.exists()){
			FileWriter fwNormal = null;
			try {
				fwNormal = new FileWriter("normalRecord.txt");
				fwNormal.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
				fwNormal.close();
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}
		
		File fileHard = new File("hardRecord.txt");
		if(!fileHard.exists()){
			FileWriter fwHard = null;
			try {
				fwHard = new FileWriter("hardRecord.txt");
				fwHard.write("Miner$9999$Miner$9999$Miner$9999$Miner$9999$Miner$9999$");
				fwHard.close();
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}

		//读取记录历史纪录的文件，存入到数组中
		if (level == EASY_MODE) {
			FileReader frEasy = null;
			try {
				frEasy = new FileReader("easyRecord.txt");
				for (int i = 0; i < 5; i++) {
					StringBuffer name = new StringBuffer("");
					char ch = '.';
					//名字与时间之间由'$'分隔开
					while ((ch = (char) frEasy.read()) != '$') {
						name.append(ch);
					}
					StringBuffer time = new StringBuffer("");
					while ((ch = (char) frEasy.read()) != '$') {
						time.append(ch);
					}
					easyRecord[i] = new MinerRecord(name.toString(), time.toString());
				}
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}
		else if (level == NORMAL_MODE) {
			FileReader frNormal = null;
			try {
				frNormal = new FileReader("normalRecord.txt");
				for (int i = 0; i < 5; i++) {
					StringBuffer name = new StringBuffer("");
					char ch = '.';
					while ((ch = (char) frNormal.read()) != '$') {
						name.append(ch);
					}
					StringBuffer time = new StringBuffer("");
					while ((ch = (char) frNormal.read()) != '$') {
						time.append(ch);
					}
					normalRecord[i] = new MinerRecord(name.toString(), time.toString());
				}
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}
		else if (level == HARD_MODE) {
			FileReader frHard = null;
			try {
				frHard = new FileReader("hardRecord.txt");
				for (int i = 0; i < 5; i++) {
					StringBuffer name = new StringBuffer("");
					char ch = '.';
					while ((ch = (char) frHard.read()) != '$') {
						name.append(ch);
					}
					StringBuffer time = new StringBuffer("");
					while ((ch = (char) frHard.read()) != '$') {
						time.append(ch);
					}
					hardRecord[i] = new MinerRecord(name.toString(), time.toString());
				}
			} catch (IOException e2) {
				System.out.println(e2.toString());
			}
		}

		setMineArea();// 设置雷区的方法

		setResizable(false);// 不可调节界面大小
		setVisible(true);//设置可见

		myEvent();// 关闭窗口事件
	}

	// 设置雷区、界面
	public void setMineArea() {
		// 简单级别
		if (level == EASY_MODE) {
			// 设置restMine、time标签和restart按键并加入到界面的适当位置
			restMine = new JLabel("Rest:" + EASY_MINE_COUNT);
			restMine.setBounds(20, 20, 250, 30);
			restMine.setFont(new Font("Serif", Font.PLAIN, 28));
			time = new JLabel("Time: 0s");
			time.setBounds(310, 20, 250, 30);
			time.setFont(new Font("Serif", Font.PLAIN, 28));
			restart = new JButton();
			restart.setBounds(175, 20, 85, 30);
			restart.setText("Restart!");// Restart按键
			add(restMine);
			add(restart);
			add(time);

			setBounds(0, 0, 450, 540);// 设置界面大小
			makeArea(EASY_MINE_LENGTH);// 添加雷区按键的方法
			makeMines(EASY_MINE_LENGTH, EASY_MINE_COUNT);// 埋雷方法
		} 
		else if (level == NORMAL_MODE) {
			restMine = new JLabel("Rest:" + NORMAL_MINE_COUNT);
			restMine.setBounds(20, 20, 250, 30);
			restMine.setFont(new Font("Serif", Font.PLAIN, 28));
			time = new JLabel("Time: 0s");
			time.setBounds(540, 20, 250, 30);
			time.setFont(new Font("Serif", Font.PLAIN, 28));
			restart = new JButton();
			restart.setBounds(290, 20, 85, 30);
			restart.setText("Restart!");
			add(restMine);
			add(restart);
			add(time);

			setBounds(0, 0, 673, 760);
			makeArea(NORMAL_MINE_LENGTH);
			makeMines(NORMAL_MINE_LENGTH, NORMAL_MINE_COUNT);
		} 
		else if (level == HARD_MODE) {
			restMine = new JLabel("Rest:" + HARD_MINE_COUNT);
			restMine.setBounds(20, 20, 250, 30);
			restMine.setFont(new Font("Serif", Font.PLAIN, 28));
			time = new JLabel("Time: 0s");
			time.setBounds(1210, 20, 250, 30);
			time.setFont(new Font("Serif", Font.PLAIN, 28));
			restart = new JButton();
			restart.setBounds(627, 20, 85, 30);
			restart.setText("Restart!");
			add(restMine);
			add(restart);
			add(time);

			setBounds(0, 0, 1350, 760);
			makeArea(HARD_MINE_LENGTH, HARD_MINE_WIDTH);
			makeMines(HARD_MINE_LENGTH, HARD_MINE_WIDTH, HARD_MINE_COUNT);
		}

		// 为restart按键添加事件监听
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeComp();
				restartGame();
			}
		});
	}

	// 简单和普通两个级别的添加雷区按键的方法
	public void makeArea(int size) {
		mines = new JButton[size][size];// size参数是雷区边长,声明足够数量的按键

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mines[i][j] = new JButton();
				mines[i][j].addMouseListener(this); // 添加鼠标监听器

				mines[i][j].setBorder(BorderFactory.createRaisedBevelBorder());// 设置按键凸起
				mines[i][j].setName(i + "_" + j); // 方便判断是点击了哪个按钮
				// 为每个按键设置适当位置，按键边长为45
				mines[i][j].setBounds(i * 45 + 17, j * 45 + 60, 45, 45); 
				add(mines[i][j]);// 加入到界面中
			}
		}
	}

	// 困难级别添加雷区按键的方法
	public void makeArea(int length, int width) {
		mines = new JButton[length][width];

		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++) {
				mines[i][j] = new JButton();
				mines[i][j].addMouseListener(this);

				mines[i][j].setBorder(BorderFactory.createRaisedBevelBorder());
				mines[i][j].setName(i + "_" + j);
				mines[i][j].setBounds(i * 45 + 17, j * 45 + 60, 45, 45);
				add(mines[i][j]);
			}
		}
	}

	// 简单和普通两个级别的埋雷方法
	public void makeMines(int size, int amount) {
		int i = 0, x, y;
		Random random = new Random();
		map = new int[size][size];
		while (i < amount) {
			// 产生随机数，随机埋雷
			x = random.nextInt(size);
			y = random.nextInt(size);

			// 判断新随机产生的位置是否已经埋过雷，0表示还未埋过，1表示已经埋过
			if (map[x][y] == 0) {
				map[x][y] = 1;
				i++; // 不记重复产生的雷
			}
		}
	}

	// 困难级别埋雷方法
	public void makeMines(int length, int width, int amount) {
		int i = 0, x, y;
		Random random = new Random();
		map = new int[length][width];
		while (i < amount) {
			x = random.nextInt(length);
			y = random.nextInt(width);

			if (map[x][y] == 0) {
				map[x][y] = 1;
				i++;
			}
		}
	}

	// 通过剩余isEnabled按键数量判断当前是否已经扫光所有的雷
	@SuppressWarnings("static-access")
	private void checkSuccess() {
		int count = 0;
		if (level == EASY_MODE) {
			for (int i = 0; i < EASY_MINE_LENGTH; i++) {
				for (int j = 0; j < EASY_MINE_LENGTH; j++) {
					// 遇到一个isEnabled的按键就将计数用的count变量+1
					if (mines[i][j].isEnabled()) {
						count++;
					}
				}
			}

			// 如果count等于当前级别的雷数就胜利
			if (count == EASY_MINE_COUNT) {
				showMine();
				timeFlag = false;
				canBeClicked = false;
				winFlag = true;
				loseFlag = false;
				
				//如果此次游戏的时间比第五名的纪录要短，则记录玩家姓名，更新纪录
				if(timeCount<Integer.valueOf(easyRecord[4].getTime())){
					win = new JOptionPane();
					// 记录玩家姓名
					String name = win.showInputDialog("Unbelievable!\nYou've cleared all the mines in " + timeCount
							+ " seconds!\nIt's a new record!\nPlease write down your name here~");
					if (name.equals(""))// 如果玩家没有输入姓名，默认姓名为“Miner”
						name = "Miner";
					
					//判断新产生的纪录应该排在的位置
					int j = 0;
					while(Integer.valueOf(easyRecord[j].getTime())<timeCount){
						if(j==4){
							break;
						}
						j++;
					}
					
					//后面的纪录依次后移一位
					for (int k = 4; k > j; k--) {
						easyRecord[k].setName(easyRecord[k - 1].getName());
						easyRecord[k].setTime(easyRecord[k - 1].getTime());
					}
					//将新产生的纪录存放到相应位置
					easyRecord[j].setName(name);
					easyRecord[j].setTime(timeCount+"");
					
					//重写纪录文件
					FileWriter fwEasy = null;
					try {
						fwEasy = new FileWriter("easyRecord.txt");
						fwEasy.write(easyRecord[0].getName()+"$"+easyRecord[0].getTime()+"$"
									+easyRecord[1].getName()+"$"+easyRecord[1].getTime()+"$"
									+easyRecord[2].getName()+"$"+easyRecord[2].getTime()+"$"
									+easyRecord[3].getName()+"$"+easyRecord[3].getTime()+"$"
									+easyRecord[4].getName()+"$"+easyRecord[4].getTime()+"$");
						fwEasy.close();
					} catch (IOException e2) {
						System.out.println(e2.toString());
					}
				}
				//游戏胜利但是未打破纪录
				else{
					win = new JOptionPane();
					win.showMessageDialog(null, "Congratulations!\nYou've cleared all the mines in " + timeCount
							+ " seconds!");
				}
			}
		}
		else if (level == NORMAL_MODE) {
			for (int i = 0; i < NORMAL_MINE_LENGTH; i++) {
				for (int j = 0; j < NORMAL_MINE_LENGTH; j++) {
					if (mines[i][j].isEnabled()) {
						count++;
					}
				}
			}

			if (count == NORMAL_MINE_COUNT) {
				showMine();
				timeFlag = false;
				canBeClicked = false;
				winFlag = true;
				loseFlag = false;
				
				if(timeCount<Integer.valueOf(normalRecord[4].getTime())){
					win = new JOptionPane();
					String name = win.showInputDialog("Unbelievable!\nYou've cleared all the mines in " + timeCount
							+ " seconds!\nIt's a new record!\nPlease write down your name here~");
					if (name.equals(""))
						name = "Miner";
					
					int j = 0;
					while(Integer.valueOf(normalRecord[j].getTime())<timeCount){
						if(j==4){
							break;
						}
						j++;
					}
					
					for (int k = 4; k > j; k--) {
						normalRecord[k].setName(normalRecord[k - 1].getName());
						normalRecord[k].setTime(normalRecord[k - 1].getTime());
					}
					normalRecord[j].setName(name);
					normalRecord[j].setTime(timeCount+"");
					
					FileWriter fwEasy = null;
					try {
						fwEasy = new FileWriter("normalRecord.txt");
						fwEasy.write(normalRecord[0].getName()+"$"+normalRecord[0].getTime()+"$"
									+normalRecord[1].getName()+"$"+normalRecord[1].getTime()+"$"
									+normalRecord[2].getName()+"$"+normalRecord[2].getTime()+"$"
									+normalRecord[3].getName()+"$"+normalRecord[3].getTime()+"$"
									+normalRecord[4].getName()+"$"+normalRecord[4].getTime()+"$");
						fwEasy.close();
					} catch (IOException e2) {
						System.out.println(e2.toString());
					}
				}
				else{
					win = new JOptionPane();
					win.showMessageDialog(null, "Congratulations!\nYou've cleared all the mines in " + timeCount
							+ " seconds!");
				}
			}
		} 
		else if (level == HARD_MODE) {
			for (int i = 0; i < HARD_MINE_LENGTH; i++) {
				for (int j = 0; j < HARD_MINE_WIDTH; j++) {
					if (mines[i][j].isEnabled()) {
						count++;
					}
				}
			}

			if (count == HARD_MINE_COUNT) {
				showMine();
				timeFlag = false;
				canBeClicked = false;
				winFlag = true;
				loseFlag = false;

				if(timeCount<Integer.valueOf(hardRecord[4].getTime())){
					win = new JOptionPane();
					String name = win.showInputDialog("Unbelievable!\nYou've cleared all the mines in " + timeCount
							+ " seconds!\nIt's a new record!\nPlease write down your name here~");
					if (name.equals(""))
						name = "Miner";
					
					int j = 0;
					while(Integer.valueOf(hardRecord[j].getTime())<timeCount){
						if(j==4){
							break;
						}
						j++;
					}
					
					for (int k = 4; k > j; k--) {
						hardRecord[k].setName(hardRecord[k - 1].getName());
						hardRecord[k].setTime(hardRecord[k - 1].getTime());
					}
					hardRecord[j].setName(name);
					hardRecord[j].setTime(timeCount+"");
					
					FileWriter fwEasy = null;
					try {
						fwEasy = new FileWriter("hardRecord.txt");
						fwEasy.write(hardRecord[0].getName()+"$"+hardRecord[0].getTime()+"$"
									+hardRecord[1].getName()+"$"+hardRecord[1].getTime()+"$"
									+hardRecord[2].getName()+"$"+hardRecord[2].getTime()+"$"
									+hardRecord[3].getName()+"$"+hardRecord[3].getTime()+"$"
									+hardRecord[4].getName()+"$"+hardRecord[4].getTime()+"$");
						fwEasy.close();
					} catch (IOException e2) {
						System.out.println(e2.toString());
					}
				}
				else{
					win = new JOptionPane();
					win.showMessageDialog(null, "Congratulations!\nYou've cleared all the mines in " + timeCount
							+ " seconds!");
				}
			}
		}

		// 如果失败
		if (loseFlag) {
			lose = new JOptionPane();
			lose.showMessageDialog(null, "               You Lose!");
			loseFlag = false;
		}
	}

	// 深度优先搜索
	private void dfs(int x, int y, int d) {
		map[x][y] = 2;
		int i, tx, ty, count = 0;
		if (level == EASY_MODE) {
			// 循环move二维数组走遍与其相邻的八个点
			for (i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];

				// 判断是否是合理的位置
				if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH) {
					if (map[tx][ty] == 1) {
						count++;// 该点附近雷数统计
					}
				}
			}

			// 如果这个点周围没有雷，深度优先遍历搜索统计周围的点
			if (count == 0) {
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());// 设置按键凹下
				for (i = 0; i < 8; i++) {
					tx = x + move[i][0];
					ty = y + move[i][1];

					if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH && map[tx][ty] != 2) {
						dfs(tx, ty, d + 1);
					}
				}
			}
			// 如果周围有雷且自己不是雷，显示周围的雷数
			else if (map[x][y] != 1) {
				mines[x][y].setText(count + "");
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
			}

			// 如果不是雷，设置成监听无效
			if (map[x][y] != 1)
				mines[x][y].setEnabled(false);
		} 
		else if (level == NORMAL_MODE) {
			for (i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];

				if (tx >= 0 && tx < NORMAL_MINE_LENGTH && ty >= 0 && ty < NORMAL_MINE_LENGTH) {
					if (map[tx][ty] == 1) {
						count++;
					}
				}
			}

			if (count == 0) {
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
				for (i = 0; i < 8; i++) {
					tx = x + move[i][0];
					ty = y + move[i][1];

					if (tx >= 0 && tx < NORMAL_MINE_LENGTH && ty >= 0 && ty < NORMAL_MINE_LENGTH && map[tx][ty] != 2) {
						dfs(tx, ty, d + 1);
					}
				}
			} 
			else if (map[x][y] != 1) {
				mines[x][y].setText(count + "");
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
			}

			if (map[x][y] != 1)
				mines[x][y].setEnabled(false);
		} 
		else {
			for (i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];

				if (tx >= 0 && tx < HARD_MINE_LENGTH && ty >= 0 && ty < HARD_MINE_WIDTH) {
					if (map[tx][ty] == 1) {
						count++;// 该点附近雷数统计
					}
				}
			}

			if (count == 0) {
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
				for (i = 0; i < 8; i++) {
					tx = x + move[i][0];
					ty = y + move[i][1];

					if (tx >= 0 && tx < HARD_MINE_LENGTH && ty >= 0 && ty < HARD_MINE_WIDTH && map[tx][ty] != 2) {
						dfs(tx, ty, d + 1);
					}
				}
			} 
			else if (map[x][y] != 1) {
				mines[x][y].setText(count + "");
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
			}

			if (map[x][y] != 1)
				mines[x][y].setEnabled(false);
		}
	}

	// 游戏最后将所有有雷的位置显示出来
	private void showMine() {
		if (level == EASY_MODE) {
			for (int i = 0; i < EASY_MINE_LENGTH; i++) {
				for (int j = 0; j < EASY_MINE_LENGTH; j++) {
					if (map[i][j] == 1) {
						mines[i][j].setText("#");
					}
				}
			}
		} else if (level == NORMAL_MODE) {
			for (int i = 0; i < NORMAL_MINE_LENGTH; i++) {
				for (int j = 0; j < NORMAL_MINE_LENGTH; j++) {
					if (map[i][j] == 1) {
						mines[i][j].setText("#");
					}
				}
			}
		} else if (level == HARD_MODE) {
			for (int i = 0; i < HARD_MINE_LENGTH; i++) {
				for (int j = 0; j < HARD_MINE_WIDTH; j++) {
					if (map[i][j] == 1) {
						mines[i][j].setText("#");
					}
				}
			}
		}
	}

	// 退出事件
	public void myEvent() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}

	// 移除组件的方法
	public void removeComp() {
		if (level == EASY_MODE) {
			// 将所有雷区按键全部移除
			for (int i = 0; i < EASY_MINE_LENGTH; i++) {
				for (int j = 0; j < EASY_MINE_LENGTH; j++) {
					remove(mines[i][j]);
				}
			}

			// 移除restMine、time标签和restart按键
			remove(restMine);
			remove(time);
			remove(restart);
		} else if (level == NORMAL_MODE) {
			for (int i = 0; i < NORMAL_MINE_LENGTH; i++) {
				for (int j = 0; j < NORMAL_MINE_LENGTH; j++) {
					remove(mines[i][j]);
				}
			}

			remove(restMine);
			remove(time);
			remove(restart);
		} else if (level == HARD_MODE) {
			for (int i = 0; i < HARD_MINE_LENGTH; i++) {
				for (int j = 0; j < HARD_MINE_WIDTH; j++) {
					remove(mines[i][j]);
				}
			}

			remove(restMine);
			remove(time);
			remove(restart);
		}
	}

	// 更新游戏的方法
	public void restartGame() {
		timeFlag = false;
		firstClickFlag = true;
		canBeClicked = true;
		winFlag = false;
		loseFlag = false;
		timeCount = 0;
		init();// 重新调用init()函数
		revalidate();// 刷新整个面板
	}

	// 鼠标点击事件监听
	public void mouseClicked(MouseEvent e) {
		// 获取鼠标点击的按键
		Object obj = e.getSource();
		String[] tmp_str = ((JButton) obj).getName().split("_");
		int x = Integer.parseInt(tmp_str[0]);
		int y = Integer.parseInt(tmp_str[1]);

		// 单击右键的事件
		if (e.getButton() == 3) {
			// 只有isEnabled的按键才可以被右键单击
			if (mines[x][y].isEnabled() && canBeClicked && !firstClickFlag) {
				// 右键单击的三个状态来回切换，其中处于“v”状态左键单击无效
				if ("v".equals(mines[x][y].getText())) {
					mines[x][y].setText("?");
				} else if ("?".equals(mines[x][y].getText())) {
					mines[x][y].setText("");
					restMine.setText("Rest:" + (++minesCount));
				} else {
					mines[x][y].setText("v");
					restMine.setText("Rest:" + (--minesCount));
				}
			}
		}
		// 单击左键的事件
		else if (e.getButton() == 1 && canBeClicked) {
			// 处于“v”或者“?”状态的按键左键单击无效
			clickMines(x, y);
		}
	}

	// 点击按键
	public void clickMines(int x, int y) {
		if (map[x][y] == 1 && ("v".equals(mines[x][y].getText()) || "?".equals(mines[x][y].getText()))) {
		}
		// 踩到雷的情况
		else if (map[x][y] == 1) {
			timeFlag = false;
			firstClickFlag = true;
			canBeClicked = false;
			showMine();
			loseFlag = true;
		}
		// 未踩到雷的情况
		else if (!"v".equals(mines[x][y].getText()) && !"?".equals(mines[x][y].getText())) {
			dfs(x, y, 0);
			if (firstClickFlag) {
				firstClickFlag = false;
				timeFlag = true;
			}
		}

		// 检查游戏是否结束
		if (!winFlag)
			checkSuccess();
	}

	// 计算周围插旗（“v”）的数量
	public int countv(int x, int y) {
		int tx, ty, count = 0;
		if (level == EASY_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH
						&& "v".equals(mines[tx][ty].getText())) {
					count++;
				}
			}
		} else if (level == NORMAL_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < NORMAL_MINE_LENGTH && ty >= 0 && ty < NORMAL_MINE_LENGTH
						&& "v".equals(mines[tx][ty].getText())) {
					count++;
				}
			}
		} else if (level == HARD_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < HARD_MINE_LENGTH && ty >= 0 && ty < HARD_MINE_WIDTH
						&& "v".equals(mines[tx][ty].getText())) {
					count++;
				}
			}
		}
		return count;
	}

	// 将某一数字周围的非雷按键全部点开
	public void clearAround(int x, int y) {
		int tx, ty;
		if (level == EASY_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH) {
					clickMines(tx, ty);
				}

			}
		} else if (level == NORMAL_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < NORMAL_MINE_LENGTH && ty >= 0 && ty < NORMAL_MINE_LENGTH) {
					clickMines(tx, ty);
				}

			}
		} else if (level == HARD_MODE) {
			for (int i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];
				if (tx >= 0 && tx < HARD_MINE_LENGTH && ty >= 0 && ty < HARD_MINE_WIDTH) {
					clickMines(tx, ty);
				}

			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		//获取被点击的按键的横纵坐标
		Object obj = e.getSource();
		String[] tmp_str = ((JButton) obj).getName().split("_");
		int x = Integer.parseInt(tmp_str[0]);
		int y = Integer.parseInt(tmp_str[1]);

		// 左右键同时按下（只有右键先释放才有效）
		if (e.getModifiersEx() == (e.BUTTON1_DOWN_MASK + e.BUTTON3_DOWN_MASK)) {
			int tx, ty;
			// 周围的雷数和周围插旗的数量（“v”）相同才可以清除周围的未插旗的按键
			if (Integer.valueOf(mines[x][y].getText()) == countv(x, y)) {
				clearAround(x, y);
			}
		}
	}

	// 多线程用来计时
	@Override
	public void run() {
		// TODO Auto-generated method stub
		timeCount = 0;
		while (true) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if (timeFlag) {
				timeCount++;
				time.setText("Time: " + timeCount + "s");
			}
		}
	}

	// 以下为继承MouseListener接口必须重载实现的方法
	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}
}
