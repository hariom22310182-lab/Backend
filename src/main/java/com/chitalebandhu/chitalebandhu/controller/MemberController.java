package com.chitalebandhu.chitalebandhu.controller;

import com.chitalebandhu.chitalebandhu.DTOs.PagedResponse;
import com.chitalebandhu.chitalebandhu.entity.Member;
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
    @Autowired
    private TaskService taskService;

    @GetMapping("all")
    public List<Member> getAllMembers(){
        return memberService.getAllMembers();
    }

    @PostMapping
    public boolean addMember(@RequestBody Member member){
        memberService.addMember(member);
        return true;
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
    public ResponseEntity<Member> getMemberById(@PathVariable String myId){
       Member member = memberService.getMemberById(myId);
       return ResponseEntity.ok(member);
    }

    @PutMapping("update/{myId}")
    public boolean updateMemberById(@PathVariable String myId,@RequestBody Member member){
        memberService.updateMemberById(myId , member);
        return true;
    }

    @DeleteMapping("delete/{myId}")
    public boolean deleteMemberById(@PathVariable String myId){
        memberService.deleteMemberById(myId) ;
        return true;
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