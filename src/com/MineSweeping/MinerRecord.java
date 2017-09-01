package com.MineSweeping;

//一个记录单元

public class MinerRecord {
	//类成员为玩家姓名和此玩家此次游戏的时间
	private String name;
	private String time;
	
	public MinerRecord(String name,String time) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.time = time;
	}

	//setter和getter方法
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
}