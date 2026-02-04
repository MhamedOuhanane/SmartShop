import { useSelector } from 'react-redux'
import './App.css'
import type { RootState } from './app/store'
import type { UserState } from './features/user/UserType';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import PublicRoute from './routes/PublicRoute';
import LoginForm from './pages/login/LoginForm';
import HomeRedirect from './components/auth/HomeRedirect';
import Unauthorized from './pages/error/Unauthorized';
import NotFound from './pages/error/NotFound';

function App() {
  const {isAuthenticated, user}: UserState = useSelector((state: RootState) => state.user);

    console.log(user);
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomeRedirect />} />
        <Route element={<PublicRoute isAuth={isAuthenticated} />}>
          <Route element={<AuthLayout />}>
            <Route path='/login' element={<LoginForm />}/> 
          </Route>
        </Route>

        <Route path='/unauthorized' element={<Unauthorized />} />
        <Route path='*' element={<NotFound />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
