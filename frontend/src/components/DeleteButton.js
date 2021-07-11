import React from "react";
import Button from "@material-ui/core/Button";
import { makeStyles } from "@material-ui/core/styles";
import DeleteIcon from "@material-ui/icons/Delete";
import { useState } from "react";
import CircularLoading from "./CircularLoading";

const useStyles = makeStyles((theme) => ({
  button: {
    margin: theme.spacing(1),
  },
}));

export default function DeleteButton(props) {
  const classes = useStyles();
  const [disabled, setDisabled] = useState(false);
  const handleOnClick = () => {
    props.click(props.id);
    setDisabled(true);
  };

  return (
    <div>
      <Button
        variant="contained"
        color="secondary"
        className={classes.button}
        startIcon={disabled ? <CircularLoading /> : <DeleteIcon />}
        disabled={disabled}
        onClick={handleOnClick}
      >
        Delete
      </Button>
    </div>
  );
}
