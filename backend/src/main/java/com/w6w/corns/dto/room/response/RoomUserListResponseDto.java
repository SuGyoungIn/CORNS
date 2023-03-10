package com.w6w.corns.dto.room.response;

import com.w6w.corns.dto.user.UserResponseDto;
import io.swagger.annotations.ApiModel;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@ToString
@ApiModel(value="쫑알룸 유저 리스트 응답 정보")
public class RoomUserListResponseDto {

    RoomUserResponseDto roomUser;

    UserResponseDto user;

}
