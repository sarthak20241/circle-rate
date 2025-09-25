package com.circlerate.circle_rate.user.service;

import com.circlerate.circle_rate.auth.model.Role;
import com.circlerate.circle_rate.user.model.Approval;
import com.circlerate.circle_rate.user.model.User;
import com.circlerate.circle_rate.user.repository.ApprovalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ApprovalService {
    private final ApprovalRepository approvalRepository;

    

    public void requestApproval(User user, Role role){
        Approval approvalRequest= new Approval(user,role);
        approvalRepository.save(approvalRequest);
    }
}
