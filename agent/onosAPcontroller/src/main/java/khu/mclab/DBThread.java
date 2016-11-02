package khu.mclab;


import java.net.*;
import java.util.concurrent.*;
import java.io.*; 
import khu.mclab.*;


public class DBThread implements Runnable{

	private Socket connectionSocket;

	public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
	public static final String MYSQL_URL = "jdbc:mysql://163.180.118.44:3306/AP_Information?"
			+ "user=root&password=mclab1";
	public static final String MYSQL_USER = "root";
	public static final String MYSQL_PASSWORD = "mclab1";
	
	private MySQLdb db = null;
	private ExecutorService executor;
	private	ServerSocket listenSocket; 
	

	private static final int AP_REGISTRATION_REQUEST = 0;
	private static final int AP_REGISTRATION_RESPONSE = 1;
	private static final int AP_STATE_UPDATE_REQUEST = 2;
	private static final int AP_STATE_UPDATE_RESPONSE = 3;
	private static final int AP_LIST_REQUEST = 4;
	private static final int AP_LIST_RESPONSE = 5;
	private static final int AP_TERMINATE_REQUEST = 6;
	private static final int AP_TERMINATE_RESPONSE = 7;


	private static final int END_OF_MESSAGE = 99;

	private static final String delimiter = "\\|";



	public DBThread(Socket connectionSocket) {
		this.connectionSocket = connectionSocket;
		db = new MySQLdb(MYSQL_DRIVER,MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
	}


	public DBThread(MySQLdb db, ExecutorService executor, ServerSocket listenSocket) {
		this.db = db;
		this.executor = executor;
		this.listenSocket = listenSocket;
	}


	protected String MakeMessage(int mType, String param) {
		return String.format("%d|%s|99", mType, param);
	}

	public int byteToint(byte[] arr){
		return (arr[0] & 0xff)<<24 | (arr[1] & 0xff)<<16 |
				(arr[2] & 0xff)<<8 | (arr[3] & 0xff);
	}

	@Override
		public void run() {
			try {
				listenSocket = new ServerSocket(12015);
				System.out.println("Start");
				Socket connectionSocket;
				packetHandler packetHandlerThread;

				System.out.println("Waiting to request");
				while(true) {
					connectionSocket = listenSocket.accept();
					if(connectionSocket != null) {
					executor.execute(new packetHandler(connectionSocket, db));
					}
					/*
					while((connectionSocket = listenSocket.accept()) != null)
					{
						executor.execute(new packetHandler(connectionSocket, db));
					}*/

				}
			}

		catch(Exception e) {
			e.printStackTrace();
		}
}


	private class packetHandler implements Runnable {

		private Socket connectionSocket;
		private MySQLdb db;

		public packetHandler(Socket connectionSocket, MySQLdb db) {
			this.connectionSocket = connectionSocket;
			this.db = db;
		}
		public void run() {

			try {
				while(true)
				{
					InputStream in = connectionSocket.getInputStream();
					BufferedInputStream br = new BufferedInputStream(in);

					OutputStream out = connectionSocket.getOutputStream();
					BufferedOutputStream bo = new BufferedOutputStream(out);


					byte[] recv = new byte[1024];
					int length = br.read(recv);


					String message = new String(recv, 0, length);
					System.out.println(message);

					String parser[];
					parser = message.split(delimiter);

					int msg_type = Integer.parseInt(parser[0]);


					AP ap = new AP(parser);
					//AP ap = parseAP(parser);

					String sendMessage = null;

					/*
					 * processing by msg_type.
					 * make message to send ack.
					 */
					switch(msg_type) {

					case AP_REGISTRATION_REQUEST:
						db.insert(ap);
						sendMessage = MakeMessage(AP_REGISTRATION_RESPONSE, "");
						break;
					case AP_REGISTRATION_RESPONSE:
						break;
					case AP_STATE_UPDATE_REQUEST:
						//UPDATE DB
						db.update(ap);
						sendMessage = MakeMessage(AP_STATE_UPDATE_RESPONSE, "");
						break;
					case AP_STATE_UPDATE_RESPONSE:
						break;
					case AP_LIST_REQUEST:
						/*
						 * SELECT DB
						 * MAKE MESSAGE
						 */
						sendMessage = MakeMessage(AP_LIST_RESPONSE, "");
						break;
					case AP_LIST_RESPONSE:
						break;
					case AP_TERMINATE_REQUEST:
						br.close();
						bo.close();
						connectionSocket.close();
						return;
					default:
						break;
					}

					byte[] send = sendMessage.getBytes();

					bo.write(send);
					bo.flush();



				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

}
