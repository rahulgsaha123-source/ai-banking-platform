import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import Register from './pages/Register';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Transfer from './pages/Transfer';
import ProtectedRoute from './components/ProtectedRoute';
import Notifications from './pages/Notifications';
import AiAssistant from './pages/AiAssistant';
<Route path="/ai-assistant" element={<AiAssistant />} />

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />   
        <Route path="/register" element={<Register />} />
        <Route path="/login" element={<Login />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/transfer" element={<Transfer />} />
        <Route path="/dashboard" element={<ProtectedRoute><Dashboard /></ProtectedRoute>} />
        <Route path="/notifications" element={<Notifications />} />
        <Route path="/ai-assistant" element={<AiAssistant />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;