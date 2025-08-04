package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.user.model.Approval;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.ApprovalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApprovalService {
    @Autowired
    ApprovalRepository approvalRepository;

    public void requestApproval(User user, Role role){
        Approval approvalRequest= new Approval(user,role);
        approvalRepository.save(approvalRequest);
    }
}
