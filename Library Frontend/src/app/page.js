'use client'
import { useRouter } from 'next/navigation';

export default function Home() {
    const router = useRouter();

    const navigateToSignIn = () => {
        router.push('/signin');
    };

    const navigateToSignUp = () => {
        router.push('/signup');
    };

    return (
        <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
            <h1 className="text-4xl font-bold mb-4 text-gray-800">Welcome to our Library!</h1>
            <p className="text-lg mb-6 text-gray-600">Please select one of the following options:</p>
            <div className="flex space-x-4">
                <button
                    onClick={navigateToSignIn}
                    className="bg-blue-500 text-white py-2 px-6 rounded hover:bg-blue-600 transition duration-300"
                >
                    Sign In
                </button>
                <p>or</p>
                <button
                    onClick={navigateToSignUp}
                    className="bg-green-500 text-white py-2 px-6 rounded hover:bg-green-600 transition duration-300"
                >
                    Sign Up
                </button>
            </div>
        </div>
    );
}
