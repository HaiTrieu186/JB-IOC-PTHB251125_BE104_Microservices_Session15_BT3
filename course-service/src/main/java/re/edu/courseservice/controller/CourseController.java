package re.edu.courseservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    // Danh sách tĩnh thay cho Database theo yêu cầu đề bài
    private final List<String> courses = new ArrayList<>(List.of("Java Spring Boot", "Microservices Architecture"));

    // 1. Chỉ STUDENT và INSTRUCTOR mới được XEM
    @GetMapping
    @PreAuthorize("hasAuthority('STUDENT') or hasAuthority('INSTRUCTOR')")
    public ResponseEntity<List<String>> getAllCourses() {
        return ResponseEntity.ok(courses);
    }

    // 2. Chỉ INSTRUCTOR mới được TẠO
    @PostMapping
    @PreAuthorize("hasAuthority('INSTRUCTOR')")
    public ResponseEntity<String> createCourse(@RequestBody String courseName) {
        courses.add(courseName);
        return ResponseEntity.ok("Tạo khóa học thành công: " + courseName);
    }
}