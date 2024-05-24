document.getElementById('addUserForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const userId = document.getElementById('userId').value;
    const userName = document.getElementById('userName').value;
    const userAge = document.getElementById('userAge').value;
    const userPreferences = document.getElementById('userPreferences').value;

    fetch('/addUser', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify({ id: userId, name: userName, age: parseInt(userAge), preferences: userPreferences })
    })
    .then(response => response.text())
    .then(data => alert(data));
});

document.getElementById('deleteUserForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const userId = document.getElementById('deleteUserId').value;

    fetch('/deleteUser?id=' + userId, {
        method: 'POST'
    })
    .then(response => response.text())
    .then(data => alert(data));
});

document.getElementById('getRecommendationsForm').addEventListener('submit', function(event) {
    event.preventDefault();
    const userId = document.getElementById('recommendUserId').value;

    fetch('/recommendations?id=' + userId)
    .then(response => response.json())
    .then(data => {
        const recommendationsDiv = document.getElementById('recommendations');
        recommendationsDiv.innerHTML = '<h3>Recommendations:</h3>';
        data.forEach(user => {
            recommendationsDiv.innerHTML += `<p>${user.name} (Age: ${user.age}) - Preferences: ${user.preferences}</p>`;
        });
    });
});
