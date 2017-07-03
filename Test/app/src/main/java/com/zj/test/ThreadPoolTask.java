package com.zj.test;

import java.io.Serializable;

public class ThreadPoolTask implements Runnable, Serializable {
	private Object attachData;

	ThreadPoolTask(Object tasks) {
		this.attachData = tasks;
	}

	public void run() {
		System.out.println(" " + attachData);
		attachData = null;
	}

	public Object getTask() {
		return this.attachData;
	}
}
