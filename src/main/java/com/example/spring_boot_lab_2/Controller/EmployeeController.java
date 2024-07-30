package com.example.spring_boot_lab_2.Controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring_boot_lab_2.API.ApiRespnse;
import com.example.spring_boot_lab_2.Model.Employee;

import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/employees")

public class EmployeeController {
    List<Employee> employees = new ArrayList<>();

    @GetMapping("")
    public ResponseEntity getAllEmployees(){
        if(employees.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse("Employees list is empty!"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(employees);
    }

    @PostMapping("/add") 
    public ResponseEntity addEmployee(@Valid @RequestBody Employee employee, Errors err){
        if(err.hasErrors()){
            String message = err.getFieldError().getDefaultMessage(); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse(message)); 
        }

        for(Employee employeeId : employees){
            if(employeeId.getId().equalsIgnoreCase(employee.getId())){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This id ("+ employee.getId() +") already used"); 
            }
        }

        employees.add(employee);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiRespnse(employee.getName() + " Added to employees list")); 
    }

    @PutMapping("/{id}/update")
    public ResponseEntity updateEmployee(@PathVariable String id, @Valid  @RequestBody Employee updateEmployee, Errors err){
        if(err.hasErrors()){
            String message = err.getFieldError().getDefaultMessage(); 
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse(message)); 
        }
        for(Employee employee : employees){
            if(employee.getId().equalsIgnoreCase(id)){
                employee.setId(id);
                employee.setName(updateEmployee.getName());
                employee.setAge(updateEmployee.getAge());
                employee.setEmail(updateEmployee.getEmail());
                employee.setPhoneNumber(updateEmployee.getPhoneNumber());
                employee.setAnnualLeave(updateEmployee.getAnnualLeave());
                employee.setPosition(updateEmployee.getPosition());
                employee.setHireDate(updateEmployee.getHireDate());
                return ResponseEntity.status(HttpStatus.OK).body(new ApiRespnse(updateEmployee.getName() + " Updated"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse(" Employee whit this ID (" + id + ") not found"));
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity deleteEmployee(@PathVariable String id){
        for(Employee employee : employees){
            if(employee.getId().equalsIgnoreCase(id)){
                employees.remove(employee); 
                return ResponseEntity.status(HttpStatus.OK).body(new ApiRespnse(employee.getName() + " Deleted"));
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse(" Employee whit this ID (" + id + ") not found"));
    }


    @GetMapping("/search")
    public ResponseEntity searchEmployees(@RequestBody String position){
        ArrayList supervisors = new ArrayList<>();
        ArrayList coordinators = new ArrayList<>();

        if(position.equalsIgnoreCase("supervisor") || position.equalsIgnoreCase("coordinator")){
            for(Employee employee : employees){
                if(employee.getPosition().equalsIgnoreCase("supervisor")){
                    supervisors.add(employee);
                }else if(employee.getPosition().equalsIgnoreCase("coordinator")){
                    coordinators.add(employee);
                }
            }
            
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse("Position must be either 'supervisor', or 'coordinator'")); 
        }

        return ResponseEntity.status(HttpStatus.OK).body(position.equalsIgnoreCase("supervisor") ? supervisors : coordinators); 
    }
    


    @GetMapping("/range/{minAge}/{maxAge}")
    public ResponseEntity getAges(@PathVariable int minAge, @PathVariable int maxAge){
        List rangeAges = new ArrayList<>(); 

        if(minAge <= 0 && maxAge <= 0){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse("Please enter a vaild age")); 
        }


        for(Employee employee : employees){
            if(employee.getAge() >= minAge && employee.getAge() <= maxAge){
                rangeAges.add(employee); 
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(rangeAges); 
    }


    @PutMapping("/{id}/leave")
    public ResponseEntity annualLeave(@PathVariable String id){

        for(Employee employee : employees){

            if(!employee.getId().equals(id)){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse("This employee whit this ID (" + id + ")"));
            }

            if(!employee.isOnLeave()){
                if(employee.getAnnualLeave() > 0){
                    employee.setOnLeave(true);
                    employee.setAnnualLeave(employee.getAnnualLeave() - 1);
                    return ResponseEntity.status(HttpStatus.OK).body(new ApiRespnse(employee.getName() + " leave succcessfully " + employee.getAnnualLeave())); 
                }
            }else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiRespnse("You are already on leave"));
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body("Success");
    }

    @GetMapping("/get/noAnnual")
    public ResponseEntity getNoAnnualLeave(){
        List noAnnualLeave = new ArrayList<>(); 

        for(Employee employee : employees){
            if(employee.getAnnualLeave() == 0){
                noAnnualLeave.add(employee); 
            }
        }
        return ResponseEntity.status(HttpStatus.OK).body(noAnnualLeave); 
    }


    @PutMapping("/promote/{superId}/{empId}")
    public ResponseEntity promoteEmployee(@PathVariable String superId, @PathVariable String empId){

        if(employees.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Employees list empty"); 
        }
        
        for(Employee s : employees){
            if(s.getId().equals(superId) && s.getPosition().equals("supervisor")){
                for(Employee e : employees){
                    if(e.getId().equals(empId) && e.getAge() >= 30 && e.isOnLeave() == false && e.getPosition().equals("coordinator")){
                        e.setPosition("supervisor");
                        return ResponseEntity.status(HttpStatus.OK).body("Promote this employee (" + e.getName()+")to supervisor"); 
                    }
                }
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Your age less than 30 or you are on leave now ");
            }
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Access denied"); 
    }


}