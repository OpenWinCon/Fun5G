/*
 * Copyright 2016 Open Networking Laboratory
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
package mclab;


import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.commands.Argument;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


/**
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "ncf-grpc",
         description = "AP Controller")
public class NetconfGrpcCommand extends AbstractShellCommand {
    @Argument(index = 0, name = "command", description = "Command", required = true, multiValued = false)
    private String cmd = null;
    @Argument(index = 1, name = "ip ", description = "target ip", required = false, multiValued = false)
    private String arg1 = null;
    @Argument(index = 2, name = "value", description = "target ip or value", required = false, multiValued = false)
    private String arg2 = null;
    @Argument(index = 3, name = "value", description = "values", required = false, multiValued = false)
    private String APpw = null;

    /*********************
	 *
	 * Client Dictonary Format : ( IP : nfcGrpcClient? or SSID? ) -> DB?
	 *
	 * Hello operation flow : MAKE OBJECT -> Hello Operation -> if the connecting is success, add dictonary,
	 * if not, failed return -> Show currnet Config { IP, SSID, PASSWORD, ... }
	 *
	 * getConfig operation flow: Find Object from Dictonary -> Get Config -> show Current Config ( IP, SSID, PASSWORD ... )
	 *
	 * editConfig operation flow: Find Object from Diconary -> Make List or file for backuping current config
	 * -> Edit Config -> Show Current Config ( IP, SSID, PASSWORD ... )
	 *
	 * Lock operation flow : TODO
	 *
	 *
	 *
	 * ********************/

    private NetconfGrpcService service;

    private Map<String, Commands> commands;

    protected ConcurrentMap<String, nfcGrpcClient> ap_list;





    public boolean checkIP (String ip) {
    	try {
		if( ip == null || ip.isEmpty() ) {
			return false;
		}

		String[] parts = ip.split( "\\." );
		if ( parts.length != 4) {
			return false;
		}

		for ( String s : parts ) {
			int i = Integer.parseInt ( s );
			if( (i < 0) || i > 255) {
				return false;
			}
		}

		if ( ip.endsWith(".") ) {
			return false;
		}

		return true;


	} catch (NumberFormatException nfe) {
		return false;
	}
    }

    /* old version
    protected String MakeMessage(int mType, String param) {
	return String.format("|%d|%s", mType, param);
    }
*/
/*Old version

    protected void SendCommand(String ip, int mType, String param) {
	try {



			    Socket sock = new Socket(ip, 12014);
			    OutputStream out = sock.getOutputStream();
			    BufferedOutputStream bo = new BufferedOutputStream(out);

			    InputStream in = sock.getInputStream();
			    BufferedInputStream br = new BufferedInputStream(in);

			    String sendMessage = MakeMessage(mType, param);

			    byte[] send = sendMessage.getBytes();
			    bo.write(send);
			    bo.flush();

			    byte[] recv = new byte[1024];
			    int length = br.read(recv);
				

			    String message = new String(recv, 4, length);
			    print(message);

			    bo.close();
			    br.close();

			
			    sock.close();

			    
		    }
		    catch(Exception e)
		    {}
 
 	
	
    
    }
*/
    @Override
    protected void execute() {

            service=get(NetconfGrpcService.class);
            ap_list = service.getaplist();
            commands = new HashMap<String, Commands>();


            commands.put("help", new Commands() {
                public void invoke() {
                    print("Netconf GRPC Client, version 0.1.0");
                    print("");
                    print("");
                    print("Available commands:");
                    print("help			Display this text");
                    print("list			Show the connected NETCONF-GRPC servers");
                    print("connect			Connect to a NETCONF-GRPC server (send <Hello> operation)");
                    print("edit-config		Like NETCONF <edit-config> operation");
                    print("get-config		Like NETCONF <get-config> operation");
                    print("");
                    print("");
                }
            });

        commands.put("connect", new Commands() {
            public void invoke() {
                if(arg1 == null) {
                    error("Should be type IP");
                    return;
                }

                if(checkIP(arg1) == false) {
                    error("IP is not correct");
                    return;
                }

                String host = arg1;

                if( ap_list.get(host) != null) {
                    print("already connected");
                    return;
                }
                else {
                    nfcGrpcClient client = new nfcGrpcClient(host, 50051);
                    ap_list.put(host, client);
                    if( client.checkExist() == false ) {
                        error("Client is not exist");
                        return;
                    }
                    print(client.toString());
                    return;
                }
            }

        });

        commands.put("milestone1", new Commands() {
            public void invoke() {

                ConcurrentMap<String, nfcGrpcClient> milestone_list = new ConcurrentHashMap<String, nfcGrpcClient>();

                for( int i = 0; i < 1000; i++) {
                    nfcGrpcClient client = new nfcGrpcClient("163.180.118.62",50051, "formilestones");
                    milestone_list.put(String.valueOf(i), client);
                }
                for( int i = 0; i < 1000; i++) {
                    milestone_list.get(String.valueOf(i)).Hello();
                    print(String.valueOf(i) + ": " + milestone_list.get(String.valueOf(i)).toString());
                }
            }

        });


        commands.get(cmd).invoke();

    }
        /*
	if (cmd.equals("help") || cmd.equals("h")) {
			//TODO: make help command.
			print("Netconf GRPC Client, version 0.1.0");
			print("	Available commands:");
			print("	help			Display this text");
			print("	list			Show the connected NETCONF-GRPC servers");
			print("	connect			Connect to a NETCONF-GRPC server (send <Hello> operation)");
			print(" edit-config		Like NETCONF <edit-config> operation");
			print("	get-config		Like NETCONF <get-config> operation");
			print("");
		}
    		else if (cmd.equals("show")) {
    			//TODO: make show command
			print("show");
		}
		else if (cmd.equals("clear")) {
			//TODO: make cli clear command
			print("clear");
		}
		else {
			if( validIP(cmd) ) {
				String ip = cmd;
				if (arg1 != null)
				{

					if (arg1.equals("start")) {
						SendCommand(ip, 1, " ");	
						print("start");
					}
					else if (arg1.equals("stop")) {
						SendCommand(ip, 2, " ");	
						print("stop");
					}
					else if (arg1.equals("reboot")) {
						SendCommand(ip, 3, " ");	
						print("reboot");
					}
					else if (arg1.equals("ssid")) {
						if(arg2 != null) {
							SendCommand(ip, 5, arg2);	
							print(arg2);
						}
						else {
							print("invalid value");
						}
					}
					else if (arg1.equals("password")) {
						if(arg2 != null) {
							if(arg2.length() < 8)
							{
								print("invalid value");

							}
							else
							{
								SendCommand(ip, 6, arg2);	
								print(arg2);
							}
						}
						else {
							SendCommand(ip, 9, " ");	
							print("off password");
						}
					}
					else if (arg1.equals("channel")) {
						if(arg2 != null) {
							SendCommand(ip, 11, arg2);	
							print(arg2);
						}
						else {
							print("invalid value");
						}
					}
					else if (arg1.equals("mode")) {
						if(arg2 != null) {
							SendCommand(ip, 12, arg2);	
							print(arg2);
						}
						else {
							print("invalid value");
						}
					}
					else if (arg1.equals("broadcast")) {
						if(arg2 != null) {
							if(arg2.equals("on") || arg2.equals("ON"))
							{
								SendCommand(ip, 13, "1");	
							}
							else if(arg2.equals("off") || arg2.equals("OFF"))
							{
								SendCommand(ip, 13, "0");	
							}
							else
							{
								print("invalid value");
							}
							print(arg2);
						}
						else {
							print("invalid value");
						}
					}
					else
					{
						print("invalid command");
					}
				}
				else {
					print("invalid command");
				}
			}
			else 
			{
				print("invalid ip");
			}
		}
    		
      }
*/
}
