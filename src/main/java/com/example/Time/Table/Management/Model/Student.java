package com.example.Time.Table.Management.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Setter
@Getter
@Entity
@Table(name = "students")
public class Student extends User {


    private String studentId;  // Unique Student ID

    @ManyToMany
    @JoinTable(
            name = "student_courses",
            joinColumns = @JoinColumn(name = "student_id"),
            inverseJoinColumns = @JoinColumn(name = "course_code")
    )
    private Set<Course> enrolledCourses = new HashSet<>();

    public Student() {}

    public Student(String email, String password, String name, String phoneNumber, String studentId) {
        super(email, password, UserRole.STUDENT, name, phoneNumber);
        this.studentId = studentId;
    }

    public List<String> getEnrolledCourseCodes() {
        return enrolledCourses.stream()
                .map(Course::getCourseCode)
                .collect(Collectors.toList());
    }


    public Student(User user, String email) {
        this.setId(user.getId());
        this.setEmail(user.getEmail());
        this.setPassword(user.getPassword());
        this.setName(user.getName());
        this.setPhoneNumber(user.getPhoneNumber());
        this.setRole(user.getRole());
        this.setVerified(user.isVerified());
        this.studentId = email;
    }

}
