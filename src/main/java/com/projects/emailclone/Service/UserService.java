package com.projects.emailclone.Service;

import com.projects.emailclone.Model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    public void saveUser(User user);
    public boolean findUser(User user);
}
