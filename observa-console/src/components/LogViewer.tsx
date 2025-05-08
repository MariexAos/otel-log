import React, { useEffect, useCallback } from 'react';
import { Card, Typography, Spin } from 'antd';
import { Virtuoso } from 'react-virtuoso';
import useLogStore from '../stores/logStore';
import { type LogRecord } from '../services/api';
import HighlightText from './HighlightText';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';

const { Text } = Typography;

// 设置 dayjs 使用中文
dayjs.locale('zh-cn');

// 定义列宽常量
const COLUMN_WIDTHS = {
  timestamp: 180,
  namespace: 120,
  pod: 150,
  container: 120,
} as const;

const LogViewer: React.FC = () => {
  const { logs, loading, error, queryParams, fetchLogs, searchLogs, setQueryParams } = useLogStore();

  useEffect(() => {
    if (!logs) {
      console.log('Initial fetch logs');
      fetchLogs();
    }
  }, [logs, fetchLogs]);

  useEffect(() => {
    if (queryParams.log_query !== undefined) {
      console.log('Search logs with query:', queryParams.log_query);
      searchLogs(queryParams.log_query);
    }
  }, [queryParams.log_query, searchLogs]);

  const loadMore = useCallback(async () => {
    if (!loading && logs?.records && logs.hasMore) {
      console.log('Loading more logs');
      const currentFrom = queryParams.from || 0;
      const currentSize = queryParams.size || 50;
      setQueryParams({ from: currentFrom + currentSize });
      await fetchLogs(true);
    }
  }, [loading, logs, queryParams.from, queryParams.size, fetchLogs, setQueryParams]);

  const formatDate = (dateString: string) => {
    return dayjs(dateString).format('YYYY年MM月DD日 HH:mm:ss');
  };

  const renderLogItem = (index: number, record: LogRecord) => (
    <div style={{ 
      padding: '8px 16px',
      borderBottom: '1px solid #f0f0f0',
      display: 'flex',
      gap: '16px',
      alignItems: 'flex-start'
    }}>
      <div style={{ width: COLUMN_WIDTHS.timestamp, flexShrink: 0 }}>
        {formatDate(record.timestamp)}
      </div>
      <div style={{ width: COLUMN_WIDTHS.namespace, flexShrink: 0 }}>
        {record.namespace}
      </div>
      <div style={{ width: COLUMN_WIDTHS.pod, flexShrink: 0 }}>
        {record.pod}
      </div>
      <div style={{ width: COLUMN_WIDTHS.container, flexShrink: 0 }}>
        {record.container}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <HighlightText 
          text={record.body} 
          highlight={queryParams.log_query || ''} 
        />
      </div>
    </div>
  );

  const renderFooter = () => {
    if (!loading) return null;
    return (
      <div style={{ 
        padding: '16px', 
        textAlign: 'center',
        borderTop: '1px solid #f0f0f0'
      }}>
        <Spin size="small" />
        <Text style={{ marginLeft: 8 }}>加载更多...</Text>
      </div>
    );
  };

  if (error) {
    return (
      <Card style={{ backgroundColor: '#fff2f0' }}>
        <Text type="danger">{error}</Text>
      </Card>
    );
  }

  if (!logs?.records?.length) {
    return (
      <Card>
        <Text type="secondary">No logs found</Text>
      </Card>
    );
  }

  return (
    <Card style={{ height: 'calc(100vh - 200px)' }}>
      <div style={{ 
        display: 'flex',
        padding: '8px 16px',
        borderBottom: '1px solid #f0f0f0',
        backgroundColor: '#fafafa',
        fontWeight: 'bold',
        position: 'sticky',
        top: 0,
        zIndex: 1,
        gap: '16px',
        alignItems: 'center'
      }}>
        <div style={{ width: COLUMN_WIDTHS.timestamp, flexShrink: 0 }}>时间</div>
        <div style={{ width: COLUMN_WIDTHS.namespace, flexShrink: 0 }}>命名空间</div>
        <div style={{ width: COLUMN_WIDTHS.pod, flexShrink: 0 }}>容器组</div>
        <div style={{ width: COLUMN_WIDTHS.container, flexShrink: 0 }}>容器</div>
        <div style={{ flex: 1, minWidth: 0 }}>日志</div>
      </div>
      <Virtuoso
        style={{ height: 'calc(100vh - 300px)' }}
        data={logs.records}
        itemContent={renderLogItem}
        endReached={loadMore}
        overscan={200}
        components={{
          Footer: renderFooter
        }}
      />
    </Card>
  );
};

export default LogViewer; 