package mclab;
public class HeartbeatThread implements Runnable{ 
private MySQLdb db;   public HeartbeatThread(MySQLdb db) {      this.db = db;    } public HeartbeatThread() {  // TODO Auto-generated constructor stub }  public void run() {  db.heartbeat(); }}
