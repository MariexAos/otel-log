import React from 'react';
import {
  Paper,
  TextField,
  Button,
  Box,
  Typography,
  Stack,
} from '@mui/material';
import { Search as SearchIcon } from '@mui/icons-material';
import useLogStore from '../stores/logStore';

const LogSearch: React.FC = () => {
  const { queryParams, setQueryParams, fetchLogs } = useLogStore();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    fetchLogs();
  };

  return (
    <Paper sx={{ p: 2, mb: 2 }}>
      <form onSubmit={handleSearch}>
        <Stack spacing={2}>
          <Typography variant="h6" gutterBottom>
            容器日志查询
          </Typography>
          <Box sx={{ display: 'flex', gap: 2, flexWrap: 'wrap' }}>
            <TextField
              sx={{ flex: 1, minWidth: '200px' }}
              label="命名空间"
              value={queryParams.namespace_query || ''}
              onChange={(e) => setQueryParams({ namespace_query: e.target.value })}
              size="small"
            />
            <TextField
              sx={{ flex: 1, minWidth: '200px' }}
              label="容器组"
              value={queryParams.pod_query || ''}
              onChange={(e) => setQueryParams({ pod_query: e.target.value })}
              size="small"
            />
            <TextField
              sx={{ flex: 1, minWidth: '200px' }}
              label="容器"
              value={queryParams.container_query || ''}
              onChange={(e) => setQueryParams({ container_query: e.target.value })}
              size="small"
            />
          </Box>
          <TextField
            fullWidth
            label="关键字"
            value={queryParams.log_query || ''}
            onChange={(e) => setQueryParams({ log_query: e.target.value })}
            size="small"
            multiline
            rows={2}
          />
          <Box sx={{ display: 'flex', justifyContent: 'flex-end' }}>
            <Button
              type="submit"
              variant="contained"
              startIcon={<SearchIcon />}
              sx={{ minWidth: 120 }}
            >
              查询
            </Button>
          </Box>
        </Stack>
      </form>
    </Paper>
  );
};

export default LogSearch; 