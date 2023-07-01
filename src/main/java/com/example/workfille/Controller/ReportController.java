package com.example.workfille.Controller;

import com.example.workfille.Service.EmployeeService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@RestController
@RequestMapping("/report")
public class ReportController {

    private final EmployeeService employeeService;

    public ReportController(EmployeeService employeeService){
        this.employeeService=employeeService;
    }

    @PostMapping("/")
    public int report(){
        return employeeService.generateReport();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Resource> find(@PathVariable int id){
        Resource resource= (Resource) employeeService.findReport(id);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"report.json\"")
                .body(resource);

    }
    @GetMapping("file/{id}")
    public ResponseEntity<Resource> findFile(@PathVariable int id){
        var file=employeeService.findReportFile(id);
        if (file==null){
            return ResponseEntity.noContent().build();
        }
        try {
            InputStreamResource resource=new InputStreamResource(new FileInputStream(file));
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=report.json")
                    .contentLength(file.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        }catch (FileNotFoundException e){
            return ResponseEntity.internalServerError().build();
        }

    }
}
