package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;

    @Transactional
    @Test
    public void testMember() {
        Member member = new Member("memberA");

        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(member.getId());

        assertThat(member).isEqualTo(findMember);
    }

}