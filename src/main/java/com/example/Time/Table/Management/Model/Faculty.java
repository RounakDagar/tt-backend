package com.example.Time.Table.Management.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Entity
@Table(name = "faculty")
public class Faculty extends User {

    @Column(unique = true, nullable = false)
    private String facultyId;

    @ManyToMany(mappedBy = "faculties")
    @JsonIgnore
    private Set<Course> courses = new HashSet<>();

    public Faculty() {}

    public Faculty(String email, String password, String name, String phoneNumber, String facultyId) {
        super(email, password, UserRole.FACULTY, name, phoneNumber);
        this.facultyId = facultyId;
    }

    public Faculty(User user, String facultyId) {
        this.setId(user.getId());
        this.setEmail(user.getEmail());
        this.setPassword(user.getPassword());
        this.setName(user.getName());
        this.setPhoneNumber(user.getPhoneNumber());
        this.setRole(user.getRole());
        this.setVerified(user.isVerified());
        this.facultyId = facultyId;
    }

    public Faculty orElseThrow(Object o) {
        return null;
    }
}
