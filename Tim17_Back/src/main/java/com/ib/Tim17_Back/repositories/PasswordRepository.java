package com.ib.Tim17_Back.repositories;

import com.ib.Tim17_Back.models.PasswordUser;
import com.ib.Tim17_Back.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PasswordRepository extends JpaRepository<PasswordUser,Long> {
    List<PasswordUser> findAllByUser(User user);
}
