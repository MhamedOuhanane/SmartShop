import { useSelector } from 'react-redux'
import './App.css'
import type { RootState } from './app/store'
import type { User } from './features/user/UserType';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import PublicRoute from './routes/PublicRoute';

function App() {
  const user: User = useSelector((state: RootState) => state.user.user);
  const isAuth: boolean = useSelector((state: RootState) => state.user.isAuthenticated);

  return (
    <BrowserRouter>
      <Routes>

        <Route element={<PublicRoute isAuth={isAuth} />}>
          <Route element={<AuthLayout />}>
            <Route path='/login' /> 
          </Route>
        </Route>

      </Routes>
    </BrowserRouter>
  )
}

export default App
