/**
 *	Copyright 2016 SmartThings
 *   Optimized by Murali Kesavan
 *
 *  NEW CAPABILITIES
 *  Ability to report Energy Consumption
 * 	Ability to control Reporting Frequency
 *  Ability to Report Change in usage as Vibration / Acceleration 
 *  
 *  OPEN ISSUE
 *  "Show power in activity feed" Input parameter is reset everytime  
 *
 *  ============================================================================
 *  Inspiration and Reference
 *
 *	Base Code: SmartThings SmartPower Outlet Device Handler
 *
 *  Energy Meter Code: Adrian Caramaliu (ady624)  
 *  Article: https://community.smartthings.com/t/smartthings-plug-as-energy-meter/52065/3
 *  Code: https://raw.githubusercontent.com/ady624/CoRE/master/devicetypes/ady624/enhanced-smartpower-outlet.src/enhanced-smartpower-outlet.groovy
 *
 *  Reducing Power Reporting Interval Noise in a Device Handler
 *  Article: https://community.smartthings.com/t/reducing-power-reporting-noise-in-a-device-type/47091/19
 *  Code: https://github.com/instanttim/SmartThings/blob/master/devicetypes/instanttim/smartpower-outlet.src/smartpower-outlet.groovy
 *
 *  Zooz Smart Outlet: Kevin LaFramboise (krlaframboise)
 *  Acceleration Sensor / Vibration Code
 *  Article: https://community.smartthings.com/t/release-zooz-power-switch-zooz-smart-plug/97220?u=krlaframboise7
 *  Code:  https://github.com/krlaframboise/SmartThings/tree/master/devicetypes/krlaframboise/zooz-power-switch.src
============================================================================
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

	definition(
		name: "Opti Power Outlet", 
		namespace: "Outlet", 
		author: "Murali Kesavan", 
		mnmn: "zenForz",
		vid: "generic-switch-power", 
		ocfDeviceType: "oic.d.smartplug", 
		runLocally: true,
		minHubCoreVersion: '000.017.0012', 
		executeCommandsLocally: true) {

		// Capabilities in alphabetical order
		capability "Acceleration Sensor"
		capability "Actuator" // always include if the device has commands
		capability "Configuration"
		capability "Energy Meter"
		capability "Health Check"
		capability "Light"
		capability "Outlet"
		capability "Power Meter"
		capability "Refresh"
		capability "Sensor" // always include if the device has attributes
		capability "Switch"
		//capability "Voltage Measurement"

		// All the Attributes here 
		// TODO refactor > make pulse = lastCheckin from Zooz
		attribute "pulse", "string" // device tracks pulse in state 
        attribute "report", "string" // new show = OLD display
		
		/* 
		attribute "history", "string"
		attribute "firmwareVersion", "string"		
        */

		// New Custom Commands
        command "reset" // resetToFactoryDefault
		command "toggle"
		command "identify"
		
		// Fingerprint of Devices
        // SmartThings Outlet - Samjin 
		fingerprint profileId: "0104", inClusters: "0000,0003,0006,0009,0B04", 
        	outClusters: "0019", manufacturer: "Samjin", model: "outlet", 
            deviceJoinName: "SmartThings Outlet" //Outlet
        // Iris Outlet - CentraLite 3210-L 
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0702,0B04,0B05,FC03", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3210-L", 
            deviceJoinName: "Iris Outlet" //Iris Smart Plug
        // SmartThings Outlet - CentraLite 3200
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3200", 
            deviceJoinName: "SmartThings Outlet" 
        // SmartThings Outlet - CentraLite 3200-SGB
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "3200-Sgb", 
            deviceJoinName: "SmartThings Outlet" 
        // SmartThings Outlet - CentraLite
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", manufacturer: "CentraLite", model: "4257050-RZHAC", 
            deviceJoinName: "Centralite Outlet" 
        // SmartThings Outletv4
		fingerprint profileId: "0104", inClusters: "0000, 0003, 0004, 0005, 0006, 000F, 0B04", 
        	outClusters: "0019", manufacturer: "SmartThings", model: "outletv4", 
            deviceJoinName: "SmartThings Outlet"
        // Generic Outlet 
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0B04,0B05", 
        	outClusters: "0019", deviceJoinName: "Outlet"
        // Innr Outlet 
		fingerprint profileId: "0010", inClusters: "0000 0003 0004 0005 0006 0008 0702 0B05", 
        	outClusters: "0019", manufacturer: "innr", model: "SP 120", 
            deviceJoinName: "Innr Outlet" 
        // Aurora Smart Plug 51AU 
		fingerprint profileId: "0104", inClusters: "0000,0002,0003,0004,0005,0006,0009,0B04,0702", 
        	outClusters: "0019,000A,0003,0406", manufacturer: "Aurora", model: "SmartPlug51AU", 
            deviceJoinName: "Aurora Outlet" 
		// Aurora Smart Plug 50AU
		fingerprint profileId: "0104", inClusters: "0000,0003,0004,0005,0006,0008,0B04", 
        	outClusters: "0019", manufacturer: "Aurora", model: "SingleSocket50AU", 
            deviceJoinName: "Aurora Outlet" 
        // Sercomm Smart Power Plug
		fingerprint profileId: "0104", inClusters: "0000,0001,0003,0004,0005,0006,0B04,0B05,0702", 
        	outClusters: "0003,000A,0B05,0019", manufacturer: " Sercomm Corp.", model: "SZ-ESW01-AU", 
            deviceJoinName: "Sercomm Outlet" 
        // Zooz Power Switch v 1.0 & 2.0
		//fingerprint mfr:"027A", prod:"0101", model:"000D", deviceJoinName: "Zooz Power Switch" 
        //  Zooz Power Plug v 1.0
		//fingerprint mfr:"027A", prod:"0101", model:"000A", deviceJoinName: "Zooz Smart Plug" 
	} // EOF definition

	// simulator metadata
	simulator {
	
		// status messages
		status "on": "on/off: 1"
		status "off": "on/off: 0"
		// TODO toggle?
		
		// reply messages
		reply "zcl on-off on": "on/off: 1"
		reply "zcl on-off off": "on/off: 0"
		// TODO toggle?

	} // EOF simulator

	preferences {
		section ("Education") {
			image(name: 'educationalcontent', multiple: true, images: [
					"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS1.jpg",
					"http://cdn.device-gse.smartthings.com/Outlet/US/OutletUS2.jpg"
			])
		}

		section ("Reporting") {
			// Input Parameter 1: reportIntervalMin denotes the Minimum interval in seconds between power reports
			input name: "reportIntervalMin", 
				type: "number", 
				required: false,
				displayDuringSetup: true, 
				range: "1..100",	
				title: "What is the Minimum interval in seconds between power reports?",		
				defaultValue: reportIntervalMin

			// Input Parameter 2: reportIntervalMax denotes the Maximum interval in seconds between power reports
			input name: "reportIntervalMax", 
				type: "number", 
				required: false,
				displayDuringSetup: true,		
				range: "11..600", 
				title: "What is the Maximum interval in seconds between power reports?",	
				defaultValue: reportIntervalMax

			// Input Parameter 3: reportPowerLevel denotes the change in Watts to report
			input "reportPowerLevel", 
				type: "number", 
				required: false, 
				displayDuringSetup: true, 
				title: "How many Watts of Power change required to report?",		
				defaultValue: reportPowerLevel		

			// Input Parameter 4: isAlwaysOn flag to determine whether output stay on Always
			input "isAlwaysOn", 
				type: "bool", 
				required: false, 
				displayDuringSetup: true, 
				title: "Always On?",
				defaultValue: isAlwaysOn

			// Input Parameter 5: isPowerReportOn flag determines whether to report power in activity feed
			input "isPowerReportOn", 
				type: "bool", 
				required: false, 
				displayDuringSetup: true,		
				defaultValue: isPowerReportOn, 
				title: "Report power in activity feed?"

			// Input Parameter 6: isTraceOn determines whether to log high-level activities
			input "isTraceOn", 
				type: "bool",
				required: false, 		
				displayDuringSetup: true,
				title: "Show Trace Statements?",
				defaultValue: isTraceOn

			// Input Parameter 7: isDebugOn determines whether to log detailed activities
			input "isDebugOn", 
				type: "bool",
				required: false, 		
				displayDuringSetup:true,
				title: "Show Debug Statements?",
				defaultValue: isDebugOn
		}
	}

	// UX tile definitions
	tiles(scale: 2) {
		multiAttributeTile(name: "switch", type: "generic", width: 6, height: 4, canChangeIcon: true) {
			tileAttribute("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label: 'On', action: "switch.off", icon: "st.switches.switch.on",
                	backgroundColor: "#00A0DC" //, nextState: "turningOff"
				attributeState "off", label: 'Off', action: "switch.on", icon: "st.switches.switch.off",
                	backgroundColor: "#ffffff" //, nextState: "turningOn"
			}
            
			// SECONDARY_CONTROL
			tileAttribute("power", key: "SECONDARY_CONTROL") {
				attributeState "power", label: '${currentValue}', action: "reset", 
						unit: 'W', icon:"st.secondary.refresh-icon", backgroundColor: "#ffffff" 
			}
            
			//https://community.smartthings.com/t/multiattributetile-example-for-nest-thermostat/40485/10?u=the.kesavan
            tileAttribute ("device.acceleration", key: "ACCELERATION") {
				attributeState "active", label: 'Active', icon:"st.secondary.vibration-icon", backgroundColor: "#ffffff" 
				attributeState "inactive", label: 'Inactive', icon:"st.secondary.vibration-icon"
			}
		}

		standardTile("refresh", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "refresh", label: 'Refresh', action: "refresh.refresh", icon: "st.secondary.refresh-icon"
		}

		standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "reset", label:'Reset kWh', action:"reset", icon:"st.secondary.refresh-icon"
		}

		//Zooz - purpose?
		standardTile("refresh", "device.refresh", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "refresh", label:'Refresh', action: "refresh", icon:"st.secondary.refresh-icon"
		}

		//Zooz - purpose? 
		standardTile("reset", "device.reset", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "reset", label:'Reset', action: "reset", icon:"st.secondary.refresh-icon"
		}

		valueTile("power", "device.power", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "power", label:'${currentValue}', icon:"st.secondary.energy-icon", unit: "W"
		}
        
		valueTile("energy", "device.energy", inactiveLabel: false, decoration: "flat", width: 4, height: 1) {
			state "energy", label:'${currentValue}', icon:"st.secondary.energy-icon", unit: "kWh"
		}

		//Zooz
		/*valueTile("voltage", "device.voltage", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "voltage", label:'${currentValue}', icon:"st.secondary.voltage-icon", unit: "V"
		}
        
		//Zooz
		valueTile("current", "device.current", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "current", label:'${currentValue}', icon:"st.secondary.current-icon", unit: "A"
		}*/
        
		//Zooz
		/*valueTile("history", "device.history", decoration:"flat",width: 6, height: 3) {
			state "history", label:'${currentValue}'
		}
		
		//Zooz
		valueTile("firmwareVersion", "device.firmwareVersion", decoration:"flat", width:3, height: 1) {
			state "firmwareVersion", label:'Firmware ${currentValue}'
		}*/

		//https://community.smartthings.com/t/multiattributetile-example-for-nest-thermostat/40485/10?u=the.kesavan
        // update the tile below to reflect Zooz Outlet Device Handler
        /*standardTile("acceleration", "device.acceleration", decoration: "flat", width: 2, height: 2) {
 			state "idle", action:"polling.poll", label:'${name}', icon: 'http://cdn.device-icons.smartthings.com/sonos/pause-icon@2x.png'
 			state "cooling", action:"polling.poll", label:' ', icon: "st.thermostat.cooling"
 			state "heating", action:"polling.poll", label:' ', icon: "st.thermostat.heating"
 			state "fan only", action:"polling.poll", label:'${name}', icon: "st.Appliances.appliances11"
 		}
		*/

		// Iris
        /*
		valueTile("elapsedTimeDisplay", "device.elapsedTimeDisplay", decoration: "flat", width: 5, height: 1){
			state "default", label: 'Time: ${currentValue}', unit: "h"
		}      

		standardTile("configure", "device.switch", inactiveLabel: false, decoration: "flat", width: 1, height: 1) {
			state "default", label:"", action:"configure", icon:"st.secondary.configure"
		}
		*/

		// the order in which the items are listed in details determines the order in which the caps will be displayed
		// Use details() to specify all other tiles that should be available on the device details screen. 
		// The tiles will layout in left-to-right, top-to-bottom order beginning with the first argument:
		main ("switch")
		details(["switch", "power", "energy", "acceleration", "refresh", "reset"])
		// details(["switch", "power", "energy", "acceleration", "current", "firmwareVersion", "history", "refresh", "reset", "voltage"])
		//details(["energy", "power", "reset","refresh", "switch"])
		// Iris: //details(["switch","energyDisplay","resetUsage","power","elapsedTimeDisplay","refresh"])
	} // EOF tiles
} // EOF metadata 

/** 
 * parse()
 * This method is responsible for creating events for the attributes of the device’s capabilities
 * It processes the incoming messages (zigbee or z-wave) from the device and generate events 
 * for the respective attributes of the device's capabilities
 */
def parse(description) {

	logMessage ("parse()")

	logMessage ("Description is $description", "trace")

	// save pulse (i.e. last time we got a message from device)
	checkPulse()
	//state.heartbeat = Calendar.getInstance().getTimeInMillis()


	// initialize the last power value used to throttle updates.
	state.lastPowerValue = ((state.lastPowerValue == null)) ? 0 : state.lastPowerValue

	def inEvent = zigbee.getEvent(description)
	def inMsg = zigbee.parseDescriptionAsMap(description)

	logMessage ("Event is $inEvent", "trace")
    
	if (inEvent) {
	
		if ((inEvent.name == "power") ) {
		
			// process power event in watts 
			doPower (inEvent.value)

		} else if (inEvent.name == "switch") {
	
			doSwitch ()

		}
	} else if (inMsg?.cluster == "0B04") {

		if (inMsg?.attrId == "050B") {
		// process power event in watts 
			doPower (inMsg.value)
		} 
	} else {
	
		def inCluster = zigbee.parse(description)

		if (inCluster && inCluster.clusterId == 0x0006 && (inCluster.command == 0x07 || inCluster.command == 0x0B)) {

			if (inCluster.data[0] == 0x00 || cluster.data[0] == 0x02) {

				logMessage("ON/OFF/TOGGLE REPORTING CONFIG RESPONSE: " + inCluster)

				inEvent = checkPulse()

			} else {

				logMessage("ON/OFF/TOGGLE REPORTING CONFIG FAILED- error code:${inCluster.data[0]}", "warn")
				inEvent = null
			}
		} else if (inCluster && inCluster.clusterId == 0x0B04 && inCluster.command == 0x07){
			if (inCluster.data[0] == 0x00) {
				// Get a power meter reading
				runIn(5, "refresh")
				logMessage ("POWER REPORTING CONFIG RESPONSE: " + inCluster)
			} else {
				logMessage ("POWER REPORTING CONFIG FAILED- error code:${cluster.data[0]}", "error")
				inEvent = null
			}
		} else if (inCluster && inCluster.clusterId == 0x0003 && inCluster.command == 0x04) {
			logMessage ("LOCATING DEVICE FOR 30 SECONDS", "info")
		} else {

			logMessage("DID NOT PARSE MESSAGE for description : $description", "warn")

			logMessage("${inCluster}")

		}
	}
	return inEvent ? createEvent(inEvent) : inEvent
}

/**
 * initialize() 
 * initialize all values 
 */ 
def initialize() {

	logMessage ("initialize()")

	// TODO Update this method to initialize all state variables 
	state.pulse = 0
	state.lastPulseCheck = 0
	state.lastPowerValue = 0
	state.energy = 0
	state.energySince = 0
	state.reportPeriodicity = 60 // 1 minute
	//def reportPeriodicity = 60 // 1 minute
	
	state.lastSwitch = 0

	if (isTraceOn || isDebugOn)
	{
		logMessage ("Verbose logging has been enabled for the next 30 minutes.", "info")
		//TODO http://docs.smartthings.com/en/latest/ref-docs/smartapp-ref.html#runin
		runIn(1800, logsOff)
	}
}

/**
 * installed() 
 * calls initialize and configure
 */ 
def installed(){
	logMessage ("installed()")
	
	initialize()
    configure()
}

/**
 * updated() 
 * calls initialize and configure
 */ 
def updated(){
	logMessage ("updated")
	
	initialize()
    configure()
}

/**
 * on() 
 * turns off the device 
 */ 
def off() {
	// if NOT isAlwaysOn 
	if (!setting?.isAlwaysOn){
		logMessage ("off()")
		zigbee.off()
	} else {
		logMessage ("off() - OFF command ignored: Always ON setting is active!", "warn")
	}
}

/**
 * on() 
 * turns on the device 
 */ 
def on() {
	// if NOT isAlwaysOn 
	if (!setting?.isAlwaysOn){
		logMessage ("on()")
		zigbee.on()
	} else {
		logMessage ("on() - ON command ignored: Always ON setting is active!", "warn")
	}
}

/**
 * toggle()
 * Toggles the device on/off state.
 */
def toggle() {
	// if NOT isAlwaysOn 
	if (!setting?.isAlwyasOn) {
		logMessage ("toggle()", "trace")
		zigbee.command(0x0006, 0x02)
	} else {
		logMessage ("toggle() - toggle command ignored: Always ON setting is active!", "warn")
	}
}

/**
 * identify() 
 * flashes the LED on plug to identify it
 */ 
def identify()
{
	logMessage ("identify()")
	zigbee.writeAttribute(0x0003, 0x0000, DataType.UINT16, 0x00A)
}

/**
 * ping() 
 * Used by Device-Watch in attempt to reach the Device
 */ 
def ping() {

	logMessage ("ping()")

	checkPulse()

	// if the reportingInterval is met, refresh
	return zigbee.onOffRefresh()
}

/**
 * reset() 
 * resets the device to factory defaults but does not unpair the device.
 */ 
def reset() {

	logMessage ("reset()")
	logMessage ("Resetting device to factory defaults!", "warn")

	// Reset Energy
    state.energySince = state.lastPulseCheck
    state.energy = 0
    sendEvent(name: "energy", value: 0)
	sendEvent(name: "report", value: "${getEnergyValue()} in ${getEnergySince()}")

	// TODO Reset Power
	// TODO Reset Voltage
	// TODO Reset Current

	// Configure
	runIn (15, configure)
	
	// Refresh
	runIn (10, refresh)
	
}

/**
 * refresh()
 * Refreshes the device by requesting manufacturer-specific information.
 * Note: This is called from the refresh capbility
 */ 
 def refresh() {
	logMessage ("refresh()")
	ping()
	zigbee.onOffRefresh() + zigbee.refreshData("0x0400", "0x0B04", "0x050B") 
			+ zigbee.electricMeasurementPowerRefresh() + zigbee.readAttribute(0x0B04, 0x0505) 
			+ zigbee.readAttribute(0x0B04, 0x0300)
}

/** 
 * configure ()
 * Configures the Zigbee / Z-Wave repeater associations and 
 * establishes periodic device check.
 */
def configure() {
	logMessage ("configure()")

	checkPulse()
	
	// On/Off reporting of 0 seconds, maximum of 15 minutes if the device does not report any on/off activity
	zigbee.onOffConfig(0, 900) + powerConfig() + refresh()
}

/**
 * powerConfig() 
 * this method is for devices with min reporting interval as 1 seconds 
 * and reporting interval if no activity as 10min (600s) min change in value is 01 
 * The send-me-a-report is custom to the attribute type for CentraLite
 */		
def powerConfig() {

	logMessage ("powerConfig()")
	
	/* Old Code
	[
		"zdo bind 0x${device.deviceNetworkId} 1 ${endpointId} 0x0B04 {${device.zigbeeId}} {}",
		"delay 200",
		"zcl global send-me-a-report 0x0B04 0x050B 0x29 30 600 {05 00}",
		"send 0x${device.deviceNetworkId} 1 ${endpointId}", "delay 500"
	]
	*/
	
	// Calculate threshold
	def powerDelta = (Float.parseFloat(minDeltaV ?: "1") * 10)
	
	logDebug "Configuring power reporting intervals; min: ${intervalMin}, max: ${intervalMax}, delta: ${minDeltaV}, endpointId: ${endpointId}"
	
	def cfg = []
	// https://docs.smartthings.com/en/latest/ref-docs/zigbee-ref.html#zigbee-configurereporting

	cfg +=	zigbee.configureReporting(0x0B04, 0x050B, 0x29, (int) (reportIntervalMin ?: 5), (int) (reportIntervalMax ?: 600), (int) powerDelta)	// Wattage report.
	cfg +=	zigbee.configureReporting(0x0B04, 0x0505, 0x21, 30, 900, 1)																// Voltage report
	cfg +=	zigbee.configureReporting(0x0B04, 0x0300, 0x21, 900, 86400, 1)  														// AC Frequency Report
	
	return cfg
	
}

/**
 * doPower(powerValue)
 * processes power event
 */
private doPower (powerValue = 0) {
	logMessage ("doPower($powerValue)")
	
	if (powerValue > 0) {
		// divisor = energy is measured in kWh, but stored in mWh for better accuracy
		powerValue = powerValue / (( (device.getDataValue("divisor")) ?: 10 ) as int)
	}
	def powerChange = (powerValue - state.lastPowerValue) * ((powerValue > state.lastPowerValue) ? 1 : -1)

	state.lastPowerValue = powerValue
	state.lastPulseCheck = now()

	if (powerChange > setting?.reportPowerLevel) {

		doAcceleration("active")
		
		if (isPowerReportOn) {
			//createEvent(name: "energy", value: state.energy / 1000000) 
			//createEvent(name: "show", value: "${getEnergyValue()} in ${getEnergySince()}")
			def inEvent = sendEvent(name: "power", value: powerValue, 
				descriptionText: "${device.displayName} power is ${powerValue} Watts", translatable: true)           
			logMessage ("${device.displayName} power is ${powerValue} watts", "trace")
			//doEnergy()
		}
	}
}

/**
 * doSwitch ()
 * processes switch event 
 */
private doSwitch () {
	logMessage ("doSwitch()")
	
	inEvent = createEvent(name: inEvent.name, value: inEvent.value, 
		descriptionText: "${device.displayName} is ${value.toUpperCase()}", translatable: true)
	
	if (inEvent.value == "off") {
		// Since the switch has reported that it is off it can't be using any power.  
		//Set to zero in case the power report does not arrive, but do not report in event logs.
		doPower(true)
	}	
	// DEVICE HEALTH MONITOR: Switch state (on/off) should report every 10mins or so, regardless of any state changes.
	// Capture the time of this message
	inEvent = checkPulse()
	return inEvent
}

/**
 * doAcceleration(state = "inactive")
 * processes acceleration event 
 * Change in power level that too more than reportPowerLevel is 
 * considered an acceleration, thus triggers a acceleration Event
 */
private doAcceleration (state = "inactive") {
	logMessage ("doAcceleration($state)")
	
	def inEvent = sendEvent(name:"acceleration", value:state, displayed:false)

	return inEvent
}

/**
 * doEnergy (state = "inactive")
 * processes energy event 
 * energy is measured in kWh, but stored in mWh for better accuracy
 */
private doEnergy (state = "energy") {
	logMessage ("doEnergy($state)")

	def timeElapsed = (now() - state.lastPulseCheck) / 3600														 
	state.energy = (device.currentValue("power") * timeElapsed) + (state.energy ?: 0)
	createEvent(name: "energy", value: state.energy / 1000000) 
	createEvent(name: "report", value: "${getEnergyValue()} in ${getEnergySince()}")

}

/**
 * getEnergyValue()
 * returns whether isPowerReportOn flag is ON (i.e. True) or Off (i.e. False)
 */ 
private getEnergyValue() {

	logMessage ("getEnergyValue()")
	
	if (!state || !state.energy) {
    	return "0Wh!!!"
    }
    if (state.energy > 1000000) {
    	return String.format("%.3f", state.energy / 1000000.00) + "kWh"
    } else {
    	return String.format("%.1f", state.energy / 1000.00) + "Wh"
    }
}


/**
 * getEnergySince()
 * what is the logic?
 */ 
private getEnergySince() {

	logMessage ("getEnergySince()")
	
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

/**
 * checkPulse()
 * Healthcheck method
 */ 
private checkPulse() {
	
	logMessage ("checkPulse()", "trace")
	
	// TODO Need somekind of validation - reporting interval or something
	//sendEvent(name: "heartbeat", value: "alive", displayed:false)
	state.lastPulseCheck = new Date().time
	sendEvent(name: "pulse", value: convertToLocalTimeString(new Date()), displayed: false, isStateChange: true)

	// Set the Health Check interval so that it can be skipped twice plus 5 minutes.
	// enrolls with default periodic reporting until newer 5 min interval is confirmed
	def checkInterval = ((reportIntervalMax * 2) + (5 * 60))
	sendEvent(name: "checkInterval", value: checkInterval, displayed: false, 
                	data: [protocol: "zigbee", hubHardwareId: device.hub.hardwareID])

}

/*
private logsOff()
{
    logMessage ("debug logging disabled...", "warn")
    device.updateSetting("isTraceOn", [value:"false",type:"bool"])
	device.updateSetting("isDebugOn", [value:"false",type:"bool"])
}
*/

/**
 * isPowerReportOn()
 * returns whether isPowerReportOn flag is ON (i.e. True) or Off (i.e. False)
 */ 
private isPowerReportOn () {

	logMessage ("getIsPowerReportOn()")

	//def isPowerReportOn = ((settings?.isPowerReportOn == null)) ? false : settings?.isPowerReportOn
	// ?: Elvis Operator - https://mrhaki.blogspot.com/2009/08/groovy-goodness-elvis-operator.html
	def isPowerReportOn = (settings?.isPowerReportOn) ?: false

	if (isPowerReportOn != settings?.isPowerReportOn) {
		// reset the Setting
		//device.updateSetting("isPowerReportOn", [value: isPowerReportOn, type: "bool"])
	}

    return isPowerReportOn 
}

/**
 * getReportIntervalMin()
 * returns reportIntervalMin value 
 */ 
private getReportIntervalMin() {
	
	logMessage ("getReportIntervalMin()")

	def reportIntervalMin  = ((settings?.reportIntervalMin)) ?: (60 * 3) // default 3 minutes

	if (reportIntervalMin != settings?.reportIntervalMin) {
		// reset the Setting
		//TODO
        //device.updateSetting("reportIntervalMin", [value: reportIntervalMin, type: "number"])
	}
    return reportIntervalMin
}

/**
 * getReportIntervalMax()
 * returns reportIntervalMax value 
 */ 
private getReportIntervalMax() {
	
	logMessage ("getReportIntervalMax()")

	def reportIntervalMax  = ((settings?.reportIntervalMax)) ?: 10800 // default 3 hours

	if (reportIntervalMax != settings?.reportIntervalMax) {
		// reset the Setting
		//device.updateSetting("reportIntervalMax", [value: reportIntervalMax, type: "number"])
	}
    return reportIntervalMin
}

/**
 * getReportPowerLevel()
 * returns reportPowerLevel value 
 */ 
private getReportPowerLevel() {
	
	logMessage ("getReportPowerLevel()")

	def reportPowerLevel = ((settings?.reportPowerLevel)) ?: 10 // default 10 watts

	if (reportPowerLevel != settings?.reportPowerLevel) {
		// reset the Setting
		//TODO
        //device.updateSetting("reportPowerLevel", [value: reportPowerLevel, type: "number"])
	}
	
    return reportPowerLevel 
}

/**
 * isAlwaysOn()
 * returns whether isAlwaysOn flag is ON (i.e. True) or Off (i.e. False)
 */ 
private isAlwaysOn() {
	
	logMessage ("isAlwaysOn()")
	
	def isAlwaysOn = ((settings?.isAlwaysOn)) ?: false

	if (isAlwaysOn != settings?.isAlwaysOn) {
		// reset the Setting
		//device.updateSetting("isAlwaysOn", [value: isAlwaysOn, type: "bool"])
	}

	return isAlwaysOn
}

/**
 * isTraceOn()
 * returns whether isTraceOn flag is ON (i.e. True) or Off (i.e. False)
 */ 
private isTraceOn() {
	
	logMessage ("isDebugOn()")
	
	def isDebugOn = ((settings?.isDebugOn)) ?: false

	if (isTraceOn != settings?.isTraceOn) {
		// reset the Setting
		//device.updateSetting("isTraceOn", [value: isTraceOn, type: "bool"])
	}

	return isDebugOn
}

/**
 * isDebugOn()
 * returns whether isDebugOn flag is ON (i.e. True) or Off (i.e. False)
 */ 
private isDebugOn() {
	
	logMessage ("isDebugOn()")
	
	def isDebugOn = ((settings?.isDebugOn)) ?: false

	if (isDebugOn != settings?.isDebugOn) {
		// reset the Setting
		//device.updateSetting("isDebugOn", [value: isDebugOn, type: "bool"])
	}
	return isDebugOn
}

/**
 * logMessage(msg, mType = "debug")
 * unified method to log all kinds of message
 */ 
private logMessage(msg, mType = "debug") {
    switch(mType) {
        case "error":
    		log.error "$msg"
        case "warn":
    		log.warn "$msg"
        case "trace":
    		log.trace "$msg"
        case "info":
    		log.info "$msg"
        default:
			if (isDebugOn) {
				log.debug "$msg"
			}
    }
}

/**
 * getDeviceCap()
 * gets the Device Capability
 */ 
private getDeviceCap (devType = "switch") {
	// Device Capability List here https://docs.smartthings.com/en/latest/capabilities-reference.html#capabilities-taxonomy
    switch(devType) {
        case "accelerationSensor":
            return ["active","inactive"]
        case "contactSensor":
            return ["open","closed"]
        case "lock":
            return ["lock","unlock"]
        case "motionSensor":
            return ["active","inactive"]
        case "moistureSensor":
            return ["wet","dry"]
        case "switch":
            return ["on","off"]
        default:
            return ["UNKNOWN"]
    }
}

/**
 * convertToLocalTimeString (dt)
 * converts given date to Local Time String 
 */ 
private convertToLocalTimeString(dt) {
	logMessage ("convertToLocalTimeString()")
	
	def timeZoneId = location?.timeZone?.ID
	if (timeZoneId) {
		return dt.format("MM/dd/yyyy hh:mm:ss a", TimeZone.getTimeZone(timeZoneId))
	}
	else {
		return "$dt"
	}	
}

//=== End of Code

