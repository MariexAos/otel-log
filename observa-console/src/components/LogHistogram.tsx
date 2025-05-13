import React, { useEffect } from 'react';
import { Card, Button, Spin, Tooltip, Statistic, Space, Typography } from 'antd';
import { Column } from '@ant-design/plots';
import { ReloadOutlined, EyeOutlined, EyeInvisibleOutlined } from '@ant-design/icons';
import useLogStore from '../stores/logStore';
import dayjs from 'dayjs';

const { Text } = Typography;

interface HistogramDataPoint {
  time: string;
  count: number;
}

// 搜索结果统计组件
const SearchResultStats: React.FC<{
  totalResults: number;
  onRefresh: () => void;
  onToggleView: () => void;
  showHistogram: boolean;
}> = ({ totalResults, onRefresh, onToggleView, showHistogram }) => {
  return (
    <Card 
      size="small" 
      style={{ marginBottom: showHistogram ? 0 : 16 }}
      styles={{ body: { padding: '16px' } }}
      variant="borderless"
    >
      <div style={{ 
        display: 'flex', 
        justifyContent: 'space-between', 
        alignItems: 'center',
      }}>
        <Statistic 
          title="搜索结果" 
          value={totalResults} 
          style={{ marginBottom: 0 }}
        />
        <Space>
          <Tooltip title="刷新直方图数据">
            <Button 
              type="text" 
              icon={<ReloadOutlined />}
              onClick={onRefresh}
            />
          </Tooltip>
          <Button 
            type="text" 
            icon={showHistogram ? <EyeInvisibleOutlined /> : <EyeOutlined />}
            onClick={onToggleView}
          />
        </Space>
      </div>
    </Card>
  );
};

// 直方图可视化组件
const HistogramChart: React.FC<{
  data: HistogramDataPoint[];
  isLoading: boolean;
  showUpdateHint: boolean;
}> = ({ data, isLoading, showUpdateHint }) => {
  const config = {
    data,
    xField: 'time',
    yField: 'count',
    columnWidthRatio: 0.3,
    color: '#B8DCEC',
    animation: false,
    xAxis: {
      tickCount: 8,
    },
    yAxis: {
      title: null,
    },
    tooltip: {
      items: [
        {name: '数量', channel: 'y'},
      ],
    },
    label: false,
    interactions: [{ type: 'element-active' }],
  };

  if (isLoading) {
    return (
      <div style={{ textAlign: 'center', padding: '32px 0' }}>
        <Spin>
          <div style={{ padding: '50px', background: 'rgba(0, 0, 0, 0.05)' }}>
            加载中...
          </div>
        </Spin>
      </div>
    );
  }

  if (data.length === 0) {
    return (
      <div style={{ textAlign: 'center', padding: '32px 0' }}>
        <Text type="secondary">暂无数据</Text>
      </div>
    );
  }

  return (
    <>
      <div style={{ height: '160px', padding: '16px 16px 0' }}>
        <Column {...config} />
      </div>
      {showUpdateHint && (
        <div style={{ textAlign: 'center', padding: '8px 0' }}>
          <Text type="secondary" style={{ fontSize: '12px' }}>
            已加载更多日志，点击刷新按钮更新直方图
          </Text>
        </div>
      )}
    </>
  );
};

// 主组件
const LogHistogram: React.FC = () => {
  const { 
    histogram, 
    histogramLoading,
    showHistogram,
    toggleHistogram,
    totalResults,
    fetchHistogram,
    queryParams,
    loading
  } = useLogStore();

  // 使用固定的初始加载时更新策略
  useEffect(() => {
    if (!showHistogram) return;
    
    const isInitialLoad = (queryParams.from || 0) === 0;
    
    if (isInitialLoad && !loading) {
      fetchHistogram();
    }
  }, [
    fetchHistogram, 
    showHistogram, 
    loading,
    queryParams.from,
    queryParams.log_query, 
    queryParams.start_time, 
    queryParams.end_time,
    queryParams.interval
  ]);

  const handleToggleHistogram = () => {
    toggleHistogram();
  };

  const handleRefresh = () => {
    fetchHistogram();
  };

  // Format time display based on interval
  const formatTime = (time: string) => {
    // Default to hour format
    const format = queryParams.interval === '1d' ? 'MM-DD' : 
                  queryParams.interval === '1h' ? 'HH:mm' : 
                  queryParams.interval === '1m' ? 'HH:mm:ss' : 'HH:mm';
    return dayjs(time).format(format);
  };

  // 判断是否显示更新提示
  const shouldShowUpdateHint = () => {
    return (queryParams.from || 0) > 0;
  };

  // 处理直方图数据格式化
  const histogramData: HistogramDataPoint[] = histogram?.histograms.map(item => ({
    time: formatTime(item.time),
    count: item.count
  })) || [];

  // 如果不显示直方图，只显示统计部分
  if (!showHistogram) {
    return (
      <SearchResultStats 
        totalResults={totalResults}
        onRefresh={handleRefresh}
        onToggleView={handleToggleHistogram}
        showHistogram={false}
      />
    );
  }

  // 显示完整直方图和统计信息
  return (
    <div 
      style={{ marginBottom: 16 }}
    >
      <SearchResultStats 
        totalResults={totalResults}
        onRefresh={handleRefresh}
        onToggleView={handleToggleHistogram}
        showHistogram={true}
      />
      
      <div style={{ padding: !histogramData.length || histogramLoading ? 0 : '16px 0 8px' }}>
        <Card>
        <HistogramChart 
          data={histogramData}
          isLoading={histogramLoading}
          showUpdateHint={shouldShowUpdateHint()}
        />
        </Card>
      </div>
    </div>
  );
};

export default LogHistogram; 