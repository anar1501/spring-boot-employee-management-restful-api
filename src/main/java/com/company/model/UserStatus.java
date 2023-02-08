package com.company.model;

import javax.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("ALL")
@Data
@Entity
public class UserStatus implements Serializable{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "status", cascade = CascadeType.ALL)
    private Set<User> users = new HashSet<>();
}
