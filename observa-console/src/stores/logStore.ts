import { create } from 'zustand';
import logApi, { type LogQueryParams, type LogResponse } from '../services/api';

interface LogStore {
  logs: LogResponse | null;
  loading: boolean;
  error: string | null;
  queryParams: LogQueryParams;
  setQueryParams: (params: Partial<LogQueryParams>) => void;
  fetchLogs: () => Promise<void>;
}

const useLogStore = create<LogStore>((set, get) => ({
  logs: null,
  loading: false,
  error: null,
  queryParams: {
    operation: 'query',
    sort: 'desc',
    from: 0,
    size: 50,
  },
  setQueryParams: (params) => {
    set((state) => ({
      queryParams: { ...state.queryParams, ...params },
    }));
  },
  fetchLogs: async () => {
    const { queryParams } = get();
    set({ loading: true, error: null });
    try {
      const response = await logApi.queryLogs(queryParams);
      set({ logs: response, loading: false });
    } catch (err) {
      console.error('Failed to fetch logs:', err);
      set({ error: 'Failed to fetch logs', loading: false });
    }
  },
}));

export default useLogStore; 