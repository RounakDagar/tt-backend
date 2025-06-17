package com.example.Time.Table.Management.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "timetables")
public class Timetable {

    // Getters and Setters
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String day;
    private String timeSlot;
    private String venue;
    private Boolean attended;


    @ManyToOne
    @JoinColumn(name = "course_code")
    private Course course;

    @ManyToOne
    @JoinColumn(name = "faculty_id")  // faculty who teaches this class
    private User faculty;

    @ManyToMany
    @JoinTable(
            name = "timetable_students",
            joinColumns = @JoinColumn(name = "timetable_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )

    private List<Student> students; // students allowed to view/attend this lecture

    // Constructors
    public Timetable() {}

    public Timetable(String day, String timeSlot, String venue, Course course, User faculty, List<Student> students) {
        this.day = day;
        this.timeSlot = timeSlot;
        this.venue = venue;
        this.course = course;
        this.faculty = faculty;
        this.students = students;
    }


}
