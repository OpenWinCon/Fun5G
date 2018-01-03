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


import org.apache.felix.scr.annotations.*;

import org.onlab.osgi.DefaultServiceDirectory;
import org.onosproject.core.CoreService;
import org.onosproject.net.Device;
import org.onosproject.net.device.DeviceEvent;
import org.onosproject.net.device.DeviceListener;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.device.PortStatistics;
import org.onosproject.net.flow.FlowRuleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.*;

/**
 * Skeletal ONOS application component.
 */
@Component(immediate = true)
@Service
public class NetconfGrpcComponent implements NetconfGrpcService{

    private final Logger log = LoggerFactory.getLogger(getClass());

	private ConcurrentMap<String, ncfGrpcClient> ap_list;
	private ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	@Reference(cardinality = ReferenceCardinality.MANDATORY_UNARY)
	protected CoreService coreService;
	protected DeviceService deviceService;
	protected DeviceService deviceServiceForflow;
	protected FlowRuleService flowRuleService;


	@Activate
	    protected void activate() {
		    try {
		    	/*****************************
		    	 * TODO:
		    	 *
		    	 * ***************************/
				coreService = DefaultServiceDirectory.getService(CoreService.class);
				flowRuleService = DefaultServiceDirectory.getService(FlowRuleService.class);
				deviceServiceForflow = DefaultServiceDirectory.getService(DeviceService.class);
				deviceService.addListener(new InnerDeviceListener());

				ap_list = new ConcurrentHashMap<String, ncfGrpcClient>();
				executor.scheduleAtFixedRate(this::trafficMonitoring, 1, 5, TimeUnit.SECONDS);

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
    public ConcurrentMap<String, ncfGrpcClient> getaplist() { return this.ap_list; }

	private void trafficMonitoring() {
		Iterable<Device> devices = deviceService.getDevices();

		for(Device device : devices) {
			List<PortStatistics> ports = deviceService.getPortDeltaStatistics(device.id());

			long traffic = 0;
			for(PortStatistics port: ports) {
				traffic = (port.bytesReceived())/port.durationSec();
				System.out.println("DeviceID: " + device.id() + "\nCurrent traffic: " + String.format("%.2f", (double) traffic * 8 / 1024/ 1024) + "(Mbps)");
			}
		}
	}

	private class InnerDeviceListener implements DeviceListener {
		@Override
		public void event(DeviceEvent event) {
			switch (event.type()) {
				case DEVICE_ADDED:
				case DEVICE_AVAILABILITY_CHANGED:
					if(deviceService.isAvailable(event.subject().id())) {
						log.info("Device connected {}", event.subject().id());
					}
					break;
				case PORT_UPDATED:
					System.out.println("port updated");
					System.out.println("traffics: " + deviceService.getPortDeltaStatistics(event.subject().id()));
					log.info("traffics: " + deviceService.getPortDeltaStatistics(event.subject().id()));
					break;
				case DEVICE_UPDATED:
				case DEVICE_REMOVED:
				case DEVICE_SUSPENDED:
				case PORT_ADDED:
				case PORT_REMOVED:
				default:
					break;

			}
		}
	}



}
