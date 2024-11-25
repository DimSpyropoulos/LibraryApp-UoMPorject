'use client'
import { useRouter } from 'next/navigation';
import NavigationMenu from "@/app/component/navigationMenu";

export default function UserPage() {
    const router = useRouter();

    const navigateToSearch = () => {
        router.push('/search');
    };

    const navigateToReturnBook = () => {
        router.push('/loan');
    };

    return (
        <>
            <NavigationMenu />
            <div className="flex flex-col items-center justify-center min-h-screen bg-gray-100 p-6">
                <div className="w-full max-w-md bg-white rounded-lg shadow-md p-6">
                    <h2 className="text-2xl font-bold mb-4 text-center">Καλώς ήρθες!</h2>
                    <p className="text-gray-600 mb-6 text-center">Επιλέξτε μια από τις παρακάτω επιλογές:</p>
                    <div className="flex flex-col space-y-4">
                        <button
                            onClick={navigateToSearch}
                            className="w-full bg-blue-500 text-white py-2 px-4 rounded hover:bg-blue-600 transition duration-300"
                        >
                            Αναζήτηση/Δανεισμός Βιβλίου
                        </button>
                        <button
                            onClick={navigateToReturnBook}
                            className="w-full bg-green-500 text-white py-2 px-4 rounded hover:bg-green-600 transition duration-300"
                        >
                            Επιστροφή Βιβλίου
                        </button>
                    </div>
                </div>
            </div>
        </>
    );
}
