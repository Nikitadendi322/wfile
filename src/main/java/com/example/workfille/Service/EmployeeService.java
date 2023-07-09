package com.example.workfille.Service;

import aj.org.objectweb.asm.TypeReference;
import com.example.workfille.Employee.Employee;
import com.example.workfille.Employee.Report;
import com.example.workfille.dto.EmployeeDto;
import com.example.workfille.exception.EmployeeNotFoundException;
import com.example.workfille.exception.EmployeeNotValidException;
import com.example.workfille.repository.EmployeeRepository;
import com.example.workfille.repository.ReportRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EmployeeService {
    Logger logger= LoggerFactory.getLogger(EmployeeService.class);


    private final EmployeeRepository employeeRepository;

    private final EmployeeMapper employeeMapper;
    private final ObjectMapper objectMapper;
    private final ReportRepository reportRepository;


    public EmployeeService(EmployeeRepository employeeRepository, EmployeeMapper employeeMapper, ObjectMapper objectMapper, ReportRepository reportRepository) {

        this.employeeRepository = employeeRepository;
        this.employeeMapper = employeeMapper;
        this.objectMapper = objectMapper;
        this.reportRepository = reportRepository;
    }

    @PostConstruct
    public void init() {
        employeeRepository.deleteAll();
        employeeRepository.saveAll(
                List.of(
                        new Employee("Иван", 10_000),
                        new Employee("Катя", 30_000),
                        new Employee("Петя", 25_000)
                )
        );
    }

    public int getSumOfSalaries() {
        return employeeRepository.getSumOfSalaries();
    }

    public EmployeeDto getEmployeeWithMinSalary() {
        logger.info("Вызван метод поиска сотрудника по минимальной заработной плате");
        Page<EmployeeDto> page = employeeRepository.getEmployeeWithMinSalary(PageRequest.of(0, 1));
        if (page.isEmpty()) {
            return null;
        }
        return page.getContent().get(0);

    }

    public EmployeeDto getEmployeeWithMaxSalary() {
        logger.info("Вызван метод поиска сотрудника по максимальной заработной плате");
        List<EmployeeDto> employeeWithMaxSalary = getEmployeeWithHighestSalary();
        if (employeeWithMaxSalary.isEmpty()) {
            return null;
        }
        return employeeWithMaxSalary.get(0);
    }

    public List<EmployeeDto> getEmployeeWithSalaryHigherThanAverage() {
        double average = employeeRepository.getAverageOfSalaries();
        return getEmployeeWithSalaryHigherThan(average);
    }

    public List<EmployeeDto> createBatch(List<EmployeeDto> employees) {
        Optional<EmployeeDto> incorrectEmployee = employees.stream()
                .filter(employee -> employee.getSalary() <= 0 || employee.getName() == null
                        || employee.getName().isEmpty())
                .findFirst();
        if (incorrectEmployee.isPresent()) {
            throw new EmployeeNotValidException(incorrectEmployee.get());
        }
        return employeeRepository.saveAll(employees.stream()
                        .map(employeeMapper::toEntity)
                        .collect(Collectors.toList()))
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());


    }

    public void update(int id, EmployeeDto employee) {
        logger.info("Вызван метод изменения сотрудника");
        Employee oldEmployee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        oldEmployee.setSalary(employee.getSalary());
        oldEmployee.setName(employee.getName());
        employeeRepository.save(oldEmployee);
        logger.debug("Сотрудник изменен и тперь он {}",employee);
    }

    public EmployeeDto get(int id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .map(employeeDto -> {
                    employeeDto.setPosition(null);
                    return employeeDto;
                })
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public void delete(int id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employeeRepository.delete(employee);

    }

    public List<EmployeeDto> getEmployeeWithSalaryHigherThan(double salary) {
        logger.info("Вызван метод поиска сотрудника по средней заработной плате");
        return employeeRepository.findEmployeeBySalaryIsGreaterThan(salary).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<EmployeeDto> getEmployeeWithHighestSalary() {
        return employeeRepository.getEmployeeWithMaxSalary();
    }

    public List<EmployeeDto> getEmployee(@Nullable String position) {
        return Optional.ofNullable(position)
                .map(employeeRepository::findEmployeeByPosition_Position)
                .orElseGet(employeeRepository::findAll)
                .stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public EmployeeDto getFullInfo(int id) {
        return employeeRepository.findById(id)
                .map(employeeMapper::toDto)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    public List<EmployeeDto> getEmployeeFromPage(int page) {
        return employeeRepository.findAll(PageRequest.of(page, 10)).stream()
                .map(employeeMapper::toDto)
                .collect(Collectors.toList());
    }

    public int generateReport() {
        var report = employeeRepository.buildReport();
        try {


            var content = objectMapper.writeValueAsString(report);
            var path=generateReportFile(content);

            var reportEntity = new Report();
            reportEntity.setReport(content);
            reportEntity.setPath(path);
            return reportRepository.save(reportEntity).getId();
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot generated report", e);
        }
    }

    public String generateReportFile(String content){
        var f =new File("report_"+System.currentTimeMillis()+".json");
        try(var writer=new FileWriter(f)){
            writer.write(content);
        }catch (IOException e){
            throw new RuntimeException(e);
        }
        return f.getName();
    }

    public Resource findReport(int id) {
        return (Resource) new ByteArrayResource(
                reportRepository.findById(id)
                        .orElseThrow(() -> new IllegalStateException("Report with id" + id + " not Found"))
                        .getReport()
                        .getBytes(StandardCharsets.UTF_8));

    }

    public void upload(MultipartFile file) {
        try {
                List<EmployeeDto> dtos = objectMapper.readValue(file.getBytes(), new TypeReference<>() {
                });
                dtos.stream()
                    .map(employeeMapper::toEntity)
                    .forEach(e -> employeeRepository::save);

        }catch (IOException e){
            throw new RuntimeException(e);
        }
    }

    public File findReportFile(int id) {
        reportRepository.findById(id)
                .map(Report::getPath)
                .map(File::new)
                .orElse(null);
        return null;
    }

    public Logger getLogger() {
        return logger;
    }

    public void setLogger(Logger logger) {
        this.logger = logger;
    }
}
