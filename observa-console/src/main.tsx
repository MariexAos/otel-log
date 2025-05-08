import * as React from 'react';
import * as ReactDOM from 'react-dom/client';
import 'antd/dist/reset.css';
import '@ant-design/v5-patch-for-react-19';
import App from './App.tsx'

ReactDOM.createRoot(document.querySelector("#root")!).render(
  <React.StrictMode>
    <App />
  </React.StrictMode>
);
