package com.MineSweeping;

public class MineSweeping {
	public static void main(String[] args) {
		MyFrame myFrame = new MyFrame();
		Thread thread = new Thread(myFrame);
		thread.start();
	}
}