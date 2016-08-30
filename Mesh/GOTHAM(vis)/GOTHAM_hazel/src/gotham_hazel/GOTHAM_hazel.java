/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gotham_hazel;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.Config;
import com.hazelcast.core.Client;
import com.hazelcast.core.ClientListener;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import java.net.Socket;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author junhoya924
 */
public class GOTHAM_hazel extends CommonGround {

    private static boolean m_isMaster = false;
    private static String m_wlanInterface;

   
    private static String m_masterIP = "192.168.0.10";
    private static String m_masterPort = "5701";

    /**
     * main
     *
     * @param args the command line arguments args[0] : interface name args[1] :
     * boolean master=1, slave=2 args[2] : master ip address
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here

        initializing(args);
        
        
        System.out.println("before update");
        Update m_update = new Update();
    }

    public static void initializing(String[] initialArgs) throws Exception {
        m_wlanInterface = initialArgs[0];
        m_masterIP = initialArgs[2];
        m_myMAC = getMyMac(m_wlanInterface);
        
        if (initialArgs[1].equals("1")) {
            m_isMaster = true;
            m_masterMAC = m_myMAC;
            hazel_master();
        }else if(initialArgs[1].equals("2")) {
            m_isMaster = false;
            hazel_slave();
            System.out.println("before make Matrix");
        }
        
        m_matrixInstance = new Matrix(m_matrix, m_nodeList, m_outNodeList, MAX_NODE, m_isMaster);
        m_matrixInstance.nodeIn(m_myMAC);

    }

    public static void hazel_master() {
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);

       instance.getClientService().addClientListener(new ClientListner());
       
        m_matrix = new List[MAX_NODE];
        for (int i = 0; i < MAX_NODE; i++) {
            m_matrix[i] = instance.getList("matrix" + ":" + String.valueOf(i));
        }
        
        m_nodeList = instance.getList("nodeList");
        m_outNodeList = instance.getList("outNodeList");
        
        m_mapClientMac = instance.getMap("client");
        m_mapClientMac.put(instance.getLocalEndpoint().getUuid(), m_myMAC);
        m_masterMACList = instance.getList("masterMAC");
        m_masterMACList.add(m_masterMAC);
        
    }

    public static void hazel_slave() {
        ClientConfig clientConfig = new ClientConfig();
        clientConfig.addAddress(m_masterIP + ":" + m_masterPort);
        
        HazelcastInstance client = HazelcastClient.newHazelcastClient(clientConfig);
        
        m_matrix = new List[MAX_NODE];
        for (int i = 0; i < MAX_NODE; i++) {
            m_matrix[i] = client.getList("matrix" + ":" + String.valueOf(i));
        }
        
        m_nodeList = client.getList("nodeList");
        m_outNodeList = client.getList("outNodeList");
        
        m_mapClientMac = client.getMap("client");
        m_mapClientMac.put(client.getLocalEndpoint().getUuid(), m_myMAC);
        
        m_masterMACList = client.getList("masterMAC");
        m_masterMAC = m_masterMACList.get(0);
        
        
        System.out.println("finish slave initialization");
        
    }
    
    public static class ClientListner implements ClientListener {

        @Override
        public void clientConnected(Client client) {
            System.out.println("client connected : " + client.toString());
            
        }

        @Override
        public void clientDisconnected(Client client) {
            System.out.println("client disconnected : " + client.toString());
            m_matrixInstance.nodeOut(m_mapClientMac.get(client.getUuid()));
            m_mapClientMac.remove(client.getUuid());
            
            
        }
        
    }
            
            
}
