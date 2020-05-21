/*
 *	Copyright 2016 SmartThings
 *   Optimized by Murali Kesavan
 *
 *  NEW CAPABILITIES
 *  Ability to report Energy Consumption
 * 	Ability to control Reporting Frequency 
 *  
 *  OPEN ISSUE
 *  "Show power in activity feed" Input parameter is reset everytime  
 *
 *  ============================================================================
 *  Inspiration and Reference
 *
 *	Base Code: SmartThings SmartPower Outlet Device Handler
 *
 *  Energy Meter Code: Adrian Caramaliu  
 *  Article: https://community.smartthings.com/t/smartthings-plug-as-energy-meter/52065/3
 *  Code: https://raw.githubusercontent.com/ady624/CoRE/master/devicetypes/ady624/enhanced-smartpower-outlet.src/enhanced-smartpower-outlet.groovy
 *
 *  Reducing Power Reporting Interval Noise in a Device Handler
 *  Article: https://community.smartthings.com/t/reducing-power-reporting-noise-in-a-device-type/47091/19
 *  Code: https://github.com/instanttim/SmartThings/blob/master/devicetypes/instanttim/smartpower-outlet.src/smartpower-outlet.groovy
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
	// Automatically generated. Make future change here.
	definition(name: "Opti Power Outlet", namespace: "SmartThings/Outlet", author: "Murali Kesavan",
			mnmn: "geniHome", vid: "generic-switch-power", 
            ocfDeviceType: "oic.d.smartplug", runLocally: true, 
            minHubCoreVersion: '000.017.0012', executeCommandsLocally: true) {

		// Capabilities
		capability "Actuator"
		capability "Configuration"
		capability "Energy Meter"
		capability "Health Check"
		capability "Light"
		capability "Outlet"
		capability "Power Meter"
		capability "Refresh"
		capability "Sensor"
		capability "Switch"

		// indicates that device keeps track of heartbeat (in state.heartbeat)
		attribute "heartbeat", "string"
        attribute "show", "string" // new show = OLD display
        
		// reset
        //command "reset"
		
		// Fingerprint of Devices
        fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3200", 
            deviceJoinName: "SmartThings Outlet" //Outlet
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3200-Sgb", 
            deviceJoinName: "SmartThings Outlet" //Outlet
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "4257050-RZHAC", 
            deviceJoinName: "Centralite Outlet" //Outlet
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 000F, 0B04", 
        	outClusters: "0019", manufacturer: "SmartThings", model: "outletv4", 
            deviceJoinName: "SmartThings Outlet" //Outlet
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", deviceJoinName: "Outlet"
		fingerprint profileId: "0104", inClusters: "0000,0003,0006,0009,0B04", 
        	outClusters: "0019", manufacturer: "Samjin", model: "outlet", 
            deviceJoinName: "SmartThings Outlet" //Outlet
		fingerprint profileId: "0010", inClusters: "0000 0003 0004 0005 0006 0008 0702 0B05", 
        	outClusters: "0019", manufacturer: "innr", model: "SP 120", 
            deviceJoinName: "Innr Outlet" //Innr Smart Plug
		fingerprint profileId: "0104", inClusters: "0000,0002,0003,0004,0005,0006,0009,0B04,0702", 
        	outClusters: "0019,000A,0003,0406", manufacturer: "Aurora", model: "SmartPlug51AU", 
            deviceJoinName: "Aurora Outlet" //Aurora SmartPlug
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0008,0B04", 
        	outClusters: "0019", manufacturer: "Aurora", model: "SingleSocket50AU", 
            deviceJoinName: "Aurora Outlet" //Aurora SmartPlug
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0702,0B04,0B05,FC03", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3210-L", 
            deviceJoinName: "Iris Outlet" //Iris Smart Plug
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0006,0B04,0B05,0702", 
        	outClusters: "0003,000A,0B05,0019", manufacturer: " Sercomm Corp.", model: "SZ-ESW01-AU", 
            deviceJoinName: "Sercomm Outlet" //Sercomm Smart Power Plug
	}

	// simulator metadata
	simulator {
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"

		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
	}

	preferences {
		section {
			image(name: 'educationalcontent', multiple: true, images: [
					"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS1.jpg",
					"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS2.jpg"
			])
		}

		input name: "prefLogPower", type: "bool", title: "Show power in activity feed?", 
				description: "Click to set", defaultValue: false, required: true, displayDuringSetup: true

		input name: "prefLogPowerDelta", type: "decimal", title: "Only if power changes by…", 
				description: "Enter in watts", defaultValue: '10', required: true, displayDuringSetup: true	
	}

	// UI tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "lighting", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on",
                	backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off",
                	backgroundColor: "#ffffff", nextState: "turningOn"
				attributeState "turningOn", label: 'Turning On', action: "switch.off", icon: "st.switches.switch.on",
                	backgroundColor: "#00A0DC", nextState: "turningOff"
				attributeState "turningOff", label: 'Turning Off', action: "switch.on", icon: "st.switches.switch.off",
                	backgroundColor: "#ffffff", nextState: "turningOn"
			}
			tileAttribute("power", key: "SECONDARY_CONTROL") {
				attributeState "power", label: '${currentValue}', unit: 'W', icon:"st.secondary.energy"
			}
		}

		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label: '', action: "refresh.refresh", icon: "st.secondary.refresh"
		}

		valueTile("energy", "device.display", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
			state "default", label:'${currentValue}', icon:"st.secondary.energy", unit: "kWh"
		}

		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", label:'Reset kWh', action:"reset", icon:"st.secondary.refresh-icon"
		}

		valueTile("power", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue} W', icon:"st.secondary.energy", unit: "W"
		}
		
		main "switch"
		details(["switch","energy", "reset","refresh"])
		//details(["switch", "refresh"])
	}
}

// Parse incoming device messages to generate events
def parse(String description) {
	log.debug "Description is $description"

	// save heartbeat (i.e. last time we got a message from device)
	state.heartbeat = Calendar.getInstance().getTimeInMillis()

	// initialize the last power value used to throttle updates.
	state.lastPowerValue = ((state.lastPowerValue == null)) ? 0 : state.lastPowerValue
	/* if (state.lastPowerValue == null) {
		state.lastPowerValue = 0
	}*/

	def event = zigbee.getEvent(description)
	log.debug "Event is $event"

	if (event) {
		if (event.name == "power") {
			/* 	Dividing by 10 as the Divisor is 10000 and unit is kW for the device. 
            	AttrId: 0302 and 0300. Simplifying to 10
				power level is an integer. The exact power level with correct units needs
                to be handled in the device type to account for the different Divisor 
                value (AttrId: 0302) and POWER Unit (AttrId: 0300). 
                CLUSTER for simple metering is 0702 */

            // divisor = energy is measured in kWh, but stored in mWh for better accuracy
			def showPowerActivity = prefLogPower
			def pDivisor = device.getDataValue("divisor")
			pDivisor = pDivisor ? (pDivisor as int) : 10
			def pValue = (event.value as Integer) / pDivisor 
			def pChange = (pValue > state.lastPowerValue) ? (pValue - state.lastPowerValue) : 0
			/* Moved this check to declaration. remove codeblock after testing
			if (pChange < 0) {
            	pChange = -pChange
			}*/

			if (state.powerLastReported > 0) {
				// calculate elapsed time in milli hours, we'll multiply this with W to get mWh for better accuracy
				def elapsed = (now() - state.powerLastReported) / 3600 
				def energy = device.currentValue("power") * elapsed //energy calculated in mWh
				if (state.energySince > 0) {
					state.energy = state.energy + energy
				} else {
					state.energy = energy
					state.energySince = now()
				}
				//energy is measured in kWh, but stored in mWh for better accuracy
				sendEvent(name: "energy", value: state.energy / 1000000) 
				sendEvent(name: "show", value: "${getEnergyValue()} in ${getEnergySince()}")
			}

			state.lastPowerValue = pValue
			state.powerLastReported = now()
            			
            if (showPowerActivity == true) {
            	if (pChange > prefLogPowerDelta) {

					/*
					if (state.powerLastReported > 0) {
						// calculate elapsed time in milli hours, we'll multiply this with W to get mWh for better accuracy
						def elapsed = (now() - state.powerLastReported) / 3600 
						def energy = device.currentValue("power") * elapsed //energy calculated in mWh
						if (state.energySince > 0) {
							state.energy = state.energy + energy
						} else {
							state.energy = energy
							state.energySince = now()
						}
						//energy is measured in kWh, but stored in mWh for better accuracy
						sendEvent(name: "energy", value: state.energy / 1000000) 
						sendEvent(name: "show", value: "${getEnergyValue()} in ${getEnergySince()}")
					}
 
	                state.lastPowerValue = pValue
					state.powerLastReported = now()
					*/
					sendEvent(name: "power", value: pValue)

                } else {
					showPowerActivity = false
                }
            }

			event = createEvent(name: event.name, value: pValue, 
            	descriptionText: '{{ device.displayName }} power is {{ value }} Watts', translatable: true)           
                            
		} else if (event.name == "switch") {
			//def descriptionText = event.value == "on" ? '{{ device.displayName }} is On' : '{{ device.displayName }} is Off'
			//event = createEvent(name: event.name, value: event.value, descriptionText: descriptionText, translatable: true)
			event = createEvent(name: event.name, value: event.value, 
				descriptionText: '{{ device.displayName }} is {{ value.toUpperCase() }}', translatable: true)
		}
	} else {
		def cluster = zigbee.parse(description)

		if (cluster && cluster.clusterId == 0x0006 && cluster.command == 0x07) {
			if (cluster.data[0] == 0x00) {
				log.debug "ON/OFF REPORTING CONFIG RESPONSE: " + cluster
				event = createEvent(name: "checkInterval", value: 60 * 12, displayed: false, 
                	data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])
			} else {
				log.warn "ON/OFF REPORTING CONFIG FAILED- error code:${cluster.data[0]}"
				event = null
			}
		} else {
			log.warn "DID NOT PARSE MESSAGE for description : $description"
			log.debug "${cluster}"
		}
	}
	return event ? createEvent(event) : event
}

def off() {
	zigbee.off()
}

def on() {
	zigbee.on()
}

def ping() {
	// PING is used by Device-Watch in attempt to reach the Device
	return zigbee.onOffRefresh()
}

def refresh() {
	sendEvent(name: "heartbeat", value: "alive", displayed:false)
	// zigbee.onOffRefresh() + zigbee.electricMeasurementPowerRefresh() 
	zigbee.onOffRefresh() + zigbee.refreshData("0x0400", "0x0B04", "0x050B")
}

def reset() {
    state.energySince = state.powerLastReported
    state.energy = 0
    sendEvent(name: "energy", value: 0)
	sendEvent(name: "show", value: "${getEnergyValue()} in ${getEnergySince()}")
}

def configure() {
	// Setting proper divisor for Aurora AOne 13A Smart Plug
	def deviceModel = device.getDataValue("model")
	def divisorValue = deviceModel == "SingleSocket50AU" ? "1" : "10"
	device.updateDataValue("divisor", divisorValue)

	// Device-Watch allows 2 check-in misses from device + ping (plus 1 min lag time)
	// enrolls with default periodic reporting until newer 5 min interval is confirmed
	sendEvent(name: "checkInterval", value: 2 * 10 * 60 + 1 * 60, displayed: false, 
		data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

	// OnOff minReportTime 0 seconds, maxReportTime 5 min. Reporting interval if no activity
	//reset ()	
	//refresh() + zigbee.onOffConfig(0, 300) + zigbee.electricMeasurementPowerConfig()
	zigbee.onOffConfig(0, 300) + powerConfig() + refresh()
}

//power config for devices with min reporting interval as 1 seconds and reporting interval if no activity as 10min (600s)
//min change in value is 01
def powerConfig() {
	[
		"zdo bind 0x${device.deviceNetworkId} 1 ${endpointId} 0x0B04 {${device.zigbeeId}} {}", "delay 200",
		"zcl global send-me-a-report 0x0B04 0x050B 0x29 30 600 {05 00}",				//The send-me-a-report is custom to the attribute type for CentraLite
		"send 0x${device.deviceNetworkId} 1 ${endpointId}", "delay 500"
	]
}

def getEnergyValue() {
	if (!state || !state.energy) {
    	return "0Wh!!!"
    }
    if (state.energy > 1000000) {
    	return String.format("%.3f", state.energy / 1000000.00) + "kWh"
    } else {
    	return String.format("%.1f", state.energy / 1000.00) + "Wh"
    }
}

def getEnergySince() {
	if (!state || !state.energySince || state.energySince <= 0) {
    	return "0s!!!"
    }
    def dur = (now() - state.energySince) / 1000
    def d = (dur / 86400).toBigInteger()
    def h = ((dur - d * 86400) / 3600).toBigInteger()
    def m = ((dur - d * 86400 - h * 3600) / 60).toBigInteger()
    def s = (dur - d * 86400 - h * 3600 - m * 60).toBigInteger()
    return (d > 0 ? "${d} day" + (d > 1 ? "s " : " ") : " ") + "$h".padLeft(2, "0") + ":" + "$m".padLeft(2, "0") + ":" + "$s".padLeft(2, "0")
}
//=== End of Code
