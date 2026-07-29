// src/components/DocumentUpload.jsx
import { useState } from 'react';
import axiosClient from '../api/axiosClient';

function DocumentUpload() {
  const [file, setFile] = useState(null);
  const [status, setStatus] = useState('');
  const [loading, setLoading] = useState(false);

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files.length > 0) {
      setFile(e.target.files[0]);
      setStatus(''); 
    }
  };

  const handleUpload = async (e) => {
    e.preventDefault();
    if (!file) {
      setStatus('Please select a file first.');
      return;
    }

    setLoading(true);
    setStatus('Uploading to secure vault...');

    const formData = new FormData();
    formData.append('file', file); 

    try {
      const response = await axiosClient.post('/api/documents/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });
      
      setStatus(`✅ ${response.data}`); 
      setFile(null); 
    } catch (error) {
      setStatus(`❌ Upload failed: ${error.response?.data || error.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="p-6 bg-white border border-gray-200 rounded-lg shadow-sm mt-6">
      <h3 className="text-lg font-semibold text-gray-800 mb-3 border-b pb-2">KYC Document Upload</h3>
      
      <form onSubmit={handleUpload} className="flex flex-col gap-3">
        <input 
          type="file" 
          onChange={handleFileChange}
          className="block w-full text-sm text-gray-600
            file:mr-4 file:py-2 file:px-4
            file:rounded file:border-0
            file:text-sm file:font-semibold
            file:bg-blue-50 file:text-blue-700
            hover:file:bg-blue-100 transition cursor-pointer"
        />
        
        <button 
          type="submit" 
          disabled={!file || loading}
          className="bg-gray-800 text-white py-2 px-4 rounded hover:bg-gray-900 disabled:bg-gray-400 transition w-full md:w-auto self-start"
        >
          {loading ? 'Uploading...' : 'Secure Upload'}
        </button>
      </form>

      {status && (
        <p className={`mt-3 text-sm font-medium ${status.startsWith('✅') ? 'text-green-600' : status.startsWith('❌') ? 'text-red-600' : 'text-blue-600'}`}>
          {status}
        </p>
      )}
    </div>
  );
}

export default DocumentUpload;