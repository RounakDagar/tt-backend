package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.Faculty;
import com.example.Time.Table.Management.Model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FacultyRepository extends JpaRepository<Faculty, String> {
    Optional<Faculty> findByFacultyId(String facultyId);

    Optional<Faculty> findByEmail(String email);

}