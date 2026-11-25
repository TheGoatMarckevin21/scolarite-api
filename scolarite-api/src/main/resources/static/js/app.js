const loginView = document.getElementById('login-view');
const appView = document.getElementById('app-view');
const loginForm = document.getElementById('login-form');
const loginError = document.getElementById('login-error');
const userEmailSpan = document.getElementById('user-email');

// Utilitaire centralisé : ajoute automatiquement le JWT à chaque appel API
async function apiFetch(url, options = {}) {
    const token = localStorage.getItem('jwt_token');
    const headers = { 'Content-Type': 'application/json', ...options.headers };
    if (token) headers['Authorization'] = `Bearer ${token}`;

    const res = await fetch(url, { ...options, headers });
    if (res.status === 401 || res.status === 403) {
        throw new Error("Accès refusé ou session expirée.");
    }
    return res;
}

// 1. Connexion
loginForm.addEventListener('submit', async (e) => {
    e.preventDefault();
    loginError.textContent = '';

    const email = document.getElementById('email').value;
    const motDePasse = document.getElementById('password').value;

    try {
        const res = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, motDePasse })
        });
        if (!res.ok) throw new Error("Identifiants incorrects.");

        const data = await res.json();
        localStorage.setItem('jwt_token', data.token);
        localStorage.setItem('user_email', data.email);
        afficherApp();
    } catch (err) {
        loginError.textContent = err.message;
    }
});

// 2. Déconnexion
document.getElementById('logout-btn').addEventListener('click', () => {
    localStorage.clear();
    appView.classList.add('hidden');
    loginView.classList.remove('hidden');
});

// 3. Création d'une filière
document.getElementById('filiere-form').addEventListener('submit', async (e) => {
    e.preventDefault();
    const msg = document.getElementById('filiere-msg');
    msg.textContent = '';

    const code = document.getElementById('filiere-code').value;
    const libelle = document.getElementById('filiere-libelle').value;

    try {
        const res = await apiFetch('/api/catalogue/filieres', {
            method: 'POST',
            body: JSON.stringify({ code, libelle })
        });
        if (!res.ok) {
            const err = await res.json();
            throw new Error(err.message || "Erreur lors de l'ajout.");
        }
        e.target.reset();
        msg.textContent = "Filière ajoutée avec succès.";
        chargerFilieres();
    } catch (err) {
        msg.textContent = err.message;
    }
});

// 4. Chargement des tableaux
async function chargerFilieres() {
    const res = await apiFetch('/api/catalogue/filieres');
    const filieres = await res.json();
    const tbody = document.getElementById('filieres-table');
    tbody.innerHTML = filieres.map(f =>
        `<tr><td>${f.id}</td><td><strong>${f.code}</strong></td><td>${f.libelle}</td></tr>`
    ).join('');
}

async function chargerInscriptions() {
    const res = await apiFetch('/api/inscriptions?page=0&size=10');
    const page = await res.json();
    const tbody = document.getElementById('inscriptions-table');
    tbody.innerHTML = page.content.map(i =>
        `<tr>
            <td><strong>${i.etudiant.matricule}</strong></td>
            <td>${i.etudiant.nom} ${i.etudiant.prenom}</td>
            <td>${i.filiere.code}</td>
            <td><span class="badge">${i.statut}</span></td>
        </tr>`
    ).join('');
}

function afficherApp() {
    userEmailSpan.textContent = localStorage.getItem('user_email');
    loginView.classList.add('hidden');
    appView.classList.remove('hidden');
    chargerFilieres();
    chargerInscriptions();
}

document.getElementById('refresh-btn').addEventListener('click', chargerInscriptions);

// Reconnexion automatique si le token est déjà présent
if (localStorage.getItem('jwt_token')) afficherApp();