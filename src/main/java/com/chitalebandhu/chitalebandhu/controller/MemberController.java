package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.PagedResponse;
import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.entity.Notification;
import com.chitalebandhu.chitalebandhu.services.MemberService;
import com.chitalebandhu.chitalebandhu.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("members")
public class MemberController {

    @Autowired
    private MemberService memberService;

    @GetMapping("all")
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }

    @GetMapping("username/{userName}")
    public String getMemberUserName(@PathVariable String userName){
        return memberService.getMemberUserName(userName);
    }

    @PostMapping
    public void addMember(@RequestBody Member member){
        memberService.addMember(member);
    }

    @GetMapping("count")
    public long getMemberCount(){
        return memberService.getMemberCount();
    }

    @GetMapping("count/{type}/{ownerId}")
    public long getProjectCount(@PathVariable String ownerId, @PathVariable String type){
        return memberService.getProjectCount(ownerId, type);
    }

    @GetMapping("{ownerId}/projects/{status}/count")
    public long getCountByStatus(@PathVariable String ownerId, @PathVariable String status){
        return memberService.getStatusCount(ownerId, status);
    }

    @GetMapping("id/{myId}")
    public Member getMemberById(@PathVariable String myId){
       return memberService.getMemberById(myId);
    }

    @PutMapping("update/{myId}")
    public void updateMemberById(@PathVariable String myId,@RequestBody Member member){
        memberService.updateMemberById(myId , member);
    }

    @DeleteMapping("delete/{myId}")
    public void deleteMemberById(@PathVariable String myId){
        memberService.deleteMemberById(myId);
    }

    // Paginated endpoint
    @GetMapping("paginated")
    public ResponseEntity<PagedResponse<Member>> getMembersPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<Member> membersPage = memberService.getAllMembersPaginated(page, size);
            return new ResponseEntity<>(new PagedResponse<>(membersPage), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}