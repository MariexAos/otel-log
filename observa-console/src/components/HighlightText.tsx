import React from 'react';
import { Box } from '@mui/material';

interface HighlightTextProps {
  text: string;
  highlight: string;
}

const HighlightText: React.FC<HighlightTextProps> = ({ text, highlight }) => {
  if (!highlight.trim()) {
    return <>{text}</>;
  }

  const parts = text.split(new RegExp(`(${highlight})`, 'gi'));

  return (
    <>
      {parts.map((part, i) => (
        part.toLowerCase() === highlight.toLowerCase() ? (
          <Box
            key={i}
            component="span"
            sx={{
              backgroundColor: 'warning.light',
              color: 'warning.contrastText',
              padding: '0 2px',
              borderRadius: '2px',
            }}
          >
            {part}
          </Box>
        ) : (
          <React.Fragment key={i}>{part}</React.Fragment>
        )
      ))}
    </>
  );
};

export default HighlightText; 