package com.example.workfille.Controller;

import com.example.workfille.Service.EmployeeService;
import com.example.workfille.dto.EmployeeDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping("/salary/sum")
    public int getSumOfSalaries() {
        return employeeService.getSumOfSalaries();
    }

    @GetMapping("/salary/min")
    public EmployeeDto getEmployeeWithMinSalary() {
        return employeeService.getEmployeeWithMinSalary();
    }

    @GetMapping("/salary/max")
    public EmployeeDto getEmployeeWithMaxSalary() {
        return employeeService.getEmployeeWithMaxSalary();
    }

    @GetMapping("/high-salary")
    public List<EmployeeDto> getEmployeeWithSalaryHigherThanAverage() {
        return employeeService.getEmployeeWithSalaryHigherThanAverage();
    }

    @PostMapping
    public List<EmployeeDto> createBatch(@RequestBody List<EmployeeDto> employees) {
        return employeeService.createBatch(employees);
    }

    @PutMapping("/{id}")
    public void update(@PathVariable int id, @RequestBody EmployeeDto employee) {
        employeeService.update(id, employee);
    }

    @GetMapping("/{id}")
    public EmployeeDto get(@PathVariable int id) {
        return employeeService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        employeeService.delete(id);
    }

    @GetMapping("/salaryHigherThan")
    public List<EmployeeDto> getEmployeeWithSalaryHigherThan(@RequestParam int salary) {
        return employeeService.getEmployeeWithSalaryHigherThan(salary);
    }
    @GetMapping("/withHighestSalary")
    public List<EmployeeDto> getEmployeeWithHighestSalary() {
        return employeeService.getEmployeeWithHighestSalary();
    }
    @GetMapping("/position")
    public List<EmployeeDto> getEmployee(@RequestParam(required = false)String position) {
        return employeeService.getEmployee(
                Optional.ofNullable(position)
                        .filter(pos->!pos.isEmpty())
                        .orElse(null)
        );
    }
    @GetMapping("/{id}/fullInfo")
    public EmployeeDto getFullInfo(@PathVariable int id) {
        return employeeService.getFullInfo(id);
    }
    @GetMapping("/page?page=")
    public List<EmployeeDto> getEmployeeFromPage(@RequestParam(required = false, defaultValue = "0")int page) {
        return employeeService.getEmployeeFromPage(page);
    }
    @PostMapping(value = "/upload",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void upload(@RequestPart("employees") MultipartFile file){
        employeeService.upload(file);
    }



}
