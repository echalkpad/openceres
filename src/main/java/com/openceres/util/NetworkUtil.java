package com.openceres.util;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

public class NetworkUtil {

	// public static String getIpAddres()
	// {
	// InetAddress ip = null;
	// try {
	// ip = InetAddress.getLocalHost();
	// } catch (UnknownHostException e) {
	//
	// e.printStackTrace();
	// }
	// if(ip != null) return ip.getHostAddress();
	// else return null;
	// }

	public static String getIpAddres() {
		try {
			Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
			while (interfaces.hasMoreElements()) {
				NetworkInterface current = interfaces.nextElement();
				if (!current.isUp() || current.isLoopback() || current.isVirtual())
					continue;
				Enumeration<InetAddress> addresses = current.getInetAddresses();
				if (!current.getName().startsWith("e"))
					continue;
				while (addresses.hasMoreElements()) {
					InetAddress current_addr = addresses.nextElement();
					if (current_addr.isLoopbackAddress())
						continue;
					if (current_addr.getHostAddress().contains("."))
						return current_addr.getHostAddress();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return null;
	}
}
