package com.example.Time.Table.Management.Service;

import com.example.Time.Table.Management.Model.Student;
import com.example.Time.Table.Management.Model.StudentAttendance;
import com.example.Time.Table.Management.Model.Timetable;
import com.example.Time.Table.Management.Repo.StudentAttendanceRepository;
import com.example.Time.Table.Management.Repo.TimetableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class StudentAttendanceService {

    @Autowired
    private StudentAttendanceRepository studentAttendanceRepository;

    @Autowired
    private TimetableRepository timetableRepository;

    public StudentAttendance markAttendance(String studentId, Long timetableId, boolean attended) {
        // Retrieve the timetable to obtain the course code.
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("Timetable not found"));
        String courseCode = timetable.getCourse().getCourseCode();

        // Fetch existing attendance record or create a new one.
        StudentAttendance attendance = studentAttendanceRepository
                .findByStudentIdAndTimetableId(studentId, timetableId)
                .orElse(new StudentAttendance(studentId, courseCode, timetableId, attended));
        attendance.setAttended(attended);
        return studentAttendanceRepository.save(attendance);
    }

    public List<StudentAttendance> getAttendance(String studentId, String courseCode) {
        return studentAttendanceRepository.findByStudentIdAndCourseCode(studentId, courseCode);
    }

    public void saveAll(List<StudentAttendance> attendanceList) {
        studentAttendanceRepository.saveAll(attendanceList);
    }

    public long countByStudentIdAndCourseCode(String studentId, String courseCode) {
        return studentAttendanceRepository.countByStudentIdAndCourseCode(studentId, courseCode);
    }

    public long countByStudentIdAndCourseCodeAndAttendedTrue(String studentId, String courseCode) {
        return studentAttendanceRepository.countByStudentIdAndCourseCodeAndAttendedTrue(studentId, courseCode);
    }

    public Set<String> findDefaulterStudentIds(String courseCode) {
        return studentAttendanceRepository.findDefaulterStudentIds(courseCode);
    }

    public Boolean getAttendanceForTimetable(Long id, String id1) {
        Optional<StudentAttendance> studentAttendance = studentAttendanceRepository.findByStudentIdAndTimetableId(id1,id);
        return studentAttendance.map(StudentAttendance::isAttended).orElse(false);
    }

    public Optional<StudentAttendance> findByStudentIdAndTimetableId(String studentId, Long timetableId) {
        return studentAttendanceRepository.findByStudentIdAndTimetableId(studentId,timetableId);
    }
}
