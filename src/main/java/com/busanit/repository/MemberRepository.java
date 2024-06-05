package com.busanit.repository;

import com.busanit.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    // 중복 회원 검사
    Optional<Member> findByEmail(String email);

    // (비밀번호 변경시) 기존 비밀번호 확인용
    @Query("SELECT m.password FROM Member m WHERE m.email = :email")
    String findByPassword(String email);

    // 사용자 email 로 member_id 찾기
    @Query("SELECT m.id FROM Member m WHERE m.email = :email")
    Long findUserIdx(String email);

    // 아이디(이메일) 찾기
    @Query("SELECT m.email FROM Member m WHERE m.name = :name AND m.age = :age")
    List<String> findUserEmails(@Param("name") String name, @Param("age") String age);
//    String findUserEmail(String name, String age);

    // 제공된 정보값과 일치하는 비밀번호가 있는지 검사
    @Query("SELECT COUNT(m) FROM Member m WHERE m.name = :name AND m.age = :age AND m.email = :email")
    Long findUserPassword(String name, String age, String email);

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

    @Query("SELECT m.name FROM Member m WHERE m.email = :email")
    Optional<String> findNameByEmail(@Param("email") String email);

    // 멤버십 등급 수정
    @Modifying
    @Transactional
    @Query("UPDATE Member m SET m.grade_code = :userEditGrade WHERE m.email = :email")
    void updateGrade(long userEditGrade, String email);
}
