package com.kream.chouxkream.auth.repository;

import com.kream.chouxkream.auth.model.entity.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

}
