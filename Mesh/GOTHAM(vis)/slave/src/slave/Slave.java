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
package slave;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.io.*;
import java.util.TreeSet;

public class Slave {

    private static String m_ip;
    private static String m_port;
    private static String m_myMAC;
    private static String[] m_originator;
    private static String[] m_nextHop;

    //main
    public static void main(String[] args) throws Exception {
        m_ip = "192.168.0.10";
        m_port = "35000";

        //////////////////////////////////////////////////////////////////socket connection
        Socket clientSocket;

        clientSocket = new Socket(m_ip, Integer.valueOf(m_port));
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
        InputStream is = clientSocket.getInputStream();
        DataInputStream input = new DataInputStream(is);
        //////////////////////////////////////////////////////////////////

        while (true) { 
            updateStatus();

            outToServer.writeUTF(m_myMAC);
            outToServer.writeUTF(String.valueOf(m_originator.length));
            outToServer.writeUTF(String.valueOf(m_nextHop.length));

            System.out.println(input.readUTF());

            for (int i = 0; i < m_originator.length; i++) {
                outToServer.writeUTF(m_originator[i]);
            }

            Thread.sleep(1000);

            for (int i = 0; i < m_nextHop.length; i++) {
                outToServer.writeUTF(m_nextHop[i]);
            }

            System.out.println("sleep 5 sec");
            Thread.sleep(5000);
            System.out.println("wake up\n\n");

        }
    }

    /*
     * input command Sring
     * execute command String and write outcomde to file and screen
     * output void
     */
    public static void shellCmd(String command) throws Exception {
        FileWriter fw = new FileWriter("status.txt");
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
     * input String array String[]
     * remove duplicated string in array
     * output Object[]
     */
    public static Object[] removeDuplicateArray(String[] array) {
        Object[] removeArray = null;
        TreeSet<String> ts;
        ts = new TreeSet<String>();
        for (int i = 0; i < array.length; i++) {
            ts.add(array[i]);
        }

        removeArray = ts.toArray();
        return removeArray;
    }

    /*
     * input void
     * parsing from status.txt file, get Originator and nexthop information
     * output void
     */
    public static void updateStatus() throws Exception {
        String command = command = "batctl o";
        shellCmd(command);//command 실행 및 status.txt파일로 write

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

        m_originator = new String[nodeCount];
        m_nextHop = new String[nodeCount];

        m_myMAC = br.readLine().substring(46, 63);
        System.out.println("my mac : " + m_myMAC);

        br.readLine();
        for (int i = 0; i < nodeCount; i++) {
            temp[0] = br.readLine();
            m_originator[i] = temp[0].substring(0, 17);
            m_nextHop[i] = temp[0].substring(36, 53);
        }

        Object[] noDupNextHop = removeDuplicateArray(m_nextHop);
        m_nextHop = new String[noDupNextHop.length];

        for (int i = 0; i < noDupNextHop.length; i++) {
            m_nextHop[i] = noDupNextHop[i].toString();
        }

    }
}
