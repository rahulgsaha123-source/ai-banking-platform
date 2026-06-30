// src/pages/AiAssistant.jsx
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { aiService } from '../services/aiService';

function AiAssistant() {
  const navigate = useNavigate();
  const [input, setInput] = useState('');
  const [messages, setMessages] = useState([
    { sender: 'ai', text: 'Hello! I am your AI Banking Assistant. How can I help you manage your finances today?' }
  ]);
  const [loading, setLoading] = useState(false);

  const handleSend = async (e) => {
    e.preventDefault();
    if (!input.trim()) return;

    const userMessage = input;
    setMessages((prev) => [...prev, { sender: 'user', text: userMessage }]);
    setInput('');
    setLoading(true);

    try {
      const responseText = await aiService.askQuestion(userMessage);
      setMessages((prev) => [...prev, { sender: 'ai', text: responseText }]);
    } catch (error) {
      setMessages((prev) => [...prev, { sender: 'ai', text: 'Sorry, I am having trouble connecting to the server right now.' }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-4xl mx-auto mt-10 p-6 bg-white rounded-lg shadow-md h-[80vh] flex flex-col">
      <div className="flex items-center justify-between mb-4 border-b pb-4">
        <h2 className="text-2xl font-bold text-gray-800">AI Banking Assistant</h2>
        <button onClick={() => navigate('/dashboard')} className="text-blue-600 hover:underline">
          &larr; Back to Dashboard
        </button>
      </div>

      <div className="flex-1 overflow-y-auto p-4 space-y-4 bg-gray-50 rounded-md border border-gray-200">
        {messages.map((msg, index) => (
          <div key={index} className={`flex ${msg.sender === 'user' ? 'justify-end' : 'justify-start'}`}>
            <div className={`max-w-[75%] p-3 rounded-lg ${msg.sender === 'user' ? 'bg-blue-600 text-white rounded-br-none' : 'bg-white text-gray-800 border border-gray-300 rounded-bl-none shadow-sm'}`}>
              <p className="whitespace-pre-wrap text-sm">{msg.text}</p>
            </div>
          </div>
        ))}
        {loading && (
          <div className="flex justify-start">
            <div className="bg-gray-200 text-gray-600 p-3 rounded-lg rounded-bl-none animate-pulse text-sm">
              Typing...
            </div>
          </div>
        )}
      </div>

      <form onSubmit={handleSend} className="mt-4 flex gap-2">
        <input
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Ask about saving strategies, budgeting, etc..."
          className="flex-1 p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
          disabled={loading}
        />
        <button
          type="submit"
          disabled={loading || !input.trim()}
          className="bg-blue-600 text-white px-6 py-3 rounded-lg hover:bg-blue-700 disabled:bg-blue-300 transition"
        >
          Send
        </button>
      </form>
    </div>
  );
}

export default AiAssistant;