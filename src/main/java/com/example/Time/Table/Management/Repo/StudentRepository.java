package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentId(String studentId);

    List<Student> findByStudentIdIn(Set<String> studentIds);

    Optional<Student> findById(Long id);
    List<Student> findByEnrolledCourses_CourseCode(String courseCode);

}
