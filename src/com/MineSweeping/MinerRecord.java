package com.MineSweeping;

//һ����¼��Ԫ

public class MinerRecord {
	//���ԱΪ��������ʹ���Ҵ˴���Ϸ��ʱ��
	private String name;
	private String time;
	
	public MinerRecord(String name,String time) {
		// TODO Auto-generated constructor stub
		this.name = name;
		this.time = time;
	}

	//setter��getter����
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