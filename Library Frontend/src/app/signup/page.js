'use client'
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import axiosInstance from '../utils/axiosInstance';

export default function SignUp() {
    const [formData, setFormData] = useState({
        firstName: '',
        lastName: '',
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
        const { firstName, lastName, email, password } = formData;

        if (!firstName || !lastName || !email || !password) {
            setError('Παρακαλώ συμπληρώστε όλα τα πεδία.');
            return;
        }

        try {
            const response = await axiosInstance.post('/users/register', formData);
            alert('Ο λογαριασμός δημιουργήθηκε με επιτυχία!');
            router.push('/signin');
        } catch (error) {
            if (error.response && error.response.data && error.response.data.errors) {
                const errorMessage = error.response.data.errors.find(e => e.message.includes("duplicate key value violates unique constraint"));
                if (errorMessage) {
                    setError('Το email που χρησιμοποιήσατε υπάρχει ήδη.');
                } else {
                    setError('Υπήρξε πρόβλημα κατά τη δημιουργία του λογαριασμού. Δοκιμάστε ξανά.');
                }
            } else {
                setError('Υπήρξε πρόβλημα κατά τη δημιουργία του λογαριασμού. Δοκιμάστε ξανά.');
            }
        }
    };



    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
            <div className="w-full max-w-md bg-white rounded-lg shadow-md p-6">
                <h2 className="text-2xl font-bold mb-6 text-center">Εγγραφή</h2>
                {error && <p className="text-red-500 mb-4">{error}</p>}
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">Όνομα:</label>
                    <input
                        type="text"
                        name="firstName"
                        value={formData.firstName}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
                <div className="mb-4">
                    <label className="block text-gray-700 mb-2">Επίθετο:</label>
                    <input
                        type="text"
                        name="lastName"
                        value={formData.lastName}
                        onChange={handleChange}
                        className="w-full px-3 py-2 border border-gray-300 rounded focus:outline-none focus:ring-2 focus:ring-blue-500"
                    />
                </div>
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
                    className="w-full bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 transition duration-300"
                >
                    Δημιουργία Λογαριασμού
                </button>
            </div>
        </div>
    );
}
