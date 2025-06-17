package com.example.Time.Table.Management.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AttendanceSummaryDTO {
    private String studentId;
    private String courseCode;
    private long totalClasses;
    private long attendedClasses;
    private double attendancePercentage;

    public AttendanceSummaryDTO(String studentId, String courseCode, long total, long attended) {
        this.studentId = studentId;
        this.courseCode = courseCode;
        this.totalClasses = total;
        this.attendedClasses = attended;
        this.attendancePercentage = total == 0 ? 0.0 : (attended * 100.0 / total);
    }

}

