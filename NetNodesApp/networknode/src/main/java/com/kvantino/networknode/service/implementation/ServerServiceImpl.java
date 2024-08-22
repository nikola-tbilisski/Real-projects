package com.kvantino.networknode.service.implementation;

import com.kvantino.networknode.enumaration.Status;
import com.kvantino.networknode.model.Server;
import com.kvantino.networknode.repo.ServerRepo;
import com.kvantino.networknode.service.ServerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ServerServiceImpl implements ServerService {
    private final ServerRepo serverRepo;

    private Random random = new Random();

    @Override
    @Transactional
    public Server createServer(Server server) {
        log.info("Saving new server: {}", server.getName());
        server.setImageUrl(setServerImageUrl());

        return serverRepo.save(server);
    }

    @Override
    @Transactional
    public Server pingServer(String ipAddress) throws IOException {
        log.info("Pinging server IP: {}", ipAddress);
        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);
        serverRepo.save(server);

        return server;
    }

    @Override
    public Collection<Integer> scanServerPorts(String ipAddress, int portMaxToScan) throws IOException {
        log.info("Scan server ports: {}", ipAddress);
        ConcurrentLinkedQueue<Integer> openPorts = new ConcurrentLinkedQueue<>();

        Server server = serverRepo.findByIpAddress(ipAddress);
        InetAddress address = InetAddress.getByName(ipAddress);
        server.setStatus(address.isReachable(10000) ? Status.SERVER_UP : Status.SERVER_DOWN);

        if (server.getStatus() == Status.SERVER_UP) {
            try (ExecutorService executorService = Executors.newCachedThreadPool()) {
                AtomicInteger port = new AtomicInteger(0);
                while (port.get() <= portMaxToScan) {
                    final int currentPort = port.getAndIncrement();
                    executorService.submit(() -> {
                        try (Socket socket = new Socket()) {
                            socket.connect(new InetSocketAddress(server.getIpAddress(), currentPort), 1000);
                            openPorts.add(currentPort);
                        } catch (IOException ignored) {}
                    });
                }

                executorService.shutdown();

                try {
                    executorService.awaitTermination(10, TimeUnit.MINUTES);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        List<Integer> openPortList = new ArrayList<>();

        while (!openPorts.isEmpty()) {
            openPortList.add(openPorts.poll());
        }

        return openPortList;
    }

    @Override
    public Collection<Server> getServerList(int limit) {
        log.info("Fetching all servers");

        return serverRepo.findAll(PageRequest.of(0, limit)).toList();
    }


    @Override
    public Server getServerById(Long id) {
        log.info("Fetching server by id: {}", id);

        return serverRepo.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Server updateServer(Server server) {
        log.info("Updating server: {}", server.getName());

        return serverRepo.save(server);
    }

    @Override
    @Transactional
    public Boolean deleteServer(Long id) {
        log.info("Deleting server by id: {}", id);
        serverRepo.deleteById(id);

        return Boolean.TRUE;
    }

    private String setServerImageUrl() {
        String[] imageNames = {"server1.png", "server2.png", "server3.png", "server4.png"};

        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/server/image/" + imageNames[this.random.nextInt(4)])
                .toUriString();
    }
}
