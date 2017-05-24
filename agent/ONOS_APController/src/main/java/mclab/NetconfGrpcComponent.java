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


import org.apache.felix.scr.annotations.Activate;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Deactivate;

import org.apache.felix.scr.annotations.Service;
import org.onosproject.app.ApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class NetconfGrpcComponent implements NetconfGrpcService{

    private final Logger log = LoggerFactory.getLogger(getClass());

	private ConcurrentMap<String, nfcGrpcClient> ap_list;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    @Activate
	    protected void activate() {
		    try {
		    	/*****************************
		    	 * TODO:
		    	 *
		    	 * ***************************/
				ap_list = new ConcurrentHashMap<String, nfcGrpcClient>();
			//	executor.scheduleAtFixedRate(this::monitoringAP, 1, 5, TimeUnit.SECONDS);

				log.info("Started");
		    }
		    catch(Exception e)
		    {}
    }

    @Deactivate
    protected void deactivate() {
        log.info("Stopped");
        executor.shutdown();
    }

    @Override
    public ConcurrentMap<String, nfcGrpcClient> getaplist() { return this.ap_list; }

    private void monitoringAP() {
    	/*
    		TODO: check AP's status
    	 */
    	log.info("start monitoringAP");
	}

}
