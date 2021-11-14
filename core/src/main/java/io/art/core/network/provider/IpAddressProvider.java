/*
 * ART
 *
 * Copyright 2019-2021 ART
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

package io.art.core.network.provider;

import io.art.core.collection.ImmutableMap;
import lombok.experimental.*;
import static io.art.core.collection.ImmutableMap.*;
import static java.net.NetworkInterface.*;
import static io.art.core.constants.NetworkConstants.*;
import static io.art.core.constants.StringConstants.*;
import java.net.*;
import java.util.*;

@UtilityClass
public class IpAddressProvider {
    public static String getIpAddress() {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = getNetworkInterfaces();
        } catch (SocketException throwable) {
            return LOCALHOST_IP_ADDRESS;
        }
        while (networkInterfaces.hasMoreElements()) {
            Enumeration<InetAddress> addressEnumeration = networkInterfaces.nextElement().getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = addressEnumeration.nextElement();
                String currentAddress = inetAddress.getHostAddress();
                if (!inetAddress.isLoopbackAddress() && IP_4_REGEX_PATTERN.matcher(currentAddress).matches()) {
                    return currentAddress;
                }

            }
        }
        return LOCALHOST_IP_ADDRESS;
    }

    public static ImmutableMap<String, String> getIpAddresses() {
        Enumeration<NetworkInterface> networkInterfaces;
        try {
            networkInterfaces = getNetworkInterfaces();
        } catch (SocketException throwable) {
            return emptyImmutableMap();
        }
        ImmutableMap.Builder<String, String> addresses = immutableMapBuilder();
        while (networkInterfaces.hasMoreElements()) {
            NetworkInterface networkInterface = networkInterfaces.nextElement();
            Enumeration<InetAddress> addressEnumeration = networkInterface.getInetAddresses();
            while (addressEnumeration.hasMoreElements()) {
                InetAddress inetAddress = addressEnumeration.nextElement();
                String currentAddress = inetAddress.getHostAddress();
                if (!inetAddress.isLoopbackAddress() && IP_4_REGEX_PATTERN.matcher(currentAddress).matches()) {
                    addresses.put(networkInterface.getName(), currentAddress);
                }

            }
        }
        return addresses.build();
    }

    public static String translateLocalHostToIp(String host) {
        return LOCALHOST.equalsIgnoreCase(host) ? LOCALHOST_IP_ADDRESS : host;
    }
}
