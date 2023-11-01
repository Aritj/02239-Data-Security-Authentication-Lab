package Cryptography;

import java.util.Date;

public class Session {	
	private Date loginStarted;
	private boolean isActive = true;
	private String uid;
	
	public Session(String uid) {
		this.uid = uid;
		this.loginStarted = new Date();
	};
	
	public Date getSessionTime() {
		return loginStarted;
	}
	
	public boolean getSessionState() {
		return isActive 
			? timeoutCheck()
			: false;
	}
	
	public void killSession() {
		this.isActive = false;
	}
	
	public String getUser() {
		return this.uid;
	}

    private Boolean timeoutCheck() {
        long timeoutThreshold = 100;
		long elapsedTime = System.currentTimeMillis() - loginStarted.getTime();
        this.isActive = elapsedTime < timeoutThreshold;

		return this.isActive;
    }
}