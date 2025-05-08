import React from 'react';
import { Typography } from 'antd';

const { Text } = Typography;

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
          <Text
            key={i}
            style={{
              backgroundColor: '#faad14',
              color: '#fff',
              padding: '0 2px',
              borderRadius: '2px',
            }}
          >
            {part}
          </Text>
        ) : (
          <React.Fragment key={i}>{part}</React.Fragment>
        )
      ))}
    </>
  );
};

export default HighlightText; 