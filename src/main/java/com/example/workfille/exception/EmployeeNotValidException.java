package com.example.workfille.exception;

import com.example.workfille.dto.EmployeeDto;

public class EmployeeNotValidException extends RuntimeException {

    private final EmployeeDto employee;

    public EmployeeNotValidException(EmployeeDto employee) {
        this.employee = employee;
    }

    public EmployeeDto getEmployee() {
        return employee;
    }
}
