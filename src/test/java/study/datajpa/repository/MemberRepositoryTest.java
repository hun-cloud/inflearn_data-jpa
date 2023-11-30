package study.datajpa.repository;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Test
    public void testMember() {
        Member member = new Member("memberAA");
        Member saveMember = memberRepository.save(member);

        Member findMember = memberRepository.findById(saveMember.getId()).get();

        assertThat(saveMember).isEqualTo(findMember);
    }

    @Test
    void basicCRUD() {
        Member member1 = new Member("memberA");
        Member member2 = new Member("memberB");

        memberRepository.save(member1);
        memberRepository.save(member2);

        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();

        // 단건 조회 검
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        // 리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);
    }

    @Test
    public void findByUsernameAndAgeGreaterThen() {
        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> memberA = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 13);
        List<Member> memberB = memberRepository.findByUsernameAndAgeGreaterThan("memberA", 16);

        assertThat(memberA.size()).isEqualTo(1);
        assertThat(memberB.size()).isEqualTo(0);
    }

    @Test
    public void testQuery() {
        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> memberA = memberRepository.findUser("memberA", 15);

        assertThat(memberA.size()).isEqualTo(1);
    }

    @Test
    public void testusernameList() {
        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        memberRepository.save(member1);
        memberRepository.save(member2);

        List<String> usernames = memberRepository.findUsernameList();

        assertThat(usernames.size()).isEqualTo(2);
    }

    @Test
    public void findMemberDto() {

        Team team = new Team("teamA");
        teamRepository.save(team);

        Member member1 = new Member("memberA", 15);
        member1.setTeam(team);
        Member member2 = new Member("memberB", 15);
        member2.setTeam(team);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<MemberDto> memberDto = memberRepository.findMemberDto();

        System.out.println(memberDto);
    }

}