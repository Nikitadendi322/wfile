package com.example.workfille.Service;

import com.example.workfille.Employee.Employee;
import com.example.workfille.dto.EmployeeDto;
import com.example.workfille.dto.PositionDto;
import org.aspectj.weaver.Position;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class EmployeeMapper {


    public Employee toEntity(EmployeeDto employeeDto){
        Employee employee=new Employee();
        employee.setName(employee.getName());
        employee.setSalary(employee.getSalary());
        var positionDto=employeeDto.getPosition();
        return employee;
    }
    public EmployeeDto toDto(Employee employee){
        EmployeeDto employeeDto=new EmployeeDto();
        employeeDto.setId(employeeDto.getId());
        employeeDto.setName(employeeDto.getName());
        employeeDto.setSalary(employeeDto.getSalary());
        var position=employee.getPosition();
        var positionDto=new PositionDto(position.getId(), position.getPosition());
        employeeDto.setPosition(positionDto);
        return employeeDto;
    }
}
