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
	// �ֱ��Ǽ򵥡��еȡ���������������������򵥺��еȼ�������Ϊ�����Σ����Ѽ�������Ϊ������
	private final int EASY_MINE_COUNT = 10;
	private final int NORMAL_MINE_COUNT = 30;
	private final int HARD_MINE_COUNT = 85;

	// �ֱ��Ǽ򵥡��еȡ�������������������߳����򵥺��еȼ�������Ϊ�����Σ����Ѽ�������Ϊ������
	private final int EASY_MINE_LENGTH = 9;
	private final int NORMAL_MINE_LENGTH = 14;
	private final int HARD_MINE_LENGTH = 29;
	private final int HARD_MINE_WIDTH = 14;

	// ������������ĳ���
	private final int EASY_MODE = 1;
	private final int NORMAL_MODE = 2;
	private final int HARD_MODE = 3;

	private int minesCount;// ʵʱ��¼ʣ������
	private int timeCount;// ��¼��ǰ��ʱ
	private int level = 1;// ��¼��ǰ�Ѽ�����Ϸ��ʼ��ʱĬ��Ϊ�򵥼����Ѷ�
	private boolean timeFlag;// ����Ƿ��Ǵ��ڼ�ʱ״̬
	private boolean firstClickFlag;// ����Ƿ��Ѿ������һ��
	private boolean canBeClicked;// ������а����Ƿ���Ա��������Ҫ������Ϸ�������ð������������
	private boolean winFlag, loseFlag;// ��־�Ƿ�ʤ����ʧ��
	// ����ÿһ��Ԫ��Χ�׵�����ʱ��Ҫ������������
	private int[][] move = { { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 } };// �˸�����

	// ���ò˵������˵����Ӳ˵� ��record��
	private JMenuBar menuBar;
	private JMenu set, record, setLevel, about;// �˵����������á���¼�����ڵ�
	private JMenuItem exitChoice, easyMode, normalMode, hardMode, information, history;
	private JButton restart;// ���¿�ʼ����
	private JButton[][] mines;// ��������
	private int[][] map;// ��ͼ��0����û���ף�1��������
	private JLabel restMine, time;// ʣ����������ʱ

	private JOptionPane win, lose;// ʤ����ʧ���Ƿֱ𵯳��Ի���
	private MinerRecord[] easyRecord, normalRecord, hardRecord;//��¼��ʷ��¼������

	// ���췽��
	public MyFrame() {
		timeFlag = false;
		firstClickFlag = true;
		canBeClicked = true;
		winFlag = false;
		loseFlag = false;
		init();
	}

	// ��ʼ������
	public void init() {
		setLayout(null);// ��ʹ�ò��ֹ�������ÿ���ؼ���λ����setBounds�趨

		// ���˵����뵽������
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

		// ����minesCount��ֵ
		if (level == EASY_MODE)
			minesCount = EASY_MINE_COUNT;
		else if (level == NORMAL_MODE)
			minesCount = NORMAL_MINE_COUNT;
		else
			minesCount = HARD_MINE_COUNT;

		// ΪeasyModeѡ�����ʱ�����
		easyMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();// �����Ƴ������ķ���
				level = EASY_MODE;// �ı伶��
				restartGame();// ���ø�����Ϸ�ķ���
			}
		});

		// ΪnormalModeѡ�����ʱ�����
		normalMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();
				level = NORMAL_MODE;
				restartGame();
			}
		});

		// ΪhardModeѡ�����ʱ�����
		hardMode.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				removeComp();
				level = HARD_MODE;
				restartGame();
			}
		});

		// ΪexitChoiceѡ������¼�����
		exitChoice.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);// �˳�
			}
		});

		//Ϊhistoryѡ������¼�����
		history.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				RecordFrame recordFrame = new RecordFrame();//����RankList����
			}
		});

		//Ϊinformation����¼�����
		information.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				//������Ϣ
				new JOptionPane().showMessageDialog(null, "�꼶��2015��\n�༶��2��\n������������\nѧ�ţ�3015216038\n������Ʒ��");
			}
		});

		easyRecord = new MinerRecord[5];
		normalRecord = new MinerRecord[5];
		hardRecord = new MinerRecord[5];
		
		//����Ҳ����ļ����ȴ����ļ�
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

		//��ȡ��¼��ʷ��¼���ļ������뵽������
		if (level == EASY_MODE) {
			FileReader frEasy = null;
			try {
				frEasy = new FileReader("easyRecord.txt");
				for (int i = 0; i < 5; i++) {
					StringBuffer name = new StringBuffer("");
					char ch = '.';
					//������ʱ��֮����'$'�ָ���
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

		setMineArea();// ���������ķ���

		setResizable(false);// ���ɵ��ڽ����С
		setVisible(true);//���ÿɼ�

		myEvent();// �رմ����¼�
	}

	// ��������������
	public void setMineArea() {
		// �򵥼���
		if (level == EASY_MODE) {
			// ����restMine��time��ǩ��restart���������뵽������ʵ�λ��
			restMine = new JLabel("Rest:" + EASY_MINE_COUNT);
			restMine.setBounds(20, 20, 250, 30);
			restMine.setFont(new Font("Serif", Font.PLAIN, 28));
			time = new JLabel("Time: 0s");
			time.setBounds(310, 20, 250, 30);
			time.setFont(new Font("Serif", Font.PLAIN, 28));
			restart = new JButton();
			restart.setBounds(175, 20, 85, 30);
			restart.setText("Restart!");// Restart����
			add(restMine);
			add(restart);
			add(time);

			setBounds(0, 0, 450, 540);// ���ý����С
			makeArea(EASY_MINE_LENGTH);// ������������ķ���
			makeMines(EASY_MINE_LENGTH, EASY_MINE_COUNT);// ���׷���
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

		// Ϊrestart��������¼�����
		restart.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				removeComp();
				restartGame();
			}
		});
	}

	// �򵥺���ͨ���������������������ķ���
	public void makeArea(int size) {
		mines = new JButton[size][size];// size�����������߳�,�����㹻�����İ���

		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				mines[i][j] = new JButton();
				mines[i][j].addMouseListener(this); // �����������

				mines[i][j].setBorder(BorderFactory.createRaisedBevelBorder());// ���ð���͹��
				mines[i][j].setName(i + "_" + j); // �����ж��ǵ�����ĸ���ť
				// Ϊÿ�����������ʵ�λ�ã������߳�Ϊ45
				mines[i][j].setBounds(i * 45 + 17, j * 45 + 60, 45, 45); 
				add(mines[i][j]);// ���뵽������
			}
		}
	}

	// ���Ѽ���������������ķ���
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

	// �򵥺���ͨ������������׷���
	public void makeMines(int size, int amount) {
		int i = 0, x, y;
		Random random = new Random();
		map = new int[size][size];
		while (i < amount) {
			// ������������������
			x = random.nextInt(size);
			y = random.nextInt(size);

			// �ж������������λ���Ƿ��Ѿ�����ף�0��ʾ��δ�����1��ʾ�Ѿ����
			if (map[x][y] == 0) {
				map[x][y] = 1;
				i++; // �����ظ���������
			}
		}
	}

	// ���Ѽ������׷���
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

	// ͨ��ʣ��isEnabled���������жϵ�ǰ�Ƿ��Ѿ�ɨ�����е���
	@SuppressWarnings("static-access")
	private void checkSuccess() {
		int count = 0;
		if (level == EASY_MODE) {
			for (int i = 0; i < EASY_MINE_LENGTH; i++) {
				for (int j = 0; j < EASY_MINE_LENGTH; j++) {
					// ����һ��isEnabled�İ����ͽ������õ�count����+1
					if (mines[i][j].isEnabled()) {
						count++;
					}
				}
			}

			// ���count���ڵ�ǰ�����������ʤ��
			if (count == EASY_MINE_COUNT) {
				showMine();
				timeFlag = false;
				canBeClicked = false;
				winFlag = true;
				loseFlag = false;
				
				//����˴���Ϸ��ʱ��ȵ������ļ�¼Ҫ�̣����¼������������¼�¼
				if(timeCount<Integer.valueOf(easyRecord[4].getTime())){
					win = new JOptionPane();
					// ��¼�������
					String name = win.showInputDialog("Unbelievable!\nYou've cleared all the mines in " + timeCount
							+ " seconds!\nIt's a new record!\nPlease write down your name here~");
					if (name.equals(""))// ������û������������Ĭ������Ϊ��Miner��
						name = "Miner";
					
					//�ж��²����ļ�¼Ӧ�����ڵ�λ��
					int j = 0;
					while(Integer.valueOf(easyRecord[j].getTime())<timeCount){
						if(j==4){
							break;
						}
						j++;
					}
					
					//����ļ�¼���κ���һλ
					for (int k = 4; k > j; k--) {
						easyRecord[k].setName(easyRecord[k - 1].getName());
						easyRecord[k].setTime(easyRecord[k - 1].getTime());
					}
					//���²����ļ�¼��ŵ���Ӧλ��
					easyRecord[j].setName(name);
					easyRecord[j].setTime(timeCount+"");
					
					//��д��¼�ļ�
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
				//��Ϸʤ������δ���Ƽ�¼
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

		// ���ʧ��
		if (loseFlag) {
			lose = new JOptionPane();
			lose.showMessageDialog(null, "               You Lose!");
			loseFlag = false;
		}
	}

	// �����������
	private void dfs(int x, int y, int d) {
		map[x][y] = 2;
		int i, tx, ty, count = 0;
		if (level == EASY_MODE) {
			// ѭ��move��ά�����߱��������ڵİ˸���
			for (i = 0; i < 8; i++) {
				tx = x + move[i][0];
				ty = y + move[i][1];

				// �ж��Ƿ��Ǻ����λ��
				if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH) {
					if (map[tx][ty] == 1) {
						count++;// �õ㸽������ͳ��
					}
				}
			}

			// ����������Χû���ף�������ȱ�������ͳ����Χ�ĵ�
			if (count == 0) {
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());// ���ð�������
				for (i = 0; i < 8; i++) {
					tx = x + move[i][0];
					ty = y + move[i][1];

					if (tx >= 0 && tx < EASY_MINE_LENGTH && ty >= 0 && ty < EASY_MINE_LENGTH && map[tx][ty] != 2) {
						dfs(tx, ty, d + 1);
					}
				}
			}
			// �����Χ�������Լ������ף���ʾ��Χ������
			else if (map[x][y] != 1) {
				mines[x][y].setText(count + "");
				mines[x][y].setBorder(BorderFactory.createLoweredBevelBorder());
			}

			// ��������ף����óɼ�����Ч
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
						count++;// �õ㸽������ͳ��
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

	// ��Ϸ����������׵�λ����ʾ����
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

	// �˳��¼�
	public void myEvent() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				System.exit(0);
			}
		});
	}

	// �Ƴ�����ķ���
	public void removeComp() {
		if (level == EASY_MODE) {
			// ��������������ȫ���Ƴ�
			for (int i = 0; i < EASY_MINE_LENGTH; i++) {
				for (int j = 0; j < EASY_MINE_LENGTH; j++) {
					remove(mines[i][j]);
				}
			}

			// �Ƴ�restMine��time��ǩ��restart����
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

	// ������Ϸ�ķ���
	public void restartGame() {
		timeFlag = false;
		firstClickFlag = true;
		canBeClicked = true;
		winFlag = false;
		loseFlag = false;
		timeCount = 0;
		init();// ���µ���init()����
		revalidate();// ˢ���������
	}

	// ������¼�����
	public void mouseClicked(MouseEvent e) {
		// ��ȡ������İ���
		Object obj = e.getSource();
		String[] tmp_str = ((JButton) obj).getName().split("_");
		int x = Integer.parseInt(tmp_str[0]);
		int y = Integer.parseInt(tmp_str[1]);

		// �����Ҽ����¼�
		if (e.getButton() == 3) {
			// ֻ��isEnabled�İ����ſ��Ա��Ҽ�����
			if (mines[x][y].isEnabled() && canBeClicked && !firstClickFlag) {
				// �Ҽ�����������״̬�����л������д��ڡ�v��״̬���������Ч
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
		// ����������¼�
		else if (e.getButton() == 1 && canBeClicked) {
			// ���ڡ�v�����ߡ�?��״̬�İ������������Ч
			clickMines(x, y);
		}
	}

	// �������
	public void clickMines(int x, int y) {
		if (map[x][y] == 1 && ("v".equals(mines[x][y].getText()) || "?".equals(mines[x][y].getText()))) {
		}
		// �ȵ��׵����
		else if (map[x][y] == 1) {
			timeFlag = false;
			firstClickFlag = true;
			canBeClicked = false;
			showMine();
			loseFlag = true;
		}
		// δ�ȵ��׵����
		else if (!"v".equals(mines[x][y].getText()) && !"?".equals(mines[x][y].getText())) {
			dfs(x, y, 0);
			if (firstClickFlag) {
				firstClickFlag = false;
				timeFlag = true;
			}
		}

		// �����Ϸ�Ƿ����
		if (!winFlag)
			checkSuccess();
	}

	// ������Χ���죨��v����������
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

	// ��ĳһ������Χ�ķ��װ���ȫ���㿪
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
		//��ȡ������İ����ĺ�������
		Object obj = e.getSource();
		String[] tmp_str = ((JButton) obj).getName().split("_");
		int x = Integer.parseInt(tmp_str[0]);
		int y = Integer.parseInt(tmp_str[1]);

		// ���Ҽ�ͬʱ���£�ֻ���Ҽ����ͷŲ���Ч��
		if (e.getModifiersEx() == (e.BUTTON1_DOWN_MASK + e.BUTTON3_DOWN_MASK)) {
			int tx, ty;
			// ��Χ����������Χ�������������v������ͬ�ſ��������Χ��δ����İ���
			if (Integer.valueOf(mines[x][y].getText()) == countv(x, y)) {
				clearAround(x, y);
			}
		}
	}

	// ���߳�������ʱ
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

	// ����Ϊ�̳�MouseListener�ӿڱ�������ʵ�ֵķ���
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
