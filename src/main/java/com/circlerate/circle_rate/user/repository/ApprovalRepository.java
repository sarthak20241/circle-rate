package com.circlerate.circle_rate.user.repository;

import com.circlerate.circle_rate.user.model.Approval;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApprovalRepository extends MongoRepository<Approval,String> {
}
