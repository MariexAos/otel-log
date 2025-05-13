import React from 'react';
import { Card, Input, Button, Typography, Space, Row, Col } from 'antd';
import { SearchOutlined, ReloadOutlined } from '@ant-design/icons';
import { DatePicker } from 'antd';
import type { Dayjs } from 'dayjs';
import dayjs from 'dayjs';
import useLogStore from '../stores/logStore';

const { Title } = Typography;
const { TextArea } = Input;

const LogSearch: React.FC = () => {
  const { queryParams, setQueryParams, fetchLogs, resetLogs } = useLogStore();

  const handleSearch = (e: React.FormEvent) => {
    e.preventDefault();
    // 重置分页并重新搜索
    setQueryParams({ from: 0 });
    fetchLogs();
  };

  const handleReset = () => {
    // 先重置查询参数
    setQueryParams({
      namespace_query: '',
      pod_query: '',
      container_query: '',
      log_query: '',
      start_time: undefined,
      end_time: undefined,
      from: 0, // 重置分页
    });
    // 然后重置日志状态并重新获取
    resetLogs();
  };

  const handleTimeChange = (field: 'start_time' | 'end_time') => (date: Dayjs | null) => {
    if (date) {
      // 格式化为 ISO 8601 格式，但保留本地时区
      const formattedDate = date.format('YYYY-MM-DDTHH:mm:ss');
      setQueryParams({ [field]: formattedDate });
    } else {
      setQueryParams({ [field]: undefined });
    }
  };

  return (
    <Card style={{ marginBottom: 16 }}>
      <form onSubmit={handleSearch}>
        <Space direction="vertical" size="middle" style={{ width: '100%' }}>
          <Title level={5}>日志搜索</Title>
          <Row gutter={16}>
            <Col flex="1">
              <Input
                placeholder="命名空间"
                value={queryParams.namespace_query || ''}
                onChange={(e) => setQueryParams({ namespace_query: e.target.value })}
                allowClear
              />
            </Col>
            <Col flex="1">
              <Input
                placeholder="容器组"
                value={queryParams.pod_query || ''}
                onChange={(e) => setQueryParams({ pod_query: e.target.value })}
                allowClear
              />
            </Col>
            <Col flex="1">
              <Input
                placeholder="容器"
                value={queryParams.container_query || ''}
                onChange={(e) => setQueryParams({ container_query: e.target.value })}
                allowClear
              />
            </Col>
          </Row>
          <Row gutter={16}>
            <Col flex="1">
              <DatePicker
                showTime={{ format: 'HH:mm:ss' }}
                format="YYYY-MM-DD HH:mm:ss"
                value={queryParams.start_time ? dayjs(queryParams.start_time) : null}
                onChange={handleTimeChange('start_time')}
                placeholder="开始时间"
                style={{ width: '100%' }}
              />
            </Col>
            <Col flex="1">
              <DatePicker
                showTime={{ format: 'HH:mm:ss' }}
                format="YYYY-MM-DD HH:mm:ss"
                value={queryParams.end_time ? dayjs(queryParams.end_time) : null}
                onChange={handleTimeChange('end_time')}
                placeholder="结束时间"
                style={{ width: '100%' }}
              />
            </Col>
          </Row>
          <TextArea
            placeholder="关键词"
            value={queryParams.log_query || ''}
            onChange={(e) => setQueryParams({ log_query: e.target.value })}
            rows={2}
          />
          <Row justify="end" gutter={16}>
            <Col>
              <Button
                icon={<ReloadOutlined />}
                onClick={handleReset}
                style={{ minWidth: 120 }}
              >
                重置
              </Button>
            </Col>
            <Col>
              <Button
                type="primary"
                htmlType="submit"
                icon={<SearchOutlined />}
                style={{ minWidth: 120 }}
              >
                搜索
              </Button>
            </Col>
          </Row>
        </Space>
      </form>
    </Card>
  );
};

export default LogSearch; 