package com.kream.chouxkream.jwt.repository;

import com.kream.chouxkream.jwt.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface JwtRepository extends CrudRepository<RefreshToken, String> {

}
