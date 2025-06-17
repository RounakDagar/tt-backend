package com.example.Time.Table.Management.controller;

import com.example.Time.Table.Management.Model.Course;
import com.example.Time.Table.Management.Model.Student;
import com.example.Time.Table.Management.Repo.UserRepository;
import com.example.Time.Table.Management.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/add")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> addCourse(@RequestBody Map<String, Object> courseData) {
        String courseCode = (String) courseData.get("courseCode");
        String courseName = (String) courseData.get("courseName");
        int semester = (int) courseData.get("semester");
        List<String> facultyIds = (List<String>) courseData.get("facultyIds");

        Course saved = courseService.addCourse(courseCode, courseName, semester, new HashSet<>(facultyIds));
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Course>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }

    @GetMapping("/faculty/{facultyId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<List<Course>> getFacultyCourses(@PathVariable String facultyId) {
        return ResponseEntity.ok(courseService.getCoursesOfFaculty(facultyId));
    }

    @GetMapping("/{courseCode}")
    public ResponseEntity<Course> getCourseByCode(@PathVariable String courseCode) {
        return courseService.getCourseByCode(courseCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{courseCode}/students")
    public ResponseEntity<Set<Student>> getEnrolledStudents(@PathVariable String courseCode) {
        return courseService.getCourseByCode(courseCode)
                .map(course -> ResponseEntity.ok(course.getEnrolledStudents()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/update")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<Course> updateCourse(@RequestBody Course course) {
        return ResponseEntity.ok(courseService.updateCourse(course));
    }

    @DeleteMapping("/delete/{courseCode}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> deleteCourse(@PathVariable String courseCode) {
        courseService.deleteCourse(courseCode);
        return ResponseEntity.ok("Course deleted successfully!");
    }

    @GetMapping("/students/{studentId}/courses")
    public ResponseEntity<List<Course>> getStudentCourses(@PathVariable String studentId) {
        List<Course> courses = courseService.getCoursesByStudentId(studentId);
        return ResponseEntity.ok(courses);
    }

    @PostMapping("/{courseCode}/enroll")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> enrollMultipleStudents(
            @PathVariable String courseCode,
            @RequestBody Set<String> studentIds) {

        courseService.enrollStudentsToCourse(studentIds, courseCode);
        return ResponseEntity.ok("All students enrolled successfully.");
    }
    @DeleteMapping("/{courseCode}/drop/{studentId}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<String> dropCourse(@PathVariable String courseCode, @PathVariable String studentId) {
        courseService.dropCourse(courseCode, studentId);
        return ResponseEntity.ok("Course dropped successfully.");
    }



}
