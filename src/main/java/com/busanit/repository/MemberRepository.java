package com.busanit.repository;

import com.busanit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 중복 회원 검사
    Optional<Member> findByEmail(String email);

    // 아이디(이메일) 찾기
    @Query("SELECT m.email FROM Member m WHERE m.name = :name AND m.age = :age")
    String findUserEmail(String name, String age);

    // 비밀번호 수정
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.password = :password WHERE m.email = :email")
    void updatePassword(String password, String email);

    // 소셜 회원 특정조건 해당시 나이, 비밀번호 수정
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.password = :password, m.age = :age WHERE m.email = :email")
    void updatePasswordAndAge(String password, String age, String email);
}
