package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {  // Changed Long to String as courseCode is a String

    Optional<Course> findByCourseCode(String courseCode);

    // Fixed JPQL query to reference the facultyId from the Faculty object
    @Query("SELECT c FROM Course c JOIN c.faculties f WHERE f.facultyId = :facultyId")
    List<Course> findCoursesByFacultyId(@Param("facultyId") String facultyId);


    @Query("SELECT c FROM Course c JOIN c.enrolledStudents s WHERE s.studentId = :studentId")
    List<Course> findCoursesByStudentId(@Param("studentId") String studentId);



}