package com.tmser.core.listener.impl;

import com.tmser.core.bo.LogObject;
import com.tmser.core.listener.LogListener;

public class BlankLogListener implements LogListener{

	@Override
	public void addLog(String loginfo) {
		System.out.println("#####"+loginfo);
	}

	@Override
	public void addLog(LogObject logObject) {
		System.out.println("#####"+logObject.getLogInfo());
	}

}
