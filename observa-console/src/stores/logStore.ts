import { create } from 'zustand';
import logApi, { type LogQueryParams, type LogResponse, type HistogramResponse } from '../services/api';

interface LogStore {
  logs: LogResponse | null;
  histogram: HistogramResponse | null;
  loading: boolean;
  histogramLoading: boolean;
  error: string | null;
  showHistogram: boolean;
  queryParams: LogQueryParams;
  totalResults: number;
  histogramUpdateStrategy: 'always' | 'initialOnly' | 'manual';
  setQueryParams: (params: Partial<LogQueryParams>) => void;
  fetchLogs: (append?: boolean) => Promise<void>;
  fetchHistogram: () => Promise<void>;
  resetLogs: () => void;
  searchLogs: (query: string) => Promise<void>;
  toggleHistogram: () => void;
  setHistogramUpdateStrategy: (strategy: 'always' | 'initialOnly' | 'manual') => void;
}

const useLogStore = create<LogStore>((set, get) => ({
  logs: null,
  histogram: null,
  loading: false,
  histogramLoading: false,
  error: null,
  showHistogram: true,
  totalResults: 0,
  histogramUpdateStrategy: 'initialOnly',
  queryParams: {
    operation: 'query',
    sort: 'desc',
    from: 0,
    size: 50,
    interval: '1d',
  },
  setQueryParams: (params) => {
    console.log('Setting query params:', params);
    set((state) => ({
      queryParams: { ...state.queryParams, ...params },
    }));
  },
  fetchLogs: async (append = false) => {
    const { queryParams } = get();
    console.log('Fetching logs with params:', queryParams);
    set({ loading: true, error: null });
    try {
      const response = await logApi.queryLogs(queryParams);
      console.log('API response:', response);
      set((state) => ({
        logs: append && state.logs ? {
          ...response,
          records: [...state.logs.records, ...response.records],
          hasMore: response.records.length === queryParams.size
        } : {
          ...response,
          hasMore: response.records.length === queryParams.size
        },
        totalResults: response.total || 0,
        loading: false
      }));
    } catch (err) {
      console.error('Failed to fetch logs:', err);
      set({ error: 'Failed to fetch logs', loading: false });
    }
  },
  fetchHistogram: async () => {
    const { queryParams } = get();
    console.log('Fetching histogram with params:', queryParams);
    set({ histogramLoading: true, error: null });
    try {
      const response = await logApi.queryHistogram(queryParams);
      console.log('Histogram API response:', response);
      set({ histogram: response, histogramLoading: false });
    } catch (err) {
      console.error('Failed to fetch histogram:', err);
      set({ error: 'Failed to fetch histogram', histogramLoading: false });
    }
  },
  resetLogs: () => {
    console.log('Resetting logs');
    set((state) => ({
      logs: null,
      histogram: null,
      totalResults: 0,
      queryParams: {
        ...state.queryParams,
        from: 0
      }
    }));
  },
  searchLogs: async (query: string) => {
    console.log('Searching logs with query:', query);
    const { setQueryParams, resetLogs, fetchLogs, fetchHistogram } = get();
    resetLogs();
    setQueryParams({ log_query: query, from: 0 });
    
    try {
      await fetchLogs();
      await fetchHistogram();
    } catch (error) {
      console.error('Error during search:', error);
    }
  },
  toggleHistogram: () => {
    set((state) => ({ showHistogram: !state.showHistogram }));
  },
  setHistogramUpdateStrategy: (strategy) => {
    console.log('Setting histogram update strategy:', strategy);
    set({ histogramUpdateStrategy: strategy });
  }
}));

export default useLogStore; 