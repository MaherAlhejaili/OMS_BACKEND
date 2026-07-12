package com.avnzor.oms_backend.employees.repository;

import com.avnzor.oms_backend.employees.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Integer> {

    List<Employee> findByIdIn(Collection<Integer> ids);
}
