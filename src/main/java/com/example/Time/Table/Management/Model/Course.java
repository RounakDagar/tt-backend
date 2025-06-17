package com.example.Time.Table.Management.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    private String courseCode; // e.g., CS101

    @Column(nullable = false)
    private String courseName;

    @Column(nullable = false)
    private int semester;

    @ManyToMany
    @JoinTable(
            name = "course_faculty",
            joinColumns = @JoinColumn(name = "course_code"),
            inverseJoinColumns = @JoinColumn(name = "faculty_id")
    )
    @JsonIgnore
    private Set<Faculty> faculties = new HashSet<>();



    // Many-to-Many: A course has many students, and a student can enroll in many courses
    @ManyToMany(mappedBy = "enrolledCourses")
    @JsonIgnore
    private Set<Student> enrolledStudents = new HashSet<>();

    public Course(String courseCode, String courseName, int semester) {
        this.courseCode = courseCode;
        this.courseName=courseName;
        this.semester=semester;
    }

    public Course(Course course) {
    }
}
