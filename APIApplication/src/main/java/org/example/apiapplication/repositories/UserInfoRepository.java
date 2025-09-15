package org.example.apiapplication.repositories;

import org.example.apiapplication.entities.user.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoRepository extends CrudRepository<UserInfo, Integer> {
}
