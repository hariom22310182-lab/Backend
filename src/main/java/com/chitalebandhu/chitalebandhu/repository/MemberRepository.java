package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends MongoRepository<Member , String> {
	List<Member> findByRoleIgnoreCase(String role);
	Optional<Member> findFirstByEmailIgnoreCase(String email);
	boolean existsByEmailIgnoreCase(String email);

	// Pagination method
	Page<Member> findByRoleIgnoreCase(String role, Pageable pageable);
}
