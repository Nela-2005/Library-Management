package LibraryManagement.service;
import LibraryManagement.entity.User;
import LibraryManagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class SignupService {

    private final UserRepository userRepository;


    public SignupService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public SignupResponse saveUser(User user) {
        System.out.println("Saving user");
        try {
            userRepository.save(user);
            return new SignupResponse(Boolean.TRUE,"Perdoruesi u ruajt me sukses");
        }catch (Exception e){
            return new SignupResponse(Boolean.FALSE, "Perdoruesi nuk u ruajt me sukses sepse  : " + e.getMessage());
        }
    }

    public class SignupResponse {
        private String message;
        private boolean success;

        public SignupResponse(boolean success,String message) {
            this.message = message;
            this.success = success;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public boolean isSuccess() {
            return success;
        }

        public void setSuccess(boolean success) {
            this.success = success;
        }

        @Override
        public String toString() {
            return "signupResponse{" +
                    "message='" + message + '\'' +
                    ", success=" + success +
                    '}';
        }
    }
}
