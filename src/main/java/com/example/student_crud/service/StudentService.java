package com.example.student_crud.service;




import com.example.student_crud.dto.StudentDTO;
import com.example.student_crud.entity.Student;
import com.example.student_crud.exception.StudentNotFoundException;
import com.example.student_crud.exception.EmailAlreadyExistsException;
import com.example.student_crud.repository.StudentRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private EmailService emailService;

    public StudentDTO createStudent(StudentDTO studentDTO) {
        if (studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + studentDTO.getEmail());
        }

        Student student = new Student();
        BeanUtils.copyProperties(studentDTO, student);
        Student savedStudent = studentRepository.save(student);

        // Send registration email
        emailService.sendRegistrationEmail(savedStudent.getEmail(), savedStudent.getName());

        StudentDTO responseDTO = new StudentDTO();
        BeanUtils.copyProperties(savedStudent, responseDTO);
        return responseDTO;
    }

    public StudentDTO getStudent(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        StudentDTO studentDTO = new StudentDTO();
        BeanUtils.copyProperties(student, studentDTO);
        return studentDTO;
    }

    public List<StudentDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(student -> {
                    StudentDTO dto = new StudentDTO();
                    BeanUtils.copyProperties(student, dto);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public StudentDTO updateStudent(Long id, StudentDTO studentDTO) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new StudentNotFoundException("Student not found with id: " + id));

        // Check if email is being changed and if new email already exists
        if (!student.getEmail().equals(studentDTO.getEmail()) &&
                studentRepository.existsByEmail(studentDTO.getEmail())) {
            throw new EmailAlreadyExistsException("Email already registered: " + studentDTO.getEmail());
        }

        BeanUtils.copyProperties(studentDTO, student);
        student.setId(id);
        Student updatedStudent = studentRepository.save(student);

        StudentDTO responseDTO = new StudentDTO();
        BeanUtils.copyProperties(updatedStudent, responseDTO);
        return responseDTO;
    }

    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new StudentNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }
}
