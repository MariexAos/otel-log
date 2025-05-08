import React from 'react';
import { ConfigProvider, theme } from 'antd';
import zhCN from 'antd/locale/zh_CN';
import LogSearch from './components/LogSearch';
import LogViewer from './components/LogViewer';
import LogHistogram from './components/LogHistogram';

const App: React.FC = () => (
  <ConfigProvider
    locale={zhCN}
    theme={{
      algorithm: theme.defaultAlgorithm,
    }}
  >
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '32px 0' }}>
      <LogSearch />
      <LogHistogram />
      <LogViewer />
    </div>
  </ConfigProvider>
);

export default App;