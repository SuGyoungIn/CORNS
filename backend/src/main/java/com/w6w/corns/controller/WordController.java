package com.w6w.corns.controller;

import com.w6w.corns.dto.word.request.CreateWordRequestDto;
import com.w6w.corns.dto.word.request.ModifyWordRequestDto;
import com.w6w.corns.dto.word.request.UpdateWordStatusRequestDto;
import com.w6w.corns.dto.word.response.WordReponseDto;
import com.w6w.corns.service.word.WordService;
import com.w6w.corns.util.PageableResponseDto;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController()
@RequestMapping("word")
public class WordController {

    private final Logger logger = LoggerFactory.getLogger(WordController.class);

    @Autowired
    WordService wordService;

    @ApiOperation(value = "쫑알단어 목록", notes = "status, page, size를 parameter로 요청")
    @GetMapping(value = "/{userId}")
    private ResponseEntity<?> searchBySlice(@PathVariable int userId, @RequestParam String baseTime, @RequestParam int wordStatus, @PageableDefault Pageable pageable) {
        logger.debug("userId: {}", userId);
        Map resultMap = new HashMap<>();
        HttpStatus status;

        try {
            PageableResponseDto response = wordService.searchBySlice(baseTime, wordStatus, pageable);
            status = HttpStatus.OK;
            return new ResponseEntity<PageableResponseDto>(response, status);

        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultMap, status);
    }

    @ApiOperation(value = "쫑알 단어 추가", notes = "wordEng, wordKor를 request body에 담아서 요청")
    @PostMapping
    private ResponseEntity<?> save(@RequestBody CreateWordRequestDto request) {
        logger.debug("request: {}", request);
        Map resultMap = new HashMap<>();
        HttpStatus status;

        try {
            WordReponseDto response = wordService.saveWord(request);
            status = HttpStatus.OK;
            return new ResponseEntity<WordReponseDto>(response, status);
        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultMap, status);
    }

    @ApiOperation(value = "쫑알 단어 수정", notes = "wordSq, wordKor, wordEng를 보내면 내용 수정")
    @PutMapping
    private ResponseEntity<?> modifyWord(@RequestBody ModifyWordRequestDto request) {
        logger.debug("request: {}", request);
        Map resultMap = new HashMap<>();
        HttpStatus status;

        try {
            WordReponseDto response = wordService.modifyWord(request);
            status = HttpStatus.OK;
            return new ResponseEntity<WordReponseDto>(response, status);
        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultMap, status);
    }

    @ApiOperation(value = "쫑알 단어 상태 변경", notes = "wordSq, status를 요청으로 보내면 상태 변경")
    @PatchMapping
    private ResponseEntity<?> updateWordStatus(@RequestBody UpdateWordStatusRequestDto request) {
        logger.debug("request: {}", request);
        Map resultMap = new HashMap<>();
        HttpStatus status;

        try {
            wordService.updateWordStatus(request);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultMap, status);
    }

    @ApiOperation(value = "쫑알 단어 삭제", notes = "wordSq로 단어 삭제")
    @DeleteMapping(value = "/{wordSq}")
    private ResponseEntity<?> delete( @PathVariable int wordSq) {
        logger.debug("wordSq: {}", wordSq);
        Map resultMap = new HashMap<>();
        HttpStatus status;

        try {
            wordService.deleteWord(wordSq);
            status = HttpStatus.OK;

        } catch (Exception e) {
            resultMap.put("message", e.getMessage());
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<Map>(resultMap, status);
    }
}
