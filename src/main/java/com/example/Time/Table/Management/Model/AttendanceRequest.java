package com.example.Time.Table.Management.Model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

// Request payload class
@Getter
@Setter
public class AttendanceRequest {
    // Getters and Setters
    private Long timetableId;
    private List<AttendanceEntry> attendances;

    @Setter
    @Getter
    public static class AttendanceEntry {
        // Getters and Setters
        private String studentId;
        private boolean attended;

    }

}
