import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { login } from '../../apis/authApi';

export default function LoginPage() {
    const navigate = useNavigate();
    const { saveCustomer } = useAuth();

    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);
        try {
            const data = await login(email, password);
            saveCustomer(data);
            navigate('/');
        } catch (err) {
            const msg = err?.response?.data?.message || 'Đăng nhập thất bại. Vui lòng thử lại.';
            setError(msg);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.wrapper}>
            <div style={styles.card}>
                {/* Logo / Tên khách sạn */}
                <div style={styles.logoArea}>
                    <p style={styles.hotelName}>ROYAL HOTEL</p>
                    <p style={styles.subtitle}>Chào mừng trở lại</p>
                </div>

                <form onSubmit={handleSubmit} style={styles.form}>
                    <div style={styles.field}>
                        <label style={styles.label}>Email</label>
                        <input
                            id="login-email"
                            type="email"
                            value={email}
                            required
                            autoComplete="email"
                            placeholder="your@email.com"
                            onChange={(e) => setEmail(e.target.value)}
                            style={styles.input}
                            onFocus={(e) => e.target.style.borderColor = '#D4A574'}
                            onBlur={(e) => e.target.style.borderColor = '#ddd'}
                        />
                    </div>

                    <div style={styles.field}>
                        <label style={styles.label}>Mật khẩu</label>
                        <input
                            id="login-password"
                            type="password"
                            value={password}
                            required
                            autoComplete="current-password"
                            placeholder="••••••••"
                            onChange={(e) => setPassword(e.target.value)}
                            style={styles.input}
                            onFocus={(e) => e.target.style.borderColor = '#D4A574'}
                            onBlur={(e) => e.target.style.borderColor = '#ddd'}
                        />
                    </div>

                    {error && (
                        <div style={styles.error}>{error}</div>
                    )}

                    <button
                        id="login-submit"
                        type="submit"
                        disabled={loading}
                        style={{
                            ...styles.button,
                            opacity: loading ? 0.7 : 1,
                            cursor: loading ? 'not-allowed' : 'pointer'
                        }}
                    >
                        {loading ? 'Đang xử lý...' : 'Đăng nhập'}
                    </button>
                </form>

                <p style={styles.backLink}>
                    <span
                        style={styles.backLinkText}
                        onClick={() => navigate('/')}
                    >
                        ← Quay về trang chủ
                    </span>
                </p>
            </div>
        </div>
    );
}

const styles = {
    wrapper: {
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
        padding: '20px',
    },
    card: {
        background: '#fff',
        borderRadius: '16px',
        padding: '48px 40px',
        width: '100%',
        maxWidth: '420px',
        boxShadow: '0 25px 60px rgba(0,0,0,0.3)',
    },
    logoArea: {
        textAlign: 'center',
        marginBottom: '36px',
    },
    hotelName: {
        fontSize: '26px',
        fontWeight: '900',
        letterSpacing: '2px',
        color: '#1a1a2e',
        margin: '0 0 8px 0',
    },
    subtitle: {
        fontSize: '14px',
        color: '#888',
        margin: 0,
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
        gap: '20px',
    },
    field: {
        display: 'flex',
        flexDirection: 'column',
        gap: '6px',
    },
    label: {
        fontSize: '13px',
        fontWeight: '600',
        color: '#444',
        letterSpacing: '0.3px',
    },
    input: {
        border: '1.5px solid #ddd',
        borderRadius: '8px',
        padding: '12px 14px',
        fontSize: '15px',
        outline: 'none',
        transition: 'border-color 0.2s ease',
        fontFamily: 'inherit',
        color: '#222',
    },
    error: {
        background: '#fff2f2',
        border: '1px solid #ffcdd2',
        borderRadius: '8px',
        padding: '10px 14px',
        color: '#c62828',
        fontSize: '13px',
    },
    button: {
        backgroundColor: '#D4A574',
        color: '#fff',
        border: 'none',
        borderRadius: '8px',
        padding: '14px',
        fontSize: '15px',
        fontWeight: '600',
        fontFamily: 'inherit',
        letterSpacing: '0.3px',
        transition: 'background-color 0.2s ease, transform 0.1s ease',
        marginTop: '4px',
    },
    backLink: {
        textAlign: 'center',
        marginTop: '24px',
        marginBottom: 0,
    },
    backLinkText: {
        fontSize: '13px',
        color: '#D4A574',
        cursor: 'pointer',
        fontWeight: '500',
    },
};
