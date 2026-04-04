package com.chitalebandhu.chitalebandhu.services;

import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.entity.Notification;
import com.chitalebandhu.chitalebandhu.entity.Tasks;
import com.chitalebandhu.chitalebandhu.entity.User;
import com.chitalebandhu.chitalebandhu.exceptions.ResourceNotFoundException;
import com.chitalebandhu.chitalebandhu.repository.MemberRepository;
import com.chitalebandhu.chitalebandhu.repository.TaskRepository;
import com.chitalebandhu.chitalebandhu.repository.UserRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskRepository taskRepository;

    public List<Member> getAllMembers(){
        return memberRepository.findByRoleIgnoreCase("USER");
    }

    public Page<Member> getAllMembersPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("_id").descending());
        return memberRepository.findByRoleIgnoreCase("USER", pageable);
    }

    public Member getMemberById(String myId){
        return memberRepository.findById(myId).orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + myId));
    }
    public String getMemberUserName(String username){
         User member =  userRepository.findFirstByUsernameIgnoreCase(username).orElseThrow(() -> new ResourceNotFoundException("Member not found with username: " + username));
       return member.getId();
    }

    public void addMember(Member member){
        String email = safe(member.getEmail());
        if (email.isEmpty()) {
            throw new IllegalArgumentException("Employee email is required");
        }

        if (memberRepository.existsByEmailIgnoreCase(email)) {
            throw new IllegalStateException("An employee with this email already exists");
        }

        member.setEmail(email);
        memberRepository.save(member);
    }


    public void deleteMemberById(String myId){
        Member existingMember = memberRepository.findById(myId)
            .orElseThrow(() -> new ResourceNotFoundException("Member not found with id: " + myId));

        List<Tasks> ownedItems = taskRepository.findByOwnerId(myId).orElse(List.of());

        long activeProjects = ownedItems.stream()
            .filter(t -> "PROJECT".equalsIgnoreCase(safe(t.getType())))
            .filter(t -> !isDoneStatus(t.getStatus()))
            .count();

        if (activeProjects > 0) {
            throw new IllegalStateException(
                "Cannot delete member \"" + existingMember.getName() +
                    "\". They are assigned to " + activeProjects + " incomplete project(s)."
            );
        }

        long activeTasks = ownedItems.stream()
            .filter(t -> "TASK".equalsIgnoreCase(safe(t.getType())))
            .filter(t -> !isDoneStatus(t.getStatus()))
            .count();

        if (activeTasks > 0) {
            throw new IllegalStateException(
                "Cannot delete member \"" + existingMember.getName() +
                    "\". They still have " + activeTasks + " incomplete task(s)."
            );
        }

        String linkedEmail = safe(existingMember.getEmail());
        List<User> linkedUsers = linkedEmail.isEmpty()
            ? List.of()
            : userRepository.findAllByUsernameIgnoreCase(linkedEmail);
        if (!linkedUsers.isEmpty()) {
            userRepository.deleteAll(linkedUsers);
        }

        // Keep historical tasks/projects but remove dangling owner reference.
        if (!ownedItems.isEmpty()) {
            List<Tasks> cleaned = new ArrayList<>(ownedItems);
            cleaned.forEach(t -> t.setOwnerId(""));
            taskRepository.saveAll(cleaned);
        }

        memberRepository.deleteById(myId);
    }

    public long getProjectCount(String ownerId, String type){
        return taskRepository.countByOwnerIdAndType(ownerId, type);
    }

    public long getMemberCount(){
        return memberRepository.count();
    }

    public long getStatusCount(String ownerId, String status){
        return taskRepository.countByOwnerIdAndStatus(ownerId, status);
    }

    public void addNotification(String ownerId, Notification notification){
        Member member = getMemberById(ownerId);
        member.addNotification(notification);
        memberRepository.save(member);
    }

    public List<Notification> getNotification(String ownerId){
        return getMemberById(ownerId).getNotifications();
    }

    public void removeNotification(String ownerId, Notification notification){
        Member member = getMemberById(ownerId);
        member.removeNotification(notification);
        memberRepository.save(member);
    }

    public Member updateMemberById(String myId, Member newMember){
        Optional <Member> existingMember = memberRepository.findById(myId);

        if(existingMember.isEmpty()){
            return null;
        }

        if(newMember.getEmail() != null && !newMember.getEmail().trim().isEmpty()){
            existingMember.get().setEmail(newMember.getEmail());
        }

        if(newMember.getName() != null && !newMember.getName().trim().isEmpty()){
            existingMember.get().setName(newMember.getName());
        }

        if(newMember.getMobileNo() != null && !newMember.getMobileNo().trim().isEmpty()){
            existingMember.get().setMobileNo(newMember.getMobileNo());
        }

        if(newMember.getRole() != null && !newMember.getRole().trim().isEmpty()){
            existingMember.get().setRole(newMember.getRole());
        }

        return memberRepository.save(existingMember.get());
    }

    private boolean isDoneStatus(String status) {
        String value = safe(status).toUpperCase();
        return "DONE".equals(value) || "COMPLETED".equals(value);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
