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
package com.mesh.masterwebserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 *
 * @author junhoya924
 * 
 * websocket java code
 */
@ServerEndpoint("/javaWebSocket") // endpoint
public class meshWeb {
    private Set<Session> m_peers = Collections.synchronizedSet(new HashSet<Session>());
    
    private String m_ip;                    // master ip
    private String m_port;                  // master port
    
    private Socket m_clientSocket;
    private DataOutputStream m_outToServer;
    private InputStream m_is;
    private DataInputStream m_dis;
    
    public meshWeb() throws IOException, ParseException{
        System.out.println("start");
        
        m_ip = "127.0.0.1";
        m_port = "35001";
        
        m_clientSocket = new Socket(m_ip, Integer.valueOf(m_port));
        m_outToServer = new DataOutputStream(m_clientSocket.getOutputStream());
        m_is = m_clientSocket.getInputStream();
        m_dis = new DataInputStream(m_is);
                
    }
    @OnMessage
    public String onMessage(String message) throws IOException, ParseException {
        System.out.println("onMessage");
        m_outToServer.writeUTF("message");
        System.out.println(m_dis.readUTF());
        int nodeCount = Integer.valueOf(m_dis.readUTF());
        if(nodeCount < 3){
            System.out.println("node is not enough");
            return "node is not enough";
        }
        
        String jsonString = m_dis.readUTF();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObj = (JSONObject) jsonParser.parse(jsonString);
        return jsonObj.toString();
    }
     @OnOpen
    public void onOpen (Session peer) {
        System.out.println("open");
        m_peers.add(peer);
    }

    @OnClose
    public void onClose (Session peer) {
        try {
            System.out.println("close");
            m_outToServer.writeUTF("close");
            m_peers.remove(peer);
        } catch (IOException ex) {
            Logger.getLogger(meshWeb.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    @OnError
    public void onError(Throwable t) {
    }
    
}
