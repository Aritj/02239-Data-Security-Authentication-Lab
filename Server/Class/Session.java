package Server.Class;

import java.io.Serializable;
import java.util.Date;

public class Session implements Serializable {	
	private Date loginStarted;
	private boolean isActive = true;
	private String uid;
	
	public Session(String uid) {
		this.uid = uid;
		this.loginStarted = new Date();
	};

	public String getUid() {
		return uid;
	}
	
	public boolean getSessionState() {
		return isActive 
			? timeoutCheck()
			: false;
	}

	private Boolean timeoutCheck() {
		long timeoutThreshold = 120; // 2 minutes
		long elapsedTime = (System.currentTimeMillis() - loginStarted.getTime()) / 1000; // Convert milliseconds to seconds
		this.isActive = elapsedTime < timeoutThreshold;
	
		return this.isActive;
	}
}