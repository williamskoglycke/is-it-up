import React from "react";
import { makeStyles } from "@material-ui/core/styles";
import Table from "@material-ui/core/Table";
import TableBody from "@material-ui/core/TableBody";
import TableCell from "@material-ui/core/TableCell";
import TableContainer from "@material-ui/core/TableContainer";
import TableHead from "@material-ui/core/TableHead";
import TableRow from "@material-ui/core/TableRow";
import Paper from "@material-ui/core/Paper";
import DeleteButton from "./DeleteButton";

const useStyles = makeStyles({
  table: {
    minWidth: 650,
  },
});

export default function ServiceTable(props) {
  const classes = useStyles();

  return (
    <TableContainer component={Paper}>
      <Table className={classes.table} aria-label="service table">
        <TableHead>
          <TableRow>
            <TableCell>Status</TableCell>
            <TableCell align="right">Name</TableCell>
            <TableCell align="right">Url</TableCell>
            <TableCell align="right">Created at</TableCell>
            <TableCell align="right">Updated at</TableCell>
            <TableCell align="right">Delete</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {props.services.services?.map((service) => (
            <TableRow key={service.name + service.url}>
              <TableCell component="th" scope="row">
                {service.status}
              </TableCell>
              <TableCell align="right">{service.name}</TableCell>
              <TableCell align="right">{service.url}</TableCell>
              <TableCell align="right">{service.createdAt}</TableCell>
              <TableCell align="right">{service.updatedAt}</TableCell>
              <TableCell align="right">
                <DeleteButton id={service.id} click={props.deleteService} />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
}
