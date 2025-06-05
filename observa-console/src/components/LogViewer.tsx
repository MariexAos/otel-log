import React, { useEffect, useCallback, useState } from 'react';
import { Card, Typography, Spin, Dropdown, Checkbox, Button } from 'antd';
import { SettingOutlined } from '@ant-design/icons';
import { Virtuoso } from 'react-virtuoso';
import useLogStore from '../stores/logStore';
import { type LogRecord } from '../services/api';
import HighlightText from './HighlightText';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';

const { Text } = Typography;

// 设置 dayjs 使用中文
dayjs.locale('zh-cn');

// 定义列配置
const COLUMNS = {
  timestamp: { key: 'timestamp', label: '时间', width: 220 },
  cluster: { key: 'cluster', label: '集群', width: 100 },
  namespace: { key: 'namespace', label: '命名空间', width: 120 },
  pod: { key: 'pod', label: '容器组', width: 150 },
  container: { key: 'container', label: '容器', width: 120 },
  body: { key: 'body', label: '日志', width: undefined }, // flex: 1
} as const;

type ColumnKey = keyof typeof COLUMNS;

const LogViewer: React.FC = () => {
  const { logs, loading, error, queryParams, fetchLogs, searchLogs, setQueryParams } = useLogStore();
  
  // 管理可见列状态
  const [visibleColumns, setVisibleColumns] = useState<ColumnKey[]>([
    'timestamp', 'cluster', 'namespace', 'pod', 'container', 'body'
  ]);

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
    const date = dayjs(dateString);
    return date.format('YYYY年MM月DD日 HH:mm:ss.SSS');
  };

  // 处理列可见性变化
  const handleColumnVisibilityChange = (checkedValues: ColumnKey[]) => {
    // 确保至少保留一列
    if (checkedValues.length > 0) {
      setVisibleColumns(checkedValues);
    }
  };

  // 渲染列设置菜单
  const renderColumnSettings = () => {
    const options = Object.entries(COLUMNS).map(([key, config]) => ({
      label: config.label,
      value: key as ColumnKey,
    }));

    return (
      <div style={{ 
        padding: '12px',
        backgroundColor: '#fff',
        borderRadius: '8px',
        boxShadow: '0 6px 16px 0 rgba(0, 0, 0, 0.08), 0 3px 6px -4px rgba(0, 0, 0, 0.12), 0 9px 28px 8px rgba(0, 0, 0, 0.05)',
        border: '1px solid #f0f0f0',
        minWidth: '160px'
      }}>
        <div style={{ 
          marginBottom: '8px', 
          fontWeight: 'bold',
          color: '#262626',
          fontSize: '14px'
        }}>
          列显示设置
        </div>
        <Checkbox.Group
          options={options}
          value={visibleColumns}
          onChange={handleColumnVisibilityChange}
          style={{ 
            display: 'flex', 
            flexDirection: 'column', 
            gap: '6px'
          }}
        />
      </div>
    );
  };

  // 获取单个列的渲染内容
  const getColumnContent = (columnKey: ColumnKey, record: LogRecord) => {
    switch (columnKey) {
      case 'timestamp':
        return formatDate(record.timestamp);
      case 'cluster':
        return record.cluster || '-';
      case 'namespace':
        return record.namespace;
      case 'pod':
        return record.pod;
      case 'container':
        return record.container;
      case 'body':
        return (
          <HighlightText 
            text={record.body} 
            highlight={queryParams.log_query || ''} 
          />
        );
      default:
        return '';
    }
  };

  const renderLogItem = (_index: number, record: LogRecord) => (
    <div style={{ 
      padding: '8px 16px',
      borderBottom: '1px solid #f0f0f0',
      display: 'flex',
      gap: '16px',
      alignItems: 'flex-start'
    }}>
      {visibleColumns.map((columnKey) => {
        const column = COLUMNS[columnKey];
        const isBodyColumn = columnKey === 'body';
        
        return (
          <div 
            key={columnKey}
            style={{ 
              width: isBodyColumn ? undefined : column.width,
              flex: isBodyColumn ? 1 : undefined,
              flexShrink: 0,
              minWidth: isBodyColumn ? 0 : undefined
            }}
          >
            {getColumnContent(columnKey, record)}
          </div>
        );
      })}
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
    <Card 
      style={{ height: 'calc(100vh - 200px)' }}
      extra={
        <Dropdown
          dropdownRender={() => renderColumnSettings()}
          trigger={['click']}
          placement="bottomRight"
        >
          <Button 
            icon={<SettingOutlined />} 
            size="small"
            type="text"
            style={{
              color: '#8c8c8c',
              border: 'none',
              boxShadow: 'none'
            }}
          >
            列设置
          </Button>
        </Dropdown>
      }
    >
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
        {visibleColumns.map((columnKey) => {
          const column = COLUMNS[columnKey];
          const isBodyColumn = columnKey === 'body';
          
          return (
            <div 
              key={columnKey}
              style={{ 
                width: isBodyColumn ? undefined : column.width,
                flex: isBodyColumn ? 1 : undefined,
                flexShrink: 0,
                minWidth: isBodyColumn ? 0 : undefined
              }}
            >
              {column.label}
            </div>
          );
        })}
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