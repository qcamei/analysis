package net.mofancy.analysis.postgres.segment;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import QueueManager.JQueue;
import QueueManager.Job;
import QueueManager.JobSender;
import global.util.Authentication;
import net.mofancy.analysis.properties.DatabaseProperties;
import net.mofancy.analysis.util.JdbcUtils;

public class JqueueSegment {
	
	private Logger log = LoggerFactory.getLogger(JqueueSegment.class);
	
	private String jobId;
	
	private String user ;
	
	private String datasetName;
	
	private int dataset;
	
	private String fullURL;
	
	private String  projectId;
	
	private String poolId;
	
	private String projectName;
	
	private String checkJob;
	
	private String schema;
	
	private String dbname;
	
	private Authentication authentication;
	
	private Job job;
	
	
	public JqueueSegment (String user,int dataset,String fullURL,String projectName,String checkJob,Job job) {
		
		List<Map<String,Object>> databases = JdbcUtils.select(dataset);
		
		if(databases!=null&&databases.size()>0) {
			
			Map<String, Object> map = databases.get(0);
			String temp = "jdbc:postgresql://LOCALHOST:5432/DBNAME?currentSchema=SCHEMA&useUnicode=true&characterEncoding=utf8&autoReconnect=true&failOverReadOnly=false&useSSL=false&user=USER&password=PASSWORD";
	    	String url = temp.replace("LOCALHOST", map.get("dburl").toString()).replace("DBNAME", map.get("dbname").toString()).replace("SCHEMA", map.get("dbschema").toString()).replace("USER", "postgres").replace("PASSWORD", map.get("dbpwd").toString());
			
			this.poolId = map.get("dataset_key").toString();
			this.datasetName = map.get("dataset_name").toString();
			this.fullURL = url;
			this.schema = map.get("dbschema").toString();
			this.dbname = map.get("dbname").toString();
		}
		//properties.getProperty("jdbc.url").toString()+"&user="+USERNAME+"&password="+PASSWORD;
		
		this.user = user;
		this.dataset = dataset;
		
		this.projectName = projectName;
		this.checkJob = checkJob;
		this.job = initJQueueJob(job);
	}
	
	public Job getJob() {
		return job;
	}

	public void setJob(Job job) {
		this.job = job;
	}



	public Job getJob(String job_id) {
		try {
			JobSender jsend = new JobSender("GET" + job_id);
			Object o = sendJob(jsend);
			if (o == null) {
				JQueue.log("Cannot get job: " + job_id + ", returned null.");
				return null;
			}
			else if (!(o instanceof Job)) {
				JQueue.log("Cannot get job: " + job_id + ", not returned as Job, type: " + o.getClass().getSimpleName() + ", value: " + o.toString());
				return null;
			}
			return (Job) o;
		} catch (Exception e) {
			JQueue.log("Cannot get job: " + job_id);
			e.printStackTrace();
			return null;
		}
	}
	public Job initJQueueJob(Job job) {
		job.setUser(this.user);
		job.setDataset(datasetName, dataset);
		job.setDatabaseConnection(this.datasetName, this.schema, "POSTGRE", this.fullURL, this.dbname, DatabaseProperties.SCHEMA, DatabaseProperties.JQTYPE, DatabaseProperties.PORT, DatabaseProperties.FULLURL);
		job.setRequestedPage(this.getClass().getSimpleName());
		job.setRequestedURL(fullURL);
		job.setProjectID(""+projectId);
		job.setPoolID(""+poolId);
		job.setName(projectName);
		
		return job;
	}
	public boolean runImmediateJob(Job job, String message) {
		job.setType(Job.RUN_IMMEDIATE);
		if (runJQueueJob()) {
			jobId = job.getJobId() + "";
			if(!StringUtils.isEmpty(message)) {
//				String queryJobStatus = message + ". Please wait...";
			}
			return true;
		} else {
			log.error("Cannot submit run-immediate job " + job.getClass().getSimpleName() + " to JQueue");
			return false;
		}

	}
	public boolean runJQueueJob() {
		jobId = job.getJobId() + "";
		if (sendJobtoJQueue(job)) {
			if (checkJob.equals("1")) {
				log.error("Job " + job.getJobType() + " " + job.getName() + " submited");
			}
			System.out.println("start running JQueueJob: " + job.getName() + " " + job.getJobType());
			return true;
		}
		else {
			if (checkJob.equals("1")) {
				log.error("Job " + job.getJobType() + " " + job.getName() + " cannot be submited");
			}
			System.out.println("Cannot add jqueue job.");
			return false;
		}
	}
	protected boolean startJQueue() {
		try {
			String command = this.authentication.getDatasetParam().getJQueueStartScript();
			Runtime.getRuntime().exec(command);
			return true;
		}
		catch (Exception e1) {
			System.out.println("Cannot start process. Error:" + e1.getMessage());
			e1.printStackTrace();
			log.error("Cannot start JQueue process:", e1);
			return false;
		}
	}
	protected boolean stopJQueue(int param) {
		try {
			String command = this.authentication.getDatasetParam().getJQueueStopScript(param);
			Runtime.getRuntime().exec(command);
			return true;
		}
		catch (Exception e1) {
			System.out.println("Cannot stop process. Error:" + e1.getMessage());
			e1.printStackTrace();
			log.error("Cannot stop JQueue process:", e1);
			return false;
		}
	}
	protected Object sendJob(JobSender jsend) {
		return sendJob(jsend, 0);
	}
	protected Object sendJob(JobSender jsend, int tryTimes) {
		String host = "localhost";
		int socket = -999;
		try {

			return jsend.send(host, socket);
		}
		catch (Exception e) {
			if (tryTimes < 5) {
				if (e instanceof java.net.ConnectException) {
					System.out.println("JQueue job sendJob error, tries: " + (tryTimes + 1) + ". " + e.getMessage());
					if (e.getMessage().indexOf("Connection refused") >= 0) {
						if (tryTimes == 0) startJQueue();
						try {Thread.sleep(1000);}
						catch (Exception e1) {}
						return sendJob(jsend, tryTimes + 1);
					}
				}
				if (e instanceof java.net.SocketException)
				{
					if (e.getMessage().indexOf("Connection reset") >= 0)
					{
						try {Thread.sleep(1000);}
						catch (Exception e1) {}
						return sendJob(jsend, tryTimes + 1);
					}
				}
			}

			System.out.println("host=" + host + ";socket=" + socket);
			log.error("JQueue job sendJob error: " , e);
			e.printStackTrace();
		}
		return null;
	}

	protected boolean sendJobtoJQueue(Job job) {
		return sendJobtoJQueue(job, 0);
	}

	protected boolean sendJobtoJQueue(Job job, int tryTimes) {
		JobSender jsend = new JobSender(job);
		sendJob(jsend);
		return true;
	}

	public void jobFinished_Remove(String sender, String param) throws Exception {// JQueue job function
		removeFinishedJob();
	}

	public void jobFinished_RemoveAll(String sender, String param) throws Exception {// JQueue job function
		try {
			JobSender jsend = new JobSender("DROPALL");
			sendJob(jsend);
		} catch (Exception ex) {
			JQueue.log("Cannot run remove all finished job");
		}
	}

	public boolean checkRequiredJQueueVersion(String required_version) {
		try {
			JobSender jsend = new JobSender("COMPARE" + required_version);
			String result = (String) sendJob(jsend);
			boolean flag = result.startsWith("LOW");// required version is lower than running JQueue version, i.e. OK to
													// run
			if (!flag) {
				log.error("Cannot run requested job, the JQueue version is lower than required: " + required_version
						+ ". Please contact administrator for update.");
				System.out.println("JQueue version is lower:" + result);
			}
			return !flag;
		} catch (Exception e) {
			System.out.println("JQueue job error:" + e);
			return false;
		}
	}

	protected boolean removeFinishedJob() throws Exception {// JQueue job function
		String job_id = jobId;
		return removeFinishedJob(job_id);
	}

	protected boolean removeFinishedJob(int job_id) throws Exception {// JQueue job function
		return removeFinishedJob(job_id + "");
	}

	protected boolean removeFinishedJob(String job_id) throws Exception {// JQueue job function
		if (StringUtils.isEmpty(job_id)) {
			// JQueue.log("Cannot remove finished job. Empty job id");
			return false;
		}

		try {
			JobSender jsend = new JobSender("DROP" + job_id);
			sendJob(jsend);
			JQueue.log("Job " + job_id + " removed");
		} catch (Exception ex) {
			JQueue.log("Cannot run remove finished job: " + job_id);
			return false;
		}

		return true;
	}
	
}