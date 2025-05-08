import axios from 'axios';

// API基础URL
const API_BASE_URL = '/observa-api'; // 使用相对路径配合Vite代理

// 日志查询接口参数类型
export interface LogQueryParams {
  operation?: string;      // 操作类型，默认为"query"
  namespaces?: string;     // 命名空间列表
  namespace_query?: string;// 命名空间查询
  pods?: string;           // Pod列表
  pod_query?: string;      // Pod查询
  containers?: string;     // 容器列表
  container_query?: string;// 容器查询
  log_query?: string;      // 日志内容查询
  interval?: string;       // 时间间隔
  start_time?: string;     // 开始时间
  end_time?: string;       // 结束时间
  sort?: string;           // 排序方式，默认为"desc"
  from?: number;           // 分页起始位置，默认为0
  size?: number;           // 每页大小，默认为10
  cluster?: string;        // 集群名称
}

// 日志查询响应类型
export interface LogResponse {
  total: number;           // 总日志数
  records: LogRecord[];     // 日志记录列表
  hasMore: boolean;        // 是否有更多数据
}

// 日志条目类型
export interface LogRecord {
  timestamp: string;       // 日志时间
  body: string;            // 日志内容
  namespace: string;       // 命名空间
  pod: string;             // Pod名称
  container: string;       // 容器名称
  cluster?: string;        // 集群名称
  severity?: string;       // 日志级别(可选)
}

// 直方图数据接口
export interface HistogramResponse {
  total: number;           // 总日志数
  histograms: HistogramBucket[]; // 直方图数据列表
}

// 直方图数据桶接口
export interface HistogramBucket {
  time: string;           // 时间点 '2024-03-20 00:00:00'
  count: number;          // 该时间点的日志数量
}

// 日志API服务
const logApi = {
  /**
   * 查询日志
   * @param params 查询参数
   * @returns 日志查询结果
   */
  queryLogs: async (params: LogQueryParams): Promise<LogResponse> => {
    try {
      console.log('Making API request with params:', params);
      const response = await axios.get<LogResponse>(`${API_BASE_URL}/logging/v1/logs`, {
        params: {
          operation: params.operation || 'query',
          namespaces: params.namespaces,
          namespace_query: params.namespace_query,
          pods: params.pods,
          pod_query: params.pod_query,
          containers: params.containers,
          container_query: params.container_query,
          log_query: params.log_query,
          interval: params.interval,
          start_time: params.start_time,
          end_time: params.end_time,
          sort: params.sort || 'desc',
          from: params.from !== undefined ? params.from : 0,
          size: params.size || 10,
          cluster: params.cluster
        }
      });
      console.log('API response data:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to query logs:', error);
      throw error;
    }
  },

  /**
   * 查询日志直方图数据
   * @param params 查询参数
   * @returns 直方图数据
   */
  queryHistogram: async (params: Omit<LogQueryParams, 'from' | 'size'>): Promise<HistogramResponse> => {
    try {
      console.log('Making histogram API request with params:', params);
      const response = await axios.get<HistogramResponse>(`${API_BASE_URL}/logging/v1/logs`, {
        params: {
          operation: 'histogram',
          namespaces: params.namespaces,
          namespace_query: params.namespace_query,
          pods: params.pods,
          pod_query: params.pod_query,
          containers: params.containers,
          container_query: params.container_query,
          log_query: params.log_query,
          interval: params.interval,
          start_time: params.start_time,
          end_time: params.end_time,
          sort: params.sort || 'desc',
          cluster: params.cluster
        }
      });
      console.log('Histogram API response data:', response.data);
      return response.data;
    } catch (error) {
      console.error('Failed to query histogram:', error);
      throw error;
    }
  }
};

export default logApi; 