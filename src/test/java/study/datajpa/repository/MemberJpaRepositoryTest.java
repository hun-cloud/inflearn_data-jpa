package study.datajpa.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.entity.Member;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class MemberJpaRepositoryTest {

    @Autowired
    private MemberJpaRepository memberJpaRepository;


    @Test
    public void testMember() {
        Member member = new Member("memberA");

        memberJpaRepository.save(member);

        Member findMember = memberJpaRepository.find(member.getId());

        assertThat(member).isEqualTo(findMember);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);

        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        Member findMember1 = memberJpaRepository.findById(member1.getId()).get();
        Member findMember2 = memberJpaRepository.findById(member2.getId()).get();

        // 단건 조회 검
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberJpaRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberJpaRepository.count();
        assertThat(count).isEqualTo(2);


    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        memberJpaRepository.save(member1);
        memberJpaRepository.save(member2);

        List<Member> memberA = memberJpaRepository.findByUsernameAndAgeGreaterThen("memberA", 13);
        List<Member> memberB = memberJpaRepository.findByUsernameAndAgeGreaterThen("memberA", 16);

        assertThat(memberA.size()).isEqualTo(1);
        assertThat(memberB.size()).isEqualTo(0);
    }

}