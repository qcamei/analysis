package net.mofancy.analysis.jqueue;

import QueueManager.JQueueDBConnect;
import net.mofancy.analysis.properties.DatabaseProperties;

public class JqueueDBConnect extends JQueueDBConnect {
	@Override
	public void setMainDB() {
		this.message = "";
		try {
			setDatabaseConnection(-1, DatabaseProperties.NAME, "", DatabaseProperties.TYPE,DatabaseProperties.SERVER,DatabaseProperties.USERNAME,DatabaseProperties.PASSWORD,DatabaseProperties.NAME,DatabaseProperties.PORT,DatabaseProperties.FULLURL);

		} catch (Exception e) {
			System.out.println(e.toString());
			this.message = ("Error: Cannot use connect to " + DatabaseProperties.NAME + ". " + e.toString());
		}
	}
}
