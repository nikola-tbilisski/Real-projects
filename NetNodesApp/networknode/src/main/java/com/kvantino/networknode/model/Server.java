package com.kvantino.networknode.model;

import com.kvantino.networknode.enumaration.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "server")
public class Server {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @NotEmpty(message = "IP address can not be empty or null")
    @Column(name = "ip_address", unique = true)
    private String ipAddress;
    @Column(name = "name")
    private String name;
    @Column(name = "memory")
    private String memory;
    @Column(name = "type")
    private String type;
    @Column(name = "image_url")
    private String imageUrl;
    @Column(name = "status")
    private Status status;
}
