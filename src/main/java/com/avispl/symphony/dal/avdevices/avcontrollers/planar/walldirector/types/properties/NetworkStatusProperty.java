/*
 *  Copyright (c) 2025 AVI-SPL, Inc. All Rights Reserved.
 */
package com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.properties;

import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.types.Command;
import com.avispl.symphony.dal.avdevices.avcontrollers.planar.walldirector.common.Constant;

/**
 * Enum representing network status properties. Each enum value can be mapped to different commands.
 *
 * @author Kevin / Symphony Dev Team
 * @since 1.0.0
 */
public enum NetworkStatusProperty {
	HOSTNAME("Hostname", Command.HOSTNAME),
	DHCP_ENABLED("DHCPEnabled", Command.NETWORK_DHCP),
	IP_ADDRESS("IPAddress", Command.IPV4_ADDRESS),
	SUBNET_MASK("SubnetMask", Command.IPV4_NETMASK),
	GATEWAY("Gateway", Command.IPV4_GATEWAY),
	DNS_SERVER_1("DNSServer1", Command.NETWORK_DNS1),
	DNS_SERVER_2("DNSServer2", Command.NETWORK_DNS2),
	MAC_ADDRESS("MACAddress", Command.NETWORK_MAC);

	private final String name;
	private final Command command;

	NetworkStatusProperty(String name, Command command) {
		this.name = name;
		this.command = command;
	}

	/**
	 * Retrieves {@link #name}
	 *
	 * @return value of {@link #name}
	 */
	public String getName() {
		return name;
	}

	/**
	 * Retrieves {@link #command}
	 *
	 * @return value of {@link #command}
	 */
	public Command getCommand() {
		return command;
	}

	public String getCommandContent() {
		return this.command.getName() + Constant.GET_OPERATOR;
	}
}
