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
import java.util.concurrent.*;

import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;
import org.apache.felix.scr.annotations.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
public class AppComponent {

    private final Logger log = LoggerFactory.getLogger(getClass());
    protected final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    protected final String MYSQL_URL = "jdbc:mysql://localhost:3306/AP_Information?" + "user=root&password=mclab1";
    protected final String MYSQL_USER = "root";
    protected final String MYSQL_PASSWORD = "mclab1";

    protected MySQLdb db = null;
    protected ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    protected ThreadPoolExecutor conExecutor;
    @Activate
    protected void activate() {
	    try {
	    		db = new MySQLdb(MYSQL_DRIVER,MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
			executor.scheduledAtFixedRate(heartbeatThread, 1, 5, TimeUnit.SECONDS);

			conExecutor = (ThreadPoolExecutor)(Executors.newCachedThreadPool();
			conExecutor.execute(new DBThread(db, conExecutor));
		    /*****************************
		     * TODO:
		     * 1. make db thread
		     * 2. make db command
		     * 3. make heartbeat 
		     * 4. make show command
		     * ***************************/
		    log.info("Started");
	    }
	    catch(Exception e)
	    {}
    }

    @Deactivate
    protected void deactivate() {
	    log.info("Stopped");
	    executor.shutdown();
	    conExecutor.shutdown();
    }

    protected void heartbeatThread() {
    		db.heartbeat();
    }

}
