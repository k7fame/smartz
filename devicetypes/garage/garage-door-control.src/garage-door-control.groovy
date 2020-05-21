/**
 *  Garage Contact Sensor and Switch
 *
 *  Copyright 2020 Murali Kesavan
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 */
metadata {
	definition(name: "Garage Door Control", namespace: "Garage", author: "Murali Kesavan", mnmn: "geniHome", 
    	vid: "generic-contact-2", minHubCoreVersion: '000.017.0012', executeCommandsLocally: false, 
        ocfDeviceType: "oic.d.garagedoor", runLocally: true, cstHandler: true) {	

		// Capabilities
		capability "Actuator"
		capability "Contact Sensor"
		capability "Door Control"
		capability "Garage Door Control"
		capability "Health Check"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"
	}

	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'contact' attribute
	// TODO: handle 'switch' attribute

}

// handle commands
def on() {
	// TODO: handle 'on' command
	//log.debug "Executing 'on'"
    log.debug "Turning Switch and Sensor On"
	sendEvent(name: "switch", value: "on", isStateChange: true, display: true, displayed: true)
	sendEvent(name: "contact", value: "open", isStateChange: true, display: true, displayed: true)
}

def off() {
	// TODO: handle 'off' command
	//log.debug "Executing 'off'"
    log.debug "Turning Switch and Sensor Off"
    sendEvent(name: "switch", value: "off", isStateChange: true, display: true, displayed: true)
	sendEvent(name: "contact", value: "closed", isStateChange: true, display: true, displayed: true)
}
