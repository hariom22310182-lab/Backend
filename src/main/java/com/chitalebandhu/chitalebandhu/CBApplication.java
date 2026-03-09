package com.chitalebandhu.chitalebandhu;

import com.chitalebandhu.chitalebandhu.entity.Member;
import com.chitalebandhu.chitalebandhu.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CBApplication {
	public static void main(String[] args) {
		SpringApplication.run(CBApplication.class, args);
	}
//	@Bean
//	CommandLineRunner runner(MemberRepository repository){
//	 return args -> {
//		 Member m = new Member();v
//		 m.setName("Yash");
//		 repository.save(m);
//		 System.out.println("Saved to mongodb "+ m.getName());
//	 };
//	}

	//just for fun
}

