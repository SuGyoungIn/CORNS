package com.w6w.corns.controller;

import com.w6w.corns.dto.friend.FriendListInterface;
import com.w6w.corns.dto.friend.FriendListRequestDto;
import com.w6w.corns.dto.friend.FriendListResponseDto;
import com.w6w.corns.dto.friend.FriendRequestDto;
import com.w6w.corns.service.friend.FriendService;
import com.w6w.corns.util.code.FriendLogCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/friend")
@Api("친구 컨트롤러")
public class FriendController {

    @Autowired
    FriendService friendService;

    @ApiOperation("친구 신청")
    @PostMapping("/send")
    public ResponseEntity<?> sendFriend(@RequestBody FriendRequestDto friendRequestDto) {
        Map resultmap = new HashMap<>();
        HttpStatus status;

        try {
            friendService.addFriendLog(friendRequestDto, FriendLogCode.FRIEND_LOG_SEND.getCode());
            friendService.sendFriend(friendRequestDto);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }

    @ApiOperation("친구 수락")
    @PostMapping("/accept")
    public ResponseEntity<?> acceptFriend(@RequestBody FriendRequestDto friendRequestDto) {
        Map resultmap = new HashMap<>();
        HttpStatus status;

        try {
            friendService.addFriendLog(friendRequestDto, FriendLogCode.FRIEND_LOG_ACCEPT.getCode());
            friendService.acceptFriend(friendRequestDto);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }

    @ApiOperation("친구 거절")
    @PostMapping("/reject")
    public ResponseEntity<?> rejectFriend(@RequestBody FriendRequestDto friendRequestDto) {
        Map resultmap = new HashMap<>();
        HttpStatus status;

        try {
            friendService.addFriendLog(friendRequestDto, FriendLogCode.FRIEND_LOG_REJECT.getCode());
            friendService.rejectFriend(friendRequestDto);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }

    @ApiOperation("친구 삭제")
    @PostMapping("/delete")
    public ResponseEntity<?> deleteFriend(@RequestBody FriendRequestDto friendRequestDto) {
        Map resultmap = new HashMap<>();
        HttpStatus status;

        try {
            friendService.addFriendLog(friendRequestDto, FriendLogCode.FRIEND_LOG_DELETE.getCode());
            friendService.deleteFriend(friendRequestDto);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }

    @ApiOperation("친구 신청 목록")
    @GetMapping("/receive/{userId}")
    public ResponseEntity<?> getFriendReceiveList(@PathVariable int userId) {
        Map resultmap = new HashMap<>();
        HttpStatus status;

        try {
            List<FriendListResponseDto> recvList = friendService.getFriendReceiveList(userId);

            if (recvList.isEmpty()) {
                status = HttpStatus.NO_CONTENT;
            } else {
                resultmap.put("recvList", recvList);
                status = HttpStatus.OK;
            }

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }

    @ApiOperation("친구 목록")
    @GetMapping("/{userId}")
    public ResponseEntity<?> getFriendList(@PathVariable int userId, FriendListRequestDto friendListRequestDto, Pageable pageable) {
        Map resultmap = new HashMap<>();
        HttpStatus status = HttpStatus.ACCEPTED;

        try {
            friendListRequestDto.setUserId(userId);
            Slice<FriendListInterface> friendSliceList = friendService.getFriendList(friendListRequestDto, pageable);

            List<FriendListResponseDto> friendList = new ArrayList<>(friendSliceList.getSize());
            for (FriendListInterface friend : friendSliceList.getContent()) {
                friendList.add(FriendListResponseDto.builder()
                                .userId(friend.getUserId())
                                .nickname(friend.getNickname())
                                .imgUrl(friend.getImgUrl())
                                .levelNo(friend.getLevelNo())
                                .build());
            }

            if (friendList.isEmpty()) {
                status = HttpStatus.NO_CONTENT;
            } else {
                resultmap.put("friendList", friendList);
                resultmap.put("hasNext", friendSliceList.hasNext());
                status = HttpStatus.OK;
            }

        } catch (Exception e) {
            resultmap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultmap, status);
    }
}
