import React, { useState, useEffect } from 'react';
import Grid from '@mui/material/Grid';
import List from '@mui/material/List';
import ListItem from '@mui/material/ListItem';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import Checkbox from '@mui/material/Checkbox';
import Button from '@mui/material/Button';
import Paper from '@mui/material/Paper';

import DeleteIcon from '@mui/icons-material/DeleteForeverOutlined';
import EditIcon from '@mui/icons-material/EditOutlined';
import SaveIcon from '@mui/icons-material/CheckBoxOutlined';
import { Alert, IconButton, ListItemButton } from '@mui/material';
import { useDispatch, useSelector } from 'react-redux';
import { getDoneList, getTodoList } from 'store/reducers/wordListReducer';
import axios from 'axios';


function not(a, b) {
  return a.filter((value) => b.indexOf(value) === -1);
}

function intersection(a, b) {
  return a.filter((value) => b.indexOf(value) !== -1);
}

export default function TransferList() {
  const dispatch = useDispatch();
  const baseTime = "2023-02-28 00:00:00"

  useEffect(() => {
    dispatch(getTodoList(baseTime));
    dispatch(getDoneList(baseTime));
  }, [baseTime, dispatch]);

  const [checked, setChecked] = React.useState([]);
  const [todoWords, setLeft] = React.useState(useSelector((state) => state.wordListReducer.todoList)); 
  const [doneWords, setRight] = React.useState(useSelector((state) => state.wordListReducer.doneList));

  const leftChecked = intersection(checked, todoWords);
  const rightChecked = intersection(checked, doneWords);

  const handleToggle = (value) => () => {
    const currentIndex = checked.indexOf(value);
    const newChecked = [...checked];

    if (currentIndex === -1) {
      newChecked.push(value);
    } else {
      newChecked.splice(currentIndex, 1);
    }

    setChecked(newChecked);
  };

  const handleCheckedRight = () => {
    let request = leftChecked.map(function(value) {
      return {status: 2, wordSq: `${value.wordSq}`}
    });
    updateWordStatus(request);
    setRight(leftChecked.concat(doneWords));

    setLeft(not(todoWords, leftChecked));
    setChecked(not(checked, leftChecked));
  };

  const handleCheckedLeft = () => {
    setLeft(rightChecked.concat(todoWords));
    setRight(not(doneWords, rightChecked));
    setChecked(not(checked, rightChecked));
  };

  // [PATCH] 단어 상태 변경
  const updateWordStatus = async (body) => {
    console.log(body);

    try {
      const response = await axios.patch(
        `${process.env.REACT_APP_HOST}/word`, {body}
      );
      if (response.status === 200) {
        <Alert severity="success">잘 했어</Alert>
      }
    } catch (e) {
      console.log(e);
    }
  };

  // [PUT] 쫑알단어 수정
  const modifyWord = (body) => async (e) => {
    e.preventDefault();

    try {
      const response = await axios.put(
        `${process.env.REACT_APP_HOST}/word`, {body}
      );
      if (response.status === 200) {
        <Alert severity="success">잘 했어</Alert>
      }
    } catch (e) {
      console.log(e);
    }
  };

  const wordListItem = (item, id) => (
    <ListItem
    key={id}
    secondaryAction={[
      <IconButton>
        <EditIcon color="warning"></EditIcon>
      </IconButton>,
      <IconButton>
        <DeleteIcon color="error"></DeleteIcon>
      </IconButton>,
      <IconButton>
      <SaveIcon color="success"></SaveIcon>
    </IconButton>
    ]}
    disablePadding
  >
    <ListItemButton role="listitem" onClick={handleToggle(item)}>
      <ListItemIcon>
        <Checkbox
          checked={checked.indexOf(item) !== -1}
          tabIndex={-1}
          disableRipple
          inputProps={{
            'aria-labelledby': item.wordSq,
          }}
        />
      </ListItemIcon>
      <ListItemText id={item.wordEng} primary={`${item.wordEng}`} />
      <ListItemText id={item.wordKor} primary={`${item.wordKor}`} />
      <ListItemIcon></ListItemIcon>
    </ListItemButton>
  </ListItem>
  );

  return (
    <Grid container sx={{ mt: "32px" }} justifyContent="center" alignItems="center">
      <Grid item xs={5.5} sx={{ boxShadow:"4px 4px 4px rgba(0,0,0,0.25)", border: "3px solid #111" }}>
        <Grid sx={{ ml: "16px", mr: "16px" }}>
          <Grid container direction="row" justifyContent="space-between" alignItems="center">
            <h3>외워야할 쫑알단어</h3>
            <Button variant="contained">추가</Button>
          </Grid>
        </Grid>
        <Paper sx={{ width: "100%", height: "600px", overflow: 'auto' }}>
          <List dense component="div" role="list">
            {
              todoWords.map((item, id) => wordListItem(item, id))
            }
          </List>
        </Paper>
      </Grid>
      <Grid item xs={1}> 
        <Grid container direction="column" justifyContent="center" alignItems="center">
          <Button
            sx={{ my: 0.5 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedRight}
            disabled={leftChecked.length === 0}
            aria-label="move selected right"
          >
            &gt;
          </Button>
          <Button
            sx={{ my: 0.5 }}
            variant="outlined"
            size="small"
            onClick={handleCheckedLeft}
            disabled={rightChecked.length === 0}
            aria-label="move selected left"
          >
            &lt;
          </Button>
        </Grid>
      </Grid>
      <Grid item xs={5.5} sx={{ boxShadow:"4px 4px 4px rgba(0,0,0,0.25)", border: "3px solid #111" }}>
        <Grid sx={{ ml: "16px"}}><h3>외운 쫑알단어</h3></Grid>
        <Paper sx={{ width: "100%", height: "600px", overflow: 'auto' }}>
          <List dense component="div" role="list">
            {
              doneWords.map((item, id) => wordListItem(item, id))
            }
          </List>
        </Paper>
      </Grid>
    </Grid>
  );
}