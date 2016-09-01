/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gotham_hazel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 *
 * @author junhoya924@khu.ac.kr
 */
public class CommonGround {
    protected static String m_masterMAC;
    protected static String m_myMAC; 
    protected static final int m_masterToWebPort = 35001;
    protected static Matrix m_matrixInstance;
    protected static List<String>[] m_matrix;
    protected static List<String> m_nodeList;
    protected static List<String> m_outNodeList;
    protected static List<String> m_masterMACList;          
    protected static Map<String, String> m_mapClientMac;    //<hazel_uuid, MAC>
    protected static final int MAX_NODE = 100;              //MAX node count
    
    
    /*
         * input  - command(command) : String,
                    file name(fileName) : String
         * doing  - execute command and write outcome to file and screen
         * output - void
     */
    public static void shellCmd(String command, String fileName) throws Exception {
        FileWriter fw = new FileWriter(fileName);
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
         * input  - String array(array) : String[]
         * doing  - remove duplicated string in array
         * output - Object[]
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
         * input  - interface name(wlanInterface) : String
         * doing  - execute command String and write outcomde to file and screen(get interface's MAC address)
         * output - void
     */
    public static String getMyMac(String wlanInterface) throws Exception {
        String command = command = "ifconfig " + wlanInterface;
        String myMAC;
        shellCmd(command, "myMAC.txt");//command 실행 및 status.txt파일로 write

        RandomAccessFile br = new RandomAccessFile("myMAC.txt", "r");

        myMAC = br.readLine().substring(38, 55);
        System.out.println(myMAC);
        return myMAC;
    }
}
