/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gotham_hazel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author junhoya924@khu.ac.kr
 */
public final class Update extends CommonGround{
    private static String[] c_originator;
    private static String[] c_nextHop;
    
    /*
         * input  - void                     
         * doing  - making web responding thread
                    update status periodically
         * output - void
     */    
    public Update() throws Exception {        
        webWaitingThread mainWebWaitingThread = new webWaitingThread();
        mainWebWaitingThread.start();
        
        while (true) {
            System.out.println("start updateStatus");
            updateStatus();

            m_matrixInstance.removeOrigin(c_originator);

            for (String c_originator1 : c_originator) {
                m_matrixInstance.addOrigin(c_originator1);
            }
            System.out.println("After addnode function");

            for (String c_nextHop1 : c_nextHop) {
                m_matrixInstance.addOrigin(c_nextHop1);                
            }

            m_matrixInstance.updateNextHop(m_myMAC, c_nextHop);
            m_matrixInstance.printMatrix(m_myMAC);

            System.out.println("sleep 10 sec");
            Thread.sleep(10000);
            System.out.println("wake up\n\n");
        }
    }
    
    /*
         * input  - void                     
         * doing  - update status from batman_adv
         * output - void
     */ 
    public void updateStatus() throws Exception {
        String command = command = "batctl o";
        shellCmd(command, "status.txt");//command 실행 및 status.txt파일로 write

        RandomAccessFile br = new RandomAccessFile("status.txt", "r");

        int nodeCount = 0;
        String[] temp = {"null", "null"};
        while ((temp[0] = br.readLine()) != null) {
            temp[1] = temp[0];
            nodeCount++;
        }

        if (nodeCount == 3) {
            if (temp[1].equalsIgnoreCase("No batman nodes in range ...")) {
                nodeCount = 0;
            } else {
                nodeCount = 1;
            }
        } else {
            nodeCount -= 2;
        }

        br.seek(0);

        c_originator = new String[nodeCount + 1];
        c_nextHop = new String[nodeCount];

        
        m_myMAC = br.readLine().substring(46, 63);
        System.out.println("my mac : " + m_myMAC);
        c_originator[0] = m_myMAC;
        
        br.readLine();
        for (int i = 0; i < nodeCount; i++) {
            temp[0] = br.readLine();
            c_originator[i+1] = temp[0].substring(0, 17);
            c_nextHop[i] = temp[0].substring(36, 53);
            System.out.println("nextHop : " + i + " " + c_nextHop[i]);
        }

        Object[] noDupNextHop = removeDuplicateArray(c_nextHop);
        c_nextHop = new String[noDupNextHop.length];

        for (int i = 0; i < noDupNextHop.length; i++) {
            c_nextHop[i] = noDupNextHop[i].toString();
        }

    }
    
    /*
         * input  - void                     
         * doing  - web waiting thread class
         * output - void
     */ 
    public class webWaitingThread extends Thread {
        private ServerSocket c_webSocket;
        private Socket c_connectionSocket;

        @Override
        public void run() {
            try {
                c_webSocket = new ServerSocket(m_masterToWebPort); // 서버소켓 생성하여 m_masterToWebPort 포트와 바인딩한다

                while (true) {
                    System.out.println("waiting web client");
                    c_connectionSocket = c_webSocket.accept();//클라이언트의 연결 요청이 올 때까지 실행을 멈추고 대기
                    System.out.println(c_connectionSocket.getInetAddress() + " 로부터(web) 연결 요청이 들어왔습니다");
                    communicationWeb webThread = new communicationWeb(c_connectionSocket, m_matrixInstance);
                    webThread.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(GOTHAM_hazel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Communication class with web
    public class communicationWeb extends Thread {
        private Socket c_conectionSocket;
        private Matrix c_matrixInstance;

        public communicationWeb(Socket connectionSocket, Matrix matrixInstance) {
            c_conectionSocket = connectionSocket;
            c_matrixInstance = matrixInstance;
        }

        @Override
        public void run() {
            try {
                DataOutputStream outToClient = new DataOutputStream(c_conectionSocket.getOutputStream());// 소켓의 출력스트림을 얻는다
                DataInputStream dis;
                InputStream is = c_conectionSocket.getInputStream();
                dis = new DataInputStream(is);

                while (true) {
                    if (dis.readUTF().equalsIgnoreCase("close")) {
                        break;
                    }

                    outToClient.writeUTF("nice to meet you ");
                    outToClient.writeUTF(String.valueOf(c_matrixInstance.getNodeCount()));
                    
                    System.out.println("before jsonObj & get node cout : " + c_matrixInstance.getNodeCount());
                     System.out.println("getNodeList(0) : " + c_matrixInstance.getOriginList().get(0));
                     System.out.println("getNodeList(0) : " + c_matrixInstance.getOriginList().get(1));
                     System.out.println("getNodeList(0) : " + c_matrixInstance.getOriginList().get(2));
                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    for (int i = 0; i < c_matrixInstance.getNodeCount(); i++) {
                        System.out.println(i + " : for function");
                        JSONObject tempJsonObj = new JSONObject();
                        tempJsonObj.put("name", c_matrixInstance.getOriginList().get(i));
                        if (m_masterMAC.equalsIgnoreCase(c_matrixInstance.getOriginList().get(i))) {
                            tempJsonObj.put("group", 2);
                        } else if (c_matrixInstance.getOutNodeList().contains(c_matrixInstance.getOriginList().get(i))) {
                            tempJsonObj.put("group", 3);
                        } else {
                            tempJsonObj.put("group", 1);
                        }
                        jsonArray.add(tempJsonObj);
                    }
                    jsonObj.put("nodes", jsonArray);

                    System.out.println("before get matrix");
                    jsonArray = new JSONArray();
                    for (int i = 0; i < c_matrixInstance.getNodeCount(); i++) {
                        for (int j = i; j < c_matrixInstance.getNodeCount(); j++) {
                            if (c_matrixInstance.getMatrix()[i].get(j).equalsIgnoreCase("O") || c_matrixInstance.getMatrix()[j].get(i).equalsIgnoreCase("O")) {

                                JSONObject tempJsonObj = new JSONObject();

                                tempJsonObj.put("source", i);
                                tempJsonObj.put("target", j);
                                tempJsonObj.put("value", 5);
                                jsonArray.add(tempJsonObj);

                            }
                        }
                    }
                    jsonObj.put("links", jsonArray);
                    outToClient.writeUTF(jsonObj.toString());
                }

            } catch (IOException ex) {
                System.out.println(c_conectionSocket.getInetAddress().toString() + " is out");
            } finally {

            }
        }
    }
    
}
