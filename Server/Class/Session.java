package Server.Class;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

public class Session implements Serializable {
	private Date loginStarted;
	private boolean isActive = true;
	private String uid;
	private Set<String> rights;

	public Session(String uid, Set<String> rights) {
		this.uid = uid;
		this.rights = rights;
		this.loginStarted = new Date();
	};

	public String getUid() {
		return uid;
	}

	public Boolean hasPermission(String action) {
		return rights.contains(action);
	}

	public boolean getSessionState() {
		return isActive
				? timeoutCheck()
				: false;
	}

	private Boolean timeoutCheck() {
		long timeoutThreshold = 120; // 120 seconds or 2 minutes
		long elapsedTime = (System.currentTimeMillis() - loginStarted.getTime()) / 1000;
		this.isActive = elapsedTime < timeoutThreshold;

		return this.isActive;
	}
}