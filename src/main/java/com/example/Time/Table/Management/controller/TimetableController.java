package com.example.Time.Table.Management.controller;

import com.example.Time.Table.Management.Model.*;
import com.example.Time.Table.Management.Repo.*;
import com.example.Time.Table.Management.Service.StudentAttendanceService;
import com.example.Time.Table.Management.dto.AttendanceSummaryDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController
@RequestMapping("/timetables")
public class TimetableController {

    @Autowired
    private TimetableRepository timetableRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private StudentAttendanceService studentAttendanceService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private FacultyRepository facultyRepository;

    @Autowired
    private VenueRepository venueRepository;


    @PostMapping("/add")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> addTimetable(@RequestBody Timetable timetable, Principal principal) {
        Faculty facultyPrincipal = (Faculty) ((Authentication) principal).getPrincipal();
        String email = facultyPrincipal.getEmail();
        System.out.println("Principal email: " + email);

        Faculty faculty = facultyRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Faculty not found with email: " + email));

        timetable.setFaculty(faculty);

        Course course = timetable.getCourse();
        if (course == null || course.getCourseCode() == null) {
            return ResponseEntity.badRequest().body("Course is required for timetable creation");
        }

        Course actualCourse = courseRepository.findByCourseCode(course.getCourseCode()).orElseThrow(()-> new RuntimeException("Course Not Found"));

        // Check for venue or faculty conflict
        boolean venueConflict = timetableRepository.existsByVenueAndDayAndTimeSlot(
                timetable.getVenue(), timetable.getDay(), timetable.getTimeSlot());

        boolean facultyConflict = timetableRepository.existsByFacultyAndDayAndTimeSlot(
                faculty, timetable.getDay(), timetable.getTimeSlot());

        if (venueConflict || facultyConflict) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: Faculty or Venue already occupied at this time");
        }

        timetable.setCourse(actualCourse);

        // Auto-add enrolled students without duplication
        List<Student> enrolledStudents = studentRepository.findByEnrolledCourses_CourseCode(course.getCourseCode());
        Set<Student> uniqueStudents = new HashSet<>(enrolledStudents);
        timetable.setStudents(new ArrayList<>(uniqueStudents));

        Timetable saved = timetableRepository.save(timetable);
        return ResponseEntity.ok(saved);
    }


    // Students fetch their timetable by day
    @GetMapping("/day/{day}")
    @PreAuthorize("hasRole('STUDENT')")
    public List<Timetable> getStudentTimetable(@PathVariable String day, Principal principal) {
        Student studentPrincipal = (Student) ((Authentication) principal).getPrincipal();
        Long id = studentPrincipal.getId();
        System.out.println("Principle : ehehaehaubeubfuifbrifrihf  : " + id);
        Student student = studentRepository.findById(id).orElseThrow();
        System.out.println("Principle : ehehaehaubeubfuifbrifrihf  : " + student.getEnrolledCourseCodes());
        List<Timetable> timetableList =timetableRepository.findByDayAndCourse_CourseCodeIn(day, student.getEnrolledCourseCodes());
        for(Timetable timetable : timetableList){
            timetable.setAttended(studentAttendanceService.getAttendanceForTimetable(timetable.getId(),studentPrincipal.getEmail()));
        }
        return timetableList;
    }

    // Faculty fetches their own timetable
    @GetMapping("/faculty/day/{day}")
    @PreAuthorize("hasRole('FACULTY')")
    public List<Timetable> getFacultyTimetable(@PathVariable String day, Principal principal) {
        Faculty facultyPrincipal = (Faculty) ((Authentication) principal).getPrincipal();
        String email = facultyPrincipal.getEmail();
        System.out.println("Principal email: " + email);
        return timetableRepository.findByDayAndFaculty_Email(day, email);
    }

    // Mark attendance
    @PutMapping("/mark/{studentId}/{timetableId}")
    @PreAuthorize("hasRole('FACULTY')")
    public StudentAttendance markAttendance(@PathVariable String studentId,
                                            @PathVariable Long timetableId,
                                            @RequestParam boolean attended) {
        return studentAttendanceService.markAttendance(studentId, timetableId, attended);
    }

    @PutMapping("/mark-batch")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> markAttendanceForClass(@RequestBody AttendanceRequest request) {
        Long timetableId = request.getTimetableId();

        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new RuntimeException("Invalid timetable ID"));

        String courseCode = timetable.getCourse().getCourseCode();

        List<StudentAttendance> attendanceList = new ArrayList<>();

        for (AttendanceRequest.AttendanceEntry entry : request.getAttendances()) {
            String studentId = entry.getStudentId();

            Optional<StudentAttendance> existingAttendanceOpt =
                    studentAttendanceService.findByStudentIdAndTimetableId(studentId, timetableId);

            if (existingAttendanceOpt.isPresent()) {
                // ✅ Update existing attendance
                StudentAttendance existingAttendance = existingAttendanceOpt.get();
                existingAttendance.setAttended(entry.isAttended());
                attendanceList.add(existingAttendance);
            } else {
                // ✅ Create new attendance only if no existing record
                StudentAttendance newAttendance = new StudentAttendance(
                        studentId,
                        courseCode,
                        timetableId,
                        entry.isAttended()
                );
                attendanceList.add(newAttendance);
            }
        }

        studentAttendanceService.saveAll(attendanceList);  // Save updated and new entries

        return ResponseEntity.ok("Attendance marked successfully for class");
    }



    @GetMapping("/attendance-summary/{studentId}/{courseCode}")
    @PreAuthorize("hasAnyRole('FACULTY','STUDENT')")
    public ResponseEntity<AttendanceSummaryDTO> getAttendanceSummary(
            @PathVariable String studentId,
            @PathVariable String courseCode) {

        long totalClasses = studentAttendanceService.countByStudentIdAndCourseCode(studentId, courseCode);
        long attendedClasses = studentAttendanceService.countByStudentIdAndCourseCodeAndAttendedTrue(studentId, courseCode);

        AttendanceSummaryDTO summary = new AttendanceSummaryDTO(
                studentId, courseCode, totalClasses, attendedClasses
        );

        return ResponseEntity.ok(summary);
    }

    @GetMapping("/defaulters/{courseCode}")
    @PreAuthorize("hasRole('FACULTY')")
    public ResponseEntity<?> getDefaulters(@PathVariable String courseCode) {
        Set<String> defaulterIds = studentAttendanceService.findDefaulterStudentIds(courseCode);

        if (defaulterIds.isEmpty()) {
            return ResponseEntity.ok("All students have >= 75% attendance.");
        }

        List<Student> defaulters = studentRepository.findByStudentIdIn(defaulterIds);

        List<String> defaulterNames = defaulters.stream()
                .map(Student::getEmail)
                .toList();

        return ResponseEntity.ok(defaulterNames);
    }


    @GetMapping("/available-venues")
    public ResponseEntity<List<String>> getAvailableVenues(
            @RequestParam String day,
            @RequestParam String timeSlot) {

        List<Venue> allVenue = venueRepository.findAll();
        List<String> allVenues = new ArrayList<>();
        for(Venue venue : allVenue){
            allVenues.add(venue.getVenue());
        }

        List<String> bookedVenues = timetableRepository.findBookedVenues(day, timeSlot);

        List<String> availableVenues= allVenues.stream()
                .filter(venue -> !bookedVenues.contains(venue))
                .toList();
        return ResponseEntity.ok(availableVenues);
    }


    @GetMapping("/course/{courseCode}")
    public ResponseEntity<List<Timetable>> getCourseTimetable(@PathVariable String courseCode){
        return ResponseEntity.ok(timetableRepository.findByCourseCourseCode(courseCode));
    }

    @DeleteMapping("/{timetableId}")
    public void deleteTimetable(@PathVariable Long timetableId){
        timetableRepository.deleteById(timetableId);
    }



}
