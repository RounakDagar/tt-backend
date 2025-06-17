package com.example.Time.Table.Management.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Setter
@Table(name = "student_attendance")
public class StudentAttendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String studentId;

    @Setter
    private String courseCode;

    @Setter
    private Long timetableId;

    @Setter
    private boolean attended;

    public StudentAttendance() {}

    public StudentAttendance(String studentId, String courseCode, Long timetableId, boolean attended) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.timetableId = timetableId;
        this.attended = attended;
    }

}
