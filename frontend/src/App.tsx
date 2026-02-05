import { useSelector } from 'react-redux'
import './App.css'
import type { RootState } from './app/store'
import type { UserState } from './type/UserType';
import { BrowserRouter, Route, Routes } from 'react-router-dom';
import AuthLayout from './layouts/AuthLayout';
import PublicRoute from './routes/PublicRoute';
import LoginForm from './pages/login/LoginForm';
import HomeRedirect from './components/auth/HomeRedirect';
import Unauthorized from './pages/error/Unauthorized';
import NotFound from './pages/error/NotFound';
import { Toaster } from 'sonner';
import ProtectedRoute from './routes/ProtectedRoute';
import AdminLayout from './layouts/AdminLayout';
import AdminClients from './pages/admin/AdminClients';
function App() {
  const {isAuthenticated, user}: UserState = useSelector((state: RootState) => state.user);

  return (
    <>
      <Toaster position="top-right" richColors closeButton />
      <BrowserRouter>
      <Routes>
        <Route path="/" element={<HomeRedirect />} />

        <Route element={<PublicRoute isAuth={isAuthenticated} />}>
          <Route element={<AuthLayout />}>
            <Route path='/login' element={<LoginForm />}/> 
          </Route>
        </Route>

        <Route element={<ProtectedRoute isAuth={isAuthenticated} allowedRole={["ADMIN"]} userRole={user?.role ?? 'CLIENT'} />}>
          <Route path="/admin" element={<AdminLayout />}>
              <Route path="clients" element={<AdminClients />} />
              <Route path="dashboard" element={<div>Dashboard Work in Progress</div>} />
            </Route>
        </Route>

        <Route path='/unauthorized' element={<Unauthorized />} />
        <Route path='*' element={<NotFound />} />
      </Routes>
    </BrowserRouter>
    </>
  )
}

export default App
