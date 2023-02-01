import React from "react";
import { useState } from "react";
import UserProfile from "../../components/GlobalComponents/UserProfile";
import { Box, Typography, Modal } from "@mui/material";
import FriendsBtn from "./FriendsBtn";
import RequestForm from "./RequestForm";

function UserProfileModal() {
  const style = {
    position: "absolute",
    top: "50%",
    left: "50%",
    transform: "translate(-50%, -50%)",
    width: "80%",
    height: "60%",
    bgcolor: "background.paper",
    border: "3px solid #111",
    boxShadow: 24,
    p: "32px 0 200px",
  };

  // 데이터 fetch (from_id == to_id)
  // dummy data
  const user = {
    basicInfo: {
      img_url:
        "https://i.pinimg.com/564x/af/7b/de/af7bde50489a2cb932a98741b877704b.jpg",
      nickname: "isk2",
      id: 1001,
      level: 30,
      exp: 1980,
      friendCnt: 10,
      totalDay: 13,
      totalDdabong: 4,
      totalTalk: 123,
    },
    rankingList: [
      { type: "sungsil", rank: 80, indicate: 1800 },
      { type: "ddabong", rank: null, indicate: 8 },
      { type: "suda", rank: 67, indicate: 387 },
      { type: "ingi", rank: null, indicate: 4 },
    ],
  };

  const [open, setOpen] = useState(false);
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  return (
    <>
      <Box sx={style}>
        <Typography variant="h5" sx={{ ml: "32px" }}>
          {user.basicInfo.nickname} 페이지
        </Typography>
        <UserProfile user={user} />
        <Box sx={{ display: "flex", justifyContent: "center", mt: "4%" }} onClick={handleOpen}>
          <FriendsBtn status={"1"}/>
        </Box>
      </Box>

      <Modal open={open} onClose={handleClose}>
        <Box>
          <RequestForm />
        </Box>
      </Modal>
    </>
  );
}

export default UserProfileModal;
