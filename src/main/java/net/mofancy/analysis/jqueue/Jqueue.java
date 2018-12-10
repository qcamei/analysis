package net.mofancy.analysis.jqueue;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import QueueManager.JQResponse;
import QueueManager.JQThreadFactory;
import QueueManager.JQueue;
import QueueManager.Job;
import QueueManager.Worker;
import global.util.DataSet;
import global.util.DataTable;
import global.util.DatasetParameter;
import global.util.Util;

public class Jqueue implements Runnable {
	public static String version = "1.4.10";
	protected Date startTime;
	protected JqueueDBConnect jqConn;
	protected DataSet ds = null;
	protected ArrayList<Job> batch = new ArrayList<Job>();
	protected HashMap<Integer, Job> jList = new HashMap<Integer, Job>();
	protected HashMap<Integer, Future> fList = new HashMap<Integer, Future>();
	protected HashMap<String, ExecutorService> exeList = new HashMap<String, ExecutorService>();
	protected String log_file = "Unknown";
	static int LOG_SIZE = 100000;
	static int LOG_ROTATION_COUNT = 2;
	private static final Logger logger = Logger.getLogger("JQueue");
	private static ArrayList<String> errorList = new ArrayList<String>();
    private static ArrayList<String> infoList = new ArrayList<String>();
	protected static String DT_FINISHEDJOBACTION = "DT_FINISHEDJOBACTION";

	public static String getParamValue(String[] args, String param, int start_ind) {
		param = "," + param.toLowerCase() + ",";
		for (int i = start_ind; i < args.length; i++) {
			if (param.contains("," + args[i].toLowerCase() + ",")) {
				return i < args.length - 1 ? args[(i + 1)] : "";
			}
		}
		return "";
	}

	public Jqueue(int socketNumber) throws Exception {
		if (socketNumber == 0) {
			socketNumber = jQueueSocketNum();
		}
		Thread t = new Thread(this);
		t.start();
		
		createLog(socketNumber);

		new ProcessChecker();

		this.ds = new DataSet("JQueueDataSet");
		this.jqConn = new JqueueDBConnect();
		this.jqConn.setMainDB();
		if (!Util.strEmpty(this.jqConn.getErrorMsg())) {
			log(this.jqConn.getErrorMsg());
		}
		this.jqConn.setDataSet(this.ds);

		this.startTime = new Date();
		log("JQueue version: " + version);
		log("Socket listening on port " + socketNumber);
		if (!this.jqConn.startingJQueueJob()) {
			log("Cannot clear zinc_process at starting: " + this.jqConn.getErrorMsg());
		}
		int[] jobid = new int[1];
		String[] status = new String[1];
		try {
			for (;;) {
				ServerSocket listener = new ServerSocket(socketNumber);
				Socket socket = listener.accept();

				Object ob = getMessage(socket);
				if (ob == null) {
					log("NO INSTRUCTION READ");
					sendResponse(socket, "Not OK");
				} else if ((ob instanceof Job)) {
					Job job = (Job) ob;
					jobid[0] = job.getJobId();
					if (jobid[0] < 0) {
						if (!this.jqConn.getJobID(jobid)) {
							log("Cannot run query getJobID");
						}
						job.setJobId(jobid[0]);
					}
					System.out.println("Job ID=" + jobid[0] + " " + job.getClass().getSimpleName());
					String jobType = job.getJobType();
					Date startDate = job.getStartTime();
					String jobName = safe(job.getName());
					if (!this.jqConn.getJobStatus(jobid[0], status)) {
						log("Cannot get job status: " + jobName + " ID: " + jobid[0] + " and type: " + jobType);
						continue;
					}
					if ((status[0].equalsIgnoreCase(Job.CANCELLED)) || (status[0].equalsIgnoreCase(Job.STOPPED))) {
						log("Job " + jobName + " ID: " + jobid[0] + " and type: " + jobType + " is cancelled");
						continue;
					}
					if ((startDate != null) && (isAfterNow(startDate))) {
						log("Job:" + jobName + " has job id:" + jobid[0] + " and type:" + jobType);
						log("Added job " + jobid[0] + " to batch queue for " + getDateString(startDate));
						if (!this.jqConn.submitProcess(job, startDate)) {
							log("Cannot run query submitProcess: " + this.jqConn.getErrorMsg() + "\n"
									+ this.jqConn.getQuery());
						}
						this.jList.put(Integer.valueOf(jobid[0]), job);
						this.batch.add(job);
					} else {
						log("Job:" + jobName + " has job id:" + jobid[0] + " and type:" + jobType);
						if (!this.jqConn.submitProcess(job, null)) {
							log("Cannot run query submitProcess: " + this.jqConn.getErrorMsg() + "\n"
									+ this.jqConn.getQuery());
						}
						this.jList.put(Integer.valueOf(jobid[0]), job);
						addToExecQueue(job);
					}
					sendResponse(socket, "OK");
				} else if ((ob instanceof String)) {
					String buf = ob.toString().toUpperCase();
					if (buf.startsWith("COUNT_CLIENTJOB")) {
						sendResponse(socket, countClientJobs(buf.substring(15)));
					} else if (buf.startsWith("PROGRESS")) {
						Job j = getJob(buf.substring(8));
						if (j != null) {
							sendResponse(socket, j.getProgress());
						} else {
							sendResponse(socket, "Not Found");
						}
					} else if (buf.equals("STATUS")) {
						sendResponse(socket, getResponse());
					} else {
						log("Received command: " + buf);
						if (buf.equals("NEW_JOB_ID")) {
							sendResponse(socket, Integer.valueOf(getJobID()));
						} else if (buf.equals("COUNT_JOB")) {
							sendResponse(socket, countJobs());
						} else if (buf.startsWith("FINISHED_JOB")) {
							sendResponse(socket, finishedJobs(buf.substring(12)));
						} else if (buf.equals("DROPALL")) {
							sendResponse(socket, removeAllFinishedJob());
						} else if (buf.startsWith("DROP")) {
							Job j = removeJob(buf.substring(4));
							if (j != null) {
								sendResponse(socket, j);
							} else {
								sendResponse(socket, "Not Found");
							}
						} else if (buf.startsWith("OUTQ")) {
							Job j = outQJob(buf.substring(4));
							if (j != null) {
								sendResponse(socket, j);
							} else {
								sendResponse(socket, "Not Found");
							}
						} else if (buf.startsWith("GET")) {
							Job j = getJob(buf.substring(3));
							if (j != null) {
								sendResponse(socket, j);
							} else {
								sendResponse(socket, "Not Found");
							}
						} else if (buf.equals("CHECK")) {
							String str = "Version: " + version + " Started time: "
									+ Util.dateToStr(this.startTime, "dd MMM yyyy HH:mm:ss");
							sendResponse(socket, str + "\n" + Util.getServerInfo());
						} else if (buf.startsWith("COMPARE")) {
							String check_version = buf.substring(7);
							int flag = Util.compareVersion(check_version, version);
							String str = (flag == 0 ? "EQ  " : flag < 0 ? "LOW " : "HIGH") + " Version: " + version
									+ " Started time: " + Util.dateToStr(this.startTime, "dd MMM yyyy HH:mm:ss");
							int n = this.jList.size();
							if (n > 0) {
								str = str + " " + n + " job" + (n > 1 ? "s" : "") + " in the queue currently.";
							}
							sendResponse(socket, str + "\n" + Util.getServerInfo());
						} else if (buf.equals("VIEW_ERROR")) {
							sendResponse(socket, view_error());
						} else if (buf.equals("VIEW_INFO")) {
							sendResponse(socket, view_info());
						} else if (buf.equals("LOG_FILE")) {
							sendResponse(socket, this.log_file);
						} else {
							log("INVALID MESSAGE RECEIVED " + buf);
							sendResponse(socket, buf);
						}
					}
				} else {
					log("INVALID OBJECT RECEIVED " + ob.toString());
					sendResponse(socket, "Not OK");
				}
				socket.close();
				listener.close();
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	private synchronized Job getJob(String jid) {
		Job job = null;
		try {
			int id = Integer.parseInt(jid);

			System.out.println("GET " + id + " " + Util.now());
			job = (Job) this.jList.get(Integer.valueOf(id));
			if (job == null) {
				log("Cannot get job " + jid + " return null. list size: " + this.jList.size());
			}
		} catch (Exception e) {
			log("GET invalid job number");
		}
		return job;
	}

	private synchronized Job removeJob(String jid) {
		Job job = null;
		try {
			int id = Util.atoi(jid);

			log("Drop job " + id);
			job = (Job) this.jList.remove(Integer.valueOf(id));
			if (job == null) {
				return null;
			}
			boolean job_live = job.isLive();
			boolean job_running = job.isRunning();

			log("Job " + id + " live:" + job_live + " running:" + job_running);
			job.setJobDropped();

			Future<?> fut = (Future<?>) this.fList.remove(Integer.valueOf(id));
			if (fut != null) {
				if (fut.cancel(true)) {
					log("Job " + id + " cancelled");
				} else {
					log("Job " + id + " could not be cancelled");
				}
			}
			if (job_live) {
				if (job_running) {
					if (!this.jqConn.stopJob(jid)) {
						log("Cannot stop job. Error: " + this.jqConn.getErrorMsg());
					}
				} else if (!this.jqConn.cancelJob(jid)) {
					log("Cannot cancel job. Error: " + this.jqConn.getErrorMsg());
				}
				if (!jqConn.removeFinishedJob(job.getJobId()+"")) {
					log("Cannot remove finished job. Error: " + this.jqConn.getErrorMsg());
				}
			} else if (!this.jqConn.removeFinishedJob(job.getJobId()+"")) {
				log("Cannot remove finished job. Error: " + this.jqConn.getErrorMsg());
			}
			String type = job.getJobType();
			if (type.equals(Job.RUN_IMMEDIATE)) {
				String executorServicePoolName = type + job.getJobId();
				ExecutorService executor = (ExecutorService) this.exeList.get(executorServicePoolName);
				if (executor != null) {
					executor.shutdown();
				}
				this.exeList.remove(executorServicePoolName);
				log("Removed ExecutorService " + executorServicePoolName + " for " + Job.RUN_IMMEDIATE + " jobs "
						+ job.getJobId());
			}
		} catch (Exception e) {
			log("DROP invalid job number." + e.toString());
		}
		return job;
	}

	private synchronized Job outQJob(String jid) {
		Job job = null;
		try {
			int id = Util.atoi(jid);

			log("Move job " + id + " from the Q, but not stop the query");
			job = (Job) this.jList.remove(Integer.valueOf(id));
			if (job == null) {
				return null;
			}
			if (job.getJobType().equals(Job.RUN_IMMEDIATE)) {
				return job;
			}
			boolean job_live = job.isLive();
			boolean job_running = job.isRunning();

			log("Job " + id + " live:" + job_live + " running:" + job_running);
			job.setJobDropped(false);

			Future<?> fut = (Future) this.fList.remove(Integer.valueOf(id));
			if (fut != null) {
				if (fut.cancel(true)) {
					log("Job " + id + " removed from Queue");
				} else {
					log("Job " + id + " could not be removed from Queue");
				}
			}
			if (job_live) {
				if (job_running) {
					if (!this.jqConn.stopJob(jid)) {
						log("Cannot stop job. Error: " + this.jqConn.getErrorMsg());
					}
				} else if (!this.jqConn.cancelJob(jid)) {
					log("Cannot cancel job. Error: " + this.jqConn.getErrorMsg());
				}
				if (!this.jqConn.removeFinishedJob(job.getJobId()+"")) {
					log("Cannot remove finished job. Error: " + this.jqConn.getErrorMsg());
				}
			} else if (!this.jqConn.removeFinishedJob(job.getJobId()+"")) {
				log("Cannot remove finished job. Error: " + this.jqConn.getErrorMsg());
			}
		} catch (Exception e) {
			log("OUTQ invalid job number." + e.toString());
		}
		return job;
	}

	private synchronized String removeAllFinishedJob() {
		try {
			String finished_jobs = "";
			String comma = "";
			for (Job job : this.jList.values()) {
				if ((!job.isLive()) && (!job.getJobType().equals(Job.RUN_IMMEDIATE))) {
					finished_jobs = finished_jobs + comma + job.getJobId();
					comma = ",";
				}
			}
			if (Util.strEmpty(finished_jobs)) {
				return "OK";
			}
			String[] arrayOfString;
			int j = (arrayOfString = finished_jobs.split("\\,")).length;
			for (int i = 0; i < j; i++) {
				String id = arrayOfString[i];

				removeJob(id);
			}
		} catch (Exception e) {
			log("Remove all finished jobs." + e.toString());
			return "NOT OK";
		}
		return "OK";
	}

	private synchronized void addToExecQueue(Job job) {
		ExecutorService executor = null;
		String type = job.getJobType();
		String executorServicePoolName;
		if (type.equals(Job.RUN_IMMEDIATE)) {
			executorServicePoolName = type + job.getJobId();
			executor = Executors.newFixedThreadPool(1, new JQThreadFactory());
			this.exeList.put(executorServicePoolName, executor);
			log("Added threadFactory: " + executorServicePoolName);
		} else {
			executorServicePoolName = "DS" + job.getDataset();
			executor = (ExecutorService) this.exeList.get(executorServicePoolName);
			if (executor == null) {
				executor = Executors.newFixedThreadPool(1, new JQThreadFactory());
				this.exeList.put(executorServicePoolName, executor);
				log("Added threadFactory: " + executorServicePoolName);
			}
		}
		Runnable worker = new Worker(job, this.jqConn, this.fList);

		Future<?> future = executor.submit(worker);
		this.fList.put(Integer.valueOf(job.getJobId()), future);
		log("Added job " + job.getJobId() + " to queue: " + executorServicePoolName);
	}

	private boolean isAfterNow(Date d) {
		return d.after(new Date());
	}

	private Object getMessage(Socket socket) throws Exception {
		Object ob = null;
		try {
			ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
			ob = in.readObject();
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
		return ob;
	}

	public void sendResponse(Socket socket, Job job) throws Exception {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if (job != null) {
				out.writeObject(job);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void sendResponse(Socket socket, String mess) throws Exception {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if (mess != null) {
				out.writeObject(mess);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void sendResponse(Socket socket, JQResponse resp) throws Exception {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
			if (resp != null) {
				out.writeObject(resp);
			}
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public void sendResponse(Socket socket, Object obj) throws Exception {
		try {
			ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

			out.writeObject(obj);
		} catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
		}
	}

	public static void log(String s) {
		try {
			logger.info(s);
			infoList.add(Util.now() + ": " + s);
			if (infoList.size() > 50) {
				infoList.remove(0);
			}
		} catch (Exception ex) {
			System.out.println(s);
		}
	}

	public static void logErr(String s) {
		try {
			logger.severe(s);
			errorList.add(Util.now() + ": " + s);
			if (errorList.size() > 50) {
				errorList.remove(0);
			}
		} catch (Exception ex) {
			System.out.println(s);
		}
	}

	private static String safe(String s) {
		if (s == null) {
			s = "";
		}
		return s;
	}

	private static String getDateString(Date d) {
		SimpleDateFormat f = new SimpleDateFormat("E dd.MM.yyyy 'at' hh:mm:ss a zzz");
		return f.format(d);
	}

	private String view_error() {
		StringBuilder sb = new StringBuilder();
		for (String s : errorList) {
			sb.append("\n" + s);
		}
		String str = sb.toString();
		return Util.strEmpty(str) ? "No errors" : str;
	}

	private String view_info() {
		StringBuilder sb = new StringBuilder();
		for (String s : infoList) {
			sb.append("\n" + s);
		}
		return sb.toString();
	}

	public static String tempPath() {
		String def = System.getProperty("user.dir");
		String s = Util.fileSeparator(def);
		if (!def.endsWith(s)) {
			def = def + s;
		}
		def = def + "tmp" + s;
		return def;
	}

	public static String defaultPath() {
		return System.getProperty("user.dir");
	}

	public static Properties jQueueProperties() {
		Properties prop = new Properties();
		String def = System.getProperty("user.dir");
		String s = Util.fileSeparator(def);
		if (!def.endsWith(s)) {
			def = def + s;
		}
		def = def + "properties" + s;
		try {
			InputStream in = new FileInputStream(def + "jqueue.properties");
			prop.load(in);
		} catch (Exception localException) {
		}
		return prop;
	}

	public static int jQueueSocketNum() {
		return Util.atoi(jQueueProperties().getProperty("socketNumber"), 9090);
	}

	public static String jQueueProperties(String s) {
		return jQueueProperties().getProperty(s);
	}

	private synchronized int getJobID() {
		int[] jobid = new int[1];
		if (!this.jqConn.getJobID(jobid)) {
			log("Cannot run query getJobID");
			jobid[0] = Util.ntoi(Long.valueOf(new Date().getTime() / 1000L));
		}
		return jobid[0];
	}

	private synchronized String countJobs() {
		int count_live = 0;
		int count_finished = 0;
		for (Entry<Integer, Job> entry : this.jList.entrySet()) {
			if (((Job) entry.getValue()).isLive()) {
				if (!((Job) entry.getValue()).getJobType().equals(Job.RUN_IMMEDIATE)) {
					count_live++;
				}
			} else {
				count_finished++;
			}
		}
		return count_live + (count_finished > 0 ? "|" + count_finished : "");
	}

	private synchronized String countClientJobs(String client) {
		int count_live = 0;
		int count_finished = 0;
		if (client.equalsIgnoreCase("ALL_CLIENT")) {
			for (Map.Entry<Integer, Job> entry : this.jList.entrySet()) {
				if (((Job) entry.getValue()).isLive()) {
					if (!((Job) entry.getValue()).getJobType().equals(Job.RUN_IMMEDIATE)) {
						count_live++;
					}
				} else {
					count_finished++;
				}
			}
		} else {
			for (Map.Entry<Integer, Job> entry : this.jList.entrySet()) {
				if (((Job) entry.getValue()).getDatasetName().equalsIgnoreCase(client)) {
					if (((Job) entry.getValue()).isLive()) {
						if (!((Job) entry.getValue()).getJobType().equals(Job.RUN_IMMEDIATE)) {
							count_live++;
						}
					} else {
						count_finished++;
					}
				}
			}
		}
		return count_live + (count_finished > 0 ? "|" + count_finished : "");
	}

	private synchronized DataTable finishedJobs(String client) {
		String DT_FINISH_JOB = "DT_FINISH_JOB";
		try {
			if (!this.jqConn.getFinishedJob(client, DT_FINISH_JOB)) {
				logErr("Cannot get finished jobs: " + this.jqConn.getErrorMessage());
				return null;
			}
		} catch (Exception e) {
			logErr("Cannot get finished jobs: " + e.toString());
			return null;
		}
		return this.ds.getTable(DT_FINISH_JOB);
	}

	private synchronized JQResponse getResponse() {
		JQResponse resp = new JQResponse();
		resp.setJobList(this.jList);
		resp.setLiveJobList(new ArrayList(this.fList.keySet()));
		resp.setBatchJobList(this.batch);

		return resp;
	}

	public static String JQueueLogFolder() {
		try {
			String logFolder = "C:/temp";
			if (!Util.strEmpty(logFolder)) {
				return logFolder;
			}
		}  catch (Exception e) {
			System.out.println(e.toString());
		}
		return DatasetParameter.JQueueLogFolder();
	}
	
	
	private void createLog(int socketNumber) throws Exception {
		try {
			String d = new SimpleDateFormat("yyyyMMdd").format(new Date());
			this.log_file = (JQueueLogFolder() + Util.fileSeparator() + "JQueue_" + socketNumber + "_" + d + ".log");
			Handler handler = new FileHandler(this.log_file, LOG_SIZE, LOG_ROTATION_COUNT);
			SimpleFormatter formatter = new SimpleFormatter();
			handler.setFormatter(formatter);
			logger.addHandler(handler);
		} catch (Exception ex) {
			System.out.println("Cannot create logger. " + ex.toString());
		}
	}
	
	@Override
	public void run() {
		for (;;) {
			try {
				Thread.sleep(30000L);

				Iterator<Job> iter = this.batch.iterator();
				synchronized (this) {
					Job j = (Job) iter.next();
					if (!isAfterNow(j.getStartTime())) {
						addToExecQueue(j);
						iter.remove();
					}
					if (iter.hasNext()) {
						continue;
					}
				}
				synchronized (this) {
					Iterator<Integer> i = this.jList.keySet().iterator();
					Integer id = (Integer) i.next();
					Job job = (Job) this.jList.get(id);
					if (!job.isLive()) {
						if ((new Date().getTime() - job.getFinishTime().getTime()) / 1000L / 60L > 10L) {
							removeJob(job.getJobId()+"");
						}
					}
					if (i.hasNext()) {
					}
				}
			} catch (Exception localException) {
			}
		}
	}
	
	
}


class ProcessChecker implements Runnable {
	public ProcessChecker() {
		Thread t = new Thread(this);
		t.start();
	}

	public void run() {
		try {
			for (;;) {
				Thread.sleep(3600000L);
				JQueue.log("Queue is still alive ...");
			}
		} catch (Exception e) {
			JQueue.logErr("Error in checking thread : " + e);
		}
	}
}

