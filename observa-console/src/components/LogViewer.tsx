import React from 'react';
import {
  Paper,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Typography,
  CircularProgress,
  Box,
} from '@mui/material';
import useLogStore from '../stores/logStore';
import HighlightText from './HighlightText';

const LogViewer: React.FC = () => {
  const { logs, loading, error, queryParams } = useLogStore();

  if (loading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 3 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (error) {
    return (
      <Paper sx={{ p: 2, bgcolor: 'error.light' }}>
        <Typography color="error">{error}</Typography>
      </Paper>
    );
  }

  if (!logs?.records.length) {
    return (
      <Paper sx={{ p: 2 }}>
        <Typography color="text.secondary">No logs found</Typography>
      </Paper>
    );
  }

  return (
    <TableContainer component={Paper}>
      <Table size="small">
        <TableHead>
          <TableRow>
            <TableCell>时间</TableCell>
            <TableCell>命名空间</TableCell>
            <TableCell>容器组</TableCell>
            <TableCell>容器</TableCell>
            <TableCell>日志</TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
          {logs.records.map((log, index) => (
            <TableRow key={index}>
              <TableCell>{new Date(log.timestamp).toLocaleString()}</TableCell>
              <TableCell>{log.namespace}</TableCell>
              <TableCell>{log.pod}</TableCell>
              <TableCell>{log.container}</TableCell>
              <TableCell sx={{ 
                maxWidth: '500px',
                whiteSpace: 'pre-wrap',
                wordBreak: 'break-word'
              }}>
                <HighlightText 
                  text={log.body} 
                  highlight={queryParams.log_query || ''} 
                />
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </TableContainer>
  );
};

export default LogViewer; 