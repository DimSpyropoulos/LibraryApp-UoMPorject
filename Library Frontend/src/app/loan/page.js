"use client"
import {useState, useEffect} from 'react';
import axiosInstance from '../utils/axiosInstance';
import NavigationMenu from "@/app/component/navigationMenu";

export default function ReturnBookPage() {
    const [activeLoans, setActiveLoans] = useState([]);
    const [error, setError] = useState('');

    useEffect(() => {
        const fetchActiveLoans = async () => {
            try {
                const response = await axiosInstance.get('/loan/active');
                if (response.status === 200 && response.data.length > 0) {
                    setActiveLoans(response.data);
                }
            } catch (error) {
                setError('Error');
            }
        };

        fetchActiveLoans();
    }, []);

    const handleReturnBook = async (bookTitle) => {
        try {
            const response = await axiosInstance.post(`/loan/return-book?bookTitle=${bookTitle}`);
            if (response.status === 200) {
                alert('Book was returned successfully!');
                setActiveLoans(activeLoans.filter(loan => loan.bookTitle !== bookTitle)); // Update the list of active loans
            }
        } catch (error) {
            alert('There was a problem returning the book.Please try again!');
        }
    };

    return (
        <>
            <NavigationMenu/>
            <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
                <div className="w-full max-w-lg bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-2xl font-bold mb-6 text-center">Return Book</h2>
                    {error && <p className="text-red-500 mb-4">{error}</p>}
                    {activeLoans.length > 0 ? activeLoans.map((loan) => (
                        <div key={loan.loanId} className="border border-gray-300 p-4 rounded bg-gray-50 shadow-sm mb-4">
                            <p><strong>ISBN:</strong> {loan.bookIsbn}</p>
                            <p><strong>Title:</strong> {loan.bookTitle}</p>
                            <p><strong>Loan Date:</strong> {loan.loanDate}</p>
                            <p><strong>Return Date:</strong> {loan.returnDate}</p>
                            <button
                                onClick={() => handleReturnBook(loan.bookTitle)}
                                className="w-full bg-red-500 text-white py-2 px-4 rounded mt-4 hover:bg-red-600 transition duration-300"
                            >
                                Return
                            </button>
                        </div>
                    )) : <p className="text-gray-700 mt-4">No active loans found.</p>}
                </div>
            </div>
        </>
    );
}
