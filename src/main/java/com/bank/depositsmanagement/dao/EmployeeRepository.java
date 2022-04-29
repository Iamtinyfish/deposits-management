package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends CrudRepository<Employee, Long> {
    boolean existsByEmail(String email);
    boolean existsByIDCard(String IDCard);
}
