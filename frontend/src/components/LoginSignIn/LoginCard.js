import React, { useState, useEffect } from "react";
import { NavLink } from "react-router-dom";
import axios from "axios";
import SocialLogin from "./SocialLogin";

import { Box, Button } from "@mui/material";
import yellow_logo from "assets/corns_logo_yellow.png";
/** @jsxImportSource @emotion/react */
import { css } from "@emotion/react";

function LoginCard({ history }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  const onChangeEmail = (e) => {
    setEmail(e.target.value);
  };

  const onChangePassword = (e) => {
    setPassword(e.target.value);
  };

  // 로그인
  const onLogin = async (e) => {
    e.preventDefault();

    if (!email) {
      setErrorMsg("이메일을 입력해주세요.");
      return;
    }
    if (!password) {
      setErrorMsg("비밀번호를 입력해주세요.");
      return;
    }
    try {
      console.log("Log in!");
      const response = await axios.post(
        `${process.env.REACT_APP_HOST}/user/login`,
        { validateStatus: (status) => status === 200 || status === 401 },
        {
          email: email,
          password: password,
        }
      );
      console.log(response);
      if (response.status === 200) {
        // 로그인 성공
        // 세션에 저장 후 이전 페이지로 back
        history.goBack();
      } else if (response.status === 401) {
        setErrorMsg("아이디 또는 비밀번호를 잘못입력했습니다.");
      }
    } catch (e) {
      console.log(e);
    }
  };

  return (
    <Box
      sx={{
        padding: "32px 48px",
        border: "3px solid #111",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        backgroundColor: "white",
      }}
    >
      <NavLink to="/">
        <img
          src={yellow_logo}
          alt="corns-logo"
          css={css`
            height: 2rem;
          `}
        />
      </NavLink>

      <Box sx={{ width: "80%" }}>
        <h5
          css={css`
            font-size: 20px;
          `}
        >
          이메일
        </h5>
        <input
          placeholder="이메일을 입력하세요."
          value={email}
          onChange={onChangeEmail}
          css={css`
            width: 95%;
            height: 45px;
          `}
        />
      </Box>

      <Box sx={{ width: "80%" }}>
        <h5
          css={css`
            font-size: 20px;
          `}
        >
          비밀번호
        </h5>
        <input
          placeholder="비밀번호를 입력하세요."
          value={password}
          onChange={onChangePassword}
          css={css`
            width: 95%;
            height: 45px;
          `}
        />
        <p
          css={css`
            color: #ff0000;
            font-size: 12px;
          `}
        >
          {errorMsg}
        </p>
      </Box>

      <Button
        sx={{
          backgroundColor: "#3C90F2",
          color: "#111111",
          margin: "64px 0",
          width: "214px",
          height: "60px",
        }}
        onClick={onLogin}
      >
        로그인
      </Button>

      <SocialLogin />
    </Box>
  );
}

export default LoginCard;
