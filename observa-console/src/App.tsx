import React from 'react';
import { ThemeProvider, CssBaseline, Container, Box } from '@mui/material';
import theme from './theme/theme';
import LogSearch from './components/LogSearch';
import LogViewer from './components/LogViewer';

const App: React.FC = () => (
  <ThemeProvider theme={theme}>
    <CssBaseline enableColorScheme/>
    <Container maxWidth="xl">
      <Box sx={{ py: 4 }}>
        <LogSearch />
        <LogViewer />
      </Box>
    </Container>
  </ThemeProvider>
);

export default App;