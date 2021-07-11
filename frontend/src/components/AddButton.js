import React from "react";
import Button from "@material-ui/core/Button";
import { makeStyles } from "@material-ui/core/styles";
import SaveIcon from "@material-ui/icons/Save";
import CircularLoading from "./CircularLoading";

const useStyles = makeStyles((theme) => ({
  button: {
    margin: theme.spacing(1),
  },
}));

export default function SaveButton(props) {
  const classes = useStyles();
  const { saving } = props;
  return (
    <div>
      <Button
        variant="contained"
        color="primary"
        size="large"
        className={classes.button}
        startIcon={saving ? <CircularLoading /> : <SaveIcon />}
        disabled={saving}
        type="submit"
      >
        Save
      </Button>
    </div>
  );
}
