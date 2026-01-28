import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import './index.css'
import App from './App.tsx'
// import {store} from "./app/store.tsx";
// import { Provider } from "react-redux";

const container = document.getElementById('root');
if (!container) throw new Error('Root element not found')

createRoot(container).render(
  <StrictMode>
      {/*<Provider store={store}>*/}
          <App />
      {/*</Provider>*/}
  </StrictMode>,
)
