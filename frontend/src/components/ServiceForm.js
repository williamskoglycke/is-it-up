import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import TextField from "@material-ui/core/TextField";
import SaveButton from "./AddButton";
import Grid from "@material-ui/core/Grid";
import Tooltip from "@material-ui/core/Tooltip";

const useStyles = makeStyles((theme) => ({
  root: {
    "& > *": {
      margin: theme.spacing(1),
      width: "35ch",
    },
  },
}));

const handleOnSubmit = (event, addService, setSaving) => {
  event.preventDefault();
  addService(event.target[0].value, event.target[1].value);
  event.target[0].value = "";
  event.target[1].value = "";
  setSaving(true);
};

export default function ServiceForm(props) {
  const classes = useStyles();
  const { addService, setSaving, saving } = props;

  return (
    <form
      className={classes.root}
      noValidate
      autoComplete="off"
      onSubmit={(event) => handleOnSubmit(event, addService, setSaving)}
    >
      <Grid container>
        <Grid item xs={5}>
          <TextField id="standard-basic" label="Name" />
        </Grid>
        <Grid item xs={5}>
          <Tooltip title="Example https://google.se" aria-label="url">
            <TextField id="standard-basic" label="Url" />
          </Tooltip>
        </Grid>
        <Grid item xs={2}>
          <SaveButton saving={saving} />
        </Grid>
      </Grid>
    </form>
  );
}
