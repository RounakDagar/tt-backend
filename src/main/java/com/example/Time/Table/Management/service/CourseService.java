package com.example.Time.Table.Management.service;

import com.example.Time.Table.Management.Model.Course;
import com.example.Time.Table.Management.Model.Faculty;
import com.example.Time.Table.Management.Model.Student;
import com.example.Time.Table.Management.Repo.CourseRepository;
import com.example.Time.Table.Management.Repo.FacultyRepository;
import com.example.Time.Table.Management.Repo.StudentRepository;
import com.example.Time.Table.Management.Repo.TimetableRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CourseService {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private StudentRepository studentRepository;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    private TimetableRepository timetableRepo;

    public Course addCourse(String courseCode, String courseName, int semester, Set<String> facultyIds) {
        Course course = new Course();
        course.setCourseCode(courseCode);
        course.setCourseName(courseName);
        course.setSemester(semester);

        Set<Faculty> faculties = new HashSet<>();
        for (String facultyId : facultyIds) {
            Faculty faculty = facultyRepository.findByFacultyId(facultyId)
                    .orElseThrow(() -> new RuntimeException("Faculty not found: " + facultyId));
            faculties.add(faculty);
        }

        course.setFaculties(faculties);

        // VERY IMPORTANT: maintain the bidirectional relationship
        for (Faculty faculty : faculties) {
            faculty.getCourses().add(course);
        }

        return courseRepository.save(course);
    }

    @Transactional
    public void enrollStudentsToCourse(Set<String> studentIds, String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode).orElseThrow();


        List<Student> students = studentRepository.findByStudentIdIn(studentIds);

        if (students.size() != studentIds.size()) {
            throw new RuntimeException("Some students not found!");
        }

        for (Student student : students) {
            student.getEnrolledCourses().add(course);
            course.getEnrolledStudents().add(student); // maintain bidirectional
        }

        studentRepository.saveAll(students);
        // courseRepository.save(course); // optional if cascading is used
    }



    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public Optional<Course> getCourseByCode(String courseCode) {
        return courseRepository.findById(courseCode);
    }

    public Course updateCourse(Course course) {
        return courseRepository.save(course);
    }
    @Transactional
    public void deleteCourse(String courseCode) {
        entityManager.createNativeQuery("DELETE FROM student_courses WHERE course_code = :courseCode")
                .setParameter("courseCode", courseCode)
                .executeUpdate();
        timetableRepo.deleteByCourseCourseCode(courseCode);
        courseRepository.deleteById(courseCode);
    }

    public List<Course> getCoursesOfFaculty(String facultyId) {
        return courseRepository.findCoursesByFacultyId(facultyId);
    }

    public List<Course> getCoursesByStudentId(String studentId) {
        return courseRepository.findCoursesByStudentId(studentId);
    }

    public void dropCourse(String courseCode, String studentId) {
        Student student = studentRepository.findByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        if (!student.getEnrolledCourses().contains(course)) {
            throw new RuntimeException("Student is not enrolled in this course.");
        }

        student.getEnrolledCourses().remove(course);
        course.getEnrolledStudents().remove(student); // bidirectional

        studentRepository.save(student);
        courseRepository.save(course);
    }
}
