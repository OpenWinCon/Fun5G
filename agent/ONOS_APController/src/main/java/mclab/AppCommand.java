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


import java.net.*;
import java.io.*;

import org.apache.karaf.shell.commands.Command;
import org.onosproject.cli.AbstractShellCommand;
import org.apache.karaf.shell.commands.Argument;

/**
 * Sample Apache Karaf CLI command
 */
@Command(scope = "onos", name = "apcontrol",
         description = "Sample Apache Karaf CLI command")
public class AppCommand extends AbstractShellCommand {
    @Argument(index = 0, name = "command or ip", description = "APControlCommand", required = false, multiValued = false)
    private String apCommand = null;
    @Argument(index = 1, name = "ip or value", description = "command or target ip", required = false, multiValued = false)
    private String arg1 = null; 
    @Argument(index = 2, name = "value", description = "target ip or value", required = false, multiValued = false)
    private String arg2 = null;
    @Argument(index = 3, name = "value", description = "values", required = false, multiValued = false)
    private String APpw = null;


    public boolean validIP (String ip) {
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

    protected String MakeMessage(int mType, String param) {
	return String.format("|%d|%s", mType, param);
    }


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

    @Override
    protected void execute() {
	if (apCommand.equals("help") || apCommand.equals("h")) {
			//TODO: make help command.
			print("help");
		}
    		else if (apCommand.equals("show")) {
    			//TODO: make show command
			print("show");
		}
		else if (apCommand.equals("clear")) {
			//TODO: make cli clear command
			print("clear");
		}
		else {
			if( validIP(apCommand) ) {
				String ip = apCommand;
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

}
