
import { useRouter } from 'next/navigation';

function NavigationMenu() {
    const router = useRouter();

    const handleLogout = () => {
        localStorage.removeItem('token');
        router.push('../');
    };

    return (
        <nav className="bg-gray-800 text-white p-4">
            <ul className="flex space-x-4">
                <li><button onClick={() => router.push('/search')}>Search</button></li>
                <li><button onClick={() => router.push('/loan')}>Book Loans</button></li>
                <li><button onClick={handleLogout}>Sign Out</button></li>
            </ul>
        </nav>
    );
}

export default NavigationMenu;
