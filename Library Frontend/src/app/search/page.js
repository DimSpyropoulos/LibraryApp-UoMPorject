"use client"
import {useState} from 'react';
import axiosInstance from '../utils/axiosInstance';
import NavigationMenu from "@/app/component/navigationMenu";

export default function SearchPage() {
    const [searchQuery, setSearchQuery] = useState('');
    const [bookDetails, setBookDetails] = useState([]);
    const [error, setError] = useState('');

    const handleSearch = async () => {
        if (!searchQuery.trim()) {
            setError('Παρακαλώ εισάγετε έναν τίτλο ή συγγραφέα για την αναζήτηση.');
            return;
        }

        try {
            const response = await axiosInstance.get('/books/search', {
                query: searchQuery
            });

            if (response.status === 200 && response.data.length) {
                setBookDetails(response.data);
                setError('');
            } else {
                setBookDetails([]);
                setError('Δεν βρέθηκαν βιβλία με τα δοθέντα κριτήρια.');
            }
        } catch (error) {
            console.error(error);
            setBookDetails([]);
            setError('Υπήρξε πρόβλημα κατά την αναζήτηση. Δοκιμάστε ξανά.');
        }
    };
    const handleShowAll = async () => {


        try {
            const response = await axiosInstance.get('/books/search', {
                query: ""
            });

            setBookDetails(response.data);
            setError('');

        } catch (error) {
            console.error(error);
            setBookDetails([]);
            setError('Υπήρξε πρόβλημα κατά την αναζήτηση. Δοκιμάστε ξανά.');
        }
    };

    const handleBorrow = async (title) => {
        try {
            const response = await axiosInstance.post(`/loan/borrow?bookTitle=${title}`);
            if (response.status === 200) {
                alert('Το βιβλίο δανείστηκε επιτυχώς!');

                // Ενημέρωση του πίνακα bookDetails για να δείξει ότι το βιβλίο δεν είναι διαθέσιμο
                const updatedBooks = bookDetails.map(book => {
                    if (book.title === title) {
                        return { ...book, available: false };
                    }
                    return book;
                });
                setBookDetails(updatedBooks);

            } else {
                alert('Το βιβλίο δεν μπόρεσε να δανειστεί.');
            }
        } catch (error) {
            alert('Υπήρξε σφάλμα κατά την διαδικασία δανεισμού. Δοκιμάστε ξανά.');
        }
    };


    return (
        <>
            <NavigationMenu />
            <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
                <div className="w-full max-w-md bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-2xl font-bold mb-6 text-center">Search Book</h2>
                    {error && <p className="text-red-500 mb-4">{error}</p>}
                    <div className="flex items-center mb-4">
                        <input
                            type="text"
                            placeholder="Search title or author"
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="flex-grow focus:outline-none border border-gray-300 p-2 rounded-l"
                        />
                        <button
                            onClick={handleSearch}
                            className="bg-blue-500 text-white p-2 rounded-r hover:bg-blue-600 transition duration-300"
                        >
                            <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth={2}
                                 stroke="currentColor" className="w-6 h-6">
                                <path strokeLinecap="round" strokeLinejoin="round"
                                      d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
                            </svg>
                        </button>
                        <button
                            onClick={handleShowAll}
                            className="ml-2 bg-blue-500 text-white py-1 px-2 text-sm rounded hover:bg-blue-600 transition duration-300"
                        >
                            Show All
                        </button>
                    </div>
                    {bookDetails.map((book, index) => (
                        <div key={index} className="border border-gray-300 p-4 rounded bg-gray-50 shadow-sm mb-4">
                            <h3 className="text-lg font-semibold">Book Information:</h3>
                            <p><strong>ISBN:</strong> {book.isbn}</p>
                            <p><strong>Title:</strong> {book.title}</p>
                            <p><strong>Author:</strong> {book.author}</p>
                            <p><strong>Publisher:</strong> {book.publisher}</p>
                            <p><strong>Pages:</strong> {book.pages}</p>
                            <div className="flex justify-between items-center">
                                <p><strong>Available:</strong> {book.available ? 'Yes' : 'No'}</p>
                                {book.available && (
                                    <button
                                        onClick={() => handleBorrow(book.title)}
                                        className="bg-green-500 text-white py-1 px-2 text-sm rounded hover:bg-green-600 transition duration-300"
                                    >
                                        Borrow
                                    </button>
                                )}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </>

    );
}
