package client;

import java.net.InetAddress;
import java.util.Objects;

public class ServerLocation {
    private final String name;
    private final InetAddress address;
    private final int portNumber;

    private ServerLocation(final String name, final InetAddress address, final int portNumber) {
        this.name = name;
        this.address = address;
        this.portNumber = portNumber;
    }

    public static ServerLocation of(final InetAddress address, final String receivedData) {
        String[] receivedDataParts = receivedData.split("__");
        String name = receivedDataParts[0];
        int portNumber = Integer.parseInt(receivedDataParts[1].trim());
        return new ServerLocation(name, address, portNumber);
    }

    public String getName() {
        return name;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPortNumber() {
        return portNumber;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ServerLocation that = (ServerLocation) o;
        return portNumber == that.portNumber
                && Objects.equals(name, that.name)
                && Objects.equals(address, that.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, address, portNumber);
    }

    @Override
    public String toString() {
        return "ServerLocation{" + "name='" + name + '\'' + ", address=" + address + ", portNumber=" + portNumber + '}';
    }
}
