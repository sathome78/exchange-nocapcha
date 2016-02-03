package me.exrates.service;

import me.exrates.model.User;


<<<<<<< HEAD
public interface UserService {  

public int getIdByEmail(String email);  

public boolean create(User user);

public boolean ifNicknameIsUnique(String nickname);

public boolean ifEmailIsUnique(String email);

public String logIP(String email, String host);


}  
=======
public interface UserService {

    int getIdByEmail(String email);

    boolean create(User user);

    boolean ifNicknameIsUnique(String nickname);

    boolean ifEmailIsUnique(String email);

    String logIP(String email, String host);
}
>>>>>>> 04262353b47fdd14c36825d96fcecbda53d964c1
