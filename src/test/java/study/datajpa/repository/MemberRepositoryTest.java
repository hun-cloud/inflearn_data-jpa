package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;
import study.datajpa.dto.MemberDto;
import study.datajpa.entity.Member;
import study.datajpa.entity.Team;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.shouldHaveThrown;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = false)
class MemberRepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private EntityManager em;

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

    @Test
    public void findByNames() {

        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);

        memberRepository.save(member1);
        memberRepository.save(member2);

        List<Member> members = memberRepository.findByNames(Arrays.asList("memberA", "memberB"));

        assertThat(members).hasSize(2);
    }

    @Test
    void paging() {
        // given
        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberB", 10));
        memberRepository.save(new Member("memberC", 10));
        memberRepository.save(new Member("memberD", 10));
        memberRepository.save(new Member("memberE", 10));

        int age = 10;
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));

        // when
        Page<Member> page = memberRepository.findByAge(age, pageRequest);

        // then

        List<Member> content = page.getContent();
        long totalElements = page.getTotalElements();

        for (Member member : content) {
            System.out.println("member = " + member);
        }
        System.out.println(totalElements);

        assertThat(content).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumber()).isEqualTo(0); // 페이지 번호
        assertThat(page.getTotalPages()).isEqualTo(2); //
        assertThat(page.isFirst()).isTrue();
        assertThat(page.hasNext()).isTrue();

        Page<MemberDto> map = page.map(member -> new MemberDto(member.getId(), member.getUsername(), null));
    }

    @Test
    void bulkUpdate() {
        // given
        memberRepository.save(new Member("memberA", 10));
        memberRepository.save(new Member("memberB", 10));
        memberRepository.save(new Member("memberC", 20));
        memberRepository.save(new Member("memberD", 30));
        memberRepository.save(new Member("memberE", 40));

        // when
        int resultCount = memberRepository.bulkAgePlus(20);

        assertThat(resultCount).isEqualTo(3);
    }

    @Test
    void findMemberLazy() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findMemberFetchJoin();
    }

    @Test
    void findMemberEntityGraph() {
        Team teamA = new Team("teamA");
        Team teamB = new Team("teamB");
        teamRepository.save(teamA);
        teamRepository.save(teamB);

        Member member1 = new Member("memberA", 15);
        Member member2 = new Member("memberB", 15);
        member1.setTeam(teamA);
        member2.setTeam(teamB);
        memberRepository.save(member1);
        memberRepository.save(member2);

        em.flush();
        em.clear();

        List<Member> members = memberRepository.findAll();
    }

    @Test
    void queryHint() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        Member findMember = memberRepository.findById(member1.getId()).get();
        findMember.setUsername("member2");

        em.flush();
    }


    @Test
    void querylock() {
        Member member1 = new Member("member1", 10);
        memberRepository.save(member1);
        em.flush();
        em.clear();

        List<Member> members = memberRepository.findLockByUsername("member1");

        em.flush();
    }
}