package com.chitalebandhu.chitalebandhu.repository;

import com.chitalebandhu.chitalebandhu.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {

}
