/*
 *	Copyright 2016 SmartThings
 *   Optimized by Murali Kesavan
 *
 *  NEW CAPABILITIES
 *  (1) 
 * 	(2) 
 *  
 *  OPEN ISSUE
 *  How to test and silence from ST App - possibly use ActionTiles
 *
 *  ============================================================================
 *  Inspiration and Reference
 *
 *	Base Code: SmartThings Z-Wave Smoke Alarm Device Handler
 *
 *  Monoprice Z-Wave Smoke Detector: Adrian Caramaliu  
 *  Article: https://community.smartthings.com/t/monoprice-z-wave-smoke-detector-device-handler/20556
 *  Code: https://github.com/noname4444/SmartThingsPublic/blob/master/devicetypes/smartthings/zwave-smoke-alarm.src/zwave-smoke-alarm.groovy
 *
 *  ============================================================================
 *  License 
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not
 *	use this file except in compliance with the License. You may obtain a copy
 *	of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 *	WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 *	License for the specific language governing permissions and limitations
 *	under the License.
 */

metadata {
	definition (name: "Opti Smoke CO Alarm", namespace: "SmartThings/Safety", author: "Murali Kesavan", mnmn: geniHome, 
		ocfDeviceType: "oic.d.switch", runLocally: true, executeCommandsLocally: false, 
		minHubCoreVersion: '000.017.0012') {
		
        // Capabilities
        capability "Smoke Detector"
		capability "Carbon Monoxide Detector"
		capability "Sensor"
		capability "Battery"
		capability "Health Check"
        
        // Attributes
		attribute "alarmState", "string"
		attribute "deviceAlert", "string"   
        attribute "lastCheckin", "string"
		attribute "lastTested", "string"       

		// Fingerprint for Devices
		fingerprint mfr:"0138", prod:"0001", model:"0002", deviceJoinName: "First Alert Smoke Detector and Carbon Monoxide Alarm (ZCOMBO)"
		fingerprint mfr:"0138", prod:"0001", model:"0003", deviceJoinName: "First Alert Smoke Detector and Carbon Monoxide Alarm (ZCOMBO)"
		fingerprint mfr:"0154", prod:"0004", model:"0003", deviceJoinName: "POPP Co Detector", mnmn: "SmartThings", vid: "generic-carbon-monoxide-3"
		fingerprint deviceId: "0xA100", inClusters: "0x30,0x71,0x72,0x86,0x85,0x80,0x84"
	}

preferences {
    // manufacturer default wake up is every hour; optionally increase for better battery life
    input "userWakeUpInterval", "number", title: "Wake Up Interval (seconds)", 
    	description: "Default 3600 sec (10 minutes - 7 days)", 
        defaultValue: '3600', required: false, displayDuringSetup: true
}

simulator {
		status "smoke": "command: 7105, payload: 01 FF"
		status "clear": "command: 7105, payload: 01 00"
		status "test": "command: 7105, payload: 0C FF"
		status "carbonMonoxide": "command: 7105, payload: 02 FF"
		status "carbonMonoxide clear": "command: 7105, payload: 02 00"
		status "battery 100%": "command: 8003, payload: 64"
		status "battery 5%": "command: 8003, payload: 05"
	}

tiles (scale: 2){                    
		multiAttributeTile(name:"smoke", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.smoke", key: "PRIMARY_CONTROL") {
				attributeState("clear", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
				attributeState("detected", label:"SMOKE", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
				attributeState("tested", label:"TEST", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
				attributeState("tampere", label:"TAMPERE", icon:"st.alarm.smoke.tamper", backgroundColor:"#e86d13")
				attributeState("lowbat", label:"LOW BAT", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
			}
		}

		standardTile("co", "device.carbonMonoxide", width:6, height:4, inactiveLabel: false, decoration: "flat") {
			state("clear", label:"clear", icon:"st.alarm.smoke.clear", backgroundColor:"#ffffff")
			state("detected", label:"SMOKE", icon:"st.alarm.smoke.smoke", backgroundColor:"#e86d13")
			state("tested", label:"TEST", icon:"st.alarm.smoke.test", backgroundColor:"#e86d13")
		}
        
		valueTile("battery", "device.battery", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "battery", label:'${currentValue}% battery', unit:""
		}

		valueTile("lastCheckin", "device.lastCheckin", decoration: "flat", width: 2, height: 2){
			state "lastCheckin", label:'Last Checkin\n\n${currentValue}', unit:""
		}
		
		valueTile("lastTested", "device.lastTested", decoration: "flat", width: 2, height: 2){
			state "lastTested", label:'Last Tested\n\n${currentValue}', unit:""
		}
        
		main "smoke"
		details(["smoke", "co", "battery", "lastCheckin", "lastTested"])
	}
}

def installed() {
	log.debug "installed()"	
	// Device checks in every hour, this interval allows us to miss one 
    // check-in notification before marking offline
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, 
    			data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])

	def cmds = []
	createSmokeOrCOEvents("allClear", cmds) // allClear to set inital states for smoke and CO
	cmds.each { cmd -> sendEvent(cmd) }
}

def updated() {
	log.debug "updated()"	
	// Device checks in every hour, this interval allows us to miss one check-in
    // notification before marking offline
	sendEvent(name: "checkInterval", value: 2 * 60 * 60 + 2 * 60, displayed: false, 
    			data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
	//sendEvent(name: "checkInterval", value: checkInterval, displayed: false, data: [protocol: "zwave", hubHardwareId: device.hub.hardwareID])
}

// Required for HealthCheck Capability, but doesn't actually do anything because this device sleeps.
def ping() {
	log.debug "ping()"	
}

// parse events into attributes
def parse(String description) {
    //log.debug "Smoke Detector description: $description"
	def results = []
	if (description.startsWith("Err")) {
	    results << createEvent(descriptionText:description, displayed:true)
	} else {
		def cmd = zwave.parse(description, [ 0x80: 1, 0x84: 1, 0x71: 2, 0x72: 1 ])
		//def cmd = zwave.parse(description, [0x71: 2, 0x72: 1, 0x84: 2, 0x80: 1])
		if (cmd) {
            //log.debug "Smoke Detector CMD: $cmd"
            //log.debug "Smoke Detector CMD properties:  ${cmd.getProperties()}"
			zwaveEvent(cmd, results)
		}
	}
	log.debug "'$description' parsed to ${results.inspect()}"
	return results
}

def createSmokeOrCOEvents(name, results) {
	def text = null
	switch (name) {
		case "smoke":
			text = "$device.displayName smoke was detected!"
			// these are displayed:false because the composite event is the one we want to see in the app
			results << createEvent(name: "smoke", value: "detected", descriptionText: text, displayed: false)
    	    results << createEvent(name: "lastTested", value: new Date(), displayed: false, isStateChange: true)
        	// This composite event is used for updating the tile
			results << createEvent(name: "alarmState", value: name, descriptionText: text)
			break
		case "carbonMonoxide":
			text = "$device.displayName carbon monoxide was detected!"
			// these are displayed:false because the composite event is the one we want to see in the app
			results << createEvent(name: "carbonMonoxide", value: "detected", descriptionText: text, displayed: false)
    	    results << createEvent(name: "lastTested", value: new Date(), displayed: false, isStateChange: true)
        	// This composite event is used for updating the tile
			results << createEvent(name: "alarmState", value: name, descriptionText: text)
			break
		case "tested":
			text = "$device.displayName was tested!"
			results << createEvent(name: "smoke", value: "tested", descriptionText: text, displayed: false)
			results << createEvent(name: "carbonMonoxide", value: "tested", descriptionText: text, displayed: false)
    	    results << createEvent(name: "lastTested", value: new Date(), displayed: false, isStateChange: true)
        	// This composite event is used for updating the tile
			results << createEvent(name: "alarmState", value: name, descriptionText: text)
			break
		case "smokeClear":
			text = "$device.displayName smoke is clear!"
			results << createEvent(name: "smoke", value: "clear", descriptionText: text, displayed: false)
		    results << createEvent(name: "alarmState", value: name, descriptionText: text)
			name = "clear"
			break
		case "carbonMonoxideClear":
			text = "$device.displayName carbon monoxide is clear!"
			results << createEvent(name: "carbonMonoxide", value: "clear", descriptionText: text, displayed: false)
		    results << createEvent(name: "alarmState", value: name, descriptionText: text)
			name = "clear"
			break
		case "allClear":
			text = "$device.displayName all clear!"
			results << createEvent(name: "smoke", value: "clear", descriptionText: text, displayed: false)
			results << createEvent(name: "carbonMonoxide", value: "clear", displayed: false)
		    results << createEvent(name: "alarmState", value: name, descriptionText: text)
			name = "clear"
			break
		case "testClear":
			text = "$device.displayName test cleared!"
			results << createEvent(name: "smoke", value: "clear", descriptionText: text, displayed: false)
			results << createEvent(name: "carbonMonoxide", value: "clear", displayed: false)
		    results << createEvent(name: "alarmState", value: name, descriptionText: text)
			name = "clear"
			break
		case "tamper":
			text = "$device.displayName tampered!"
			results << createEvent(name: "smoke", value: "tamper", descriptionText: text, displayed: false)
			results << createEvent(name: "carbonMonoxide", value: "tamper", displayed: false)
        	results << createEvent(name: "alarmState", value: name, descriptionText: text)
			results << createEvent(name: "deviceAlert",value: name, descriptionText:text)
            break
		case "lowbat":
			text = "$device.displayName low battery!"
			results << createEvent(name: "smoke", value: "tamper", descriptionText: text, displayed: false)
			results << createEvent(name: "carbonMonoxide", value: "tamper", displayed: false)
        	results << createEvent(name: "alarmState", value: name, descriptionText: text)
			results << createEvent(name: "deviceAlert",value: name, descriptionText:text)
            break
	}
	results
}

def zwaveEvent(physicalgraph.zwave.commands.alarmv2.AlarmReport cmd, results) {
	if (cmd.zwaveAlarmType == physicalgraph.zwave.commands.alarmv2.AlarmReport.ZWAVE_ALARM_TYPE_SMOKE) {
		if (cmd.zwaveAlarmEvent == 3) {
			createSmokeOrCOEvents("tested", results)
		} else {
			createSmokeOrCOEvents((cmd.zwaveAlarmEvent == 1 || cmd.zwaveAlarmEvent == 2) ? "smoke" : "smokeClear", results)
		}
	} else if (cmd.zwaveAlarmType == physicalgraph.zwave.commands.alarmv2.AlarmReport.ZWAVE_ALARM_TYPE_CO) {
		createSmokeOrCOEvents((cmd.zwaveAlarmEvent == 1 || cmd.zwaveAlarmEvent == 2) ? "carbonMonoxide" : "carbonMonoxideClear", results)
	} else switch(cmd.alarmType) {
		case 1:
			createSmokeOrCOEvents(cmd.alarmLevel ? "smoke" : "smokeClear", results)
			break
		case 2:
			createSmokeOrCOEvents(cmd.alarmLevel ? "carbonMonoxide" : "carbonMonoxideClear", results)
			break
		case 12:  // test button pressed
			createSmokeOrCOEvents(cmd.alarmLevel ? "tested" : "testClear", results)
			break
		case 13:  // sent every hour -- not sure what this means, just a wake up notification?
			if (cmd.alarmLevel == 255) {
				results << createEvent(descriptionText: "$device.displayName checked in", isStateChange: false)
			} else {
				results << createEvent(descriptionText: "$device.displayName code 13 is $cmd.alarmLevel", isStateChange:true, displayed:false)
			}
			
			// Clear smoke in case they pulled batteries and we missed the clear msg
			if(device.currentValue("smoke") != "clear") {
				createSmokeOrCOEvents("smokeClear", results)
			}
			
			// Check battery if we don't have a recent battery event
			if (!state.lastbatt || (now() - state.lastbatt) >= 48*60*60*1000) {
				results << response(zwave.batteryV1.batteryGet())
			}
			break
		default:
			results << createEvent(displayed: true, descriptionText: "Alarm $cmd.alarmType ${cmd.alarmLevel == 255 ? 'activated' : cmd.alarmLevel ?: 'deactivated'}".toString())
			break
	}
}

// SensorBinary and SensorAlarm aren't tested, but included to preemptively support future smoke alarms
def zwaveEvent(physicalgraph.zwave.commands.sensorbinaryv2.SensorBinaryReport cmd, results) {
	if (cmd.sensorType == physicalgraph.zwave.commandclasses.SensorBinaryV2.SENSOR_TYPE_SMOKE) {
		createSmokeOrCOEvents(cmd.sensorValue ? "smoke" : "smokeClear", results)
	} else if (cmd.sensorType == physicalgraph.zwave.commandclasses.SensorBinaryV2.SENSOR_TYPE_CO) {
		createSmokeOrCOEvents(cmd.sensorValue ? "carbonMonoxide" : "carbonMonoxideClear", results)
	}
}

def zwaveEvent(physicalgraph.zwave.commands.sensoralarmv1.SensorAlarmReport cmd, results) {
	if (cmd.sensorType == 1) {
		createSmokeOrCOEvents(cmd.sensorState ? "smoke" : "smokeClear", results)
	} else if (cmd.sensorType == 2) {
		createSmokeOrCOEvents(cmd.sensorState ? "carbonMonoxide" : "carbonMonoxideClear", results)
	}
	
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpNotification cmd, results) {
	results << createEvent(descriptionText: "$device.displayName woke up", isStateChange: false)
	if (!state.lastbatt || (now() - state.lastbatt) >= 56*60*60*1000) {
		results << response([
				zwave.batteryV1.batteryGet().format(),
				"delay 2000",
				zwave.wakeUpV2.wakeUpNoMoreInformation().format()
			])
	} else {
		results << response(zwave.wakeUpV2.wakeUpNoMoreInformation())
	}
	results << createEvent(name: "deviceAlert",value:"clear",descriptionText:"No more alerts are found!")
}

def zwaveEvent(physicalgraph.zwave.commands.batteryv1.BatteryReport cmd, results) {
	def map = [ name: "battery", unit: "%", isStateChange: true ]
	state.lastbatt = now()
	if (cmd.batteryLevel == 0xFF) {
		map.value = 1
		map.descriptionText = "$device.displayName battery is low!"
	} else {
		map.value = cmd.batteryLevel
        results << createEvent(name: "deviceAlert",value:"clear",descriptionText:"Reported Battery Level: $cmd.batteryLevel")
	}
    state.lastbatt = new Date().time
	results << createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.Command cmd, results) {
	def event = [ displayed: false ]
	event.linkText = device.label ?: device.name
	event.descriptionText = "$event.linkText: $cmd"
	results << createEvent(event)
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpIntervalCapabilitiesReport cmd, results) {

    def map = [ name: "defaultWakeUpInterval", unit: "seconds" ]
	map.value = cmd.defaultWakeUpIntervalSeconds
	map.displayed = false
	state.defaultWakeUpInterval = cmd.defaultWakeUpIntervalSeconds
    results << createEvent(map)
}

def zwaveEvent(physicalgraph.zwave.commands.wakeupv2.WakeUpIntervalReport cmd, results) {

	def map = [ name: "reportedWakeUpInterval", unit: "seconds" ]
	map.value = cmd.seconds
	map.displayed = false
    results << createEvent(map)
}

/*def zwaveEvent(physicalgraph.zwave.Command cmd, results) {
	def event = [ displayed: false ]
	event.linkText = device.label ?: device.name
	event.descriptionText = "$event.linkText: $cmd"
	results << createEvent(event)
}
*/

def zwaveEvent(physicalgraph.zwave.commands.securityv1.SecurityMessageEncapsulation cmd, results) {
	def encapsulatedCommand = cmd.encapsulatedCommand([ 0x80: 1, 0x84: 1, 0x71: 2, 0x72: 1 ])
	state.sec = 1
	log.debug "encapsulated: ${encapsulatedCommand}"
	if (encapsulatedCommand) {
		zwaveEvent(encapsulatedCommand, results)
	} else {
		log.warn "Unable to extract encapsulated cmd from $cmd"
		results << createEvent(descriptionText: cmd.toString())
	}
}
// === End of Code