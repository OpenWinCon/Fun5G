/*
 * Copyright 2015-2016 junhoya924
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package master;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author junhoya924
 * 
 * master node java program
 */
public class Master {

    private static ServerSocket m_slaveSocket;
    private static Matrix m_matrix;
    private static String m_masterMAC;
    private static final String m_wlanInterface = "wlan2";

    /**
     * main
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, Exception {
        // TODO code application logic here
        getMyMac(m_wlanInterface);
        
        m_slaveSocket = new ServerSocket(35000); // binding with 65000 port
        m_matrix = new Matrix();

        webWaitingThread mainWebWaitingThread = new webWaitingThread();
        mainWebWaitingThread.start();

        while (true) {
            System.out.println("waiting slave client");
            Socket connectionSocket = m_slaveSocket.accept();//Waiting until clients request connection
            System.out.println(connectionSocket.getInetAddress() + "(slave) connection request came");

            communicationSlave slaveThread = new communicationSlave(connectionSocket);
            slaveThread.start();
        }

    }

    //Communication class with slave
    public static class communicationSlave extends Thread {

        private Socket c_conectionSocket;
        private String c_thisMac;

        public communicationSlave(Socket connectionSocket) {
            c_conectionSocket = connectionSocket;
        }

        @Override
        public void run() {
            try {
                DataOutputStream outToClient = new DataOutputStream(c_conectionSocket.getOutputStream());// get socket outputstream
                DataInputStream dis;
                InputStream is = c_conectionSocket.getInputStream();
                dis = new DataInputStream(is);

                while (true) {
                    c_thisMac = dis.readUTF();
                    synchronized (m_matrix) {
                        m_matrix.nodeIn(c_thisMac);
                    }
                    String[] Originator = new String[Integer.parseInt(dis.readUTF()) + 1];
                    String[] nextHop = new String[Integer.parseInt(dis.readUTF())];
                    outToClient.writeUTF("nice to meet you " + c_thisMac);

                    Originator[0] = c_thisMac;
                    for (int i = 1; i < Originator.length; i++) {
                        Originator[i] = dis.readUTF();
                    }
                    for (int i = 0; i < nextHop.length; i++) {
                        nextHop[i] = dis.readUTF();
                    }

                    synchronized (m_matrix) {
                        m_matrix.removeNode(Originator);
                    }

                    for (int i = 0; i < Originator.length; i++) {
                        synchronized (m_matrix) {
                            m_matrix.addNode(Originator[i]);
                        }
                        //System.out.println(Originator[i]);
                    }
                    synchronized (m_matrix) {
                        m_matrix.updateNextHop(c_thisMac, nextHop);
                        m_matrix.printMatrix(c_thisMac);
                    }
                }
            } catch (IOException ex) {
                System.out.println(c_conectionSocket.getInetAddress().toString() + " is out");
                synchronized (m_matrix) {
                    System.out.println("nodeOut call" + c_thisMac);
                    m_matrix.nodeOut(c_thisMac);
                }
               
            } 
        }
    }

    public static class webWaitingThread extends Thread {

        private ServerSocket c_webSocket;
        private Socket c_connectionSocket;
        @Override
        public void run() {
            try {
                c_webSocket = new ServerSocket(35001); // 서버소켓 생성하여 35001번 포트와 바인딩한다

                while (true) {
                    System.out.println("waiting web client");
                    c_connectionSocket = c_webSocket.accept();//클라이언트의 연결 요청이 올 때까지 실행을 멈추고 대기
                    System.out.println(c_connectionSocket.getInetAddress() + " 로부터(web) 연결 요청이 들어왔습니다");
                    communicationWeb webThread = new communicationWeb(c_connectionSocket);
                    webThread.start();
                }
            } catch (IOException ex) {
                Logger.getLogger(Master.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Communication class with web
    public static class communicationWeb extends Thread {

        private Socket c_conectionSocket;

        public communicationWeb(Socket connectionSocket) {
            c_conectionSocket = connectionSocket;
        }

        @Override
        public void run() {

            try {
                DataOutputStream outToClient = new DataOutputStream(c_conectionSocket.getOutputStream());// 소켓의 출력스트림을 얻는다
                DataInputStream dis;
                InputStream is = c_conectionSocket.getInputStream();
                dis = new DataInputStream(is);

                while (true) {
                    if(dis.readUTF().equalsIgnoreCase("close")){
                        break;
                    }
                
                        
                    outToClient.writeUTF("nice to meet you ");
                    outToClient.writeUTF(String.valueOf(m_matrix.getNodeCount()));

                    JSONObject jsonObj = new JSONObject();
                    JSONArray jsonArray = new JSONArray();

                    
                    for (int i = 0; i < m_matrix.getNodeCount(); i++) {                       
                        JSONObject tempJsonObj = new JSONObject();
                        tempJsonObj.put("name", m_matrix.c_nodeList.get(i));
                        if(m_masterMAC.equalsIgnoreCase(m_matrix.c_nodeList.get(i))){
                            tempJsonObj.put("group", 2);
                        }else if(m_matrix.c_outNodeList.contains(m_matrix.c_nodeList.get(i))){
                            tempJsonObj.put("group", 3);
                        }else {
                            tempJsonObj.put("group", 1);
                        }
                        jsonArray.add(tempJsonObj);
                    }
                    jsonObj.put("nodes", jsonArray);

                    jsonArray = new JSONArray();
                    for (int i = 0; i < m_matrix.getNodeCount(); i++) {
                        for (int j = i; j < m_matrix.getNodeCount(); j++) {
                            if (m_matrix.getMatrix()[i][j].equalsIgnoreCase("O") || m_matrix.getMatrix()[j][i].equalsIgnoreCase("O") ) {
                                
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

    //status matrix class
    public static class Matrix {

        private String[][] c_matrix;
        private int c_nodeCount;
        private final int MAX_NODE = 100;           //MAX node count
        private ArrayList<String> c_nodeList;       //Originator node List
        private ArrayList<String> c_outNodeList;    //Originator node List which slave program is dead

        public Matrix() {
            c_nodeCount = 0;
            c_matrix = new String[100][100];
            for (int i = 0; i < MAX_NODE; i++) {
                for (int j = 0; j < MAX_NODE; j++) {
                    c_matrix[i][j] = "null";
                }
            }

            c_nodeList = new ArrayList<String>();
            c_outNodeList = new ArrayList<String>();
        }

        /*
         * input node name(MAC) Sring
         * add node to nodeList(if nodeList doesn't have input String)
         * output boolean(if add return true)
         */
        public boolean addNode(String nodeName) {
            if (c_nodeCount == 99) {
                return false;
            }
            if (c_nodeList.contains(nodeName)) {
                return false;
            }
            c_nodeCount++;
            c_nodeList.add(nodeName);

            return true;
        }
        
        /*
         * input all originator node name(MAC) Sring[]
         * if there is no originator node name in nodeList, remove it
         *     (In recent originator, past originator node is disapeared)
         * output boolean(if remove at least one node, return true)
         */
        public boolean removeNode(String[] origin) {
            if (c_nodeCount == 0) {
                return false;
            }
            int removeCount = 0;
            int[] removeIndex = new int[origin.length];
            for (int i = 0; i < c_nodeList.size(); i++) {
                for (int j = 0; j < origin.length; j++) {
                    if (origin[j].equalsIgnoreCase(c_nodeList.get(i))) {
                        break;
                    }
                    if (j == origin.length - 1) {
                        removeIndex[removeCount] = i;
                        removeCount++;
                    }
                }

            }
            if (removeCount == 0) {
                return false;
            }

            for (int k = 0; k < removeCount; k++) {
                int index = removeIndex[k];
                for (int i = index; i < c_nodeCount; i++) {
                    for (int j = 0; j < c_nodeCount; j++) {
                        c_matrix[i][j] = c_matrix[i + 1][j];
                    }
                }

                for (int i = index; i < c_nodeCount; i++) {
                    for (int j = 0; j < c_nodeCount; j++) {
                        c_matrix[j][i] = c_matrix[j][i + 1];
                    }
                }
                c_nodeList.remove(index);
                c_nodeCount--;
            }
            return true;
        }

        /*
         * input originator's MAC(myMAC) Sring, nexthop array String[]
         * update next hop with each originator
         * output boolean(if update return true)
         */
        public boolean updateNextHop(String myMAC, String[] nextHop) {

            int nodeNumber = -1;
            int[] nextHopNumber = new int[nextHop.length];
            int count = 0;
            System.out.println();
            
            nodeNumber = c_nodeList.indexOf(myMAC);
            for (int i = 0; i < nextHop.length; i++) {
                nextHopNumber[i] = c_nodeList.indexOf(nextHop[i]);
                count++;
            }
            if (count != nextHop.length || nodeNumber == -1) {
                return false;
            }

            System.out.println("finish find nexhopnumber");

            for (int i = 0; i < c_nodeCount; i++) {
                c_matrix[nodeNumber][i] = "X";
            }
            for (int i = 0; i < nextHopNumber.length; i++) {
                c_matrix[nodeNumber][nextHopNumber[i]] = "O";
            }

            return true;
        }

        /*
         * input node name(MAC, information source MAC) Sring
         * print matrix with information source MAC
         * output void
         */
        public void printMatrix(String mac) {
            System.out.println("get from : " + mac);
            for (int i = 0; i < c_nodeCount; i++) {
                for (int j = 0; j < c_nodeCount; j++) {
                    System.out.print(c_matrix[i][j] + "\t");
                }
                System.out.println();
            }
        }

        /*
         * input void
         * return node count
         * output int(node count)
         */
        public int getNodeCount() {
            return c_nodeCount;
        }

        /*
         * input void
         * return nodeList
         * output ArrayList<String>(nodeList)
         */
        public ArrayList<String> getNodeList() {
            return c_nodeList;
        }

        /*
         * input void
         * return status matrix
         * output String[][](status matrix)
         */
        public String[][] getMatrix() {
            return c_matrix;
        }
        
        /*
         * input originator MAC address String
         * When the slave program in slave node is dead, update status matrix and add MAC to outNodeList
         * output void
         */
        public void nodeOut(String mac) {
            int nodeNumber = -1;
            nodeNumber = c_nodeList.indexOf(mac);
            for (int i = 0; i < c_nodeCount; i++) {
                c_matrix[nodeNumber][i] = "-";
            }
            c_outNodeList.add(mac);
        }
        
        /*
         * input originator MAC address String
         * if originator's slave program revive, remove MAC address in outNodeList
         * output void
         */ 
        public void nodeIn(String mac) {
            if (c_outNodeList.contains(mac)) {
                c_outNodeList.remove(mac);
            }
        }

    }
    
    /*
     * input command Sring
     * execute command String and write outcomde to file and screen
     * output void
     */
    public static void shellCmd(String command) throws Exception {
        FileWriter fw = new FileWriter("myMAC.txt");
        BufferedWriter bw = new BufferedWriter(fw);

        Runtime runtime = Runtime.getRuntime();
        Process process = runtime.exec(command);
        InputStream is = process.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);
        String line;
        while ((line = br.readLine()) != null) {
            System.out.println(line);
            bw.write(line);
            bw.newLine();
        }
        bw.close();
        fw.close();
    }
    
    /*
     * input interface name Sring
     * execute command String and write outcomde to file and screen(get interface's MAC address)
     * output void
     */
    public static void getMyMac(String wlanInterface) throws Exception{
        String command = command = "ifconfig " +wlanInterface;
        shellCmd(command);//command 실행 및 status.txt파일로 write

        RandomAccessFile br = new RandomAccessFile("myMAC.txt", "r");

        m_masterMAC = br.readLine().substring(38,55);
        System.out.println(m_masterMAC);

        
    }
}
