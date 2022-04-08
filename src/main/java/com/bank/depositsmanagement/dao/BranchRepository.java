package com.bank.depositsmanagement.dao;

import com.bank.depositsmanagement.entity.Branch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BranchRepository extends CrudRepository<Branch, Long> {
}
