package com.example.Time.Table.Management.Repo;

import com.example.Time.Table.Management.Model.Faculty;
import com.example.Time.Table.Management.Model.Timetable;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface TimetableRepository extends JpaRepository<Timetable, Long> {
    List<Timetable> findByDay(String day);
    List<Timetable> findByDayAndCourse_CourseCodeIn(String day, List<String> courseCodes);
    List<Timetable> findByDayAndFaculty_Email(String day, String email);

    boolean existsByVenueAndDayAndTimeSlot(String venue, String day, String timeSlot);
    boolean existsByFacultyAndDayAndTimeSlot(Faculty faculty, String day, String timeSlot);

    @Query("SELECT t.venue FROM Timetable t WHERE t.day = :day AND t.timeSlot = :timeSlot")
    List<String> findBookedVenues(@Param("day") String day, @Param("timeSlot") String timeSlot);

    List<Timetable> findByCourseCourseCode(String courseCode);

    void deleteByCourseCourseCode(String courseCode);


}
