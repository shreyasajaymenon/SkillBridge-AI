package com.skillbridge.service;

import com.skillbridge.model.Internship;
import com.skillbridge.model.Student;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SampleDataService {
    private final List<Student> students = List.of(
        new Student("ST-001","Aarav Shah",8.7,List.of("Java","Spring Boot","SQL","REST APIs"),List.of("INT-101","INT-104")),
        new Student("ST-002","Diya Patel",9.1,List.of("Python","Machine Learning","Pandas","SQL"),List.of("INT-102","INT-105")),
        new Student("ST-003","Kabir Mehta",7.8,List.of("JavaScript","React","CSS","REST APIs"),List.of("INT-103")),
        new Student("ST-004","Ananya Rao",8.9,List.of("Python","Machine Learning","TensorFlow","Statistics"),List.of("INT-102","INT-105")),
        new Student("ST-005","Ishaan Nair",7.2,List.of("Java","SQL","Git"),List.of("INT-101")),
        new Student("ST-006","Sara Khan",9.4,List.of("Python","Data Analysis","Pandas","SQL"),List.of("INT-105","INT-102")),
        new Student("ST-007","Vivaan Joshi",8.1,List.of("JavaScript","React","Node.js","CSS"),List.of("INT-103","INT-104")),
        new Student("ST-008","Myra Desai",8.5,List.of("Java","Spring Boot","Docker","REST APIs"),List.of("INT-101","INT-104")),
        new Student("ST-009","Arjun Iyer",6.8,List.of("Python","SQL","Excel"),List.of("INT-105")),
        new Student("ST-010","Aisha Fernandes",9.0,List.of("Cybersecurity","Python","Linux","Networking"),List.of("INT-104")),
        new Student("ST-011","Reyansh Kulkarni",7.9,List.of("Java","SQL","Spring Boot"),List.of("INT-101")),
        new Student("ST-012","Kiara Shah",8.3,List.of("Python","Machine Learning","Statistics","Scikit-learn"),List.of("INT-102")),
        new Student("ST-013","Advik Menon",8.8,List.of("React","JavaScript","TypeScript","CSS"),List.of("INT-103")),
        new Student("ST-014","Zara Merchant",7.5,List.of("Linux","Networking","Cybersecurity","Python"),List.of("INT-104")),
        new Student("ST-015","Rudra Sinha",8.0,List.of("SQL","Python","Data Analysis","Pandas"),List.of("INT-105")),
        new Student("ST-016","Ira Kapoor",9.2,List.of("Java","Spring Boot","Microservices","Docker"),List.of("INT-101","INT-104")),
        new Student("ST-017","Atharv Shah",7.7,List.of("HTML","CSS","JavaScript","React"),List.of("INT-103")),
        new Student("ST-018","Meera Pillai",8.6,List.of("Python","Machine Learning","NLP","Pandas"),List.of("INT-102")),
        new Student("ST-019","Dev Malhotra",6.9,List.of("Java","SQL","Git"),List.of("INT-101")),
        new Student("ST-020","Saanvi Ghosh",8.4,List.of("Cybersecurity","Linux","Networking","Bash"),List.of("INT-104"))
    );

    private final List<Internship> internships = List.of(
        new Internship("INT-101","Northstar Tech","Java Backend Intern",List.of("Java","Spring Boot","SQL","REST APIs"),7.0,3),
        new Internship("INT-102","Neuron Labs","Machine Learning Intern",List.of("Python","Machine Learning","Pandas","Statistics"),7.5,2),
        new Internship("INT-103","PixelForge","Frontend Developer Intern",List.of("JavaScript","React","CSS"),7.0,2),
        new Internship("INT-104","SecureStack","Cybersecurity Intern",List.of("Cybersecurity","Linux","Networking","Python"),7.0,2),
        new Internship("INT-105","DataSpring","Data Analyst Intern",List.of("Python","SQL","Data Analysis","Pandas"),6.5,2)
    );

    public List<Student> students() { return students; }
    public List<Internship> internships() { return internships; }
}
