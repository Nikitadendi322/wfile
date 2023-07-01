package com.example.workfille.Employee;

import jakarta.persistence.*;

@Entity

public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private int salary;

    @ManyToOne
    @JoinColumn(name="Position id")
    private com.example.workfille.Employee.position position;


    public Employee(String name, int salary) {
        this.name = name;
        this.salary = salary;
    }

    public Employee() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSalary() {
        return salary;
    }

    public void setSalary(int salary) {
        this.salary = salary;
    }

    public com.example.workfille.Employee.position getPosition() {
        return position;
    }

    public void setPosition(com.example.workfille.Employee.position position) {
        this.position = position;
    }
}

