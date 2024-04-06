package com.kream.chouxkream.user.repository;

import com.kream.chouxkream.user.model.entity.AuthNumber;
import org.springframework.data.repository.CrudRepository;

public interface AuthNumberRepositroy extends CrudRepository<AuthNumber, String> {
}
