package org.chorusbdd.chorus.stepserver;

import java.net.InetSocketAddress;

/**
 * Created by nick on 12/12/2016.
 */
public class ClientDetails {

    private final InetSocketAddress address;
    private final String clientId;

    public ClientDetails(InetSocketAddress address, String clientId) {
        this.address = address;
        this.clientId = clientId;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public String getClientId() {
        return clientId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ClientDetails that = (ClientDetails) o;

        if (!address.equals(that.address)) return false;
        return clientId.equals(that.clientId);

    }

    @Override
    public int hashCode() {
        int result = address.hashCode();
        result = 31 * result + clientId.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ClientDetails{" +
            "address=" + address +
            ", clientId='" + clientId + '\'' +
            '}';
    }
}
