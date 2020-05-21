/**
 *  IRIS Smarty Plug
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
	definition (name: "IRIS Smarty Plug", namespace: "geniHome", author: "Murali Kesavan", cstHandler: true) {
		capability "Power Meter"
		capability "Power Source"
		capability "Switch"
		capability "Switch Level"
		capability "Energy Meter"
		capability "Power Consumption Report"
		capability "Voltage Measurement"
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
	// TODO: handle 'power' attribute
	// TODO: handle 'powerSource' attribute
	// TODO: handle 'switch' attribute
	// TODO: handle 'level' attribute
	// TODO: handle 'energy' attribute
	// TODO: handle 'powerConsumption' attribute
	// TODO: handle 'voltage' attribute

}

// handle commands
def on() {
	log.debug "Executing 'on'"
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
	// TODO: handle 'off' command
}

def setLevel() {
	log.debug "Executing 'setLevel'"
	// TODO: handle 'setLevel' command
}