package io.demo.purchase.core.domain.user;

import io.demo.purchase.core.AlertUserRetryException;
import io.demo.purchase.support.exception.CoreDomainErrorType;
import io.demo.purchase.support.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserReader userReader;
    private final UserWriter userWriter;

    @Autowired
    public UserService(UserReader userReader, UserWriter userWriter) {
        this.userReader = userReader;
        this.userWriter = userWriter;
    }

    public long add(UserSignupInfo userSignUpInfo) {
        Optional<Long> optUserId = userReader.findExist(userSignUpInfo.name, userSignUpInfo.email);

        if (optUserId.isPresent())
            throw new AlertUserRetryException(CoreDomainErrorType.REQUEST_FAILED, "회원가입을 이미 완료한 유저입니다");

        return userWriter.append(userSignUpInfo.name, userSignUpInfo.email, userSignUpInfo.password);
    }

    public void updateUserRole(long userId, RoleType to) {
        userWriter.updateRole(userId, to);
    }
}
