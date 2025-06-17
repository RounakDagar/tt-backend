package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.StudentAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentAttendanceRepository extends JpaRepository<StudentAttendance, Long> {
    List<StudentAttendance> findByStudentIdAndCourseCode(String studentId, String courseCode);
    Optional<StudentAttendance> findByStudentIdAndTimetableId(String studentId, Long timetableId);

    long countByStudentIdAndCourseCode(String studentId, String courseCode);

    long countByStudentIdAndCourseCodeAndAttendedTrue(String studentId, String courseCode);

    @Query("SELECT sa.studentId " +
            "FROM StudentAttendance sa " +
            "WHERE sa.courseCode = :courseCode " +
            "GROUP BY sa.studentId " +
            "HAVING SUM(CASE WHEN sa.attended = true THEN 1 ELSE 0 END) * 1.0 / COUNT(sa) < 0.75")
    Set<String> findDefaulterStudentIds(@Param("courseCode") String courseCode);


}
