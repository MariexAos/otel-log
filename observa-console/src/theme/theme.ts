import { createTheme } from '@mui/material/styles';

// Create a theme instance with light mode forced
const theme = createTheme({
  palette: {
    mode: 'light',
    // You can customize other palette colors here if needed
  },
  components: {
    // Disable automatic dark mode adaptations for all components
  },
});

export default theme; 