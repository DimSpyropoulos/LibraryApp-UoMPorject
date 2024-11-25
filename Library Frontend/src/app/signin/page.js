'use client'
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import axiosInstance from '../utils/axiosInstance';
import useAuthStore from '../stores/authStore';

export default function SignIn() {
    const [formData, setFormData] = useState({
        email: '',
        password: ''
    });
    const [error, setError] = useState('');
    const router = useRouter();

    const handleChange = (e) => {
        setFormData({
            ...formData,
            [e.target.name]: e.target.value
        });
    };

    const handleSubmit = async () => {
        const { email, password } = formData;
        if (!email || !password) {
            setError('Παρακαλώ συμπληρώστε και τα δύο πεδία.');
            return;
        }

        try {
            const response = await axiosInstance.post('/users/login', { email, password });
            if (response.status === 200 && response.data.access_token) {
                useAuthStore.getState().setToken(response.data.access_token);
                useAuthStore.getState().setUser(response.data.user); // Προσθέτουμε τον χρήστη
                router.push('/user');
            }
        } catch (error) {
            setError('Τα στοιχεία δεν είναι σωστά. Δοκιμάστε ξανά.');
        }
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
            <div className="w-full max-w-md bg-white rounded-lg shadow-md p-6">
                <h2 className="text-2xl font-bold mb-6 text-center">Σύνδεση</h2>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">Email:</label>
                    <input
                        type="email"
                        name="email"
                        value={formData.email}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div className="mb-6">
                    <label className="block text-gray-700 mb-2">Password:</label>
                    <input
                        type="password"
                        name="password"
                        value={formData.password}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <button
                    onClick={handleSubmit}
                    className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 transition duration-300"
                >
                    Σύνδεση
                </button>
            </div>
        </div>
    );
}
