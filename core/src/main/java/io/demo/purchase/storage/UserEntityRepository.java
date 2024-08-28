package io.demo.purchase.storage;

import com.querydsl.jpa.impl.JPAQueryFactory;
import io.demo.purchase.support.exception.CoreDomainErrorType;
import io.demo.purchase.core.domain.user.User;
import io.demo.purchase.core.domain.user.UserRepository;
import io.demo.purchase.support.RoleType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
class UserEntityRepository extends QuerydslRepositorySupport implements UserRepository {

    private final UserJpaRepository userJpaRepository;
    private final JPAQueryFactory jpaQueryFactory;
    private static final QUserEntity userEntity = QUserEntity.userEntity;

    @Autowired
    public UserEntityRepository(UserJpaRepository userJpaRepository, JPAQueryFactory jpaQueryFactory) {
        super(UserEntity.class);
        this.userJpaRepository = userJpaRepository;
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public long add(String name, String email, String password) {
        UserEntity user = userJpaRepository.save(UserEntity.builder()
                                                            .name(name)
                                                            .email(email)
                                                            .password(password)
                                                            .build());
        return user.getId();
    }

    @Override
    public User find(String email) {
        UserEntity user = jpaQueryFactory.selectFrom(userEntity)
                .where(userEntity.email.eq(email))
                .fetchFirst();
        if (user == null)
            throw new NoDataException(CoreDomainErrorType.NOT_FOUND, "요청 유저를 찾지 못했습니다");
        return user.toUser();
    }

    @Override
    public User find(long userId) {
        UserEntity user = userJpaRepository.findById(userId).orElseThrow(() ->
                        new NoDataException(CoreDomainErrorType.NOT_FOUND, "요청 유저를 찾지 못했습니다"));
        return user.toUser();
    }

    @Override
    public User find(String name, String email) {
        UserEntity user = jpaQueryFactory.selectFrom(userEntity)
                .where(userEntity.name.eq(name)
                        .and(userEntity.email.eq(email)))
                .fetchFirst();

        if (user == null)
            throw new NoDataException(CoreDomainErrorType.NOT_FOUND, "요청 유저를 찾지 못했습니다");

        return user.toUser();
    }

    @Override
    public void updateRole(long userId, RoleType to) {
        UserEntity userEntity = userJpaRepository.findById(userId)
                .orElseThrow(() -> new NoDataException(CoreDomainErrorType.NOT_FOUND, "요청 유저를 찾지 못했습니다"));

        userEntity.updateRole(to);
        userJpaRepository.save(userEntity);
    }
}
