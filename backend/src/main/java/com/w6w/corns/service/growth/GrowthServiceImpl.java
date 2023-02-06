package com.w6w.corns.service.growth;

import com.w6w.corns.domain.explog.ExpLog;
import com.w6w.corns.domain.explog.ExpLogRepository;
import com.w6w.corns.domain.loginlog.LoginLogRepository;
import com.w6w.corns.domain.room.RoomRepository;
import com.w6w.corns.domain.roomuser.RoomUser;
import com.w6w.corns.domain.roomuser.RoomUserRepository;
import com.w6w.corns.domain.user.User;
import com.w6w.corns.domain.user.UserRepository;
import com.w6w.corns.dto.explog.ExpLogRequestDto;
import com.w6w.corns.dto.explog.ExpLogResponseDto;
import com.w6w.corns.dto.indicator.AmountResponseDto;
import com.w6w.corns.dto.indicator.SubjectRatioResponseDto;
import com.w6w.corns.dto.level.LevelDto;

import com.w6w.corns.dto.subject.SubjectResponseDto;
import com.w6w.corns.service.subject.SubjectService;
import com.w6w.corns.util.code.ExpCode;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import com.w6w.corns.util.PageableResponseDto;
import com.w6w.corns.util.code.RoomUserCode;
import com.w6w.corns.util.code.UserCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GrowthServiceImpl implements GrowthService {

    private final UserRepository userRepository;
    private final ExpLogRepository expLogRepository;
    private final LoginLogRepository loginLogRepository;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;
    private final SubjectService subjectService;

    public int calExpPercentile(int userId) throws Exception{

        //user에서 usercd 탈퇴, 정지를 제외한 나머지 사람들의 총 exp 순위
        int cntAll = userRepository.countByUserCd(UserCode.USER_DEFAULT.getCode());

        int rank = userRepository.rankByExp(userId);

        return rank / cntAll * 100;
    }

    public LevelDto getUserLevel(int userId) throws Exception{

        User user = userRepository.findByUserId(userId);
        return LevelDto.fromEntity(user.getLevel());
    }

    public PageableResponseDto getExpLogList(int userId, Pageable pageable, String baseTime) throws Exception{
        //baseTime -> LocalDate 타입으로
        LocalDateTime localDateTime = LocalDateTime.parse(baseTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

        Slice<ExpLog> slice = expLogRepository.findByUserIdAndRegTmLessThanEqual(userId, pageable, localDateTime);
        System.out.println("slice = " + slice.getContent());

        List<ExpLogResponseDto> users = new ArrayList<>();
        for(ExpLog expLog : slice.getContent())
            users.add(ExpLogResponseDto.fromEntity(expLog));

        return PageableResponseDto.builder().list(users).hasNext(slice.hasNext()).build();
    }

    //경험치 부여
    @Override
    @Transactional
    public void giveExp(ExpLogRequestDto expLogRequestDto){

        expLogRepository.save(expLogRequestDto.toEntity());

        //expTotal 증가
        User user = userRepository.findByUserId(expLogRequestDto.getUserId());
        user.setExpTotal(user.getExpTotal()+expLogRequestDto.getGainExp());
        System.out.println("exp 증가 후 user = " + user);
        userRepository.save(user);
    }

    //출석률 반환
    @Override
    public int calAttendanceRate(int userId) throws Exception {

        long countPerMonth = loginLogRepository.findByRegTmAndUserIdPerMonth(userId);
        System.out.println(countPerMonth);

        double rate = (double)countPerMonth / 30;
        return (int)(rate * 100);
    }

    //주제별 비율 계산
    @Override
    public List<SubjectRatioResponseDto> calSubjectRatio(int userId) throws Exception {

        //이것도 컬럼으로 갖고있는건..??
        //주제별로 매번 가져오면 select 너무 많이 함 ㅠㅠ
        List<SubjectRatioResponseDto> subjectRatio = new ArrayList<>();
        System.out.println("here");
        //roomuser에서 userid로 모든 대화 기록 가져오기
        List<RoomUser> roomUsers = roomUserRepository.findByUserIdAndRoomUserCd(userId, RoomUserCode.ROOM_USER_END.getCode());
        System.out.println("roomUsers = " + roomUsers);

        int n = subjectService.findAll().size();
        int[] count = new int[n+1];
        System.out.println("subjectService = " + subjectService.findAll());

        //각 roomuser의 room 번호로 room에서 대화 주제 가져오기
        for(RoomUser roomuser : roomUsers){
            count[roomRepository.findByRoomNo(roomuser.getRoomNo()).getSubjectNo()]++;
        }

        //총 대화 주제 수
        int sum = IntStream.of(count).sum();
        System.out.println("sum = " + sum);
        System.out.println("count.length = " + count.length);
        //주제별 비율 리스트
        for(int i=1; i<=n; i++){
            SubjectRatioResponseDto responseDto = SubjectRatioResponseDto.builder()
                    .subjectNo(i)
                    .value(subjectService.findById(i).getValue())
                    .rate(count[i] / sum * 100)
                    .build();
            System.out.println("responseDto = " + responseDto);
            subjectRatio.add(responseDto);
        }
        System.out.println("subjectRatio = " + subjectRatio);
        return subjectRatio;
    }

    //일일 경험치 획득량 계산
    @Override
    public Map<String, Object> calDailyGainedExp(int userId) throws Exception {
        //만약 사용자가 가입한지 얼마 안됐다면 날짜를 자를건지, 아니면 0으로 보여줄건지

        List<AmountResponseDto> lastWeek = new ArrayList<>();
        List<AmountResponseDto> thisWeek = new ArrayList<>();
        for(int i=0; i<14; i++){
            LocalDate date = LocalDate.now().minusDays(i);
            System.out.println(date.getYear()+ " "+date.getMonthValue()+" "+date.getDayOfMonth());
            Long sum = expLogRepository.sumByUserIdAndRegTm(userId, date.getYear(), date.getMonthValue(), date.getDayOfMonth());
            System.out.println("sum = " + sum);
            AmountResponseDto temp = AmountResponseDto.builder()
                    .x(date.toString())
                    .y(sum==null?"0":String.valueOf(sum)).build();
            if(i / 7 > 0) lastWeek.add(temp);
            else thisWeek.add(temp);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("lastWeek", lastWeek);
        result.put("thisWeek", thisWeek);
        return result;
    }
}
