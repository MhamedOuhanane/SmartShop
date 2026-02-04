import { useSelector } from 'react-redux'
import './App.css'
import type { RootState } from './app/store'
import type { UserState } from './features/user/UserType';
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import PublicRoute from './routes/PublicRoute';
import LoginForm from './pages/login/LoginForm';

function App() {
  const {isAuthenticated, user}: UserState = useSelector((state: RootState) => state.user);

    console.log(user);
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route element={<PublicRoute isAuth={isAuthenticated} />}>
          <Route element={<AuthLayout />}>
            <Route path='/login' element={<LoginForm />}/> 
          </Route>
        </Route>

      </Routes>
    </BrowserRouter>
  )
}

export default App
