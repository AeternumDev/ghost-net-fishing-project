document.addEventListener("DOMContentLoaded", function () {
    console.log("🌍 Leaflet.js Map wird initialisiert...");

    let map = L.map('map').setView([51.1657, 10.4515], 6);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
        attribution: '&copy; OpenStreetMap contributors'
    }).addTo(map);

    fetch('http://localhost:8080/ghostnetfishing/rest/geisternetze') // API-Pfad korrigiert
        .then(response => {
            if (!response.ok) {
                throw new Error(`Server antwortete mit Status ${response.status}`);
            }
            return response.json();
        })
        .then(data => {
            console.log("📌 Geisternetz-Daten geladen:", data);

            data.forEach(netz => {
                L.marker([netz.latitude, netz.longitude])
                    .addTo(map)
                    .bindPopup(`<b>Geisternetz ID:</b> ${netz.id}<br>
                                <b>Koordinaten:</b> ${netz.latitude}, ${netz.longitude}`);
            });
        })
        .catch(error => console.error("❌ Fehler beim Laden der Geisternetz-Daten:", error));
});
