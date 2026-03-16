package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MemberRepository extends MongoRepository<Member , String> {
	List<Member> findByRoleIgnoreCase(String role);

	// Pagination method
	Page<Member> findByRoleIgnoreCase(String role, Pageable pageable);
}
