package com.kvantino.networknode.service;

import com.kvantino.networknode.model.Server;

import java.io.IOException;
import java.util.Collection;

public interface ServerService {
    Server createServer(Server server);

    Server pingServer(String ipAddress) throws IOException;
    Collection<Integer> scanServerPorts(String ipAddress, int portMaxToScan);

    Collection<Server> getServerList(int limit);

    Server getServerById(Long id);

    Server updateServer(Server server);

    Boolean deleteServer(Long id);
}
