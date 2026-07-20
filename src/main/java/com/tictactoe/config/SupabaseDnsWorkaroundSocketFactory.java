package com.tictactoe.config;

import javax.net.SocketFactory;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

public class SupabaseDnsWorkaroundSocketFactory extends SocketFactory {

    private static final String TARGET_HOST = "aws-0-ap-northeast-3.pooler.supabase.com";
    private static final String RESOLVED_IP = "13.208.57.198"; // Verified reachable IPv4 address for ap-northeast-3 region

    public static SocketFactory getDefault() {
        return new SupabaseDnsWorkaroundSocketFactory();
    }

    @Override
    public Socket createSocket() throws IOException {
        return new WorkaroundSocket();
    }

    @Override
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(mapHost(host), port);
    }

    @Override
    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException {
        return new Socket(mapHost(host), port, localHost, localPort);
    }

    @Override
    public Socket createSocket(InetAddress host, int port) throws IOException {
        return new Socket(host, port);
    }

    @Override
    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return new Socket(address, port, localAddress, localPort);
    }

    private static String mapHost(String host) {
        if (TARGET_HOST.equalsIgnoreCase(host)) {
            return RESOLVED_IP;
        }
        return host;
    }

    private static class WorkaroundSocket extends Socket {
        @Override
        public void connect(SocketAddress endpoint, int timeout) throws IOException {
            if (endpoint instanceof InetSocketAddress) {
                InetSocketAddress isa = (InetSocketAddress) endpoint;
                String host = isa.getHostString();
                if (TARGET_HOST.equalsIgnoreCase(host)) {
                    // Map the unresolved target hostname directly to our verified active IPv4 endpoint
                    endpoint = new InetSocketAddress(RESOLVED_IP, isa.getPort());
                }
            }
            super.connect(endpoint, timeout);
        }
    }
}
