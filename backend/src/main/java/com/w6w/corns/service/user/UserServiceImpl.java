package com.w6w.corns.service.user;

import com.w6w.corns.domain.user.User;
import com.w6w.corns.domain.user.UserRepository;
import com.w6w.corns.dto.user.LoginResponseDto;
import com.w6w.corns.dto.user.UserRequestDto;
import com.w6w.corns.util.SHA256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;

    @Override
    @Transactional
    public int signUp(UserRequestDto requestUser) throws Exception {

        //이메일 검증
        int result = validateDuplicateUser(requestUser.getEmail());

        if (result == 1) return -1;
         else {
            requestUser.setSocial(1); //기본 회원가입 설정
            userRepository.save(requestUser.toEntity()); //회원 저장
            return 1;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public int validateDuplicateUser(String email){
        User findUser = userRepository.findByEmail(email);

        if(findUser == null) return 0; //중복 x
        else return 1; //중복
    }


    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto login(UserRequestDto requestUser) throws Exception{

        //해당 이메일을 가진 객체를 db에서 찾음
        User user = userRepository.findByEmail(requestUser.getEmail());

        //탈퇴회원 및 이용정지회원은 나중에 처리하기
        if(isSamePassword(requestUser) && user.getUserCd() == 8000){

            //따봉, 친구, 출석, 발화량 나중에 추가 필요
            return LoginResponseDto.builder()
                    .user(user)
                    .build();
        }
        return null;
    }
    @Override
    @Transactional(readOnly = true)
    public void updateLastLoginTm(int userId) throws Exception{
        User user = userRepository.getReferenceById(userId);
        user.updateLastLoginTM();
    }

    @Override
    @Transactional
    public int saveRefreshToken(int userId, String refreshToken) throws Exception {
        return userRepository.updateRefreshToken(userId, refreshToken);
    }

    //Dto 로 수정 필요
    @Override
    @Transactional(readOnly = true)
    public Object getRefreshToken(int userId) throws Exception {
        User user = userRepository.findByUserId(userId);
        return user.getRefreshToken();
    }

    @Override
    @Transactional
    public void deleteRefreshToken(int userId) throws Exception {

        userRepository.updateRefreshToken(userId, null);
    }

    @Override
    @Transactional
    public LoginResponseDto findByUserId(int userId) throws Exception{
        User user = userRepository.findByUserId(userId);
        return LoginResponseDto.builder().user(user).build();
    }

    public LoginResponseDto findByEmail(String email) throws Exception{
        User user = userRepository.findByEmail(email);
        return LoginResponseDto.builder().user(user).build();
    }

    @Override
    public boolean isSamePassword(UserRequestDto requestUser) throws Exception {

        User user = userRepository.findByUserId(requestUser.getUserId());

        if(user != null){
            String userSalt = user.getSalt();

            //입력받은 user의 password 정보
            String inputPass = requestUser.getPassword();
            String newPass = SHA256Util.getEncrypt(inputPass, userSalt);

            if(newPass.equals(user.getPassword())) return true;
        }
        return false;
    }
}
